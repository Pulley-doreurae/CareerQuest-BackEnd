package pulleydoreurae.careerquestbackend.common.community.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 댓글 추상화 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/06
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Comment extends BaseEntity {

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
