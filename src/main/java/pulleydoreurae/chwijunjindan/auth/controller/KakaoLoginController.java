package pulleydoreurae.chwijunjindan.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.chwijunjindan.auth.service.KakaoLoginService;

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

	@Autowired
	public KakaoLoginController(KakaoLoginService kakaoLoginService) {
		this.kakaoLoginService = kakaoLoginService;
	}

	@GetMapping("/login-kakao")
	public String kakaoLogin() {

		String url = kakaoLoginService.getRedirectUrl();

		log.info("[로그인-카카오] : 로그인창으로 이동");

		return "redirect:" + url;
	}

	@GetMapping("/login-kakao/code")
	@ResponseBody
	public ResponseEntity<JwtTokenResponse> kakaoLoginCode(@RequestParam(value = "code") String code) {

		log.info("[로그인-카카오] : 카카오 로그인 성공");
		String token = kakaoLoginService.getToken(code);	// 카카오 액세스 토큰
		String userDetails = kakaoLoginService.getUserDetails(token); // 액세스 토큰으로 서버로부터 받아온 사용자 정보
		// TODO: 2024/01/24 받아온 사용자 정보가 email 보다 늘어난다면 dto 로 만들기
		return kakaoLoginService.login(userDetails);
	}
}
