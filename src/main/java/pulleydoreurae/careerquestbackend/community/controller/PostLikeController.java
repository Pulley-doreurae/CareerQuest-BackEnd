package pulleydoreurae.careerquestbackend.community.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostLikeRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.service.PostLikeService;

/**
 * 좋아요 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/04/03
 */
@RestController
@RequestMapping("/api")
public class PostLikeController {

	private final PostLikeService postLikeService;

	public PostLikeController(PostLikeService postLikeService) {
		this.postLikeService = postLikeService;
	}

	@PostMapping("/posts/likes")
	public ResponseEntity<SimpleResponse> changeLikeStatus(@RequestBody PostLikeRequest postLikeRequest) {
		boolean result = postLikeService.changePostLike(postLikeRequest);

		if (!result) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg("좋아요 상태 변경에 실패했습니다.")
							.build());
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("좋아요 상태 변경에 성공했습니다.")
						.build());
	}

	@GetMapping("/posts/likes/{userId}")
	public ResponseEntity<List<PostResponse>> findAllPostLikeByUserAccount(@PathVariable String userId) {
		List<PostResponse> result = postLikeService.findAllPostLikeByUserAccount(userId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(result);
	}
}
