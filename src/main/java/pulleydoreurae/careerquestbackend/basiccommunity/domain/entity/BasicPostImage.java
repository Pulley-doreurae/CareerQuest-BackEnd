package pulleydoreurae.careerquestbackend.basiccommunity.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 게시글에 사용되는 이미지 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/04/08
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String fileName;

	@ManyToOne
	@JoinColumn(nullable = false, name = "post_id")
	private Post post;
}
