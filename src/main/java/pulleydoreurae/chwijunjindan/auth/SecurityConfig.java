package pulleydoreurae.chwijunjindan.auth;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 스프링 시큐리티 설정 클래스
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests((auth) ->
				auth
						.requestMatchers("/login", "/register", "/").permitAll()    // 로그인, 회원가입, 루트 페이지는 모두 접근 가능
						.requestMatchers("/h2-console/**").permitAll()    // h2 를 사용하기 위한 설정
						.requestMatchers(PathRequest.toH2Console()).permitAll()    // h2 를 사용하기 위한 설정
						.anyRequest().authenticated());    // 나머지는 인증된 사용자만 접근가능

		http.headers((auth) -> auth
				.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));    // h2 를 사용하기 위한 설정

		http.csrf((csrf) -> csrf.disable());    // csrf 는 우선 사용하지 않음.

		http.formLogin((login) -> login
				.loginProcessingUrl("/login"));    // 시큐리티의 기본 로그인 사용

		return http.build();
	}
}
