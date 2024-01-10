package pulleydoreurae.chwijunjindan.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 회원가입 컨트롤러를 테스트하는 클래스
 * UserAccountRepository 와 BCryptPasswordEncoder 를 MockBean 으로 설정해야 UserAccountController 를 정상적으로 불러와 테스트할 수 있다.
 * `@WebMvcTest` 를 사용하는 경우 시큐리티 설정파일도 전부 불러오지 않으므로 `@WithMockUser` 와 `csrf()` 설정을 해주어야 정상적으로 테스트할 수 있다.
 */
@WebMvcTest(UserAccountController.class)
class UserAccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserAccountRepository userAccountRepository;
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Test
	@DisplayName("1. 회원가입 테스트")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest1() throws Exception {
		// Given

		// When
		mockMvc.perform(
						post("/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("password", "testPassword"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andDo(print());

		// Then
		// 저장 메서드가 동작했는지 확인
		verify(userAccountRepository).save(any());
	}

	@Test
	@DisplayName("2. 회원가입 중복 테스트 (id)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest2() throws Exception {
		// Given
		given(userAccountRepository.existsByUserId(any())).willReturn(true);
		// When
		mockMvc.perform(
						post("/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andDo(print());

		// Then
		// id 로 검색하는 메서드가 동작했는지 확인
		verify(userAccountRepository).existsByUserId(any());
	}

	@Test
	@DisplayName("3. 회원가입 중복 테스트 (email)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest3() throws Exception {
		// Given
		given(userAccountRepository.existsByEmail(any())).willReturn(true);
		// When
		mockMvc.perform(
						post("/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andDo(print());

		// Then
		// id 로 검색하는 메서드가 동작했는지 확인
		verify(userAccountRepository).existsByUserId(any());
	}

	@Test
	@DisplayName("4. 유효성 검사를 통과못하는 테스트케이스 (id)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest4() throws Exception {
		// Given

		// When
		mockMvc.perform(
						post("/register")
								.with(csrf())
								.param("userId", "test")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andDo(print());

		// Then
	}

	@Test
	@DisplayName("5. 유효성 검사를 통과못하는 테스트케이스 (password)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest5() throws Exception {
		// Given

		// When
		mockMvc.perform(
						post("/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("password", "testPw"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andDo(print());

		// Then
	}

	@Test
	@DisplayName("6. 유효성 검사를 통과못하는 테스트케이스 (email)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest6() throws Exception {
		// Given

		// When
		mockMvc.perform(
						post("/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andDo(print());

		// Then
	}
}
