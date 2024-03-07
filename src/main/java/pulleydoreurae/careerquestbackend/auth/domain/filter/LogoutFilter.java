package pulleydoreurae.careerquestbackend.auth.domain.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.repository.JwtAccessTokenRepository;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.repository.JwtRefreshTokenRepository;

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
			// 헤더에서 사용자 id 를 불러와 redis 에 저장된 토큰들 삭제
			String userId = request.getHeader("userId");

			jwtAccessTokenRepository.deleteById(userId);
			jwtRefreshTokenRepository.deleteById(userId);

			// 응답을 JSON 형태로 하기위한 hashmap
			HashMap<String, String> msg = new HashMap<>();
			msg.put("msg", "로그아웃에 성공하였습니다.");
			response.setStatus(HttpStatus.OK.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write(gson.toJson(msg));
			return;
		}
		filterChain.doFilter(request, response);
	}
}
