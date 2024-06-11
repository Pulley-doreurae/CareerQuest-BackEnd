package pulleydoreurae.careerquestbackend.certification.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.PassRateSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationPassRateResponse;
import pulleydoreurae.careerquestbackend.certification.service.CertificationPassRateService;

/**
 * 자격증 합격률 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CertificationPassRateController {

	private final CertificationPassRateService certificationPassRateService;

	@GetMapping("/certifications/pass-rate/{certificationName}")
	public ResponseEntity<List<CertificationPassRateResponse>> findByCertificationName(
			@PathVariable String certificationName) {

		List<CertificationPassRateResponse> responses = certificationPassRateService
				.findByCertificationName(certificationName);

		return ResponseEntity.status(HttpStatus.OK)
				.body(responses);
	}

	@PostMapping("/certifications/pass-rate")
	public ResponseEntity<List<CertificationPassRateResponse>> findByCertificationName(
			@RequestBody PassRateSearchRequest request) {

		List<CertificationPassRateResponse> responses = certificationPassRateService.findBySearchRequest(request);

		return ResponseEntity.status(HttpStatus.OK)
				.body(responses);
	}
}
