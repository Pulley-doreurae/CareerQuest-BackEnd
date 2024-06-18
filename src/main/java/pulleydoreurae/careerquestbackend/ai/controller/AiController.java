package pulleydoreurae.careerquestbackend.ai.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.ai.dto.AiRequest;
import pulleydoreurae.careerquestbackend.ai.dto.AiResponse;
import pulleydoreurae.careerquestbackend.ai.service.AiService;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserIdRequest;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * AI를 담당하는 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/06/02
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class AiController {

	private final AiService aiService;

	@PostMapping("/ai")
	public ResponseEntity<?> findResult(@Valid @RequestBody AiRequest request, BindingResult bindingResult) {

		if (bindingResult != null) {
			// 검증
			ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
			if (BAD_REQUEST != null) {
				return BAD_REQUEST;
			}
		}

		AiResponse result = aiService.findResult(aiService.returnCmd(request));

		return ResponseEntity.status(HttpStatus.OK)
				.body(result);
	}

	@PostMapping("/ai/aboutMe")
	public ResponseEntity<?> createSelfIntroduction(@Valid @RequestBody UserIdRequest request, BindingResult bindingResult) {

		if (bindingResult != null) {
			// 검증
			ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
			if (BAD_REQUEST != null) {
				return BAD_REQUEST;
			}
		}

		AiResponse result = aiService.findResult(aiService.returnCmd(request.getUserId()));

		return ResponseEntity.status(HttpStatus.OK)
			.body(result);
	}

	/**
	 * 검증 메서드
	 *
	 * @param bindingResult 검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 SimpleResponse 반환
	 */
	private ResponseEntity<SimpleResponse> validCheck(BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			bindingResult.getAllErrors().forEach(objectError -> {
				String message = objectError.getDefaultMessage();

				sb.append(message).append("\n");
			});

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg(sb.toString())
							.build());
		}
		return null;
	}
}
