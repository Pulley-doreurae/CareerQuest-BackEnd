package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.community.domain.ContestCategory;
import pulleydoreurae.careerquestbackend.community.domain.Organizer;
import pulleydoreurae.careerquestbackend.community.domain.Region;
import pulleydoreurae.careerquestbackend.community.domain.Target;

/**
 * 공모전 검색조건 Request
 *
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContestSearchRequest {

	private ContestCategory contestCategory; // 공모전 분야
	private Target target; // 대상
	private Region region; // 개최지역
	private Organizer organizer; // 주관처
	private Long totalPrize; // 총상금
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
}
