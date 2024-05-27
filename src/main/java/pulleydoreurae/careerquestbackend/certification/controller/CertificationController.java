package pulleydoreurae.careerquestbackend.certification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationResponse;
import pulleydoreurae.careerquestbackend.certification.service.CertificationService;

/**
 * 자격증 기본 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/05/27
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class CertificationController {

	private final CertificationService certificationService;

	@GetMapping("/certification/{certificationName}")
	public ResponseEntity<CertificationResponse> findByName(@PathVariable String certificationName) {
		CertificationResponse response = certificationService.findByName(certificationName);

		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}
}
