package pulleydoreurae.chwijunjindan.auth.domain.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 로그인 성공 핸들러 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/01/17
 */
// TODO: 2024/01/17 로그인 성공에 관한 처리 추가하기 (아마 JWT 토큰발급)
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		setDefaultTargetUrl("/api/login-success");
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
