package pulleydoreurae.chwijunjindan.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 순환참조를 없애기 위해 인코더를 분리하여 만든 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/01/17
 */
@Configuration
public class PasswordEncoderConfig {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
