package pulleydoreurae.chwijunjindan.auth.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import pulleydoreurae.chwijunjindan.auth.domain.UserAccount;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.JwtTokenProvider;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtAccessToken;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtRefreshToken;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.repository.JwtAccessTokenRepository;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.repository.JwtRefreshTokenRepository;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 로그인을 테스트하기 위한 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/01/17
 */
@SpringBootTest    // 시큐리티 내부의 로그인 기능을 테스트하려면 @WebMvcTest 로는 어려움, 통합테스트이므로 @WithMockUser 필요 없음
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class LoginControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	JwtRefreshTokenRepository jwtRefreshTokenRepository;

	@Autowired
	JwtAccessTokenRepository jwtAccessTokenRepository;

	@BeforeEach
	public void setUp() {
		userAccountRepository.deleteAll();
		jwtRefreshTokenRepository.deleteAll();
		jwtAccessTokenRepository.deleteAll();
	}

	@AfterEach
	public void afterEach() {
		userAccountRepository.deleteAll();
		jwtRefreshTokenRepository.deleteAll();
		jwtAccessTokenRepository.deleteAll();
	}

	@Test
	@DisplayName("로그인 성공 테스트")
	void LoginSuccessTest() throws Exception {
		// Given
		UserAccount userAccount = UserAccount.builder()
				.userId("testId")
				.password(bCryptPasswordEncoder.encode("testPassword"))
				.build();
		userAccountRepository.save(userAccount);

		// When
		mockMvc.perform(post("/api/login")
						.param("username", "testId")
						.param("password", "testPassword"))
				.andDo(print())
				.andExpect(jsonPath("$.token_type").exists())
				.andExpect(jsonPath("$.access_token").exists())
				.andExpect(jsonPath("$.expires_in").exists())
				.andExpect(jsonPath("$.refresh_token").exists())
				.andExpect(jsonPath("$.refresh_token_expires_in").exists())
				// Spring REST Docs
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("username").description("로그인 할 아이디"),
								parameterWithName("password").description("로그인 할 비밀번호")
						),
						responseFields(
								fieldWithPath("token_type").description("토큰 타입"),
								fieldWithPath("access_token").description("액세스 토큰"),
								fieldWithPath("expires_in").description("액세스 토큰 유효기간"),
								fieldWithPath("refresh_token").description("리프레시 토큰"),
								fieldWithPath("refresh_token_expires_in").description("리프레시 토큰 유효기간")
						)));

		// Then
	}

	@Test
	@DisplayName("로그인 실패 테스트 (사용자를 찾을 수 없음)")
	void LoginFailTest1() throws Exception {
		// Given

		// When
		mockMvc.perform(post("/api/login")
						.param("username", "testId")
						.param("password", "testPassword"))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				// Spring REST Docs
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("username").description("로그인 할 아이디"),
								parameterWithName("password").description("로그인 할 비밀번호")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("code").description("응답 코드"),
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
						.param("username", "testId")
						.param("password", "Password"))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				// Spring REST Docs
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("username").description("로그인 할 아이디"),
								parameterWithName("password").description("로그인 할 비밀번호")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("code").description("응답 코드"),
								fieldWithPath("error").description("실패 이유")
						)));
		// Then
	}

	@Test
	@DisplayName("액세스 토큰 테스트 (유효한 액세스 토큰)")
	void ValidAccessTokenTest() throws Exception {
		// Given
		UserAccount userAccount = UserAccount.builder()
				.userId("testId")
				.password(bCryptPasswordEncoder.encode("testPassword"))
				.build();
		userAccountRepository.save(userAccount);

		JwtTokenResponse jwtTokenResponse = jwtTokenProvider.createJwtResponse("testId");
		jwtAccessTokenRepository.save(new JwtAccessToken(jwtTokenResponse.getAccess_token(), "testId"));

		// When
		mockMvc.perform(get("/index")
						.header("Authorization", "Bearer " + jwtTokenResponse.getAccess_token()))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName("Authorization").description("유효한 액세스 토큰")
						)));

		// Then
	}

	@Test
	@DisplayName("액세스 토큰 테스트 (유효하지 않은 액세스 토큰)")
	void InvalidAccessTokenTest() throws Exception {
		// Given
		String invalidJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0SWQiLCJpc3MiOiJwdWxsZXkiLCJpYXQiOjE3MDU4MjU0OTUsImV4cCI6MTcwNzk4NTQ5NX0.k3dJRYloCsuMAEiMBDxTeYG44DWLs6xHAPrTmmyHsdg";

		// When
		mockMvc.perform(get("/index")
						.header("Authorization", "Bearer " + invalidJwtToken))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName("Authorization").description("유효하지 않은 액세스 토큰")
						)));

		// Then
	}

	@Test
	@DisplayName("리프레시 토큰 테스트 (유효한 리프레시 토큰)")
	void ValidRefreshTokenTest() throws Exception {
		// Given
		String invalidJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0SWQiLCJpc3MiOiJwdWxsZXkiLCJpYXQiOjE3MDU4MjU0OTUsImV4cCI6MTcwNzk4NTQ5NX0.k3dJRYloCsuMAEiMBDxTeYG44DWLs6xHAPrTmmyHsdg";

		JwtTokenResponse jwtTokenResponse = jwtTokenProvider.createJwtResponse("testId");
		JwtRefreshToken jwtRefreshToken = new JwtRefreshToken(jwtTokenResponse.getRefresh_token(), "testId");
		jwtRefreshTokenRepository.save(jwtRefreshToken);

		// When
		mockMvc.perform(get("/index")
						.header("Authorization", "Bearer " + invalidJwtToken)
						.header("RefreshToken", jwtTokenResponse.getRefresh_token()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token_type").exists())
				.andExpect(jsonPath("$.access_token").exists())
				.andExpect(jsonPath("$.expires_in").exists())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName("Authorization").description("유효하지 않은 액세스 토큰"),
								headerWithName("RefreshToken").description("유효한 리프레시 토큰")
						),
						responseFields(
								fieldWithPath("token_type").description("토큰 타입"),
								fieldWithPath("access_token").description("새로 발급받은 액세스 토큰"),
								fieldWithPath("expires_in").description("새로 발급받은 액세스 토큰 유효기간")
						)));

		// Then
	}

	@Test
	@DisplayName("리프레시 토큰 테스트 (유효하지 않은 리프레시 토큰)")
	void InvalidRefreshTokenTest() throws Exception {
		// Given
		String invalidJwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0SWQiLCJpc3MiOiJwdWxsZXkiLCJpYXQiOjE3MDU4MjU0OTUsImV4cCI6MTcwNzk4NTQ5NX0.k3dJRYloCsuMAEiMBDxTeYG44DWLs6xHAPrTmmyHsdg";
		String invalidRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0SWQiLCJpc3MiOiJwdWxsZXkiLCJpYXQiOjE3MDU4MjU0OTUsImV4cCI6MTcwNTgyNjA5NX0.UwTqbudlGSB_lSPof00ju5_aK3T6TL2B8RQ00hCs3xc";

		// When
		mockMvc.perform(get("/index")
						.header("Authorization", "Bearer " + invalidJwtToken)
						.header("RefreshToken", invalidRefreshToken))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName("Authorization").description("유효하지 않은 액세스 토큰"),
								headerWithName("RefreshToken").description("유효하지 않은 리프레시 토큰")
						)));

		// Then
	}

	@Test
	@DisplayName("로그아웃 테스트 (Redis 에 액세스, 리프레시 토큰 제거)")
	void LogoutTest() throws Exception {
		// Given
		JwtTokenResponse jwtTokenResponse = jwtTokenProvider.createJwtResponse("testId");

		// When
		mockMvc.perform(get("/api/logout")
						.header("Authorization", "Bearer " + jwtTokenResponse.getAccess_token())
						.header("RefreshToken", jwtTokenResponse.getRefresh_token()))
				.andDo(print())
				.andExpect(status().isOk())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestHeaders(
								headerWithName("Authorization").description("로그인 하고 발급받은 액세스 토큰"),
								headerWithName("RefreshToken").description("로그인 하고 발급받은 리프레시 토큰")
						)));
		// Then
	}
}