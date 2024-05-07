package pulleydoreurae.careerquestbackend.basiccommunity.domain.entity;

import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostViewCheck;

/**
 * 게시글 중복 조회를 막기위한 Redis 객체
 *
 * @author : parkjihyeok
 * @since : 2024/04/05
 */
public class BasicPostViewCheck extends PostViewCheck {
	public BasicPostViewCheck(String userId, Long postId) {
		super(userId, postId);
	}
}
