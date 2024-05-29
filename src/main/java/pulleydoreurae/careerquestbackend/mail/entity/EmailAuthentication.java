package pulleydoreurae.careerquestbackend.mail.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

/**
 * Redis 에 저장할 이메일인증 객체
 *
 * @author : parkjihyeok
 * @since : 2024/03/02
 */
@Getter
@RedisHash(value = "EmailAuthentication", timeToLive = 60 * 10) // 이메일 인증객체의 유효기간은 10분
public class EmailAuthentication {

	@Id
	private final String email;
	private final String userId;
	private final String userName;
	private final String phoneNum;
	private final String password;
	private final String birth;
	private final String gender;
	private final Boolean isMarketed;
	private final String code;

	public EmailAuthentication(String email, String userId, String userName, String phoneNum, String password,
		String birth, String gender, String code, Boolean isMarketed) {
		this.email = email;
		this.userId = userId;
		this.userName = userName;
		this.phoneNum = phoneNum;
		this.password = password;
		this.birth = birth;
		this.gender = gender;
		this.isMarketed = isMarketed;
		this.code = code;
	}
}
