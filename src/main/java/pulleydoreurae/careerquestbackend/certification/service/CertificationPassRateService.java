package pulleydoreurae.careerquestbackend.certification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.PassRateSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationPassRateResponse;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationPassRateRepository;

/**
 * 자격증 합격률을 담당하는 Service
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@Service
@RequiredArgsConstructor
public class CertificationPassRateService {

	private final CertificationPassRateRepository certificationPassRateRepository;

	/**
	 * 자격증 이름으로 자격증 합격률을 검색하는 메서드
	 *
	 * @param certificationName 자격증 이름
	 * @return 검색결과
	 */
	public List<CertificationPassRateResponse> findByCertificationName(String certificationName) {

		return certificationPassRateRepository
				.findByCertification(certificationName)
				.stream()
				.map(c -> new CertificationPassRateResponse(certificationName, c.getExamYear(), c.getExamRound(),
						c.getExamType(), c.getPassRate())).toList();
	}

	/**
	 * 검색조건으로 자격증 합격률을 가져오는 메서드
	 *
	 * @param request 검색조건
	 * @return 검색결과
	 */
	public List<CertificationPassRateResponse> findBySearchRequest(PassRateSearchRequest request) {

		return certificationPassRateRepository.findBySearchRequest(request)
				.stream()
				.map(c -> new CertificationPassRateResponse(c.getCertification().getCertificationName(),
						c.getExamYear(), c.getExamRound(),
						c.getExamType(), c.getPassRate())).toList();
	}
}
