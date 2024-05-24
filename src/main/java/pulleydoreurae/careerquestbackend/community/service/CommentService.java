package pulleydoreurae.careerquestbackend.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.common.community.service.CommentService;
import pulleydoreurae.careerquestbackend.common.community.service.CommonCommunityService;

/**
 * 댓글 Service
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@Service
public class BasicCommentService extends CommentService {

	@Autowired
	public BasicCommentService(CommentRepository commentRepository,
			@Qualifier("commonBasicCommunityService") CommonCommunityService commonCommunityService) {
		super(commentRepository, commonCommunityService);
	}

	/**
	 * CommentRequest -> Comment 변환 메서드
	 *
	 * @param commentRequest 댓글 요청
	 * @param user           회원정보
	 * @param post           게시글 정보
	 * @return 댓글 엔티티 반환
	 */
	@Override
	public pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment commentRequestToComment(CommentRequest commentRequest, UserAccount user, Post post) {
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
	@Override
	public pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment commentRequestToCommentForUpdate(CommentRequest commentRequest, UserAccount user, Post post,
			Long commentId) {

		return Comment.builder()
				.id(commentId) // 엔티티의 Setter 사용을 막기 위해 값을 덮어씀
				.userAccount(user)
				.post(post)
				.content(commentRequest.getContent())
				.build();
	}
}
