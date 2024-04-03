package pulleydoreurae.careerquestbackend.community.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;

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

	@Autowired
	public PostService(PostRepository postRepository, UserAccountRepository userAccountRepository,
			CommentRepository commentRepository, PostLikeRepository postLikeRepository) {
		this.postRepository = postRepository;
		this.userAccountRepository = userAccountRepository;
		this.commentRepository = commentRepository;
		this.postLikeRepository = postLikeRepository;
	}

	/**
	 * 게시글 리스트를 불러오는 메서드
	 *
	 * @return Repository 에서 가져온 리스트 반환
	 */
	public List<PostResponse> getPostResponseList() {
		// TODO: 2024/03/31 페이징 처리나 불러오는 게시글의 양 조절이 필요
		return postRepository.findAll().stream()
				.map(post -> PostResponse.builder()
						.userId(post.getUserAccount().getUserId())
						.title(post.getTitle())
						.content(post.getContent())
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
	 * 게시글의 카테고리로 리스트를 불러오는 메서드
	 *
	 * @param category 카테고리 번호
	 * @return 카테고리에 맞는 리스트 반환
	 */
	public List<PostResponse> getPostResponseListByCategory(Long category) {
		// TODO: 2024/04/2 페이징 처리 혹은 게시글 양 조절이 필요
		return postRepository.findAllByCategory(category).stream()
				.map(post -> PostResponse.builder()
						.userId(post.getUserAccount().getUserId())
						.title(post.getTitle())
						.content(post.getContent())
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
	 * 한 사용자가 작성한 리스트를 불러오는 메서드
	 *
	 * @param userId 회원아이디
	 * @return 회원정보에 맞는 리스트 반환
	 */
	public List<PostResponse> getPostListByUserAccount(String userId) {
		UserAccount user = findUserAccount(userId);
		if (user == null) {
			return null;
		}
		// TODO: 2024/04/2 페이징 처리 혹은 게시글 양 조절이 필요
		return postRepository.findAllByUserAccount(user).stream()
				.map(post -> PostResponse.builder()
						.userId(post.getUserAccount().getUserId())
						.title(post.getTitle())
						.content(post.getContent())
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
	 * 하나의 게시글을 불러오는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 게시글 dto 를 반환
	 */
	public PostResponse findByPostId(Long postId) {
		Post post = findPost(postId);
		if (post == null) {
			return null;
		}

		// TODO: 2024/03/31 조회수 로직 수정이 필요
		post.setHit(post.getHit() + 1); // 조회수 증가

		return PostResponse.builder()
				.userId(post.getUserAccount().getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.hit(post.getHit())
				.commentCount(countComment(post.getId()))
				.postLikeCount(countPostLike(post.getId()))
				.category(post.getCategory())
				.createdAt(post.getCreatedAt())
				.modifiedAt(post.getModifiedAt())
				.build();
	}

	/**
	 * 게시글 저장 메서드
	 *
	 * @param postRequest 게시글 요청
	 * @return 게시글 저장에 성공하면 true 실패하면 false 리턴
	 */
	public boolean savePost(PostRequest postRequest) {
		UserAccount user = findUserAccount(postRequest.getUserId());
		if (user == null) {
			return false;
		}

		Post post = postRequestToPost(postRequest, user);
		postRepository.save(post);

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

		post = postRequestToPostForUpdate(post, postRequest, user);
		postRepository.save(post);

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

		return true;
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
}
