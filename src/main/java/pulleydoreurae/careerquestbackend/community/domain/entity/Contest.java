package pulleydoreurae.careerquestbackend.community.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import pulleydoreurae.careerquestbackend.community.domain.ContestCategory;
import pulleydoreurae.careerquestbackend.community.domain.Organizer;
import pulleydoreurae.careerquestbackend.community.domain.Region;
import pulleydoreurae.careerquestbackend.community.domain.Target;

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

	@Enumerated(value = EnumType.STRING)
	private ContestCategory contestCategory; // 공모전 분야
	@Enumerated(value = EnumType.STRING)
	private Target target; // 대상
	@Enumerated(value = EnumType.STRING)
	private Region region; // 개최지역
	@Enumerated(value = EnumType.STRING)
	private Organizer organizer; // 주관처

	private Long totalPrize; // 총상금
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
}
