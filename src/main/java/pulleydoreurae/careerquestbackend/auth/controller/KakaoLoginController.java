package pulleydoreurae.careerquestbackend.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.LoginFailResponse;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.careerquestbackend.auth.service.KakaoLoginService;

/**
 * 카카오 로그인을 담당하는 컨트롤러
 *
 * @author : parkjihyeok
 * @since : 2024/01/15
 */
@RequestMapping("/api")
@Slf4j
@Controller
public class KakaoLoginController {

	private final KakaoLoginService kakaoLoginService;
	private final String authUrl;
	private final String tokenUrl;
	private final String getUserDetailUrl;

	@Autowired
	public KakaoLoginController(KakaoLoginService kakaoLoginService,
			@Value("${LOGIN.KAKAO_AUTH_URL}") String authUrl,
			@Value("${LOGIN.KAKAO_TOKEN_URL}") String tokenUrl,
			@Value("${LOGIN.KAKAO_GET_USERDETAIL_URL}") String getUserDetailUrl) {
		this.kakaoLoginService = kakaoLoginService;
		this.authUrl = authUrl;
		this.tokenUrl = tokenUrl;
		this.getUserDetailUrl = getUserDetailUrl;
	}

	@GetMapping("/login-kakao")
	public String kakaoLogin() {

		String url = kakaoLoginService.getRedirectUrl(authUrl);

		log.info("[로그인-카카오] : 로그인창으로 이동");

		return "redirect:" + url;
	}

	@GetMapping("/login-kakao/code")
	@ResponseBody
	public ResponseEntity<?> kakaoLoginCode(@RequestParam(value = "code") String code) {

		log.info("[로그인-카카오] : 카카오 로그인 성공");
		String token = kakaoLoginService.getToken(code, tokenUrl);    // 카카오 액세스 토큰
		if (token != null) { // 정상적인 카카오 액세스 토큰을 받아왔다면
			String userDetails = kakaoLoginService.getUserDetails(token, getUserDetailUrl); // 액세스 토큰으로 서버로부터 받아온 사용자 정보
			if (userDetails != null) {
				ResponseEntity<JwtTokenResponse> response = kakaoLoginService.login(userDetails);

				if (response == null) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(LoginFailResponse.builder()
									.code(HttpStatus.BAD_REQUEST.toString())
									.error("유효하지 않은 카카오 액세스토큰")
									.build());
				}
				// TODO: 2024/01/24 받아온 사용자 정보가 email 보다 늘어난다면 dto 로 만들기
				return response;
			}

			// 유효하지 않은 액세스토큰이라서 사용자 정보를 받지 못한경우
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(LoginFailResponse.builder()
							.code(HttpStatus.BAD_REQUEST.toString())
							.error("유효하지 않은 카카오 액세스토큰")
							.build());
		}
		// 유효하지 않은 코드라 액세스 토큰을 받지 못한경우
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(LoginFailResponse.builder()
						.code(HttpStatus.BAD_REQUEST.toString())
						.error("유효하지 않은 카카오 인증코드")
						.build());
	}
}
