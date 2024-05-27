package pulleydoreurae.careerquestbackend.certification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationExamDate;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationRegistrationPeriod;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationExamDateRepository;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRegistrationPeriodRepository;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/05/27
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증 기본 서비스 테스트")
class CertificationServiceTest {

	@InjectMocks CertificationService certificationService;
	@Mock CertificationRepository certificationRepository;
	@Mock CertificationExamDateRepository certificationExamDateRepository;
	@Mock CertificationRegistrationPeriodRepository certificationRegistrationPeriodRepository;

	@Test
	@DisplayName("자격증 이름으로 자격증정보 가져오기 -실패")
	void findByNameTest1() {
	    // Given
		given(certificationRepository.findByCertificationName("정보처리기사")).willReturn(Optional.empty());

	    // When

		// Then
		assertThrows(IllegalArgumentException.class, () -> certificationService.findByName("정보처리기사"));
	}

	@Test
	@DisplayName("자격증 이름으로 자격증정보 가져오기 -성공")
	void findByNameTest2() {
	    // Given
		Certification certification = Certification.builder().certificationCode(10L).certificationName("정보처리기사").qualification("4년제").organizer("한국산업인력공단").registrationLink("https://www.hrdkorea.or.kr/").aiSummary("정보처리기사에 대한 AI 요약입니다.").build();
		List<CertificationRegistrationPeriod> periodResponses = new ArrayList<>();
		periodResponses.add(new CertificationRegistrationPeriod(100L, certification, ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 7, 10), LocalDate.of(2024, 8, 10)));
		periodResponses.add(new CertificationRegistrationPeriod(101L, certification, ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 9, 10), LocalDate.of(2024, 10, 10)));
		periodResponses.add(new CertificationRegistrationPeriod(102L, certification, ExamType.FIRST_STAGE, 1002L, LocalDate.of(2024, 9, 10), LocalDate.of(2024, 10, 10)));
		periodResponses.add(new CertificationRegistrationPeriod(103L, certification, ExamType.LAST_STAGE, 1002L, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 11, 10)));
		List<CertificationExamDate> examDateResponses = new ArrayList<>();
		examDateResponses.add(new CertificationExamDate(100L, certification, ExamType.FIRST_STAGE, 1000L, LocalDate.of(2024, 10, 1)));
		examDateResponses.add(new CertificationExamDate(101L, certification, ExamType.FIRST_STAGE, 1000L, LocalDate.of(2024, 10, 2)));
		examDateResponses.add(new CertificationExamDate(102L, certification, ExamType.FIRST_STAGE, 1000L, LocalDate.of(2024, 10, 3)));
		examDateResponses.add(new CertificationExamDate(103L, certification, ExamType.LAST_STAGE, 1000L, LocalDate.of(2024, 11, 1)));
		examDateResponses.add(new CertificationExamDate(104L, certification, ExamType.LAST_STAGE, 1000L, LocalDate.of(2024, 11, 2)));
		examDateResponses.add(new CertificationExamDate(105L, certification, ExamType.LAST_STAGE, 1000L, LocalDate.of(2024, 11, 3)));
		examDateResponses.add(new CertificationExamDate(100L, certification, ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 11, 1)));
		examDateResponses.add(new CertificationExamDate(101L, certification, ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 11, 2)));
		examDateResponses.add(new CertificationExamDate(102L, certification, ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 11, 3)));
		examDateResponses.add(new CertificationExamDate(103L, certification, ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 12, 1)));
		examDateResponses.add(new CertificationExamDate(104L, certification, ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 12, 2)));
		examDateResponses.add(new CertificationExamDate(105L, certification, ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 12, 3)));
		given(certificationRepository.findByCertificationName("정보처리기사")).willReturn(Optional.ofNullable(certification));
		given(certificationExamDateRepository.findAllByName("정보처리기사")).willReturn(examDateResponses);
		given(certificationRegistrationPeriodRepository.findAllByName("정보처리기사")).willReturn(periodResponses);

	    // When
		CertificationResponse result = certificationService.findByName("정보처리기사");

		// Then
		assertDoesNotThrow(() -> certificationService.findByName("정보처리기사"));
		assertEquals(certification.getCertificationName(), result.getCertificationName());
		assertEquals(certification.getCertificationCode(), result.getCertificationCode());
		assertEquals(certification.getQualification(), result.getQualification());
		assertEquals(certification.getOrganizer(), result.getOrganizer());
		assertEquals(certification.getRegistrationLink(), result.getRegistrationLink());
		assertEquals(certification.getAiSummary(), result.getAiSummary());
		assertEquals(4, result.getPeriodResponse().size());
		assertEquals(12, result.getExamDateResponses().size());
	}
}
