package pulleydoreurae.chwijunjindan.auth.domain.jwt.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Redis 설정 파일
 *
 * @author : parkjihyeok
 * @since : 2024/01/21
 *
 */

@Configuration
public class RedisConfig {

	private final String redisHost;

	private final int redisPort;

	private final String redisMailHost;

	private final int redisMailPort;

	public RedisConfig(@Value("${spring.data.redis.host}") String redisHost,
		@Value("${spring.data.redis.port}") int redisPort,
		@Value("${spring.data.redis.host-mail}") String redisMailHost,
		@Value("${spring.data.redis.port-mail}") int redisMailPort) {
		this.redisHost = redisHost;
		this.redisPort = redisPort;
		this.redisMailHost = redisMailHost;
		this.redisMailPort = redisMailPort;
	}

	@Bean(name = "redisJWTConnectionFactory")
	public RedisConnectionFactory redisJWTConnectionFactory() {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost, redisPort);
		lettuceConnectionFactory.afterPropertiesSet(); // 수동으로 초기화
		return lettuceConnectionFactory;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<?, ?> redisJWTTemplate(
		@Qualifier("redisJWTConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

	@Bean(name = "redisMailConnectionFactory")
	public RedisConnectionFactory redisMailConnectionFactory() {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisMailHost, redisMailPort);
		lettuceConnectionFactory.afterPropertiesSet(); // 수동으로 초기화
		return lettuceConnectionFactory;
	}

	@Bean(name = "redisMailTemplate")
	public StringRedisTemplate redisMailTemplate(
		@Qualifier("redisMailConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
		stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
		return stringRedisTemplate;
	}

}
