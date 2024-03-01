package pulleydoreurae.careerquestbackend.auth.domain.jwt.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

/**
 * Redis 에 저장할 액세스 토큰 객체
 *
 * @author : parkjihyeok
 * @since : 2024/01/22
 */
@Getter
@RedisHash(value = "JwtAccessToken", timeToLive = 60 * 10) // 액세스 토큰의 유효기간은 10분
public class JwtAccessToken {

	@Id
	private final String userId;
	private final String accessToken;

	public JwtAccessToken(String userId, String accessToken) {
		this.userId = userId;
		this.accessToken = accessToken;
	}
}
