package pulleydoreurae.careerquestbackend.auth.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserTechnologyStack;

/**
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
@DataJpaTest
@DisplayName("기술스택 Repository 테스트")
class UserTechnologyStackRepositoryTest {

	@Autowired
	UserTechnologyStackRepository userTechnologyStackRepository;
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

		UserTechnologyStack stack1 = UserTechnologyStack.builder()
				.stackId(1L)
				.userAccount(user)
				.build();

		UserTechnologyStack stack2 = UserTechnologyStack.builder()
				.stackId(2L)
				.userAccount(user)
				.build();

		UserTechnologyStack stack3 = UserTechnologyStack.builder()
				.stackId(3L)
				.userAccount(user)
				.build();

		UserTechnologyStack stack4 = UserTechnologyStack.builder()
				.stackId(4L)
				.userAccount(user)
				.build();

		// When
		userTechnologyStackRepository.save(stack1);
		userTechnologyStackRepository.save(stack2);
		userTechnologyStackRepository.save(stack3);
		userTechnologyStackRepository.save(stack4);

		List<UserTechnologyStack> stacks = new ArrayList<>();
		stacks.add(stack1);
		stacks.add(stack2);
		stacks.add(stack3);
		stacks.add(stack4);
		user.setStacks(stacks);
		userAccountRepository.save(user);
	}

	@AfterEach    // 각각의 테스트가 종료되면 데이터베이스에 저장된 내용 삭제하기
	public void after() {
		userTechnologyStackRepository.deleteAll();
		userAccountRepository.deleteAll();
	}

	@Test
	@DisplayName("1. 기술스택 저장 테스트")
	void addNewStacksTest() {
		// Given

		// Then
		assertEquals(4, userTechnologyStackRepository.findAll().size());
	}

	@Test
	@DisplayName("2. 기술스택 업데이트(부분삭제, 부분추가) 테스트")
	void updateStackTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId(userId).orElseThrow();

		UserTechnologyStack stack5 = UserTechnologyStack.builder()
				.stackId(5L)
				.userAccount(user)
				.build();
		userAccountRepository.save(user);

		userTechnologyStackRepository.delete(user.getStacks().get(1));
		user.getStacks().remove(1);
		userTechnologyStackRepository.delete(user.getStacks().get(2));
		user.getStacks().remove(2);
		userAccountRepository.save(user);
		userTechnologyStackRepository.save(stack5);
		user.getStacks().add(stack5);
		userAccountRepository.save(user);

		// Then
		assertEquals(3, userAccountRepository.findByUserId(userId).get().getStacks().size());
		assertEquals(3, userTechnologyStackRepository.findAll().size());
	}

	@Test
	@DisplayName("3. 저장된 기술스택을 회원으로 조회하는 테스트")
	void findByUserTest() {
		// Given
		UserAccount getUser = userAccountRepository.findByUserId(userId).orElseThrow();

		// Then
		List<UserTechnologyStack> getStacks = getUser.getStacks();
		assertAll(
				() -> assertEquals(getStacks.get(0),
						userTechnologyStackRepository.findById(getStacks.get(0).getId()).get()),
				() -> assertEquals(getStacks.get(1),
						userTechnologyStackRepository.findById(getStacks.get(1).getId()).get()),
				() -> assertEquals(getStacks.get(2),
						userTechnologyStackRepository.findById(getStacks.get(2).getId()).get()),
				() -> assertEquals(getStacks.get(3),
						userTechnologyStackRepository.findById(getStacks.get(3).getId()).get())
		);
	}

	@Test
	@DisplayName("4. 기술스택 삭제 테스트")
	void deleteStacksTest() {
		// Given
		UserAccount getUser = userAccountRepository.findByUserId(userId).orElseThrow();

		// When
		userTechnologyStackRepository.deleteAllByUserAccount(getUser);
		getUser.setStacks(null);
		userAccountRepository.save(getUser);
		// Then
		assertEquals(0, userTechnologyStackRepository.findAll().size());
	}
}