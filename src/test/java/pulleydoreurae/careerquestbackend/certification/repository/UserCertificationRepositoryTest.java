package pulleydoreurae.careerquestbackend.certification.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.UserCertification;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
@DataJpaTest
@DisplayName("취득한 자격증 Repository 테스트")
@Transactional
@Import(QueryDSLConfig.class)
class UserCertificationRepositoryTest {

	@Autowired UserAccountRepository userAccountRepository;
	@Autowired CertificationRepository certificationRepository;
	@Autowired UserCertificationRepository userCertificationRepository;

	@BeforeEach
	void setUp() {
		UserAccount userAccount1 = UserAccount.builder().userId("testId1").build();
		UserAccount userAccount2 = UserAccount.builder().userId("testId2").build();
		UserAccount userAccount3 = UserAccount.builder().userId("testId3").build();
		Certification certification1 = Certification.builder().certificationCode(100L).certificationName("정보처리기사").qualification("4년제").organizer("A").registrationLink("AA").aiSummary("AI").build();
		Certification certification2 = Certification.builder().certificationCode(101L).certificationName("정보보안기사").qualification("4년제").organizer("A").registrationLink("AA").aiSummary("AI").build();
		Certification certification3 = Certification.builder().certificationCode(102L).certificationName("정보처리산업기사").qualification("2년제").organizer("A").registrationLink("AA").aiSummary("AI").build();
		UserCertification userCertification1 = UserCertification.builder().userAccount(userAccount1).certification(certification1).acqDate(LocalDate.of(2024, 1, 1)).build();
		UserCertification userCertification2 = UserCertification.builder().userAccount(userAccount1).certification(certification2).acqDate(LocalDate.of(2024, 1, 2)).build();
		UserCertification userCertification3 = UserCertification.builder().userAccount(userAccount1).certification(certification3).acqDate(LocalDate.of(2024, 1, 3)).build();
		UserCertification userCertification4 = UserCertification.builder().userAccount(userAccount2).certification(certification1).acqDate(LocalDate.of(2024, 1, 5)).build();
		UserCertification userCertification5 = UserCertification.builder().userAccount(userAccount3).certification(certification1).acqDate(LocalDate.of(2024, 1, 6)).build();

		userAccountRepository.save(userAccount1);
		userAccountRepository.save(userAccount2);
		userAccountRepository.save(userAccount3);

		certificationRepository.save(certification1);
		certificationRepository.save(certification2);
		certificationRepository.save(certification3);

		userCertificationRepository.save(userCertification1);
		userCertificationRepository.save(userCertification2);
		userCertificationRepository.save(userCertification3);
		userCertificationRepository.save(userCertification4);
		userCertificationRepository.save(userCertification5);
	}

	@Test
	@DisplayName("userId로 취득한 자격증을 불러오는 테스트")
	void findByUserIdTest() {
	    // Given

	    // When
		List<UserCertification> result = userCertificationRepository.findByUserId("testId1");

		// Then
		assertEquals(3, result.size());
		assertEquals("정보처리기사", result.get(0).getCertification().getCertificationName());
		assertEquals("정보보안기사", result.get(1).getCertification().getCertificationName());
		assertEquals("정보처리산업기사", result.get(2).getCertification().getCertificationName());
	}

	@Test
	@DisplayName("자격증 이름으로 취득한 사용자들을 뽑는 테스트")
	void findByCertificationName() {
	    // Given

	    // When
		List<UserCertification> result = userCertificationRepository.findByCertificationName("정보처리기사");

		// Then
		assertEquals(3, result.size());
		assertEquals("testId1", result.get(0).getUserAccount().getUserId());
		assertEquals("testId2", result.get(1).getUserAccount().getUserId());
		assertEquals("testId3", result.get(2).getUserAccount().getUserId());
	}
}