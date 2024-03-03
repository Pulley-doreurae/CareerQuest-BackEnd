package pulleydoreurae.careerquestbackend.auth.domain.jwt.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

/**
 * Redis 에 저장할 리프레시 토큰 객체
 *
 * @author : parkjihyeok
 * @since : 2024/01/21
 */
@Getter
@RedisHash(value = "JwtRefreshToken", timeToLive = 60 * 60 * 24 * 10) // 리프레시 토큰의 유효기간은 10일
public class JwtRefreshToken {

	@Id
	private final String userId;
	private final String refreshToken;

	public JwtRefreshToken(String userId, String refreshToken) {
		this.userId = userId;
		this.refreshToken = refreshToken;
	}
}
