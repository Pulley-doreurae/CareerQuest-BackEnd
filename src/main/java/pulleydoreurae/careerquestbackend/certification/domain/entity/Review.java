package pulleydoreurae.careerquestbackend.certification.domain.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 자격증 합격 후기 / Tip Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount userAccount; // 작성자

	@Column(nullable = false)
	private String title; // 제목
	@Column(nullable = false)
	private String content; // 내용

	@Setter
	@Column(nullable = false)
	private Long view; // 조회수

	@OneToMany(mappedBy = "review")
	private List<ReviewLike> postLikes; // 좋아요

	private String certificationName; // 자격증 이름
}
