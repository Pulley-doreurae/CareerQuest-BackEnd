package pulleydoreurae.careerquestbackend.certification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.UserCertificationRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.UserCertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.UserCertification;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;
import pulleydoreurae.careerquestbackend.certification.repository.UserCertificationRepository;
import pulleydoreurae.careerquestbackend.common.service.CommonService;

/**
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("취득 자격증 서비스 테스트")
class UserCertificationServiceTest {

	@InjectMocks UserCertificationService userCertificationService;
	@Mock UserAccountRepository userAccountRepository;
	@Mock CertificationRepository certificationRepository;
	@Mock UserCertificationRepository userCertificationRepository;
	@Mock CommonService commonService;

	@Test
	@DisplayName("취득 자격증 리스트 불러오기")
	void findUserCertificationTest() {
	    // Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Certification certification1 = Certification.builder().certificationCode(100L).certificationName("정보처리기사").qualification("4년제").organizer("A").registrationLink("AA").aiSummary("AI").build();
		Certification certification2 = Certification.builder().certificationCode(101L).certificationName("정보보안기사").qualification("4년제").organizer("A").registrationLink("AA").aiSummary("AI").build();
		Certification certification3 = Certification.builder().certificationCode(102L).certificationName("정보처리산업기사").qualification("2년제").organizer("A").registrationLink("AA").aiSummary("AI").build();
		UserCertification userCertification1 = UserCertification.builder().userAccount(user).certification(certification1).acqDate(LocalDate.of(2024, 1, 1)).build();
		UserCertification userCertification2 = UserCertification.builder().userAccount(user).certification(certification2).acqDate(LocalDate.of(2024, 1, 2)).build();
		UserCertification userCertification3 = UserCertification.builder().userAccount(user).certification(certification3).acqDate(LocalDate.of(2024, 1, 3)).build();
		given(userCertificationRepository.findByUserId("testId")).willReturn(List.of(userCertification1, userCertification2, userCertification3));

	    // When
		UserCertificationResponse response = userCertificationService.findAllByUserId("testId");

		// Then
		assertEquals("testId", response.getUserId());
		assertEquals(3, response.getCertificationInfos().size());
		assertEquals("정보처리기사", response.getCertificationInfos().get(0).getCertificationName());
		assertEquals("정보보안기사", response.getCertificationInfos().get(1).getCertificationName());
		assertEquals("정보처리산업기사", response.getCertificationInfos().get(2).getCertificationName());
	}

	@Test
	@DisplayName("취득 자격증 저장 테스트 -실패 (user 찾을 수 없음)")
	void saveUserCertificationTest1() {
	    // Given
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.empty());
		UserCertificationRequest request = new UserCertificationRequest("testId", "정보처리기사", LocalDate.of(2024, 1, 1));

	    // When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> userCertificationService.saveUserCertification(request));
	}

	@Test
	@DisplayName("취득 자격증 저장 테스트 -실패 (자격증 정보를 찾을 수 없음)")
	void saveUserCertificationTest2() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.ofNullable(userAccount));
		given(certificationRepository.findByCertificationName("정보처리기사")).willReturn(Optional.empty());
		UserCertificationRequest request = new UserCertificationRequest("testId", "정보처리기사", LocalDate.of(2024, 1, 1));

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> userCertificationService.saveUserCertification(request));
	}

	@Test
	@DisplayName("취득 자격증 저장 테스트 -성공")
	void saveUserCertificationTest3() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.ofNullable(userAccount));
		Certification certification = Certification.builder().certificationName("정보처리기사").build();
		given(certificationRepository.findByCertificationName("정보처리기사")).willReturn(Optional.ofNullable(certification));
		UserCertificationRequest request = new UserCertificationRequest("testId", "정보처리기사", LocalDate.of(2024, 1, 1));

		// When

		// Then
		assertDoesNotThrow(() -> userCertificationService.saveUserCertification(request));
	}

	@Test
	@DisplayName("취즉 자격증 제거 테스트 -실패 (취득자격증을 찾을 수 없음)")
	void deleteUserCertification1() {
	    // Given
		given(userCertificationRepository.findByCertificationNameAndUserId("정보처리기사", "testId")).willReturn(Optional.empty());
		UserCertificationRequest request = new UserCertificationRequest("testId", "정보처리기사", LocalDate.of(2024, 1, 1));

	    // When

	    // Then
		assertThrows(IllegalArgumentException.class, () -> userCertificationService.deleteUserCertification(request));
	}

	@Test
	@DisplayName("취즉 자격증 제거 테스트 -성공")
	void deleteUserCertification2() {
		// Given
		UserCertification userCertification = UserCertification.builder().build();
		given(userCertificationRepository.findByCertificationNameAndUserId("정보처리기사", "testId")).willReturn(Optional.ofNullable(userCertification));
		UserCertificationRequest request = new UserCertificationRequest("testId", "정보처리기사", LocalDate.of(2024, 1, 1));

		// When

		// Then
		assertDoesNotThrow(() -> userCertificationService.deleteUserCertification(request));
	}
}
