package pulleydoreurae.chwijunjindan.auth.domain.jwt.entity;

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
	private final String refreshToken;
	private final String userId;

	public JwtRefreshToken(String refreshToken, String userId) {
		this.refreshToken = refreshToken;
		this.userId = userId;
	}
}
