package pulleydoreurae.careerquestbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * Redis 설정 파일
 *
 * @author : parkjihyeok
 * @since : 2024/01/21
 */

@Configuration
public class RedisConfig {

	private final String redisHost;

	private final int redisPort;

	public RedisConfig(@Value("${spring.data.redis.host}") String redisHost,
			@Value("${spring.data.redis.port}") int redisPort) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

}
