package pulleydoreurae.careerquestbackend.basiccommunity.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pulleydoreurae.careerquestbackend.common.community.controller.CommentController;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.common.community.service.CommentService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 댓글 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@RestController
@RequestMapping("/api")
public class BasicCommentController extends CommentController {

	public BasicCommentController(CommentService commentService) {
		super(commentService);
	}

	@GetMapping("/comments/{userId}")
	@Override
	public ResponseEntity<List<CommentResponse>> findAllByUserId(@PathVariable String userId,
			@PageableDefault(size = 30) Pageable pageable) {

		return super.findAllByUserId(userId, pageable);
	}

	@GetMapping("/posts/{postId}/comments")
	@Override
	public ResponseEntity<List<CommentResponse>> findAllByPostId(@PathVariable Long postId,
			@PageableDefault(size = 30) Pageable pageable) {

		return super.findAllByPostId(postId, pageable);
	}

	@PostMapping("/posts/{postId}/comments")
	@Override
	public ResponseEntity<?> saveComment(@PathVariable Long postId,
			@Valid @RequestBody CommentRequest commentRequest, BindingResult bindingResult) {

		return super.saveComment(postId, commentRequest, bindingResult);
	}

	@PatchMapping("/posts/{postId}/comments/{commentId}")
	@Override
	public ResponseEntity<?> updateComment(@PathVariable Long postId,
			@PathVariable Long commentId,
			@Valid @RequestBody CommentRequest commentRequest, BindingResult bindingResult) {

		return super.updateComment(postId, commentId, commentRequest, bindingResult);
	}

	@DeleteMapping("/posts/{postId}/comments/{commentId}")
	@Override
	public ResponseEntity<SimpleResponse> deleteComment(@PathVariable Long postId,
			@PathVariable Long commentId, String userId) {

		return super.deleteComment(postId, commentId, userId);
	}
}
