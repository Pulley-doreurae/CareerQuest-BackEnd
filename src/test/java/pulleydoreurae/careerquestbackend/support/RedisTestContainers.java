package pulleydoreurae.careerquestbackend.support;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * 테스트 환경에서 사용할 Redis 컨테이너를 생성하고 실행하는 클래스 / TestContainer 를 사용한다.
 *
 * @author : parkjihyeok
 * @since : 2024/03/14
 */
@Configuration
@Testcontainers
public class RedisTestContainers {

	private static final String REDIS_DOCKER_IMAGE = "redis:6-alpine";

	static {    // 위의 이미지로 컨테이너 생성
		GenericContainer<?> REDIS_CONTAINER =
				new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
						.withExposedPorts(6379) // 컨테이너의 6379 포트 오픈
						.withReuse(true); // 테스트 사이에서 컨테이너 재사용하기

		REDIS_CONTAINER.start();    // 레디스 컨테이너 실행

		// 컨테이너에 올라간 레디스와 매핑시켜준다.
		System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
	}
}
