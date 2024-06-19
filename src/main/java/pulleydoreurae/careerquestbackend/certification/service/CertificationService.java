package pulleydoreurae.careerquestbackend.certification.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.ai.dto.AiRequest;
import pulleydoreurae.careerquestbackend.ai.service.AiService;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationExamDateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationPeriodResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationExamDate;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationRegistrationPeriod;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationExamDateRepository;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRegistrationPeriodRepository;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;

/**
 * 자격증 기본 Service
 *
 * @author : parkjihyeok
 * @since : 2024/05/27
 */
@Service
@RequiredArgsConstructor
public class CertificationService {

	private final CertificationRepository certificationRepository;
	private final CertificationRegistrationPeriodRepository certificationRegistrationPeriodRepository;
	private final CertificationExamDateRepository certificationExamDateRepository;
	private final AiService aiService;

	/**
	 * 자격증 이름으로 자격증정보를 가져오는 메서드
	 *
	 * @param certificationName 자격증 이름
	 * @return 자격증 정보
	 */
	public CertificationResponse findByName(String certificationName) {
		Optional<Certification> byCertificationName = certificationRepository.findByCertificationName(
				certificationName);

		if (byCertificationName.isEmpty()) {
			throw new IllegalArgumentException("자격증 정보를 찾을 수 없습니다.");
		}

		Certification certification = byCertificationName.get();

		if (certification.getAiSummary() == null) { // 자격증 AI 요약이 비어있다면 AI요약 호출
			AiRequest request = new AiRequest(certificationName, "cert_summary");
			aiService.findResult(request);
		}

		CertificationResponse response = CertificationResponse.builder()
				.certificationCode(certification.getCertificationCode())
				.certificationName(certification.getCertificationName())
				.qualification(certification.getQualification())
				.organizer(certification.getOrganizer())
				.registrationLink(certification.getRegistrationLink())
				.aiSummary(certification.getAiSummary())
				.build();

		addDateInfo(certificationName, response);

		return response;
	}

	/**
	 * 응답에 시험일정과 접수기간을 담는 메서드
	 *
	 * @param certificationName 자격증이름
	 * @param response          자격증정보를 담은 응답
	 */
	private void addDateInfo(String certificationName, CertificationResponse response) {
		List<CertificationRegistrationPeriod> periods = certificationRegistrationPeriodRepository.findAllByName(
				certificationName);
		List<CertificationExamDate> examDates = certificationExamDateRepository.findAllByName(certificationName);

		List<CertificationPeriodResponse> periodResponse = response.getPeriodResponse();
		List<CertificationExamDateResponse> examDateResponses = response.getExamDateResponses();

		periods.forEach(period -> {
			periodResponse.add(new CertificationPeriodResponse(
					certificationName,
					period.getExamType(),
					period.getExamRound(),
					period.getStartDate(),
					period.getEndDate()
			));
		});

		examDates.forEach(examDate -> {
			examDateResponses.add(new CertificationExamDateResponse(
					certificationName,
					examDate.getExamType(),
					examDate.getExamRound(),
					examDate.getExamDate()
			));
		});
	}
}
