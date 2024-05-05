package pulleydoreurae.careerquestbackend.auth.domain.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * 비밀번호 찾기 엔티티
 *
 * @author : hanjaeseong
 * @since : 2024/04/03
 */

@Getter
@RedisHash(value = "HelpUserPassword", timeToLive = 60 * 3)
public class HelpUserPassword {

	@Id
	private final String uuid;      // userId를 대신할 식별자
	private final String helpUserId;// 비밀번호 찾기를 요청한 userId

	public HelpUserPassword(String uuid, String helpUserId) {
		this.uuid = uuid;
		this.helpUserId = helpUserId;
	}

}