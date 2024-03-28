package pulleydoreurae.careerquestbackend.auth.domain.entity;

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
 * 기술스택 엔티티
 *
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTechnologyStack extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount userAccount; // 회원정보
	@Column(nullable = false)
	private Long stackId; // 기술스택 id
}
