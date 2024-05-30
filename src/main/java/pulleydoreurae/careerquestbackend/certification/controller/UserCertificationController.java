package pulleydoreurae.careerquestbackend.certification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.UserCertificationRequest;
import pulleydoreurae.careerquestbackend.certification.service.UserCertificationService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 취득 자격증을 담당하는 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class UserCertificationController {

	private final UserCertificationService userCertificationService;

	@PostMapping("/certifications/user-certification")
	public ResponseEntity<SimpleResponse> saveUserCertification(@Valid @RequestBody UserCertificationRequest request,
			BindingResult bindingResult) {

		ResponseEntity<SimpleResponse> BAD_REQUEST = checkValid(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		userCertificationService.saveUserCertification(request);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("취득 자격증 저장에 성공했습니다.")
						.build());
	}

	@DeleteMapping("/certifications/user-certification")
	public ResponseEntity<SimpleResponse> deleteUserCertification(@Valid @RequestBody UserCertificationRequest request,
			BindingResult bindingResult) {

		ResponseEntity<SimpleResponse> BAD_REQUEST = checkValid(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		userCertificationService.deleteUserCertification(request);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("취득 자격증 제거에 성공했습니다.")
						.build());
	}

	private ResponseEntity<SimpleResponse> checkValid(BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			bindingResult.getAllErrors().forEach(objectError -> {
				String message = objectError.getDefaultMessage();
				sb.append(message).append("\\n");
			});

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					SimpleResponse.builder().msg(sb.toString()).build()
			);
		}
		return null;
	}
}
