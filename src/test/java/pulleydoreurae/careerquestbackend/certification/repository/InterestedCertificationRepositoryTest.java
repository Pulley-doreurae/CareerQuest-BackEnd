package pulleydoreurae.careerquestbackend.certification.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.InterestedCertification;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 * 관심자격증 RepositoryTest
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
@DataJpaTest
@DisplayName("관심자격증 Repository 테스트")
@Transactional
@Import(QueryDSLConfig.class)
class InterestedCertificationRepositoryTest {

	@Autowired
	InterestedCertificationRepository interestedCertificationRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	CertificationRepository certificationRepository;

	@Test
	@DisplayName("관심자격증 저장 테스트")
	void saveInterestedCertificationTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Certification certification = Certification.builder().certificationCode(1L).certificationName("정보처리기사").qualification("고졸").organizer("A").registrationLink("Link").aiSummary("Summary").build();
		certificationRepository.save(certification);
		InterestedCertification interestedCertification = InterestedCertification.builder().certification(certification).userAccount(userAccount).build();
		interestedCertificationRepository.save(interestedCertification);

		// When
		List<InterestedCertification> result = interestedCertificationRepository.findAllByUserId("testId");

		// Then
		assertEquals(1, result.size());
		assertEquals("정보처리기사", result.get(0).getCertification().getCertificationName());
	}

	@Test
	@DisplayName("관심자격증 제거 테스트")
	void deleteInterestedCertificationTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Certification certification = Certification.builder().certificationCode(1L).certificationName("정보처리기사").qualification("고졸").organizer("A").registrationLink("Link").aiSummary("Summary").build();
		certificationRepository.save(certification);
		InterestedCertification interestedCertification = InterestedCertification.builder().certification(certification).userAccount(userAccount).build();
		interestedCertificationRepository.save(interestedCertification);

		// 값 저장이 잘 되어있는지 검증 후
		InterestedCertification before = interestedCertificationRepository.findByUserIdAndCertificationName("testId", "정보처리기사").get();
		assertEquals("정보처리기사", before.getCertification().getCertificationName());

	    // When
		interestedCertificationRepository.delete(before);

	    // Then
		// 값 제거가 정상적으로 되었는지 검증
		List<InterestedCertification> result = interestedCertificationRepository.findAllByUserId("testId");
		assertEquals(0, result.size());
	}
}