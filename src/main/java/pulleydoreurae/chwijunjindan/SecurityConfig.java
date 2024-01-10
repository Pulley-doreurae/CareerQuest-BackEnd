package pulleydoreurae.chwijunjindan;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers(new AntPathRequestMatcher("/**")).permitAll())
			.headers((headers) -> headers
				.addHeaderWriter(new XFrameOptionsHeaderWriter(
					XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
			.formLogin((formLogin) -> formLogin
				.loginPage("/login")
				.defaultSuccessUrl("/")
				.usernameParameter("userId"))
			.logout((logout) -> logout
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login")
				.invalidateHttpSession(true))
		;
		return http.build();
	}

	@Bean
	public static BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
