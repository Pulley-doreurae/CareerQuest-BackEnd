package pulleydoreurae.careerquestbackend.basiccommunity.domain.entity;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;

/**
 * 댓글 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@Entity
@Getter
@NoArgsConstructor
public class BasicComment extends Comment {

	@Builder
	public BasicComment(Long id, UserAccount userAccount, Post post, String content) {
		super(id, userAccount, post, content);
	}
}
