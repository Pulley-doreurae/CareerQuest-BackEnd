package pulleydoreurae.careerquestbackend.common.community.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPost;
import pulleydoreurae.careerquestbackend.common.community.exception.CommentNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.exception.PostLikeNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostViewCheckRepository;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostViewCheck;

/**
 * 커뮤니티에서 쟈주 사용되는 메서드 모음
 *
 * @author : parkjihyeok
 * @since : 2024/04/11
 */
@Slf4j
@Service
public class CommonCommunityService {

	@Value("${IMAGES_PATH}")
	private String IMAGES_PATH;

	private final PostRepository postRepository;
	private final UserAccountRepository userAccountRepository;
	private final PostViewCheckRepository postViewCheckRepository;
	private final CommentRepository commentRepository;
	private final PostLikeRepository postLikeRepository;
	private final PostImageRepository postImageRepository;

	public CommonCommunityService(PostRepository postRepository, UserAccountRepository userAccountRepository,
			PostViewCheckRepository postViewCheckRepository, CommentRepository commentRepository,
			PostLikeRepository postLikeRepository, PostImageRepository postImageRepository) {
		this.postRepository = postRepository;
		this.userAccountRepository = userAccountRepository;
		this.postViewCheckRepository = postViewCheckRepository;
		this.commentRepository = commentRepository;
		this.postLikeRepository = postLikeRepository;
		this.postImageRepository = postImageRepository;
	}

	/**
	 * 게시글 Entity -> 게시글 Response 변환 메서드
	 *
	 * @param post    게시글 정보
	 * @param isLiked 좋아요 정보 (리스트를 출력할땐 0? 혹은 방법찾아보기)
	 * @return 변환된 객체
	 */
	public PostResponse postToPostResponse(Post post, int isLiked) {
		return PostResponse.builder()
				.userId(post.getUserAccount().getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.images(postImageToStringList(post))
				.view(post.getView())
				.commentCount(countComment(post.getId()))
				.postLikeCount(countPostLike(post.getId()))
				.category(post.getCategory())
				.isLiked(isLiked)
				.createdAt(post.getCreatedAt())
				.modifiedAt(post.getModifiedAt())
				.build();
	}

	/**
	 * 게시글 엔티티 리스트 -> 게시글 response 리스트
	 *
	 * @param postList 게시글 엔티티 리스트
	 * @return 게시글 response 리스트
	 */
	public List<PostResponse> postListToPostResponseList(Page<Post> postList) {
		return postList.stream()
				// 게시글 리스트를 반환할땐 좋아요 상태를 사용하지 않는다. (0 으로 지정)
				.map(post -> postToPostResponse(post, 0))
				.toList();
	}

	/**
	 * 게시글 id로 게시글을 찾아오는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 게시글이 있다면 게시글을, 없다면 null 리턴
	 */
	public Post findPost(Long postId) {
		Optional<Post> findPost = postRepository.findById(postId);

		if (findPost.isEmpty()) {
			log.error("게시글을 찾을 수 없습니다. postId = {}", postId);
			throw new PostNotFoundException("요청한 게시글 정보를 찾을 수 없습니다.");
		}
		return findPost.get();
	}

	/**
	 * 댓글 id 로 댓글을 찾아오는 메서드
	 *
	 * @param commentId 댓글 id
	 * @return 해당하는 댓글이 있다면 댓글을, 없다면 null 리턴
	 */
	public Comment findComment(Long commentId) {
		Optional<Comment> optionalComment = commentRepository.findById(commentId);

		if (optionalComment.isEmpty()) {
			log.error("commentId = {} 의 댓글 정보를 찾을 수 없습니다.", commentId);
			throw new CommentNotFoundException("요청한 댓글 정보를 찾을 수 없습니다.");
		}
		return optionalComment.get();
	}

	/**
	 * 회원아이디로 회원정보를 찾아오는 메서드
	 *
	 * @param userId 회원아이디
	 * @return 해당하는 회원정보가 있으면 회원정보를, 없다면 null 리턴
	 */
	public UserAccount findUserAccount(String userId) {
		Optional<UserAccount> findUser = userAccountRepository.findByUserId(userId);

		// 회원정보를 찾을 수 없다면
		if (findUser.isEmpty()) {
			log.error("{} 의 회원 정보를 찾을 수 없습니다.", userId);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return findUser.get();
	}

	/**
	 * 조회수 중복을 확인하기 위해 Optional 을 제거하는 메서드
	 *
	 * @param userId 키값
	 * @return 제거한 결과 없으면 null, 있다면 해당 객체를 리턴
	 */
	public PostViewCheck findPostViewCheck(String userId) {
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
	public Post postRequestToPost(PostRequest postRequest, UserAccount user) {
		return BasicPost.builder()
				.userAccount(user)
				.title(postRequest.getTitle())
				.content(postRequest.getContent())
				.category(postRequest.getCategory())
				.view(0L)
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
	public Post postRequestToPostForUpdate(Post post, PostRequest postRequest, UserAccount user) {
		// 엔티티의 Setter 사용을 막기위해 새로운 post 생성하며 덮어쓰기

		return BasicPost.builder()
				.id(post.getId()) // id 를 덮어씌어 수정함
				.userAccount(user)
				.title(postRequest.getTitle())
				.content(postRequest.getContent())
				.category(postRequest.getCategory())
				.view(post.getView()) // 조회수도 유지
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
	public Long countComment(Long postId) {
		Post post = findPost(postId);
		return (long)commentRepository.findAllByPost(post).size();
	}

	/**
	 * 한 게시글에 달린 좋아요를 세어주는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 해당 게시글의 좋아요 수
	 */
	public Long countPostLike(Long postId) {
		Post post = findPost(postId);
		return (long)postLikeRepository.findAllByPost(post).size();
	}

	/**
	 * 회원정보가 없는 사용자를 식별하기 위해 쿠키에 UUID 를 추가하고 해당 UUID를 리턴하는 메서드
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
	public List<String> postImageToStringList(Post post) {
		return postImageRepository.findAllByPost(post).stream()
				.map(image -> IMAGES_PATH + image.getFileName())
				.toList();
	}

	/**
	 * 게시글과 사용자 정보로 좋아요 정보 가져오기
	 *
	 * @param post 게시글 정보
	 * @param user 사용자 정보
	 * @return 게시글 좋아요
	 */
	public PostLike findPostLike(Post post, UserAccount user) {
		Optional<PostLike> optionalPostLike = postLikeRepository.findByPostAndUserAccount(post, user);

		if (optionalPostLike.isEmpty()) {
			log.error("좋아요 정보를 찾을 수 없습니다. post = {},  user = {}", post, user);
			throw new PostLikeNotFoundException("요청한 좋아요 정보를 찾을 수 없습니다.");
		}
		return optionalPostLike.get();
	}
}
