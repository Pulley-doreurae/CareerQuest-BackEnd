package pulleydoreurae.careerquestbackend.auth.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.Careers;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserCareerDetails;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
@DataJpaTest
@DisplayName("직무 Repository 테스트")
@Import(QueryDSLConfig.class)
class UserCareerDetailsRepositoryTest {

	@Autowired
	UserCareerDetailsRepository userCareerDetailsRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	CareerDetailsRepository careerDetailsRepository;
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

		Careers careers1 = Careers.builder().categoryName("사업관리").categoryType("대분류").categoryImage("/major/image/0").build();
		careerDetailsRepository.save(careers1);
		Careers careers2 = Careers.builder().categoryName("사업관리").categoryType("중분류").categoryImage("/middle/image/0").parent(careers1).build();
		careerDetailsRepository.save(careers2);
		Careers careers3 = Careers.builder().categoryName("프로젝트 관리").categoryType("소분류").categoryImage("/small/image/0").parent(careers2).build();
		Careers careers4 = Careers.builder().categoryName("해외관리").categoryType("소분류").categoryImage("/small/image/1").parent(careers2).build();
		careerDetailsRepository.save(careers3);
		careerDetailsRepository.save(careers4);

	}

	@AfterEach    // 각각의 테스트가 종료되면 데이터베이스에 저장된 내용 삭제하기
	public void after() {
		userCareerDetailsRepository.deleteAll();
		careerDetailsRepository.deleteAll();
		userAccountRepository.deleteAll();
	}

	@Test
	@DisplayName("1. 새로운 직무 저장테스트")
	void addNewCareerTest() {
		// Given
		Careers newCareers = careerDetailsRepository.findCareersByCategoryNameAndCategoryType("프로젝트 관리", "소분류").orElseThrow();
		UserAccount userAccount = userAccountRepository.findByUserId(userId).orElseThrow();

		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
			.userAccount(userAccount)
			.smallCategory(newCareers)
			.build();

		// When
		userCareerDetailsRepository.save(userCareerDetails);

		// Then
		assertEquals(userId, userAccount.getUserId());
		assertEquals("프로젝트 관리", newCareers.getCategoryName());
		assertEquals("사업관리", newCareers.getParent().getCategoryName());
		assertEquals("사업관리", newCareers.getParent().getParent().getCategoryName());
		assertEquals(1, userCareerDetailsRepository.findAll().size());
	}

	@Test
	@DisplayName("2. 직무 업데이트 테스트")
	void updateCareerTest() {
		// Given
		Careers careers = careerDetailsRepository.findCareersByCategoryNameAndCategoryType("프로젝트 관리", "소분류").orElseThrow();
		UserAccount userAccount = userAccountRepository.findByUserId(userId).orElseThrow();

		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
			.userAccount(userAccount)
			.smallCategory(careers)
			.build();

		userCareerDetailsRepository.save(userCareerDetails);
		// When
		UserCareerDetails careerDetails = userCareerDetailsRepository.findByUserAccount(userAccount).orElseThrow();
		Careers newCareers = careerDetailsRepository.findCareersByCategoryNameAndCategoryType("해외관리", "소분류").orElseThrow();


		UserCareerDetails updateUserCareerDetails = UserCareerDetails.builder()
			.id(careerDetails.getId()) // 동일한 id 로 덮어쓰기
			.smallCategory(newCareers)
			.userAccount(userAccount)
			.build();

		userCareerDetailsRepository.save(updateUserCareerDetails);

		// Then
		assertEquals(1, userCareerDetailsRepository.findAll().size());
		assertEquals(userId, userAccount.getUserId());
		assertEquals("해외관리", userCareerDetailsRepository.findByUserAccount(userAccount).orElseThrow().getSmallCategory().getCategoryName());
	}

	@Test
	@DisplayName("3. 저장된 직무를 사용자 아이디로 조회하는 테스트")
	void findByUserIdTest() {
		// Given

		// When
		UserAccount user = userAccountRepository.findByUserId(userId).orElseThrow();
		Careers careers = careerDetailsRepository.findCareersByCategoryNameAndCategoryType("해외관리", "소분류").orElseThrow();


		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
			.userAccount(user)
			.smallCategory(careers)
			.build();

		userCareerDetailsRepository.save(userCareerDetails);

		// Then
		UserAccount getUser = userAccountRepository.findByUserId(userId).orElseThrow();

		UserCareerDetails getUserUserCareerDetails = userCareerDetailsRepository.findByUserAccount(getUser).orElseThrow();

		assertAll(
			() -> assertEquals(userCareerDetails.getId(), getUserUserCareerDetails.getId()),
			() -> assertEquals(userCareerDetails.getUserAccount(), getUserUserCareerDetails.getUserAccount()),
			() -> assertEquals(userCareerDetails.getSmallCategory(), getUserUserCareerDetails.getSmallCategory()),
			() -> assertEquals(userCareerDetails.getCreatedAt(), getUserUserCareerDetails.getCreatedAt()),
			() -> assertEquals(userCareerDetails.getModifiedAt(), getUserUserCareerDetails.getModifiedAt())
		);
	}

	@Test
	@DisplayName("4. 직무 삭제 테스트")
	void removeCareerTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId(userId).orElseThrow();
		Careers careers = careerDetailsRepository.findCareersByCategoryNameAndCategoryType("해외관리", "소분류").orElseThrow();


		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
			.userAccount(user)
			.smallCategory(careers)
			.build();

		// When
		userCareerDetailsRepository.save(userCareerDetails);

		UserAccount getUser = userAccountRepository.findByUserId(userId).orElseThrow();
		UserCareerDetails getUserCareerDetails = userCareerDetailsRepository.findByUserAccount(getUser).orElseThrow();
		userCareerDetailsRepository.delete(getUserCareerDetails);

		// Then
		assertEquals(Optional.empty(), userCareerDetailsRepository.findById(getUserCareerDetails.getId()));
	}
}