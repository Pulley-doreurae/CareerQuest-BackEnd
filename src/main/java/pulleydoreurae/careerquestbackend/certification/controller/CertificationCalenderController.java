package pulleydoreurae.careerquestbackend.certification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.CertificationSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationDateResponse;
import pulleydoreurae.careerquestbackend.certification.service.CertificationCalenderService;

/**
 * 날짜로 자격증을 검색했을때 담당하는 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CertificationCalenderController {

	private final CertificationCalenderService certificationCalenderService;

	@PostMapping("/certifications/dates")
	public ResponseEntity<CertificationDateResponse> findByDate(@RequestBody CertificationSearchRequest request) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(certificationCalenderService.findByDate(request.getDate()));
	}
}
