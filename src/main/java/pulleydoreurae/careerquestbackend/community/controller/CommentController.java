package pulleydoreurae.careerquestbackend.community.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.CommentFailResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.community.service.CommentService;

/**
 * 댓글 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	/**
	 * 작성자별 댓글 조회
	 *
	 * @param userId   작성자
	 * @param pageable 페이지 정보
	 * @return 댓글 리스트
	 */
	@GetMapping("/comments/{userId}")
	public ResponseEntity<List<CommentResponse>> findAllByUserId(@PathVariable String userId,
			@PageableDefault(size = 30) Pageable pageable) {

		List<CommentResponse> comments = commentService.findListByUserAccount(userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(comments);
	}

	/**
	 * 게시글에 달린 댓글 조회
	 *
	 * @param postId   게시글 정보
	 * @param pageable 페이지 정보
	 * @return 댓글 리스트
	 */
	@GetMapping("/posts/{postId}/comments")
	public ResponseEntity<List<CommentResponse>> findAllByPostId(@PathVariable Long postId,
			@PageableDefault(size = 30) Pageable pageable) {

		List<CommentResponse> comments = commentService.findListByPostId(postId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(comments);
	}

	/**
	 * 댓글 저장
	 *
	 * @param postId         게시글 정보
	 * @param commentRequest 댓글 요청
	 * @param bindingResult  에러 검증
	 * @return 처리 결과
	 */
	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<?> saveComment(@PathVariable Long postId,
			@Valid @RequestBody CommentRequest commentRequest, BindingResult bindingResult) {

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
	 * @param postId         게시글 정보
	 * @param commentId      댓글 정보
	 * @param commentRequest 댓글 수정 요청
	 * @param bindingResult  에러 검증
	 * @return 처리 결과
	 */
	@PatchMapping("/posts/{postId}/comments/{commentId}")
	public ResponseEntity<?> updateComment(@PathVariable Long postId,
			@PathVariable Long commentId,
			@Valid @RequestBody CommentRequest commentRequest, BindingResult bindingResult) {

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
	 * @param postId    게시글 정보
	 * @param commentId 댓글 정보
	 * @param userId    요청자 정보
	 * @return 처리 결과
	 */
	@DeleteMapping("/posts/{postId}/comments/{commentId}")
	public ResponseEntity<SimpleResponse> deleteComment(@PathVariable Long postId,
			@PathVariable Long commentId, String userId) {

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
