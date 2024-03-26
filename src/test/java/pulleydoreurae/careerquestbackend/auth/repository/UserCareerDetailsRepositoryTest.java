package pulleydoreurae.careerquestbackend.auth.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserCareerDetails;

/**
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
@DataJpaTest
@DisplayName("직무 Repository 테스트")
class UserCareerDetailsRepositoryTest {

	@Autowired
	UserCareerDetailsRepository userCareerDetailsRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	String userId = "testId";

	@BeforeEach
	public void before() { // 사용자 정보 미리 저장
		UserAccount user = UserAccount.builder()
				.userId(userId)
				.userName("testName")
				.email("test@email.com")
				.phoneNum("010-1111-2222")
				.password("testPassword")
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();

		userAccountRepository.save(user);
	}

	@AfterEach    // 각각의 테스트가 종료되면 데이터베이스에 저장된 내용 삭제하기
	public void after() {
		userCareerDetailsRepository.deleteAll();
		userAccountRepository.deleteAll();
	}

	@Test
	@DisplayName("1. 새로운 직무 저장테스트")
	void addNewCareerTest() {
		// Given
		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
				.majorCategory(1L)
				.middleCategory(1L)
				.smallCategory(1L)
				.build();

		// When
		userCareerDetailsRepository.save(userCareerDetails);

		// Then
		assertEquals(1, userCareerDetailsRepository.findAll().size());
	}

	@Test
	@DisplayName("2. 직무 업데이트 테스트")
	void updateCareerTest() {
	    // Given
		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
				.majorCategory(1L)
				.middleCategory(1L)
				.smallCategory(1L)
				.build();

		// When
		UserAccount user = userAccountRepository.findByUserId(userId).orElseThrow();

		userCareerDetailsRepository.save(userCareerDetails);
		user.setUserCareerDetails(userCareerDetails);
		userAccountRepository.save(user);


		UserCareerDetails updateUserCareerDetails = UserCareerDetails.builder()
				.id(user.getUserCareerDetails().getId()) // 동일한 id 로 덮어쓰기
				.majorCategory(1L)
				.middleCategory(1L)
				.smallCategory(1L)
				.build();

		userCareerDetailsRepository.save(updateUserCareerDetails);

		// Then
		assertEquals(1, userCareerDetailsRepository.findAll().size());
	}

	@Test
	@DisplayName("3. 저장된 직무를 사용자 아이디로 조회하는 테스트")
	void findByUserIdTest() {
		// Given
		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
				.majorCategory(1L)
				.middleCategory(1L)
				.smallCategory(1L)
				.build();

		// When
		UserAccount user = userAccountRepository.findByUserId(userId).orElseThrow();

		userCareerDetailsRepository.save(userCareerDetails);
		user.setUserCareerDetails(userCareerDetails);
		userAccountRepository.save(user);

		// Then
		UserAccount getUser = userAccountRepository.findByUserId(userId).orElseThrow();

		UserCareerDetails getUserUserCareerDetails = getUser.getUserCareerDetails();
		assertAll(
				() -> assertEquals(userCareerDetails.getId(), getUserUserCareerDetails.getId()),
				() -> assertEquals(userCareerDetails.getMajorCategory(), getUserUserCareerDetails.getMajorCategory()),
				() -> assertEquals(userCareerDetails.getMiddleCategory(), getUserUserCareerDetails.getMiddleCategory()),
				() -> assertEquals(userCareerDetails.getSmallCategory(), getUserUserCareerDetails.getSmallCategory()),
				() -> assertEquals(userCareerDetails.getCreatedAt(), getUserUserCareerDetails.getCreatedAt()),
				() -> assertEquals(userCareerDetails.getModifiedAt(), getUserUserCareerDetails.getModifiedAt())
		);
	}

	@Test
	@DisplayName("4. 직무 삭제 테스트")
	void removeCareerTest() {
	    // Given
		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
				.majorCategory(1L)
				.middleCategory(1L)
				.smallCategory(1L)
				.build();

		// When
		UserAccount user = userAccountRepository.findByUserId(userId).orElseThrow();

		userCareerDetailsRepository.save(userCareerDetails);
		user.setUserCareerDetails(userCareerDetails);
		userAccountRepository.save(user);

		UserAccount getUser = userAccountRepository.findByUserId(userId).orElseThrow();
		UserCareerDetails getUserCareerDetails = getUser.getUserCareerDetails();
		user.setUserCareerDetails(null);
		userCareerDetailsRepository.delete(getUserCareerDetails);

		// Then
		assertNull(getUser.getUserCareerDetails());
		assertEquals(Optional.empty(), userCareerDetailsRepository.findById(getUserCareerDetails.getId()));
	}
}