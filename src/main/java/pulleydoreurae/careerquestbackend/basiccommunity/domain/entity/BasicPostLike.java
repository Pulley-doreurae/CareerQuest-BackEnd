package pulleydoreurae.careerquestbackend.basiccommunity.domain.entity;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;

/**
 * 게시글 좋아요 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/04/02
 */
@Entity
@Getter
@NoArgsConstructor
public class BasicPostLike extends PostLike {

	@Builder
	public BasicPostLike(Long id, Post post, UserAccount userAccount) {
		super(id, post, userAccount);
	}
}
