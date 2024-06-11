package pulleydoreurae.careerquestbackend.certification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.PassRateSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationPassRateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationPassRate;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationPassRateRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증 합격률 Service")
class CertificationPassRateServiceTest {

	@InjectMocks CertificationPassRateService service;
	@Mock CertificationPassRateRepository repository;

	@Test
	@DisplayName("자격증 이름으로 합격률 검색")
	void findByCertificationNameTest() {
	    // Given
		Certification certification1 = Certification.builder().certificationCode(10L).certificationName("정보처리기사").qualification("4년제").organizer("한국산업인력공단").registrationLink("https://www.hrdkorea.or.kr/").aiSummary("정보처리기사에 대한 AI 요약입니다.").build();

		CertificationPassRate certificationPassRate1 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate2 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();
		CertificationPassRate certificationPassRate3 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(3L).examType(ExamType.LAST_STAGE).passRate(42.0).build();
		CertificationPassRate certificationPassRate4 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(4L).examType(ExamType.LAST_STAGE).passRate(43.0).build();

		CertificationPassRate certificationPassRate5 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate6 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();
		CertificationPassRate certificationPassRate7 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(3L).examType(ExamType.LAST_STAGE).passRate(42.0).build();
		CertificationPassRate certificationPassRate8 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(4L).examType(ExamType.LAST_STAGE).passRate(43.0).build();

		given(repository.findByCertification("정보처리기사")).willReturn(List.of(certificationPassRate1, certificationPassRate2, certificationPassRate3, certificationPassRate4, certificationPassRate5, certificationPassRate6, certificationPassRate7, certificationPassRate8));

	    // When
		List<CertificationPassRateResponse> result = service.findByCertificationName("정보처리기사");

		// Then
		assertEquals(8, result.size());
		assertEquals("정보처리기사", result.get(0).getCertificationName());
	}

	@Test
	@DisplayName("검색조건으로 합격률 검색")
	void findBySearchRequest() {
	    // Given
		Certification certification1 = Certification.builder().certificationCode(10L).certificationName("정보처리기사").qualification("4년제").organizer("한국산업인력공단").registrationLink("https://www.hrdkorea.or.kr/").aiSummary("정보처리기사에 대한 AI 요약입니다.").build();

		CertificationPassRate certificationPassRate1 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate2 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();
		CertificationPassRate certificationPassRate3 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate4 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();

		PassRateSearchRequest request = PassRateSearchRequest.builder().certificationName("정보처리기사").examType(ExamType.FIRST_STAGE).build();
		given(repository.findBySearchRequest(request)).willReturn(List.of(certificationPassRate1, certificationPassRate2, certificationPassRate3, certificationPassRate4));

	    // When
		List<CertificationPassRateResponse> result = service.findBySearchRequest(request);

		// Then
		assertEquals(4, result.size());
		assertEquals(ExamType.FIRST_STAGE, result.get(0).getExamType());
	}
}
