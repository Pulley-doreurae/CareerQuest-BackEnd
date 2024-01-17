package pulleydoreurae.chwijunjindan.auth.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class LoginController {

	@RequestMapping("/login-success")
	public String loginSuccess() {
		// TODO: 2024/01/17 로그인 성공에 대한 처리 추가하기
		return "로그인 성공";
	}
}
