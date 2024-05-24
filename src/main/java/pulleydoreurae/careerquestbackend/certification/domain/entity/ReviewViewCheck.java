package pulleydoreurae.careerquestbackend.certification.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

/**
 * 자격증 후기 조회수 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Getter
@RedisHash(value = "ViewedReview", timeToLive = 60 * 10) // 10분 안에 같은 사용자가 조회한다면 조회수는 증가하지 않는다.
public class ReviewViewCheck {

	@Id
	private final String userId; // 로그인 상태라면 userId, 로그인상태가 아니라면 UUID 를 키값으로 사용한다.
	private final Long reviewId; // 해당 사용자가 봤던 postId 를 담는다.

	public ReviewViewCheck(String userId, Long reviewId) {
		this.userId = userId;
		this.reviewId = reviewId;
	}
}
