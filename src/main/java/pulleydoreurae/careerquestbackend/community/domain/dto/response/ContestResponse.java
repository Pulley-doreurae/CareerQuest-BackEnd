package pulleydoreurae.careerquestbackend.community.domain.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.community.domain.ContestCategory;
import pulleydoreurae.careerquestbackend.community.domain.Organizer;
import pulleydoreurae.careerquestbackend.community.domain.Region;
import pulleydoreurae.careerquestbackend.community.domain.Target;

/**
 * 공모전에 대한 정보를 담을 Response
 *
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ContestResponse {

	private Long contestId; // 공모전 id
	private String title; // 공모전 제목
	private String content; // 공모전 상세페이지
	private List<String> images; // 사진 리스트
	private Long view; // 조회수
	private ContestCategory contestCategory; // 공모전 분야
	private Target target; // 대상
	private Region region; // 개최지역
	private Organizer organizer; // 주관처
	private Long totalPrize; // 총상금
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
}
