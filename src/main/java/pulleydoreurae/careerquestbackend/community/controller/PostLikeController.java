package pulleydoreurae.careerquestbackend.community.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	/**
	 * 좋아요 상태를 변경하는 메서드
	 *
	 * @param postLikeRequest 좋아요 요청
	 * @param bindingResult   에러검증
	 * @return 처리결과
	 */
	@PostMapping("/posts/likes")
	public ResponseEntity<SimpleResponse> changeLikeStatus(@Valid @RequestBody PostLikeRequest postLikeRequest,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		postLikeService.changePostLike(postLikeRequest);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("좋아요 상태 변경에 성공했습니다.")
						.build());
	}

	/**
	 * 한 사용자가 좋아요 누른 게시글 리스트 반환 메서드
	 *
	 * @param userId   사용자 id
	 * @param pageable 페이지
	 * @return 조회한 리스트
	 */
	@GetMapping("/posts/likes/{userId}")
	public ResponseEntity<List<PostResponse>> findAllPostLikeByUserAccount(@PathVariable String userId,
			@PageableDefault(size = 15) Pageable pageable) {

		List<PostResponse> result = postLikeService.findAllPostLikeByUserAccount(userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(result);
	}

	/**
	 * 검증 메서드
	 *
	 * @param bindingResult 검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 ResponseEntity 반환
	 */
	private ResponseEntity<SimpleResponse> validCheck(BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			bindingResult.getAllErrors().forEach(error -> {
				sb.append(error.getDefaultMessage()).append("\n");
			});

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg(sb.toString())
							.build());
		}
		return null;
	}
}
