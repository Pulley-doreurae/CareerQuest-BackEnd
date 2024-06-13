package pulleydoreurae.careerquestbackend.community.domain.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	private String contestCategory; // 공모전 분야 -> enum으로 변경 가능할듯?
	private String target; // 대상 -> enum으로 변경 가능할듯?
	private String region; // 개최지역
	private String organizer; // 주관처
	private Long totalPrize; // 총상금
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
}
