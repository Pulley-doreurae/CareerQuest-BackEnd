package pulleydoreurae.chwijunjindan.auth.domain.jwt.entity;

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
	private final String accessToken;
	private final String userId;

	public JwtAccessToken(String accessToken, String userId) {
		this.accessToken = accessToken;
		this.userId = userId;
	}
}
