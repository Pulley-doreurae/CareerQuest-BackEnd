package pulleydoreurae.careerquestbackend.auth.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(nullable = false) // 최소한 대분류는 null 일 수 없다.
	private Long majorCategory; // 대분류
	private Long middleCategory; // 중분류
	private Long smallCategory; // 소분류
}
