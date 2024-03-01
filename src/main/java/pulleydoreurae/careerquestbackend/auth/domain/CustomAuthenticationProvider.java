package pulleydoreurae.careerquestbackend.auth.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.service.CustomUserDetailsService;

/**
 * 커스텀 AuthenticationProvider
 * 주의) 컴포넌트로 등록하면 따로 등록하지 않아도 자동으로 AuthenticationManger 에 등록이 된다.
 *
 * @author : parkjihyeok
 * @since : 2024/01/16
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final CustomUserDetailsService customUserDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public CustomAuthenticationProvider(CustomUserDetailsService customUserDetailsService,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.customUserDetailsService = customUserDetailsService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		try {    // 사용자 정보를 찾을 수 없는 경우
			CustomUserDetails userDetails = (CustomUserDetails)customUserDetailsService.loadUserByUsername(username);
			if (bCryptPasswordEncoder.matches(password, userDetails.getPassword())) { // 비밀번호가 일치한다면
				log.info("[로그인] : {} 가 로그인에 성공했습니다.", username);
				return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
			}
		} catch (UsernameNotFoundException e) {
			log.error("[로그인] : {} username = {}", e.getMessage(), username);
			throw new UsernameNotFoundException("사용자를 찾을 수 없음");
		}
		log.error("[로그인] : {} 가 비밀번호가 맞지않아 로그인에 실패했습니다.", username);
		throw new BadCredentialsException("비밀번호가 맞지않음");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
