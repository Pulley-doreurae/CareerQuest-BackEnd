package pulleydoreurae.careerquestbackend.mail.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

/**
 * Redis 에 userId 를 선점하기 위해 저장하는 객체
 *
 * @author : parkjihyeok
 * @since : 2024/03/03
 */
@Getter
@RedisHash(value = "UserInfoUserId", timeToLive = 60 * 10) // 이메일 인증객체의 유효기간은 10분
public class UserInfoUserId {

	@Id
	private final String userId;

	public UserInfoUserId(String userId) {
		this.userId = userId;
	}
}
