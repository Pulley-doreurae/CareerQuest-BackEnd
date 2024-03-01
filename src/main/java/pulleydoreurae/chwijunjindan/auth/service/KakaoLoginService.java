package pulleydoreurae.chwijunjindan.auth.service;

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.chwijunjindan.auth.domain.UserRole;
import pulleydoreurae.chwijunjindan.auth.domain.dto.KakaoLoginResponse;
import pulleydoreurae.chwijunjindan.auth.domain.dto.KakaoUserDetailsResponse;
import pulleydoreurae.chwijunjindan.auth.domain.entity.UserAccount;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 카카오 로그인을 담당하는 서비스
 *
 * @author : parkjihyeok
 * @since : 2024/01/15
 */
@Slf4j
@Service
public class KakaoLoginService {

	private final String clientId;
	private final String redirect_uri;
	private final String response_type = "code";
	private final String grant_type = "authorization_code";
	private final UserAccountRepository userAccountRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public KakaoLoginService(@Value("${LOGIN.KAKAO_API_KEY}") String clientId,
			@Value("${LOGIN.REDIRECT_URL}") String redirectUri,
			UserAccountRepository userAccountRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.clientId = clientId;
		this.redirect_uri = redirectUri;
		this.userAccountRepository = userAccountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	/**
	 * 리다이렉션할 주소를 반환하는 메서드
	 *
	 * @param url API 의 기본 url 을 전달받는다.
	 * @return 카카오 로그인 창으로 리다이렉션시켜줄 주소를 반환한다.
	 */
	public String getRedirectUrl(String url) {

		URI uri = UriComponentsBuilder.fromUriString(url)
				.queryParam("response_type", response_type)
				.queryParam("client_id", clientId)
				.queryParam("redirect_uri", redirect_uri)
				.queryParam("scope", "account_email")
				// TODO: 2024/01/22 동의 내역 추가하기
				.build()
				.toUri();

		return uri.toString();
	}

	/**
	 * 사용자가 카카오로그인에 성공하면 성공한 정보를 바탕으로 카카오 서버에서 토큰을 받아온다.
	 *
	 * @param code 카카오 로그인에 성공하여 받아온 코드
	 * @param url  호출할 API 주소
	 * @return 호출이 성공적으로 된다면 액세스토큰을 반환한다.
	 */
	public String getToken(String code, String url) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", grant_type);
		body.add("client_id", clientId);
		body.add("redirect_uri", redirect_uri);
		body.add("code", code);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		try {
			KakaoLoginResponse response = new RestTemplate().exchange(
					url,
					HttpMethod.POST,
					entity,
					KakaoLoginResponse.class
			).getBody();

			return response.getAccess_token();
		} catch (Exception e) {
			log.error("[로그인-카카오] : 유효하지않은 인증코드 --- {}", e.getMessage());
			return null;
		}
	}

	/**
	 * 카카오 서버에서 사용자 정보를 받아오는 메서드
	 *
	 * @param token 카카오에서 발급해준 액세스 토큰을 전달받는다.
	 * @param url   호출할 API 주소를 전달받는다.
	 * @return 사용자 정보를 리턴한다. 현재는 이메일을 리턴한다.
	 */
	// TODO: 2024/01/24 카카오 서버로부터 받아올 정보를 수정해야 함.
	public String getUserDetails(String token, String url) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization", "Bearer " + token);

		String[] property_keys = {"kakao_account.profile", "kakao_account.name", "kakao_account.email"};
		Gson gson = new Gson();
		URI uri = UriComponentsBuilder
				.fromUriString(url)
				.queryParam("property_keys", gson.toJson(property_keys))
				.encode()
				.build()
				.toUri();

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

		try {
			KakaoUserDetailsResponse response = new RestTemplate().exchange(
					uri,
					HttpMethod.GET,
					entity,
					KakaoUserDetailsResponse.class
			).getBody();

			return response.getKakao_account().getEmail();
		} catch (Exception e) {
			log.error("[로그인-카카오] : 유효하지않은 액세스토큰 --- {}", e.getMessage());
			return null;
		}
	}

	/**
	 * 받아온 카카오 정보로 로그인을 시도하는 메서드
	 *
	 * @param email 카카오 서버로부터 받아온 정보를 전달받아
	 * @return 로그인에 성공하면 성공에 대한 토큰을 리턴한다.
	 */
	public ResponseEntity<JwtTokenResponse> login(String email) {
		Optional<UserAccount> user = userAccountRepository.findByEmail(email);

		if (user.isEmpty()) {
			log.info("[로그인-카카오] : 데이터베이스에서 [{}] 의 정보를 찾을 수 없으므로 회원가입 진행", email);
			register(email);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", email);
		body.add("password", "kakao-register" + email);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		log.info("[로그인-카카오] : 취준진담 서비스에 사용자 [{}] 로그인 성공", email);

		// TODO: 2024/01/24 주소 수정 및 실패에 대한 처리를 추가하기
		try {
			JwtTokenResponse getResponse = new RestTemplate().exchange(
					"http://localhost:8080/api/login",
					HttpMethod.POST,
					entity,
					JwtTokenResponse.class
			).getBody();

			return ResponseEntity.status(HttpStatus.OK).body(getResponse);
		} catch (Exception e) {
			log.error("[로그인-카카오] : 서비스에 로그인할 수 없음 --- {}", e.getMessage());
			return null;
		}
	}

	/**
	 * 카카오 서버로부터 받아온 정보로 회원가입 후 로그인으로 전달한다.
	 *
	 * @param email 카카오 서버에서 받아온 이메일을 전달받아 회원가입을 진행한다.
	 */
	// TODO: 2024/01/24 받아온 정보를 바탕으로 회원가입 로직을 수정해야 함.
	public void register(String email) {
		UserAccount user = UserAccount.builder()
				.userId(email)
				.userName(email)
				.phoneNum("000-1111-2222")
				.password(bCryptPasswordEncoder.encode("kakao-register" + email))
				.email(email)
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();
		userAccountRepository.save(user);
		log.info("[로그인-카카오] : 사용자 [{}] 회원가입 성공", email);
	}
}
