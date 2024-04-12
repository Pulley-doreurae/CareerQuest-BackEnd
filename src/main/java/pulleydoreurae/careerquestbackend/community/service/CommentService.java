package pulleydoreurae.careerquestbackend.community.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;

/**
 * 댓글 Service
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@Slf4j
@Service
public class CommentService {

	private final UserAccountRepository userAccountRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	public CommentService(UserAccountRepository userAccountRepository, PostRepository postRepository,
			CommentRepository commentRepository) {
		this.userAccountRepository = userAccountRepository;
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
	}

	/**
	 * 댓글 저장 메서드
	 *
	 * @param commentRequest 댓글 요청
	 * @return 저장에 성공하면 ture, 실패하면 false
	 */
	public boolean saveComment(CommentRequest commentRequest) {
		UserAccount user = findUserAccount(commentRequest.getUserId());
		Post post = findPost(commentRequest.getPostId());

		// 회원정보를 찾을 수 없거나 게시글 정보를 찾을 수 없다면 실패
		if (user == null || post == null) {
			return false;
		}
		Comment comment = commentRequestToComment(commentRequest, user, post);
		commentRepository.save(comment);
		return true;
	}

	/**
	 * 댓글 수정 메서드
	 *
	 * @param commentRequest 댓글 요청
	 * @param commentId      댓글 id
	 * @return 수정에 성공하면 true, 실패하면 false
	 */
	public boolean updateComment(CommentRequest commentRequest, Long commentId) {
		UserAccount user = findUserAccount(commentRequest.getUserId());
		Post post = findPost(commentRequest.getPostId());
		Comment comment = findComment(commentId);

		// 회원정보가 없거나, 게시글 정보가 없거나, 댓글 정보가 없거나, 게시글과 댓글이 연결되어 있지 않거나, 작성자와 수정자가 다르다면 실패
		if (user == null || post == null || comment == null ||
				!comment.getPost().getId().equals(commentRequest.getPostId()) ||
				!comment.getUserAccount().getUserId().equals(user.getUserId())) {

			return false;
		}
		comment = commentRequestToCommentForUpdate(commentRequest, user, post, commentId);
		commentRepository.save(comment);
		return true;
	}

	/**
	 * 댓글 삭제 메서드
	 *
	 * @param commentId 댓글 id
	 * @param postId    게시글 id
	 * @param userId    회원아이디
	 * @return 삭제에 성공하면 true, 실패하면 false
	 */
	public boolean deleteComment(Long commentId, Long postId, String userId) {
		UserAccount user = findUserAccount(userId);
		Post post = findPost(postId);
		Comment comment = findComment(commentId);

		// 회원정보가 없거나, 게시글 정보가 없거나, 댓글 정보가 없거나, 게시글과 댓글이 연결되어 있지 않거나, 작성자와 요청자가 다르다면 실패
		if (user == null || post == null || comment == null ||
				!comment.getPost().getId().equals(commentId) ||
				!comment.getUserAccount().getUserId().equals(user.getUserId())) {

			return false;
		}
		commentRepository.deleteById(commentId);
		return true;
	}

	/**
	 * 한 게시글에 작성된 댓글리스트를 반환하는 메서드
	 *
	 * @param postId   게시글 id
	 * @param pageable 페이지
	 * @return 댓글 리스트
	 */
	public List<CommentResponse> findListByPostId(Long postId, Pageable pageable) {
		Post post = findPost(postId);
		return commentRepository.findAllByPostOrderByIdDesc(post, pageable).stream()
				.map(comment -> CommentResponse.builder()
						.userId(comment.getUserAccount().getUserId())
						.postId(comment.getPost().getId())
						.content(comment.getContent())
						.createdAt(comment.getCreatedAt())
						.modifiedAt(comment.getModifiedAt())
						.build()).toList();
	}

	/**
	 * 한 사용자가 작성한 댓글리스트를 반환하는 메서드
	 *
	 * @param userId   작성자 아이디
	 * @param pageable 페이지
	 * @return 댓글 리스트
	 */
	public List<CommentResponse> findListByUserAccount(String userId, Pageable pageable) {
		UserAccount user = findUserAccount(userId);
		return commentRepository.findAllByUserAccountOrderByIdDesc(user, pageable).stream()
				.map(comment -> CommentResponse.builder()
						.userId(comment.getUserAccount().getUserId())
						.postId(comment.getPost().getId())
						.content(comment.getContent())
						.createdAt(comment.getCreatedAt())
						.modifiedAt(comment.getModifiedAt())
						.build()).toList();
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
	 * 게시글 id 로 게시글을 찾아오는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 해당하는 게시글이 있다면 게시글을, 없다면 null 리턴
	 */
	private Post findPost(Long postId) {
		Optional<Post> optionalPost = postRepository.findById(postId);

		// 게시글 정보를 찾을 수 없다면
		if (optionalPost.isEmpty()) {
			log.error("postId = {} 의 게시글 정보를 찾을 수 없습니다.", postId);
			return null;
		}
		return optionalPost.get();
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
			return null;
		}
		return optionalComment.get();
	}

	/**
	 * CommentRequest -> Comment 변환 메서드
	 *
	 * @param commentRequest 댓글 요청
	 * @param user           회원정보
	 * @param post           게시글 정보
	 * @return 댓글 엔티티 반환
	 */
	private Comment commentRequestToComment(CommentRequest commentRequest, UserAccount user, Post post) {
		return Comment.builder()
				.userAccount(user)
				.post(post)
				.content(commentRequest.getContent())
				.build();
	}

	/**
	 * CommentRequest -> Comment 변환 메서드 (수정시 사용)
	 *
	 * @param commentRequest 댓글 요청
	 * @param user           회원정보
	 * @param post           게시글 정보
	 * @param commentId      댓글 id
	 * @return 댓글 엔티티 반환
	 */
	private Comment commentRequestToCommentForUpdate(CommentRequest commentRequest, UserAccount user, Post post,
			Long commentId) {

		return Comment.builder()
				.id(commentId) // 엔티티의 Setter 사용을 막기 위해 값을 덮어씀
				.userAccount(user)
				.post(post)
				.content(commentRequest.getContent())
				.build();
	}
}
