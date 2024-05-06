package pulleydoreurae.careerquestbackend.basiccommunity.domain.entity;

import java.util.List;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;

/**
 * 게시판 엔티티
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
@Entity
@Getter
@NoArgsConstructor
public class BasicPost extends Post {

	@Builder
	public BasicPost(Long id, UserAccount userAccount, String title, String content, Long view,
			List<PostLike> postLikes,
			Long category, List<Comment> comments) {
		super(id, userAccount, title, content, view, postLikes, category, comments);
	}
}
