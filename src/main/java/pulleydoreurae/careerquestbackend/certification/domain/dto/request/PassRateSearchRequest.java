package pulleydoreurae.careerquestbackend.certification.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.ExamType;

/**
 * 자격증 합격률 검색 조건
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassRateSearchRequest {

	private String certificationName; // 자격증이름
	private Long startYear; // 시작년도
	private Long endYear; // 종료년도
	private Long examRound; // 시험 정보(몇회차인지)
	private ExamType examType; // 시험 구분
	private Double minPassRate; // 최소 합격률
	private Double maxPassRate; // 최대 합격률
}
