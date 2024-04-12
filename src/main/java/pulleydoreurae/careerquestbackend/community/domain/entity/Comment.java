package pulleydoreurae.careerquestbackend.community.domain.entity;

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
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 댓글 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount userAccount; // 작성자

	@ManyToOne
	@JoinColumn(name = "post_id", nullable = false)
	private Post post; // 댓글이 작성될 게시글

	private String content; // 내용
}
