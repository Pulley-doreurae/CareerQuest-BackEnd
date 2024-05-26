package pulleydoreurae.careerquestbackend.certification.controller;

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
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewFailResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.ReviewRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.service.ReviewService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 자격증 후기 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping("/certifications/reviews")
	public ResponseEntity<List<ReviewResponse>> getReviewList(@PageableDefault(size = 15) Pageable pageable) {

		List<ReviewResponse> response = reviewService.getPostResponseList(pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}

	@GetMapping("/certifications/reviews/{certificationName}")
	public ResponseEntity<List<ReviewResponse>> getReviewListByCertificationName(
			@PathVariable(name = "certificationName") String certificationName,
			@PageableDefault(size = 15) Pageable pageable) {

		List<ReviewResponse> response = reviewService.getReviewResponseListByCertificationName(
				certificationName, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}

	@PostMapping("/certifications/reviews")
	public ResponseEntity<?> saveReview(@Valid @RequestBody ReviewRequest reviewRequest, BindingResult bindingResult) {

		// 검증
		ResponseEntity<ReviewFailResponse> BAD_REQUEST = validCheck(reviewRequest, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		reviewService.saveReview(reviewRequest);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("자격증 후기를 저장하였습니다.")
						.build());
	}

	@PatchMapping("/certifications/reviews/{reviewId}")
	public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @Valid @RequestBody ReviewRequest reviewRequest,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<ReviewFailResponse> BAD_REQUEST = validCheck(reviewRequest, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		if (reviewService.updatePost(reviewId, reviewRequest)) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(SimpleResponse.builder()
							.msg("자격증 후기를 수정하였습니다.")
							.build());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(SimpleResponse.builder()
						.msg("자격증 후기를 저장에 실패했습니다.")
						.build());
	}

	@DeleteMapping("/certifications/reviews/{reviewId}")
	public ResponseEntity<SimpleResponse> deleteReview(@PathVariable Long reviewId, String userId) {
		if (reviewService.deleteReview(reviewId, userId)) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(SimpleResponse.builder()
							.msg("자격증 후기를 삭제하였습니다.")
							.build());
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(SimpleResponse.builder()
						.msg("자격증 후기를 삭제에 실패하였습니다.")
						.build());
	}

	@GetMapping("/certifications/reviews/user/{userId}")
	public ResponseEntity<List<ReviewResponse>> getReviewListByUserId(@PathVariable String userId,
			@PageableDefault(size = 15) Pageable pageable) {

		List<ReviewResponse> responses = reviewService.getReviewListByUserAccount(userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(responses);
	}

	/**
	 * 검증 메서드
	 *
	 * @param reviewRequest 후기 요청 (검증에 실패하더라도 입력한 값은 그대로 돌려준다.)
	 * @param bindingResult 검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 ResponseEntity 반환
	 */
	private ResponseEntity<ReviewFailResponse> validCheck(ReviewRequest reviewRequest, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String[] errors = new String[bindingResult.getAllErrors().size()];
			int index = 0;
			for (ObjectError error : bindingResult.getAllErrors()) {
				errors[index++] = error.getDefaultMessage();
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ReviewFailResponse.builder()
							.title(reviewRequest.getTitle())
							.content(reviewRequest.getContent())
							.certificationName(reviewRequest.getCertificationName())
							.errors(errors)
							.build());
		}
		return null;
	}
}
