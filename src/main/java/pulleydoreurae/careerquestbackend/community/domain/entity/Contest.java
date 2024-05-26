package pulleydoreurae.careerquestbackend.community.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공모전 게시판 사용할 추가정보 엔티티
 *
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id")
	private Post post; // 연관된 게시글

	private String contestCategory; // 공모전 분야 -> 분야는 enum타입으로 분리?
	private String target; // 대상
	private String region; // 개최지역
	private String organizer; // 주관처
	private Long totalPrize; // 총상금
}
