package pulleydoreurae.careerquestbackend.certification.domain.entity;

import java.util.List;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;

/**
 * 자격증 합격 후기 / Tip Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Entity
@Getter
@NoArgsConstructor
public class CertificationReview extends Post {

	// 카테고리로 자격증 구분하기
	@Builder
	public CertificationReview(Long id, UserAccount userAccount,
			String title, String content, Long view,
			List<PostLike> postLikes,
			Long category) {
		super(id, userAccount, title, content, view, postLikes, category, null);
	}
}
