package pulleydoreurae.careerquestbackend.certification.controller;

import java.util.ArrayList;
import java.util.List;

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
import pulleydoreurae.careerquestbackend.certification.domain.dto.CertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.InterestedCertificationRequest;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.service.InterestedCertificationService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 관심자격증 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class InterestedCertificationController {

	private final InterestedCertificationService interestedCertificationService;

	@GetMapping("/certification/interest/{userId}")
	public ResponseEntity<List<CertificationResponse>> findByUserId(@PathVariable String userId) {

		List<Certification> result = interestedCertificationService.findAllByUserId(userId);
		List<CertificationResponse> response = new ArrayList<>();

		result.forEach(certification -> {
			response.add(new CertificationResponse(certification.getCertificationCode(),
					certification.getCertificationName(), certification.getQualification(),
					certification.getOrganizer(), certification.getRegistrationLink(), certification.getAiSummary()));
		});

		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}

	@PostMapping("/certification/interest")
	public ResponseEntity<SimpleResponse> changeInterestCertification(
			@Valid @RequestBody InterestedCertificationRequest request, BindingResult bindingResult) {

		ResponseEntity<SimpleResponse> BAD_REQUEST = checkValid(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		if (request.getIsInterested()) {
			interestedCertificationService.deleteInterestedCertification(request.getUserId(),
					request.getCertificationName());
		} else {
			interestedCertificationService.saveInterestedCertification(request.getUserId(),
					request.getCertificationName());
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder().msg("관심자격증 상태변화에 성공했습니다.").build());
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
