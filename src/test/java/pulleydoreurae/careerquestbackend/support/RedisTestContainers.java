package pulleydoreurae.careerquestbackend.support;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * 테스트 환경에서 사용할 Redis 컨테이너를 생성하고 실행하는 클래스 / TestContainer 를 사용한다.
 *
 * @author : parkjihyeok
 * @since : 2024/03/14
 */
@Configuration
public class RedisTestContainers {

	private static final GenericContainer redis;

	static {
		redis = new GenericContainer(DockerImageName.parse("redis:6-alpine"))
				.withExposedPorts(6379);
		redis.start();
		System.setProperty("spring.data.redis.host", redis.getHost());
		System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(6379)));
	}
}
