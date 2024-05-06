package pulleydoreurae.careerquestbackend.basiccommunity.domain.entity;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostImage;

/**
 * 게시글에 사용되는 이미지 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/04/08
 */
@Entity
@Getter
@NoArgsConstructor
public class BasicPostImage extends PostImage {

	@Builder
	public BasicPostImage(Long id, String fileName, Post post) {
		super(id, fileName, post);
	}
}
