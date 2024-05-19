package pulleydoreurae.careerquestbackend.certification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.dto.CertificationDateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationExamDate;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationRegistrationPeriod;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationExamDateRepository;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRegistrationPeriodRepository;

/**
 * 자격증 정보 서비스 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/19
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("날짜로 자격증 정보를 불러오는 서비스 테스트")
class CertificationCalenderServiceTest {
	@InjectMocks
	CertificationCalenderService certificationCalenderService;
	@Mock
	CertificationRegistrationPeriodRepository certificationRegistrationPeriodRepository;
	@Mock
	CertificationExamDateRepository certificationExamDateRepository;

	@Test
	@DisplayName("날짜로 자격증정보 찾아오기")
	void findByDateTest(){
		// Given
		Certification certification1 = Certification.builder().certificationCode(1L).certificationName("정보처리기사").Qualification("고졸").examType(ExamType.FIRST_STAGE).organizer("A").registrationLink("Link").AiSummary("Summary").build();
		Certification certification2 = Certification.builder().certificationCode(1L).certificationName("정보처리기사").Qualification("고졸").examType(ExamType.LAST_STAGE).organizer("A").registrationLink("Link").AiSummary("Summary").build();
		Certification certification3 = Certification.builder().certificationCode(2L).certificationName("정보보안기사").Qualification("고졸").examType(ExamType.FIRST_STAGE).organizer("A").registrationLink("Link").AiSummary("Summary").build();
		Certification certification4 = Certification.builder().certificationCode(2L).certificationName("정보보안기사").Qualification("고졸").examType(ExamType.LAST_STAGE).organizer("A").registrationLink("Link").AiSummary("Summary").build();

		given(certificationRegistrationPeriodRepository.findByDate(LocalDate.of(2000, 2, 3)))
				.willReturn(List.of(
						CertificationRegistrationPeriod.builder().certification(certification1).examRound(1L).startDate(
								LocalDate.of(2000, 1, 1)).endDate(LocalDate.of(2000, 2, 20)).build(),
						CertificationRegistrationPeriod.builder().certification(certification2).examRound(1L).startDate(
								LocalDate.of(2000, 1, 20)).endDate(LocalDate.of(2000, 3, 31)).build(),
						CertificationRegistrationPeriod.builder().certification(certification3).examRound(1L).startDate(
								LocalDate.of(2000, 2, 1)).endDate(LocalDate.of(2000, 2, 13)).build(),
						CertificationRegistrationPeriod.builder().certification(certification4).examRound(1L).startDate(
								LocalDate.of(2000, 1, 30)).endDate(LocalDate.of(2000, 2, 21)).build()
				));

		given(certificationExamDateRepository.findByDate(LocalDate.of(2000, 2, 3)))
				.willReturn(List.of(
						CertificationExamDate.builder().certification(certification1).examRound(1L).examDate(LocalDate.of(2000, 2, 3)).build(),
						CertificationExamDate.builder().certification(certification2).examRound(1L).examDate(LocalDate.of(2000, 2, 3)).build(),
						CertificationExamDate.builder().certification(certification3).examRound(1L).examDate(LocalDate.of(2000, 2, 3)).build(),
						CertificationExamDate.builder().certification(certification4).examRound(1L).examDate(LocalDate.of(2000, 2, 3)).build()
				));

		// When
		CertificationDateResponse result = certificationCalenderService.findByDate(LocalDate.of(2000, 2, 3));

		// Then
		assertEquals(4, result.getPeriodResponse().size());
		assertEquals(4, result.getExamDateResponses().size());
		assertEquals("정보처리기사", result.getPeriodResponse().get(0).getName());
		assertEquals("정보보안기사", result.getPeriodResponse().get(3).getName());
		assertEquals(ExamType.FIRST_STAGE, result.getPeriodResponse().get(0).getExamType());
		assertEquals(ExamType.LAST_STAGE, result.getPeriodResponse().get(1).getExamType());
	}
}
