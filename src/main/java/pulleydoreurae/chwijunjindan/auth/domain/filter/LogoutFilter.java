package pulleydoreurae.chwijunjindan.auth.domain.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.repository.JwtAccessTokenRepository;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.repository.JwtRefreshTokenRepository;

@Slf4j
public class LogoutFilter extends OncePerRequestFilter {

	private final JwtAccessTokenRepository jwtAccessTokenRepository;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;
	private final Gson gson = new Gson();

	public LogoutFilter(JwtAccessTokenRepository jwtAccessTokenRepository,
			JwtRefreshTokenRepository jwtRefreshTokenRepository) {
		this.jwtAccessTokenRepository = jwtAccessTokenRepository;
		this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String url = request.getRequestURI();
		if (url.equals("/api/logout")) {
			// 헤더에서 액세스 토큰과 리프레시 토큰을 가져와서 Redis 에서 삭제하여 로그아웃을 구현
			String accessToken = request.getHeader("Authorization");
			accessToken = accessToken.replace("Bearer ", "");
			String refreshToken = request.getHeader("RefreshToken");

			jwtAccessTokenRepository.deleteById(accessToken);
			jwtRefreshTokenRepository.deleteById(refreshToken);

			response.setStatus(HttpStatus.OK.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write(gson.toJson("로그아웃에 성공하였습니다."));
			return;
		}
		filterChain.doFilter(request, response);
	}
}
