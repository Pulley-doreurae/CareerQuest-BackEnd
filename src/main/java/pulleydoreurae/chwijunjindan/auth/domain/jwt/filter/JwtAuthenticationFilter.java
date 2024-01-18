package pulleydoreurae.chwijunjindan.auth.domain.jwt.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.chwijunjindan.auth.domain.CustomUserDetails;
import pulleydoreurae.chwijunjindan.auth.domain.UserAccount;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.JwtTokenProvider;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 토큰을 확인하는 필터
 *
 * @author : parkjihyeok
 * @since : 2024/01/18
 */
// TODO: 2024/01/18 코드 수정 필요 (토큰 확인필터)
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	private final UserAccountRepository userAccountRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
			UserAccountRepository userAccountRepository, JwtTokenProvider jwtTokenProvider) {
		super(authenticationManager);
		this.userAccountRepository = userAccountRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// 헤더에 토큰이 있는지 검사
		String header = request.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}
		// 헤더에 토큰이 존재한다면 우선 출력하고 토큰 가져오기
		System.out.println("header : " + header);
		String token = request.getHeader("Authorization")
				.replace("Bearer ", "");

		// 토큰이 유효하다면
		if (jwtTokenProvider.validateToken(token)) {

			// 토큰에서 사용자 아이디 가져오기
			String username = jwtTokenProvider.getUserPk(token);

			// 사용자가 존재한다면 인증객체를 만들어 저장
			if (username != null) {
				UserAccount userAccount = userAccountRepository.findByUserId(username)
						.orElseThrow();

				CustomUserDetails userDetails = new CustomUserDetails(userAccount);
				Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());

				SecurityContextHolder.getContext().setAuthentication(authentication);

			}
		}
		chain.doFilter(request, response);
	}
}
