package pulleydoreurae.careerquestbackend.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.*;
import static org.mockserver.model.HttpResponse.*;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.GoogleLoginResponse;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.GoogleUserDetailsResponse;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;

/**
 * 구글 API 를 Mocking 하여 테스트하는 클래스
 *
 */
@SpringBootTest // application.yml 파일을 불러 빈을 생성할 수 있도록 통합테스트 사용
class GoogleLoginServiceTest {

	@Value("${LOGIN.GOOGLE_CLIENT_ID}")
	String clientId;
	@Value("${LOGIN.GOOGLE_CLIENT_SECRET}")
	String clientSecret;
	String redirectUri = "http%3A%2F%2Flocalhost%3A8081%2Fapi%2Flogin-google%2Fcode"; // URL 인코딩 적용
	ClientAndServer mockServer;
	Gson gson = new Gson();
	@Value("${LOGIN.HOST}")
	private String host;
	@Autowired
	private GoogleLoginService googleLoginService;
	@Autowired
	private UserAccountRepository userAccountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@BeforeEach
	void setUp() {
		// 8081 포트로 MockServer 를 연다.
		mockServer = ClientAndServer.startClientAndServer(8081);
		userAccountRepository.deleteAll();
	}

	@AfterEach
	void afterEach() {
		mockServer.stop();
		userAccountRepository.deleteAll();
	}

	@Test
	@DisplayName("리다이렉트 URL 을 정상적으로 반환하는지 테스트")
	void getRedirectUrlTest() {
		// Given
		String url = "testUrl";
		URI uri = UriComponentsBuilder.fromUriString(url)
				.queryParam("scope", "email")
				.queryParam("response_type", "code")
				.queryParam("client_id", clientId)
				.queryParam("redirect_uri", host + "/api/login-google/code")
				.queryParam("prompt", "consent")
				.queryParam("access_type", "offline")
				.build()
				.toUri();

		// When
		String getUrl = googleLoginService.getRedirectUrl(url);

		// Then
		assertEquals(uri.toString(), getUrl);
	}

	@Test
	@DisplayName("구글서버로부터 토큰을 정상적으로 발급받을 수 있는지 테스트")
	void getTokenTest() {
		// Given
		String url = host + "/token"; // 구글 API 를 대신할 URL 주소

		// Mock 서버가 리턴할 값 (구글 API 가 리턴하는 값을 Mocking 한다.)
		GoogleLoginResponse response = GoogleLoginResponse.builder()
				.token_type("Bearer")
				.access_token("accessToken")
				.expires_in(1234)
				.refresh_token("refreshToken")
				.scope("email")
				.build();

		// Mock 서버에 전달해야할 Header (구글 API 와 동일하게 Mocking)
		Header header = new Header("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// Mock 서버에 전달해야할 Body (구글 API 와 동일하게 Mocking)
		String body =
				"code=testCode&client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUri
						+ "&grant_type=authorization_code";
		mockServer.when(
						request()
								.withMethod("POST")
								.withPath("/token")
								.withHeader(header)
								.withBody(body)
				)
				.respond(
						response()
								.withStatusCode(HttpStatus.OK.value())
								.withHeader("Content-type", "application/json")
								.withBody(gson.toJson(response))
				);
		// When
		String getAccessToken = googleLoginService.getToken("testCode", url);

		// Then
		assertEquals("accessToken", getAccessToken);
	}

	@Test
	@DisplayName("구글서버로부터 사용자 정보를 정상적으로 받아오는지 테스트")
	void getUserDetailsTest() {
		// Given
		String url = host + "/v2/userinfo";

		// Mock 서버에 전달해야할 Header (카카오 API 와 동일하게 Mocking)
		Header header1 = new Header("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		Header header2 = new Header("Authorization", "Bearer testToken");

		GoogleUserDetailsResponse response = GoogleUserDetailsResponse.builder()
				.id("111")
				.email("test@email.com")
				.build();

		mockServer.when(request()
						.withMethod("GET")
						.withPath("/v2/userinfo")
						.withHeader(header1)
						.withHeader(header2))
				.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(gson.toJson(response))
						.withContentType(MediaType.APPLICATION_JSON));

		// When
		String getEmail = googleLoginService.getUserDetails("testToken", url);

		// Then
		assertEquals("test@email.com", getEmail);
	}

	@Test
	@DisplayName("구글 로그인에 성공하고 데이터베이스에 존재하는 계정에 정상적으로 로그인하는 테스트")
	void loginTest() {
		// Given
		String email = "test@email.com";
		userAccountRepository.save(UserAccount.builder()
				.userId(email)
				.userName(email)
				.phoneNum("000-1111-2222")
				.password(bCryptPasswordEncoder.encode("google-register" + email))
				.email(email)
				.birth("00-01-01")
				.gender("M")
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build());

		Header header = new Header("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		String body = "username=test%40email.com&password=google-registertest%40email.com";
		JwtTokenResponse response = JwtTokenResponse.builder()
				.token_type("bearer")
				.access_token("accessToken")
				.expires_in(1111L)
				.refresh_token("refreshToken")
				.refresh_token_expires_in(3333L)
				.build();

		mockServer.when(request()
						.withMethod("POST")
						.withPath("/api/login")
						.withHeader(header)
						.withBody(body))
				.respond(response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(gson.toJson(response))
						.withContentType(MediaType.APPLICATION_JSON));

		// When
		JwtTokenResponse getResponse = googleLoginService.login(email).getBody();

		// Then
		assertAll(
				() -> assertEquals(response.getToken_type(), getResponse.getToken_type()),
				() -> assertEquals(response.getAccess_token(), getResponse.getAccess_token()),
				() -> assertEquals(response.getExpires_in(), getResponse.getExpires_in()),
				() -> assertEquals(response.getRefresh_token(), getResponse.getRefresh_token()),
				() -> assertEquals(response.getRefresh_token_expires_in(), getResponse.getRefresh_token_expires_in())
		);
	}

	@Test
	@DisplayName("구글 로그인에 성공하고 데이터베이스에 회원정보가 없다면 회원가입을 한다.")
	void registerTest() {
		// Given
		String email = "test@email.com";
		UserAccount user = UserAccount.builder()
				.userId(email)
				.userName(email)
				.phoneNum("000-1111-2222")
				.password("google-register" + email)
				.email(email)
				.birth("00-01-01")
				.gender("M")
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();

		// When
		googleLoginService.register(email);
		Optional<UserAccount> optionalGetUser = userAccountRepository.findByEmail(email);
		UserAccount getUser = optionalGetUser.get();

		// Then
		assertAll(
				() -> assertEquals(user.getUserId(), getUser.getUserId()),
				() -> assertEquals(user.getUserName(), getUser.getUserName()),
				() -> assertEquals(user.getPhoneNum(), getUser.getPhoneNum()),
				() -> assertTrue(bCryptPasswordEncoder.matches(user.getPassword(), getUser.getPassword())),
				() -> assertEquals(user.getEmail(), getUser.getEmail()),
				() -> assertEquals(user.getBirth(), getUser.getBirth()),
				() -> assertEquals(user.getGender(), getUser.getGender()),
				() -> assertEquals(user.getRole(), getUser.getRole())
		);
	}
}
