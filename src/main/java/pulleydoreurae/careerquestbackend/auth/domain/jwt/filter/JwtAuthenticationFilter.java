package pulleydoreurae.careerquestbackend.auth.domain.jwt.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.careerquestbackend.auth.domain.CustomUserDetails;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.JwtTokenProvider;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.entity.JwtAccessToken;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.entity.JwtRefreshToken;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.repository.JwtAccessTokenRepository;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.repository.JwtRefreshTokenRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;

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
		String userId = request.getHeader("userId"); // 사용자의 id 도 함께 전달받기
		if (authorization == null || !authorization.startsWith("Bearer ") || userId == null) {
			chain.doFilter(request, response);
			return;
		}
		// 헤더에 액세스토큰이 존재한다면 토큰 가져오기
		String accessToken = request.getHeader("Authorization")
				.replace("Bearer ", "");

		Optional<JwtAccessToken> getAccessToken = jwtAccessTokenRepository.findById(userId);

		// 요청한 사용자정보로 redis 에 accessToken 이 존재할 때
		if (getAccessToken.isPresent()) {
			// 토큰에서 사용자 아이디 가져오기
			String username = getAccessToken.get().getUserId();

			// 전달받은 사용자 id 와 토큰에서 꺼낸 사용자 id 가 같고 요청한 엑세스토큰이 redis 에 저장된 값과 일치할 때
			if (username.equals(userId) && accessToken.equals(getAccessToken.get().getAccessToken())) {
				UserAccount userAccount = userAccountRepository.findByUserId(username) // 해당 사용자 id 로 사용자 정보가 있는지 찾기
						.orElseThrow(() -> {
							// 로그인에 성공한 사람들(사용자 정보가 있는 경우)만 토큰을 부여받기 때문에 예외를 던진다면 데이터베이스 오류이거나 로그인 로직에 버그가 있는 것임.
							return new UsernameNotFoundException("해당 사용자 정보를 찾을 수 없습니다., DB 오류 or 로그인 로직 버그");
						});

				CustomUserDetails userDetails = new CustomUserDetails(userAccount);
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authentication);
				chain.doFilter(request, response);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setContentType("application/json;charset=UTF-8");
				response.setCharacterEncoding(StandardCharsets.UTF_8.name());
				response.getWriter().write(gson.toJson("Authorization 이 유효하지 않습니다."));
				return;
			}
		} else if (refreshToken == null) { // 액세스 토큰이 유효하지 않고 리프레시 토큰이 없다면 400 에러를 리턴
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write(gson.toJson("Authorization 이 유효하지 않습니다."));
			return;
		}

		if (refreshToken != null) {    // 리프레시 토큰이 전달되었다면

			Optional<JwtRefreshToken> jwtRefreshToken = jwtRefreshTokenRepository.findById(userId);
			if (jwtRefreshToken.isPresent() && refreshToken.equals(jwtRefreshToken.get().getRefreshToken())) {
				// 리프레시 토큰이 저장되어 있고 전달받은 리프레시 토큰이 일치한다면 새로운 액세스 토큰을 반환
				response.setStatus(HttpStatus.OK.value());
				response.setContentType("application/json;charset=UTF-8");
				response.setCharacterEncoding(StandardCharsets.UTF_8.name());
				response.getWriter().write(gson.toJson(jwtTokenProvider.refreshAccessToken(refreshToken)));
				return;
			}
			// 리프레시 토큰이 만료되어 없거나 일치하지 않는다면
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setContentType("application/json;charset=UTF-8");
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
			response.getWriter().write(gson.toJson("RefreshToken 이 유효하지 않습니다. 다시 로그인해주세요."));
		}
	}
}
