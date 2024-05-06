package pulleydoreurae.careerquestbackend.basiccommunity.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicComment;
import pulleydoreurae.careerquestbackend.basiccommunity.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;

/**
 * 댓글 Service
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@Slf4j
@Service
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommonCommunityService commonCommunityService;

	public CommentService(CommentRepository commentRepository, CommonCommunityService commonCommunityService) {
		this.commentRepository = commentRepository;
		this.commonCommunityService = commonCommunityService;
	}

	/**
	 * 댓글 저장 메서드
	 *
	 * @param commentRequest 댓글 요청
	 */
	public void saveComment(CommentRequest commentRequest) {
		UserAccount user = commonCommunityService.findUserAccount(commentRequest.getUserId());
		Post post = commonCommunityService.findPost(commentRequest.getPostId());
		Comment comment = commentRequestToComment(commentRequest, user, post);
		commentRepository.save(comment);
	}

	/**
	 * 댓글 수정 메서드
	 *
	 * @param commentRequest 댓글 요청
	 * @param commentId      댓글 id
	 * @return 수정에 성공하면 true, 실패하면 false
	 */
	public boolean updateComment(CommentRequest commentRequest, Long commentId) {
		UserAccount user = commonCommunityService.findUserAccount(commentRequest.getUserId());
		Post post = commonCommunityService.findPost(commentRequest.getPostId());
		Comment comment = commonCommunityService.findComment(commentId);

		// 게시글과 댓글이 연결되어 있지 않거나, 작성자와 수정자가 다르다면 실패
		if (!comment.getPost().getId().equals(commentRequest.getPostId()) ||
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
		UserAccount user = commonCommunityService.findUserAccount(userId);
		Post post = commonCommunityService.findPost(postId);
		Comment comment = commonCommunityService.findComment(commentId);

		// 게시글과 댓글이 연결되어 있지 않거나, 작성자와 요청자가 다르다면 실패
		if (!comment.getPost().getId().equals(post.getId()) ||
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
		Post post = commonCommunityService.findPost(postId);
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
		UserAccount user = commonCommunityService.findUserAccount(userId);
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
	 * CommentRequest -> Comment 변환 메서드
	 *
	 * @param commentRequest 댓글 요청
	 * @param user           회원정보
	 * @param post           게시글 정보
	 * @return 댓글 엔티티 반환
	 */
	private Comment commentRequestToComment(CommentRequest commentRequest, UserAccount user, Post post) {
		return BasicComment.builder()
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

		return BasicComment.builder()
				.id(commentId) // 엔티티의 Setter 사용을 막기 위해 값을 덮어씀
				.userAccount(user)
				.post(post)
				.content(commentRequest.getContent())
				.build();
	}
}
