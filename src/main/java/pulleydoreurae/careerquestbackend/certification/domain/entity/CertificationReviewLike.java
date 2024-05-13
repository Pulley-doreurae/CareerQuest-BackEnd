package pulleydoreurae.careerquestbackend.certification.domain.entity;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;

/**
 * 자격증 후기 좋아요 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Entity
@Getter
@NoArgsConstructor
public class CertificationReviewLike extends PostLike {

	@Builder
	public CertificationReviewLike(Long id, Post post, UserAccount userAccount) {
		super(id, post, userAccount);
	}
}
