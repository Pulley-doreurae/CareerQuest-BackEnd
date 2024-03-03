package pulleydoreurae.careerquestbackend.mail.repository;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;

/**
 * Redis 에 이메일 인증 관련 Repository
 *
 * @author : hanjaeseong
 * @since : 2024/02/04
 */
@Deprecated
@Repository
public class MailRepository {

	private static final int EMAIL_VERIFICATION_LIMIT_IN_SECONDS = 180;
	private final StringRedisTemplate redisTemplate;

	private final List<String> keyIdentifier = Arrays.asList("-id", "-password", "-userName", "-phoneNum");

	public MailRepository(StringRedisTemplate redisMailTemplate) {
		this.redisTemplate = redisMailTemplate;
	}

	public void saveCertificationNumber(String email, String certificationNumber) {
		redisTemplate.opsForValue()
				.set(email, certificationNumber,
						Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));

	}

	public String getCertificationNumber(String email) {
		return redisTemplate.opsForValue().get(email);
	}

	public void removeCertification(String email) {
		redisTemplate.delete(email);

		String userIdKey = email + keyIdentifier.get(0);
		redisTemplate.delete(userIdKey);
		String passwordKey = email + keyIdentifier.get(1);
		redisTemplate.delete(passwordKey);
		String userNameKey = email + keyIdentifier.get(2);
		redisTemplate.delete(userNameKey);
		String phoneNumKey = email + keyIdentifier.get(3);
		redisTemplate.delete(phoneNumKey);
	}

	public void setUserAccount(String userId, String userName, String phoneNum, String email, String password) {
		try {
			String userIdKey = email + keyIdentifier.get(0);
			redisTemplate.opsForValue().set(userIdKey, userId, Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));

			String passwordKey = email + keyIdentifier.get(1);
			redisTemplate.opsForValue()
					.set(passwordKey, password, Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));

			String userNameKey = email + keyIdentifier.get(2);
			redisTemplate.opsForValue()
					.set(userNameKey, userName, Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));

			String phoneNumKey = email + keyIdentifier.get(3);
			redisTemplate.opsForValue()
					.set(phoneNumKey, phoneNum, Duration.ofSeconds(EMAIL_VERIFICATION_LIMIT_IN_SECONDS));

		} catch (Exception e) {
			// 적절한 예외 처리 로직 추가
			e.printStackTrace();
		}
	}

	public UserAccount getUserAccount(String email) {
		return UserAccount.builder()
				.userId(redisTemplate.opsForValue().get((email + keyIdentifier.get(0))))
				.password(redisTemplate.opsForValue().get((email + keyIdentifier.get(1))))
				.userName(redisTemplate.opsForValue().get((email + keyIdentifier.get(2))))
				.phoneNum(redisTemplate.opsForValue().get((email + keyIdentifier.get(3))))
				.email(email)
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();
	}

	public boolean hasKey(String email) {
		Boolean keyExists = redisTemplate.hasKey(email);
		return keyExists != null && keyExists;
	}

}
