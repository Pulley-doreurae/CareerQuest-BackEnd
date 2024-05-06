package pulleydoreurae.careerquestbackend.common.community.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.CommentFailResponse;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.common.community.service.CommentService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 댓글 컨트롤러 추상화 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/05/06
 */
public abstract class CommentController {

	private final CommentService commentService;

	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	/**
	 * 작성자별 댓글 조회
	 *
	 * @param userId 작성자
	 * @param pageable 페이지 정보
	 * @return 댓글 리스트
	 */
	public ResponseEntity<List<CommentResponse>> findAllByUserId(String userId, Pageable pageable) {
		List<CommentResponse> comments = commentService.findListByUserAccount(userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(comments);
	}

	/**
	 * 게시글에 달린 댓글 조회
	 *
	 * @param postId 게시글 정보
	 * @param pageable 페이지 정보
	 * @return 댓글 리스트
	 */
	public ResponseEntity<List<CommentResponse>> findAllByPostId(Long postId, Pageable pageable) {
		List<CommentResponse> comments = commentService.findListByPostId(postId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(comments);
	}

	/**
	 * 댓글 저장
	 *
	 * @param postId 게시글 정보
	 * @param commentRequest 댓글 요청
	 * @param bindingResult 에러 검증
	 * @return 처리 결과
	 */
	public ResponseEntity<?> saveComment(Long postId, CommentRequest commentRequest, BindingResult bindingResult) {

		// 검증
		ResponseEntity<CommentFailResponse> BAD_REQUEST = validCheck(commentRequest, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		commentRequest.setPostId(postId);
		commentService.saveComment(commentRequest);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("댓글 등록에 성공했습니다.")
						.build());
	}

	/**
	 * 댓글 수정
	 *
	 * @param postId 게시글 정보
	 * @param commentId 댓글 정보
	 * @param commentRequest 댓글 수정 요청
	 * @param bindingResult 에러 검증
	 * @return 처리 결과
	 */
	public ResponseEntity<?> updateComment(Long postId, Long commentId, CommentRequest commentRequest,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<CommentFailResponse> BAD_REQUEST = validCheck(commentRequest, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		commentRequest.setPostId(postId);

		if (!commentService.updateComment(commentRequest, commentId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg("댓글 수정에 실패했습니다.")
							.build());
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("댓글 수정에 성공했습니다.")
						.build());
	}

	/**
	 * 댓글 삭제
	 *
	 * @param postId 게시글 정보
	 * @param commentId 댓글 정보
	 * @param userId 요청자 정보
	 * @return 처리 결과
	 */
	public ResponseEntity<SimpleResponse> deleteComment(Long postId, Long commentId, String userId) {
		if (!commentService.deleteComment(commentId, postId, userId)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg("댓글 삭제에 실패했습니다.")
							.build());
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("댓글 삭제에 성공했습니다.")
						.build());
	}

	/**
	 * 검증 메서드
	 *
	 * @param commentRequest 댓글 요청 (검증에 실패하더라도 입력한 값은 그대로 돌려준다.)
	 * @param bindingResult  검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 ResponseEntity 반환
	 */
	private ResponseEntity<CommentFailResponse> validCheck(CommentRequest commentRequest, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String[] errors = new String[bindingResult.getAllErrors().size()];
			int index = 0;
			for (ObjectError error : bindingResult.getAllErrors()) {
				errors[index++] = error.getDefaultMessage();
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(CommentFailResponse.builder()
							.content(commentRequest.getContent())
							.errors(errors)
							.build());
		}
		return null;
	}
}
