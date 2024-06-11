package pulleydoreurae.careerquestbackend.certification.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.PassRateSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationPassRate;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@DataJpaTest
@DisplayName("자격증 합격률 테스트")
@Transactional
@Import(QueryDSLConfig.class)
class CertificationPassRateRepositoryTest {

	@Autowired CertificationPassRateRepository certificationPassRateRepository;
	@Autowired CertificationRepository certificationRepository;
	@Autowired
	EntityManager em;

	@BeforeEach
	void setUp() {
		Certification certification1 = Certification.builder().certificationCode(10L).certificationName("정보처리기사").qualification("4년제").organizer("한국산업인력공단").registrationLink("https://www.hrdkorea.or.kr/").aiSummary("정보처리기사에 대한 AI 요약입니다.").build();
		Certification certification2 = Certification.builder().certificationCode(11L).certificationName("정보보안기사").qualification("4년제").organizer("한국산업인력공단").registrationLink("https://www.hrdkorea.or.kr/").aiSummary("정보보안기사에 대한 AI 요약입니다.").build();

		certificationRepository.save(certification1);
		certificationRepository.save(certification2);

		CertificationPassRate certificationPassRate1 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate2 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();
		CertificationPassRate certificationPassRate3 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(3L).examType(ExamType.LAST_STAGE).passRate(42.0).build();
		CertificationPassRate certificationPassRate4 = CertificationPassRate.builder().certification(certification1).examYear(2018L).examRound(4L).examType(ExamType.LAST_STAGE).passRate(43.0).build();

		CertificationPassRate certificationPassRate5 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate6 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();
		CertificationPassRate certificationPassRate7 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(3L).examType(ExamType.LAST_STAGE).passRate(42.0).build();
		CertificationPassRate certificationPassRate8 = CertificationPassRate.builder().certification(certification1).examYear(2021L).examRound(4L).examType(ExamType.LAST_STAGE).passRate(43.0).build();

		CertificationPassRate certificationPassRate9 = CertificationPassRate.builder().certification(certification2).examYear(2018L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate10 = CertificationPassRate.builder().certification(certification2).examYear(2018L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();
		CertificationPassRate certificationPassRate11 = CertificationPassRate.builder().certification(certification2).examYear(2018L).examRound(3L).examType(ExamType.LAST_STAGE).passRate(42.0).build();
		CertificationPassRate certificationPassRate12 = CertificationPassRate.builder().certification(certification2).examYear(2018L).examRound(4L).examType(ExamType.LAST_STAGE).passRate(43.0).build();

		CertificationPassRate certificationPassRate13 = CertificationPassRate.builder().certification(certification2).examYear(2021L).examRound(1L).examType(ExamType.FIRST_STAGE).passRate(40.0).build();
		CertificationPassRate certificationPassRate14 = CertificationPassRate.builder().certification(certification2).examYear(2021L).examRound(2L).examType(ExamType.FIRST_STAGE).passRate(41.0).build();
		CertificationPassRate certificationPassRate15 = CertificationPassRate.builder().certification(certification2).examYear(2021L).examRound(3L).examType(ExamType.LAST_STAGE).passRate(42.0).build();
		CertificationPassRate certificationPassRate16 = CertificationPassRate.builder().certification(certification2).examYear(2021L).examRound(4L).examType(ExamType.LAST_STAGE).passRate(43.0).build();

		certificationPassRateRepository.save(certificationPassRate1);
		certificationPassRateRepository.save(certificationPassRate2);
		certificationPassRateRepository.save(certificationPassRate3);
		certificationPassRateRepository.save(certificationPassRate4);
		certificationPassRateRepository.save(certificationPassRate5);
		certificationPassRateRepository.save(certificationPassRate6);
		certificationPassRateRepository.save(certificationPassRate7);
		certificationPassRateRepository.save(certificationPassRate8);
		certificationPassRateRepository.save(certificationPassRate9);
		certificationPassRateRepository.save(certificationPassRate10);
		certificationPassRateRepository.save(certificationPassRate11);
		certificationPassRateRepository.save(certificationPassRate12);
		certificationPassRateRepository.save(certificationPassRate13);
		certificationPassRateRepository.save(certificationPassRate14);
		certificationPassRateRepository.save(certificationPassRate15);
		certificationPassRateRepository.save(certificationPassRate16);
	}

	@Test
	@DisplayName("자격증 합격률 자격증 이름으로 가져오기")
	void findByCertificationTest() {
	    // Given

	    // When
		List<CertificationPassRate> result = certificationPassRateRepository.findByCertification("정보처리기사");

		// Then
		assertEquals(8, result.size());
	}

	@Test
	@DisplayName("자격증 합격률 검색조건으로 검색 (년도 1)")
	void findBySearchRequestTest1() {
	    // Given
		PassRateSearchRequest request = PassRateSearchRequest.builder().startYear(2019L).build();

	    // When
		List<CertificationPassRate> result = certificationPassRateRepository.findBySearchRequest(request);

		// Then
		assertEquals(8, result.size());
	}

	@Test
	@DisplayName("자격증 합격률 검색조건으로 검색 (년도 2)")
	void findBySearchRequestTest2() {
		// Given
		PassRateSearchRequest request = PassRateSearchRequest.builder().startYear(2021L).endYear(2022L).build();

		// When
		List<CertificationPassRate> result = certificationPassRateRepository.findBySearchRequest(request);

		// Then
		assertEquals(8, result.size());
	}

	@Test
	@DisplayName("자격증 합격률 검색조건으로 검색 (년도 + 이름)")
	void findBySearchRequestTest3() {
		// Given
		PassRateSearchRequest request = PassRateSearchRequest.builder().startYear(2021L).endYear(2022L).certificationName("정보처리기사").build();

		// When
		List<CertificationPassRate> result = certificationPassRateRepository.findBySearchRequest(request);

		// Then
		assertEquals(4, result.size());
	}

	@Test
	@DisplayName("자격증 합격률 검색조건으로 검색 (년도 + 이름 + 시험종류)")
	void findBySearchRequestTest4() {
		// Given
		PassRateSearchRequest request = PassRateSearchRequest.builder().startYear(2021L).endYear(2022L).certificationName("정보처리기사").examType(ExamType.FIRST_STAGE).build();

		// When
		List<CertificationPassRate> result = certificationPassRateRepository.findBySearchRequest(request);

		// Then
		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("자격증 합격률 검색조건으로 검색 (최소 합격률 이상)")
	void findBySearchRequestTest5() {
		// Given
		PassRateSearchRequest request = PassRateSearchRequest.builder().minPassRate(42.0).build();

		// When
		List<CertificationPassRate> result = certificationPassRateRepository.findBySearchRequest(request);

		// Then
		assertEquals(8, result.size());
	}

	@Test
	@DisplayName("자격증 합격률 검색조건으로 검색 (최소 합격률 ~ 최대 합격률)")
	void findBySearchRequestTest6() {
		// Given
		PassRateSearchRequest request = PassRateSearchRequest.builder().minPassRate(42.0).maxPassRate(42.0).build();

		// When
		List<CertificationPassRate> result = certificationPassRateRepository.findBySearchRequest(request);

		// Then
		assertEquals(4, result.size());
	}

	@Test
	@DisplayName("자격증 합격률 검색조건으로 검색 (최소 합격률 이상 + 시험 구분)")
	void findBySearchRequestTest7() {
		// Given
		PassRateSearchRequest request = PassRateSearchRequest.builder().minPassRate(42.0).examType(ExamType.FIRST_STAGE).build();

		// When
		List<CertificationPassRate> result = certificationPassRateRepository.findBySearchRequest(request);

		// Then
		assertEquals(0, result.size());
	}
}
