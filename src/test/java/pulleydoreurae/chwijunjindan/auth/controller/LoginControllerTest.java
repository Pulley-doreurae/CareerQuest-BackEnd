package pulleydoreurae.chwijunjindan.auth.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import pulleydoreurae.chwijunjindan.auth.domain.UserAccount;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 로그인을 테스트하기 위한 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/01/17
 */
@SpringBootTest	// 시큐리티 내부의 로그인 기능을 테스트하려면 @WebMvcTest 로는 어려움
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class LoginControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@BeforeEach
	public void setUp() {
		userAccountRepository.deleteAll();
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	@WithMockUser
	void LoginSuccessTest() throws Exception {
	    // Given
		UserAccount userAccount = UserAccount.builder()
				.userId("testId")
				.password(bCryptPasswordEncoder.encode("testPassword"))
				.build();
	    userAccountRepository.save(userAccount);

	    // When
		mockMvc.perform(post("/api/login")
						.with(csrf())
						.param("username", "testId")
						.param("password", "testPassword"))
				.andDo(print())
				.andExpect(redirectedUrl("/api/login-success"))
				.andExpect(status().is3xxRedirection())
				// Spring REST Docs
				.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				formParameters(	// form-data 형식
						parameterWithName("username").description("로그인 할 아이디"),
						parameterWithName("password").description("로그인 할 비밀번호"),
						parameterWithName("_csrf").description("csrf 토큰값")
				)));

	    // Then
	}

	@Test
	@DisplayName("로그인 실패 테스트 (사용자를 찾을 수 없음)")
	void LoginFailTest1() throws Exception {
		// Given

		// When
		mockMvc.perform(post("/api/login")
						.with(csrf())
						.param("username", "testId")
						.param("password", "testPassword"))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				// Spring REST Docs
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(	// form-data 형식
								parameterWithName("username").description("로그인 할 아이디"),
								parameterWithName("password").description("로그인 할 비밀번호"),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(	// Json 응답 형식
								fieldWithPath("msg").description("로그인 결과"),
								fieldWithPath("error").description("실패 이유")
						)));
		// Then
	}

	@Test
	@DisplayName("로그인 실패 테스트 (비밀번호가 맞지않음)")
	void LoginFailTest2() throws Exception {
		// Given
		UserAccount userAccount = UserAccount.builder()
				.userId("testId")
				.password(bCryptPasswordEncoder.encode("testPassword"))
				.build();
		userAccountRepository.save(userAccount);

		// When
		mockMvc.perform(post("/api/login")
						.with(csrf())
						.param("username", "testId")
						.param("password", "Password"))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				// Spring REST Docs
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(	// form-data 형식
								parameterWithName("username").description("로그인 할 아이디"),
								parameterWithName("password").description("로그인 할 비밀번호"),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(	// Json 응답 형식
								fieldWithPath("msg").description("로그인 결과"),
								fieldWithPath("error").description("실패 이유")
						)));
		// Then
	}
}