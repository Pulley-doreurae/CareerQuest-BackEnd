package pulleydoreurae.careerquestbackend.certification.domain.entity;

import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostViewCheck;

/**
 * 자격증 조회수 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
public class CertificationReviewViewCheck extends PostViewCheck {
	public CertificationReviewViewCheck(String userId, Long postId) {
		super(userId, postId);
	}
}
