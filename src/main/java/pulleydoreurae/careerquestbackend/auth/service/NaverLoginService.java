package pulleydoreurae.careerquestbackend.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.*;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;

import java.net.URI;
import java.util.Optional;

/**
 * 구글 로그인을 담당하는 서비스
 *
 */
@Slf4j
@Service
public class NaverLoginService {

    private final String host;
    private final String clientId;
    private final String clientSecret;
    private final String redirect_uri;
    private final String response_type = "code";
    private final String state = "RAMDOM_STATE";
    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ObjectMapper objectMapper;

    public NaverLoginService( @Value("${LOGIN.NAVER_CLIENT_ID}") String clientId,
                              @Value("${LOGIN.NAVER_CLIENT_SECRET}") String clientSecret,
                              @Value("${LOGIN.NAVER_REDIRECT_URL}") String redirect_uri,
                              @Value("${LOGIN.HOST}") String host,
                              UserAccountRepository userAccountRepository,
                              BCryptPasswordEncoder bCryptPasswordEncoder,
                              ObjectMapper objectMapper) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirect_uri = redirect_uri;
        this.userAccountRepository = userAccountRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.objectMapper = objectMapper;
        this.host = host;
    }

    /**
     * 리다이렉션할 주소를 반환하는 메서드
     *
     * @param authUrl API 의 기본 url 을 전달받는다.
     * @return 네이버 로그인 창으로 리다이렉션시켜줄 주소를 반환한다.
     */
    public String getRedirectUrl(String authUrl) {

        URI uri = UriComponentsBuilder.fromUriString(authUrl)
                .queryParam("response_type", response_type)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirect_uri)
                .queryParam("state",state)
                .build()
                .toUri();

        return uri.toString();
    }

    /**
     * 사용자가 네이버로그인에 성공하면 성공한 정보를 바탕으로 네이버 서버에서 토큰을 받아온다.
     *
     * @param code 네이버 로그인에 성공하여 받아온 코드
     * @param tokenUrl  호출할 API 주소
     * @return 호출이 성공적으로 된다면 액세스토큰을 반환한다.
     */
    public String getToken(String code, String state, String tokenUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("state", state);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            NaverLoginResponse responseEntity = new RestTemplate().exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    NaverLoginResponse.class
            ).getBody();

            return responseEntity.getAccess_token();
        } catch (Exception e) {
            log.error("[로그인-네이버] : 유효하지않은 인증코드 --- {}", e.getMessage());
            return null;
        }
    }

    /**
     * 네이버 서버에서 사용자 정보를 받아오는 메서드
     *
     * @param token 네이버에서 발급해준 액세스 토큰을 전달받는다.
     * @param url   호출할 API 주소를 전달받는다.
     * @return 사용자 정보를 리턴한다. 현재는 이메일을 리턴한다.
     */
    public String getUserDetails(String token, String url) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = new RestTemplate().exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            NaverUserDetailsResponse naverUserDetailsResponse = objectMapper.readValue(response.getBody(), NaverUserDetailsResponse.class);

            return naverUserDetailsResponse.getResponse().getEmail();
        } catch (Exception e) {
            log.error("[로그인-네이버] : 유효하지않은 액세스토큰 --- {}", e.getMessage());
            return null;
        }
    }

    /**
     * 받아온 네이버 정보로 로그인을 시도하는 메서드
     *
     * @param email 네이버 서버로부터 받아온 정보를 전달받아
     * @return 로그인에 성공하면 성공에 대한 토큰을 리턴한다.
     */
    public ResponseEntity<JwtTokenResponse> login(String email) {
        Optional<UserAccount> user = userAccountRepository.findByEmail(email);

        if (user.isEmpty()) {
            log.info("[로그인-네이버] : 데이터베이스에서 [{}] 의 정보를 찾을 수 없으므로 회원가입 진행", email);
            register(email);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", email);
        body.add("password", "naver-register" + email);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            JwtTokenResponse getResponse = new RestTemplate().exchange(
                    host + "/api/login",
                    HttpMethod.POST,
                    entity,
                    JwtTokenResponse.class
            ).getBody();

            return ResponseEntity.status(HttpStatus.OK).body(getResponse);
        } catch (Exception e) {
            log.error("[로그인-네이버] : 서비스에 로그인할 수 없음 --- {}", e.getMessage());
            return null;
        }

    }

    /**
     * 네이버 서버로부터 받아온 정보로 회원가입 후 로그인으로 전달한다.
     *
     * @param email 네이버 서버에서 받아온 이메일을 전달받아 회원가입을 진행한다.
     */
    public void register(String email) {
        UserAccount user = UserAccount.builder()
                .userId(email)
                .userName(email)
                .password(bCryptPasswordEncoder.encode("naver-register" + email))
                .email(email)
                .phoneNum("000-1111-2222")
                .birth("00-01-01")
                .gender("M")
                .role(UserRole.ROLE_TEMPORARY_USER)
                .build();
        userAccountRepository.save(user);
        log.info("[로그인-네이버] : 사용자 [{}] 회원가입 성공", email);
    }

}
