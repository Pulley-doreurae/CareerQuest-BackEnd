package pulleydoreurae.careerquestbackend.community.domain.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;

/**
 * 게시글 중복 조회를 막기위한 Redis 객체
 *
 * @author : parkjihyeok
 * @since : 2024/04/05
 */
@Getter
@RedisHash(value = "ViewedPost", timeToLive = 60 * 10) // 10분 안에 같은 사용자가 조회한다면 조회수는 증가하지 않는다.
public class PostViewCheck {

	@Id
	private final String userId; // 로그인 상태라면 userId, 로그인상태가 아니라면 UUID 를 키값으로 사용한다.
	private final Long postId; // 해당 사용자가 봤던 postId 를 담는다.

	public PostViewCheck(String userId, Long postId) {
		this.userId = userId;
		this.postId = postId;
	}
}
