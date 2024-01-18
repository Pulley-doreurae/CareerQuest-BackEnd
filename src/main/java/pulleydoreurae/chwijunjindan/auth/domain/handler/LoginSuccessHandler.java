package pulleydoreurae.chwijunjindan.auth.domain.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.JwtTokenProvider;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.dto.JwtTokenResponse;

/**
 * 로그인 성공 핸들러 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/01/17
 */
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final Gson gson;

	@Autowired
	public LoginSuccessHandler(JwtTokenProvider jwtTokenProvider, Gson gson) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.gson = gson;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		// TODO: 2024/01/18 JWT 토큰에 관한 처리추가해서 세션을 사용하지 않도록 수정하기
		//  토큰에 대한 값을 좀 더 추가해야 함
		String accessToken = jwtTokenProvider.createAccessToken(SecurityContextHolder.getContext().getAuthentication().getName());
		JwtTokenResponse jwtTokenResponse = JwtTokenResponse.builder()
				.token_type("bearer")
				.access_token(accessToken)
				.build();

		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		response.getWriter().write(gson.toJson(jwtTokenResponse));
	}
}
