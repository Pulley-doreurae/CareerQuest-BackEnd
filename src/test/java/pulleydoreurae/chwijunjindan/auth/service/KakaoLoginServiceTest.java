package pulleydoreurae.chwijunjindan.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

import pulleydoreurae.chwijunjindan.auth.domain.UserRole;
import pulleydoreurae.chwijunjindan.auth.domain.dto.KakaoAccount;
import pulleydoreurae.chwijunjindan.auth.domain.dto.KakaoLoginResponse;
import pulleydoreurae.chwijunjindan.auth.domain.dto.KakaoUserDetailsResponse;
import pulleydoreurae.chwijunjindan.auth.domain.entity.UserAccount;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 카카오 API 를 Mocking 하여 테스트하는 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/01/25
 */
@SpringBootTest // application.yml 파일을 불러 빈을 생성할 수 있도록 통합테스트 사용
@TestPropertySource(locations = "classpath:application.yml")
class KakaoLoginServiceTest {

	@Autowired
	private KakaoLoginService kakaoLoginService;
	@Autowired
	private UserAccountRepository userAccountRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Value("${LOGIN.KAKAO_API_KEY}")
	String clientId;
	String redirectUri = "http%3A%2F%2Flocalhost%3A8080%2Fapi%2Flogin-kakao%2Fcode"; // URL 인코딩 적용
	ClientAndServer mockServer;
	Gson gson = new Gson();

	@BeforeEach
	void setUp() {
		// 8080 포트로 MockServer 를 연다. (내부 API 를 사용하는 경우도 있으므로 8080으로 설정)
		mockServer = ClientAndServer.startClientAndServer(8080);
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
				.queryParam("response_type", "code")
				.queryParam("client_id", clientId)
				.queryParam("redirect_uri", "http://localhost:8080/api/login-kakao/code")
				.queryParam("scope", "account_email")
				.build()
				.toUri();

		// When
		String getUrl = kakaoLoginService.getRedirectUrl(url);

		// Then
		assertEquals(uri.toString(), getUrl);
	}

	@Test
	@DisplayName("카카오서버로부터 토큰을 정상적으로 발급받을 수 있는지 테스트")
	void getTokenTest() {
		// Given
		String url = "http://localhost:8080/oauth/token"; // 카카오 API 를 대신할 URL 주소

		// Mock 서버가 리턴할 값 (카카오 API 가 리턴하는 값을 Mocking 한다.)
		KakaoLoginResponse response = KakaoLoginResponse.builder()
				.token_type("Bearer")
				.access_token("accessToken")
				.expires_in(1234)
				.refresh_token("refreshToken")
				.refresh_token_expires_in(4321)
				.build();

		// Mock 서버에 전달해야할 Header (카카오 API 와 동일하게 Mocking)
		Header header = new Header("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// Mock 서버에 전달해야할 Body (카카오 API 와 동일하게 Mocking)
		String body = "grant_type=authorization_code&client_id=" + clientId + "&redirect_uri=" + redirectUri
				+ "&code=testCode";
		mockServer.when(HttpRequest.request()
						.withMethod("POST")
						.withPath("/oauth/token")
						.withHeader(header)
						.withBody(body))
				.respond(HttpResponse.response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(gson.toJson(response))
						.withContentType(MediaType.APPLICATION_JSON));
		// When
		String getAccessToken = kakaoLoginService.getToken("testCode", url);

		// Then
		assertEquals("accessToken", getAccessToken);
	}

	@Test
	@DisplayName("카카오서버로부터 사용자 정보를 정상적으로 받아오는지 테스트")
	void getUserDetailsTest() {
		// Given
		String url = "http://localhost:8080/v2/user/me";

		// Mock 서버에 전달해야할 Header (카카오 API 와 동일하게 Mocking)
		Header header1 = new Header("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		Header header2 = new Header("Authorization", "Bearer testToken");
		String[] property_keys = {"kakao_account.profile", "kakao_account.name", "kakao_account.email"};
		Parameter parameter = new Parameter("property_keys", gson.toJson(property_keys));

		KakaoUserDetailsResponse response = KakaoUserDetailsResponse.builder()
				.id(111L)
				.kakao_account(KakaoAccount.builder()
						.email("test@email.com")
						.build())
				.build();

		mockServer.when(HttpRequest.request()
						.withMethod("GET")
						.withPath("/v2/user/me")
						.withHeader(header1)
						.withHeader(header2)
						.withQueryStringParameter(parameter))
				.respond(HttpResponse.response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(gson.toJson(response))
						.withContentType(MediaType.APPLICATION_JSON));

		// When
		String getEmail = kakaoLoginService.getUserDetails("testToken", url);

		// Then
		assertEquals("test@email.com", getEmail);
	}

	@Test
	@DisplayName("카카오 로그인에 성공하고 데이터베이스에 존재하는 계정에 정상적으로 로그인하는 테스트")
	void loginTest() {
		// Given
		String email = "test@email.com";
		userAccountRepository.save(UserAccount.builder()
				.userId(email)
				.userName(email)
				.phoneNum("000-1111-2222")
				.password(bCryptPasswordEncoder.encode("kakao-register" + email))
				.email(email)
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build());

		Header header = new Header("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		String body = "username=test%40email.com&password=kakao-registertest%40email.com";
		JwtTokenResponse response = JwtTokenResponse.builder()
				.token_type("bearer")
				.access_token("accessToken")
				.expires_in(1111L)
				.refresh_token("refreshToken")
				.refresh_token_expires_in(3333L)
				.build();

		mockServer.when(HttpRequest.request()
						.withMethod("POST")
						.withPath("/api/login")
						.withHeader(header)
						.withBody(body))
				.respond(HttpResponse.response()
						.withStatusCode(HttpStatus.OK.value())
						.withBody(gson.toJson(response))
						.withContentType(MediaType.APPLICATION_JSON));

		// When
		JwtTokenResponse getResponse = kakaoLoginService.login(email).getBody();

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
	@DisplayName("카카오 로그인에 성공하고 데이터베이스에 회원정보가 없다면 회원가입을 한다.")
	void registerTest() {
		// Given
		String email = "test@email.com";
		UserAccount user = UserAccount.builder()
				.userId(email)
				.userName(email)
				.phoneNum("000-1111-2222")
				.password("kakao-register" + email)
				.email(email)
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();

		// When
		kakaoLoginService.register(email);
		Optional<UserAccount> optionalGetUser = userAccountRepository.findByEmail(email);
		UserAccount getUser = optionalGetUser.get();

		// Then
		assertAll(
				() -> assertEquals(user.getUserId(), getUser.getUserId()),
				() -> assertEquals(user.getUserName(), getUser.getUserName()),
				() -> assertEquals(user.getPhoneNum(), getUser.getPhoneNum()),
				() -> assertTrue(bCryptPasswordEncoder.matches(user.getPassword(), getUser.getPassword())),
				() -> assertEquals(user.getEmail(), getUser.getEmail()),
				() -> assertEquals(user.getRole(), getUser.getRole())
		);
	}
}
