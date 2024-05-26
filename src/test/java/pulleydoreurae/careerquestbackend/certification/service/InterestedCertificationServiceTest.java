package pulleydoreurae.careerquestbackend.certification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.InterestedCertification;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;
import pulleydoreurae.careerquestbackend.certification.repository.InterestedCertificationRepository;

/**
 * 관심자격증 서비스 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("관심자격증 서비스 테스트")
class InterestedCertificationServiceTest {

	@InjectMocks
	InterestedCertificationService interestedCertificationService;
	@Mock
	InterestedCertificationRepository interestedCertificationRepository;
	@Mock
	UserAccountRepository userAccountRepository;
	@Mock
	CertificationRepository certificationRepository;

	@Test
	@DisplayName("사용자가 관심자격증에 등록한 자격증 반환 테스트")
	void findAllByUserIdTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		Certification certification = Certification.builder().certificationCode(1L).certificationName("정보처리기사").qualification("고졸").organizer("A").registrationLink("Link").aiSummary("Summary").build();
		given(interestedCertificationRepository.findAllByUserId("testId")).willReturn(List.of(InterestedCertification.builder().certification(certification).userAccount(userAccount).build()));

	    // When
		List<Certification> result = interestedCertificationService.findAllByUserId("testId");

		// Then
		assertEquals(1, result.size());
		assertEquals("정보처리기사", result.get(0).getCertificationName());
	}

	@Test
	@DisplayName("관심 자격증 추가 테스트")
	void saveInterestedCertificationTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		Certification certification = Certification.builder().certificationCode(1L).certificationName("정보처리기사").qualification("고졸").organizer("A").registrationLink("Link").aiSummary("Summary").build();
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.of(userAccount));
		given(certificationRepository.findByCertificationName("정보처리기사")).willReturn(Optional.of(certification));

	    // When
		interestedCertificationService.saveInterestedCertification("testId", "정보처리기사");

	    // Then
		verify(userAccountRepository).findByUserId("testId");
		verify(certificationRepository).findByCertificationName("정보처리기사");
		verify(interestedCertificationRepository).save(any());
	}

	@Test
	@DisplayName("관심 자격증 제거 테스트 (실패 - 관심자격증 정보를 찾을 수 없음)")
	void deleteInterestedCertificationTest1() {
	    // Given

	    // When
		assertThrows(IllegalArgumentException.class,
				() -> interestedCertificationService.deleteInterestedCertification("testId", "정보처리기사"));
		// interestedCertificationService.deleteInterestedCertification("testId", "정보처리기사");

	    // Then
		verify(interestedCertificationRepository).findByUserIdAndCertificationName("testId", "정보처리기사");
	}

	@Test
	@DisplayName("관심 자격증 제거 테스트 (성공)")
	void deleteInterestedCertificationTest2() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		Certification certification = Certification.builder().certificationCode(1L).certificationName("정보처리기사").qualification("고졸").organizer("A").registrationLink("Link").aiSummary("Summary").build();
		InterestedCertification interestedCertification = InterestedCertification.builder().userAccount(userAccount).certification(certification).build();
		given(interestedCertificationRepository.findByUserIdAndCertificationName("testId", "정보처리기사")).willReturn(Optional.ofNullable(interestedCertification));

		// When
		interestedCertificationService.deleteInterestedCertification("testId", "정보처리기사");

		// Then
		verify(interestedCertificationRepository).findByUserIdAndCertificationName("testId", "정보처리기사");
	}
}
