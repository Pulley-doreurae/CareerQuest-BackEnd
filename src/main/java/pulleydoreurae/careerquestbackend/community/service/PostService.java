package pulleydoreurae.careerquestbackend.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.common.service.FileManagementService;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostImage;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostViewCheck;
import pulleydoreurae.careerquestbackend.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostViewCheckRepository;

/**
 * 게시판을 담당하는 Service
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
@Slf4j
@Service
public class PostService {

	private final PostRepository postRepository;
	private final UserAccountRepository userAccountRepository;
	private final CommentRepository commentRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostViewCheckRepository postViewCheckRepository;
	private final PostImageRepository postImageRepository;
	private final FileManagementService fileManagementService;

	@Value("${IMAGES_PATH}")
	private String IMAGE_PATH;

	@Autowired
	public PostService(PostRepository postRepository, UserAccountRepository userAccountRepository,
			CommentRepository commentRepository, PostLikeRepository postLikeRepository,
			PostViewCheckRepository postViewCheckRepository, PostImageRepository postImageRepository,
			FileManagementService fileManagementService) {
		this.postRepository = postRepository;
		this.userAccountRepository = userAccountRepository;
		this.commentRepository = commentRepository;
		this.postLikeRepository = postLikeRepository;
		this.postViewCheckRepository = postViewCheckRepository;
		this.postImageRepository = postImageRepository;
		this.fileManagementService = fileManagementService;
	}

	/**
	 * 게시글 리스트를 불러오는 메서드
	 *
	 * @param pageable 페이지
	 * @return Repository 에서 가져온 리스트 반환
	 */
	public List<PostResponse> getPostResponseList(Pageable pageable) {
		return postListToPostResponseList(postRepository.findAllByOrderByIdDesc(pageable));
	}

	/**
	 * 게시글의 카테고리로 리스트를 불러오는 메서드
	 *
	 * @param category 카테고리 번호
	 * @param pageable 페이지
	 * @return 카테고리에 맞는 리스트 반환
	 */
	public List<PostResponse> getPostResponseListByCategory(Long category, Pageable pageable) {

		return postListToPostResponseList(postRepository.findAllByCategoryOrderByIdDesc(category, pageable));
	}

	/**
	 * 한 사용자가 작성한 리스트를 불러오는 메서드 (15 개 씩 페이지로 나눠서 호출함)
	 *
	 * @param userId   회원아이디
	 * @param pageable 페이지
	 * @return 회원정보에 맞는 리스트 반환
	 */
	public List<PostResponse> getPostListByUserAccount(String userId, Pageable pageable) {
		UserAccount user = findUserAccount(userId);
		if (user == null) {
			return null;
		}
		return postListToPostResponseList(postRepository.findAllByUserAccountOrderByIdDesc(user, pageable));
	}

	/**
	 * 게시글 검색 메서드
	 *
	 * @param keyword  키워드
	 * @param category 카테고리 (필수값 X)
	 * @param pageable 페이지
	 * @return 검색결과
	 */
	public List<PostResponse> searchPosts(String keyword, Long category, Pageable pageable) {

		// 카테고리 없이 전체 검색
		if (category == null) {
			return postListToPostResponseList(
					postRepository.searchByKeyword(keyword, pageable));
		}

		// 카테고리 포함 검색
		return postListToPostResponseList(
				postRepository.searchByKeywordAndCategory(keyword, category, pageable));
	}

	/**
	 * 하나의 게시글을 불러오는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 게시글 dto 를 반환
	 */
	public PostResponse findByPostId(HttpServletRequest request, HttpServletResponse response, Long postId) {
		Post post = findPost(postId);
		if (post == null) {
			return null;
		}

		// 회원정보(userId) 가져오기
		String name;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// 인증 정보가 있고 로그인한 사용자 일때
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			name = authentication.getName();
		} else { // 회원정보가 없다면 쿠키와 UUID 를 사용해 식별한다.
			name = getUUID(request, response);
		}

		PostViewCheck postViewCheck = findPostViewCheck(name);
		if (postViewCheck == null || !postViewCheck.getPostId().equals(postId)) { // Redis 에 저장되어 있지 않다면 저장하고 조회수 증가
			post.setHit(post.getHit() + 1); // 조회수 증가
			postViewCheckRepository.save(new PostViewCheck(name, postId)); // 지정한 시간동안 저장
		}

		// userId 로 회원을 가져온다.
		UserAccount user = findUserAccount(name);
		// null 이거나 좋아요 누른 정보를 가져올 수 없다면 0, 눌렀다면 1
		int isLiked = postLikeRepository.existsByPostAndUserAccount(post, user) ? 1 : 0;

		return PostResponse.builder()
				.userId(post.getUserAccount().getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.images(postImageToStringList(post))
				.hit(post.getHit())
				.commentCount(countComment(post.getId()))
				.postLikeCount(countPostLike(post.getId()))
				.category(post.getCategory())
				.isLiked(isLiked)
				.createdAt(post.getCreatedAt())
				.modifiedAt(post.getModifiedAt())
				.build();
	}

	/**
	 * 이미지 저장 메서드
	 *
	 * @param images 컨트롤러로부터 전달받은 이미지 파일들
	 * @return 한장이라도 실패하면 null, 성공하면 저장한 이미지 파일들의 파일명을 리스트 형태로 반환
	 */
	public List<String> saveImage(List<MultipartFile> images) {
		List<String> successFiles = new ArrayList<>();
		images.forEach(image -> {
			String savedFile = fileManagementService.saveFile(image, IMAGE_PATH);
			// 파일 저장에 한건이라도 실패한다면
			if (savedFile == null) {
				fileManagementService.deleteFile(successFiles, IMAGE_PATH);
				throw new RuntimeException("파일 저장에 실패했습니다.");
			}
			successFiles.add(savedFile);
		});
		return successFiles;
	}

	/**
	 * 게시글 저장 메서드
	 *
	 * @param postRequest 게시글 요청
	 * @return 게시글 저장에 성공하면 true 실패하면 false 리턴
	 */
	@Transactional // 사진 저장과 게시글 저장을 하나의 트랜잭션으로 묶음
	public boolean savePost(PostRequest postRequest) {
		UserAccount user = findUserAccount(postRequest.getUserId());
		List<String> fileNames = postRequest.getImages();

		// 사용자 정보를 확인할 수 없다면 (저장에 실패한다면)
		if (user == null) {
			// 서버에 저장된 사진이 있을경우 삭제
			if (fileNames != null) {
				fileManagementService.deleteFile(fileNames, IMAGE_PATH);
			}
			return false;
		}

		Post post = postRequestToPost(postRequest, user);
		try {
			postRepository.save(post);
		} catch (Exception e) {
			log.error("게시글 저장 실패 {}", e.getMessage());
			// 예외가 발생해 게시글 저장에 실패한 경우 미리 저장한 사진 정보를 삭제한다.
			if (fileNames != null) {
				fileManagementService.deleteFile(fileNames, IMAGE_PATH);
			}
			throw new RuntimeException("게시글 저장에 실패했습니다.");
		}

		// 게시글 저장이 무사히 완료되고 사진이 서버에 저장되어 있다면 데이터베이스에 해당 정보 입력
		if (fileNames != null) {
			fileNames.forEach(fileName -> {
				PostImage image = PostImage.builder()
						.post(post)
						.fileName(fileName)
						.build();
				postImageRepository.save(image);
			});
		}

		return true;
	}

	/**
	 * 게시글 수정 메서드
	 *
	 * @param postId      게시글 id
	 * @param postRequest 게시글 수정요청
	 * @return 수정에 성공하면 true 실패하면 false
	 */
	public boolean updatePost(Long postId, PostRequest postRequest) {
		Post post = findPost(postId);
		UserAccount user = findUserAccount(postRequest.getUserId());
		// 게시글을 찾지못하거나, 회원정보가 없거나(null 인 경우), 작성자와 수정자가 다르다면 실패
		if (post == null || user == null || !post.getUserAccount().getUserId().equals(user.getUserId())) {
			return false;
		}

		if (postRequest.getImages() != null) {
			updateImages(postRequest.getImages(), post);
		}

		Post updatedPost = postRequestToPostForUpdate(post, postRequest, user);
		postRepository.save(updatedPost);

		return true;
	}

	/**
	 * 게시글 삭제 메서드
	 *
	 * @param postId 게시글 id
	 * @param userId 삭제 요청자
	 * @return 삭제 요청이 성공이면 true 실패하면 false
	 */
	public boolean deletePost(Long postId, String userId) {
		UserAccount user = findUserAccount(userId);
		Post post = findPost(postId);
		// 게시글을 찾지못하거나, 회원정보가 없거나(null 인 경우), 작성자와 요청자가 다르다면 실패 (권한 없음)
		if (post == null || user == null || !post.getUserAccount().getUserId().equals(user.getUserId())) {
			return false;
		}
		postRepository.deleteById(postId);

		List<PostImage> fileNames = postImageRepository.findAllByPost(post);
		// 저장된 사진 파일이 존재한다면 게시글 삭제하면서 사진도 삭제
		if (fileNames != null) {
			fileManagementService.deleteFile(fileNames.stream().map(PostImage::getFileName).toList(), IMAGE_PATH);
		}

		return true;
	}

	/**
	 * 게시글 엔티티 리스트 -> 게시글 response 리스트
	 *
	 * @param postList 게시글 엔티티 리스트
	 * @return 게시글 response 리스트
	 */
	private List<PostResponse> postListToPostResponseList(Page<Post> postList) {
		return postList.stream()
				.map(post -> PostResponse.builder()
						.userId(post.getUserAccount().getUserId())
						.title(post.getTitle())
						.content(post.getContent())
						.images(postImageToStringList(post))
						.hit(post.getHit())
						.commentCount(countComment(post.getId()))
						.postLikeCount(countPostLike(post.getId()))
						.category(post.getCategory())
						.createdAt(post.getCreatedAt())
						.modifiedAt(post.getModifiedAt())
						.build())
				.toList();
	}

	/**
	 * 게시글 id로 게시글을 찾아오는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 게시글이 있다면 게시글을, 없다면 null 리턴
	 */
	private Post findPost(Long postId) {
		Optional<Post> findPost = postRepository.findById(postId);

		if (findPost.isEmpty()) {
			log.warn("게시글을 찾을 수 없습니다. postId = {}", postId);
			return null;
		}
		return findPost.get();
	}

	/**
	 * 회원아이디로 회원정보를 찾아오는 메서드
	 *
	 * @param userId 회원아이디
	 * @return 해당하는 회원정보가 있으면 회원정보를, 없다면 null 리턴
	 */
	private UserAccount findUserAccount(String userId) {
		Optional<UserAccount> findUser = userAccountRepository.findByUserId(userId);

		// 회원정보를 찾을 수 없다면
		if (findUser.isEmpty()) {
			log.error("{} 의 회원 정보를 찾을 수 없습니다.", userId);
			return null;
		}
		return findUser.get();
	}

	/**
	 * 조회수 중복을 확인하기 위해 Optional 을 제거하는 메서드
	 *
	 * @param userId 키값
	 * @return 제거한 결과 없으면 null, 있다면 해당 객체를 리턴
	 */
	private PostViewCheck findPostViewCheck(String userId) {
		Optional<PostViewCheck> findPostViewCheck = postViewCheckRepository.findById(userId);
		return findPostViewCheck.orElse(null);
	}

	/**
	 * 게시글 요청 -> 게시글 엔티티 변환 메서드 (작성시 사용)
	 *
	 * @param postRequest 게시글 요청
	 * @param user        회원정보
	 * @return 게시글 엔티티
	 */
	private Post postRequestToPost(PostRequest postRequest, UserAccount user) {
		return Post.builder()
				.userAccount(user)
				.title(postRequest.getTitle())
				.content(postRequest.getContent())
				.category(postRequest.getCategory())
				.hit(0L)
				.build();
	}

	/**
	 * 게시글 수정 요청 -> 게시글 엔티티 변환 메서드 (수정시 사용)
	 *
	 * @param post        수정할 게시글 엔티티 전달
	 * @param postRequest 수정할 게시글
	 * @param user        작성자(수정자)
	 * @return 게시글 엔티티
	 */
	private Post postRequestToPostForUpdate(Post post, PostRequest postRequest, UserAccount user) {
		// 엔티티의 Setter 사용을 막기위해 새로운 post 생성하며 덮어쓰기

		return Post.builder()
				.id(post.getId()) // id 를 덮어씌어 수정함
				.userAccount(user)
				.title(postRequest.getTitle())
				.content(postRequest.getContent())
				.category(postRequest.getCategory())
				.hit(post.getHit()) // 조회수도 유지
				.comments(post.getComments()) // 댓글 리스트도 유지
				.postLikes(post.getPostLikes()) // 좋아요 리스트도 유지
				.build();
	}

	/**
	 * 한 게시글에 달린 댓글을 세어주는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 해당 게시글의 댓글 수
	 */
	private Long countComment(Long postId) {
		Post post = findPost(postId);
		return (long)commentRepository.findAllByPost(post).size();
	}

	/**
	 * 한 게시글에 달린 좋아요를 세어주는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 해당 게시글의 좋아요 수
	 */
	private Long countPostLike(Long postId) {
		Post post = findPost(postId);
		return (long)postLikeRepository.findAllByPost(post).size();
	}

	/**
	 * 회원정보가 없는 사용자를 식별하기 위해 UUID 를 리턴하는 메서드
	 *
	 * @param request  요청
	 * @param response 응답
	 * @return UUID 값 리턴
	 */
	public String getUUID(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		String name = null;

		if (cookies != null) { // 쿠키가 있다면 UUID 가 있는지 확인한다.
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("UUID")) {
					name = cookie.getValue();
					break;
				}
			}
		}
		if (name == null) { // 쿠키가 없거나 있더라도 UUID 값이 없다면 UUID 값을 새로 만들어 쿠키에 저장한다.
			name = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("UUID", name);
			cookie.setMaxAge(60 * 60 * 24); // UUID 쿠키는 10일 동안 사용한다.
			response.addCookie(cookie); // UUID 값 쿠키에 저장
		}

		return name;
	}

	/**
	 * 한 게시글달린 사진 파일명들을 반환하는 메서드
	 *
	 * @param post 게시글 정보
	 * @return 저장된 파일명 리스트
	 */
	private List<String> postImageToStringList(Post post) {
		return postImageRepository.findAllByPost(post).stream()
				.map(PostImage::getFileName)
				.toList();
	}

	/**
	 * 새롭게 전달된 사진 정보를 바탕으로 삭제될 파일을 서버와 데이터베이스에서 제거하고 새로운 내용 데이터베이스에 추가하는 메서드
	 *
	 * @param images 업데이트된 사진정보
	 * @param post   게시글 정보
	 */
	public void updateImages(List<String> images, Post post) {
		// 서버에서 삭제할 파일명 찾기
		List<String> fileNamesToDelete = postImageRepository.findAllByPost(post).stream()
				.map(PostImage::getFileName)
				.filter(fileName -> !images.contains(fileName)).toList();

		// 서버에서 먼저 파일들 삭제
		fileManagementService.deleteFile(fileNamesToDelete, IMAGE_PATH);
		// 삭제한 파일들 데이터베이스에서 삭제
		fileNamesToDelete.forEach(postImageRepository::deleteByFileName);

		// 새로 추가된 파일명들만 데이터베이스에 저장
		images.stream()
				.filter(fileName -> !postImageRepository.existsByFileName(fileName))
				.forEach(fileName -> {
					PostImage image = PostImage.builder()
							.post(post)
							.fileName(fileName)
							.build();

					postImageRepository.save(image);
				});
	}
}
