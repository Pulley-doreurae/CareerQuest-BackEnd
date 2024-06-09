package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

	private String contestCategory; // 공모전 분야 -> enum으로 변경 가능할듯?
	private String target; // 대상 -> enum으로 변경 가능할듯?
	private String region; // 개최지역
	private String organizer; // 주관처
	private Long totalPrize; // 총상금
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
}
