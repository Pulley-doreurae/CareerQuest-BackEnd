package pulleydoreurae.chwijunjindan.auth.domain.jwt.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.chwijunjindan.auth.domain.CustomUserDetails;
import pulleydoreurae.chwijunjindan.auth.domain.entity.UserAccount;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.JwtTokenProvider;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtAccessToken;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtRefreshToken;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.repository.JwtAccessTokenRepository;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.repository.JwtRefreshTokenRepository;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 토큰을 확인하는 필터
 *
 * @author : parkjihyeok
 * @since : 2024/01/18
 */
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final UserAccountRepository userAccountRepository;
	private final JwtRefreshTokenRepository jwtRefreshTokenRepository;
	private final JwtAccessTokenRepository jwtAccessTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final Gson gson = new Gson();

	@Autowired
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
			UserAccountRepository userAccountRepository, JwtRefreshTokenRepository jwtRefreshTokenRepository,
			JwtAccessTokenRepository jwtAccessTokenRepository, JwtTokenProvider jwtTokenProvider) {
		super(authenticationManager);
		this.userAccountRepository = userAccountRepository;
		this.jwtRefreshTokenRepository = jwtRefreshTokenRepository;
		this.jwtAccessTokenRepository = jwtAccessTokenRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// 헤더에 토큰이 있는지 검사
		String authorization = request.getHeader("Authorization");
		String refreshToken = request.getHeader("RefreshToken");
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}
		// 헤더에 액세스토큰이 존재한다면 토큰 가져오기
		String accessToken = request.getHeader("Authorization")
				.replace("Bearer ", "");

		Optional<JwtAccessToken> getAccessToken = jwtAccessTokenRepository.findById(accessToken);

		// 액세스토큰이 유효하다면
		if (getAccessToken.isPresent()) {

			// 토큰에서 사용자 아이디 가져오기
			String username = getAccessToken.get().getUserId();

			// 사용자가 존재한다면 인증객체를 만들어 저장
			if (username != null) {
				UserAccount userAccount = userAccountRepository.findByUserId(username)
						.orElseThrow();
				// TODO: 2024/01/21 실패하는 경우에 대한 처리 수정하기

				CustomUserDetails userDetails = new CustomUserDetails(userAccount);
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authentication);
				chain.doFilter(request, response);
			}
		} else if (getAccessToken.isEmpty() && refreshToken == null) { // 액세스 토큰이 유효하지 않고 리프레시 토큰이 없다면 400 에러를 리턴
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write(gson.toJson("Authorization 이 유효하지 않습니다."));
			return;
		}

		if (refreshToken != null) {    // 리프레시 토큰이 전달되었다면

			Optional<JwtRefreshToken> jwtRefreshToken = jwtRefreshTokenRepository.findById(refreshToken);
			if (jwtRefreshToken.isEmpty()) { // 리프레시 토큰이 만료되어 없다면
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setContentType("application/json;charset=UTF-8");
				response.setCharacterEncoding(StandardCharsets.UTF_8.name());
				response.getWriter().write(gson.toJson("RefreshToken 이 유효하지 않습니다. 다시 로그인해주세요."));
				return;
			}
			// 리프레시 토큰이 유효하다면 새로운 액세스 토큰을 반환
			response.setStatus(HttpStatus.OK.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write(gson.toJson(jwtTokenProvider.refreshAccessToken(refreshToken)));
		}
	}
}
