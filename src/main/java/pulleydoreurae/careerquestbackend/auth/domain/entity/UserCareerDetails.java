package pulleydoreurae.careerquestbackend.auth.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 회원 상세정보 (회원직무 엔티티)
 *
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCareerDetails  extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false) // 소분류는 null 일 수 없다.
	private String smallCategory; // 소분류

	@OneToOne // 단방향 매핑, 직무정보가 회원정보에 대한 정보를 가진다.
	@JoinColumn(name = "user_id")
	private UserAccount userAccount;
}
