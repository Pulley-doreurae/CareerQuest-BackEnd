package pulleydoreurae.careerquestbackend.certification.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.certification.domain.dto.CertificationDateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.CertificationExamDateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.CertificationPeriodResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationExamDate;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationRegistrationPeriod;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationExamDateRepository;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRegistrationPeriodRepository;

/**
 * 자격증의 정보를 담당하는 서비스
 *
 * @author : parkjihyeok
 * @since : 2024/05/19
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CertificationCalenderService {

	private final CertificationRegistrationPeriodRepository certificationRegistrationPeriodRepository;
	private final CertificationExamDateRepository certificationExamDateRepository;

	/**
	 * 날짜로 자격증 일정 정보를 받아오는 메서드
	 *
	 * @param date 요청 날짜
	 * @return 요청에 맞는 응답
	 */
	public CertificationDateResponse findByDate(LocalDate date) {
		List<CertificationExamDate> examDates = certificationExamDateRepository.findByDate(date);
		List<CertificationRegistrationPeriod> periods = certificationRegistrationPeriodRepository.findByDate(date);

		CertificationDateResponse response = new CertificationDateResponse();

		addCertificationExamDate(examDates, response);
		addCertificationPeriod(periods, response);

		return response;
	}

	/**
	 * 자격증 접수기간을 response에 담는 메서드
	 *
	 * @param periods 요청한 날짜에 맞는 접수일정들
	 * @param response 접수기간을 저장한 response
	 */
	private void addCertificationPeriod(List<CertificationRegistrationPeriod> periods,
			CertificationDateResponse response) {
		periods.forEach((period) -> {
			Certification certification = period.getCertification();
			CertificationPeriodResponse periodResponse = new CertificationPeriodResponse(
					certification.getCertificationName(), certification.getExamType(), period.getExamRound(),
					period.getStartDate(), period.getEndDate());

			response.getPeriodResponse().add(periodResponse);
		});
	}

	/**
	 * 자격증 시험일정을 response에 담는 메서드
	 *
	 * @param examDates 요청한 날짜에 맞는 시험일정들
	 * @param response 시험일정을 저장한 response
	 */
	private void addCertificationExamDate(List<CertificationExamDate> examDates, CertificationDateResponse response) {
		examDates.forEach((examDate) -> {
			Certification certification = examDate.getCertification();
			CertificationExamDateResponse examResponse = new CertificationExamDateResponse(
					certification.getCertificationName(), certification.getExamType(), examDate.getExamRound(),
					examDate.getExamDate());

			response.getExamDateResponses().add(examResponse);
		});
	}
}
