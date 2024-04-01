package pulleydoreurae.careerquestbackend.community.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.community.service.CommentService;

/**
 * 댓글 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@RestController
@RequestMapping("/api")
public class CommentController {

	private final CommentService commentService;

	@Autowired
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}

	@GetMapping("/comments/{userId}")
	public ResponseEntity<List<CommentResponse>> findAllByUserId(@PathVariable String userId) {
		List<CommentResponse> comments = commentService.findListByUserAccount(userId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(comments);
	}

	@GetMapping("/posts/{postId}/comments")
	public ResponseEntity<List<CommentResponse>> findAllByPostId(@PathVariable Long postId) {
		List<CommentResponse> comments = commentService.findListByPostId(postId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(comments);
	}

	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<SimpleResponse> saveComment(@PathVariable Long postId,
			@RequestBody CommentRequest commentRequest) {

		commentRequest.setPostId(postId);

		if (!commentService.saveComment(commentRequest)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg("댓글 등록에 실패했습니다.")
							.build());
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("댓글 등록에 성공했습니다.")
						.build());
	}

	@PatchMapping("/posts/{postId}/comments/{commentId}")
	public ResponseEntity<SimpleResponse> updateComment(@PathVariable Long postId,
			@PathVariable Long commentId,
			@RequestBody CommentRequest commentRequest) {

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
}
