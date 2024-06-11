package pulleydoreurae.careerquestbackend.certification.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pulleydoreurae.careerquestbackend.certification.domain.ExamType;

/**
 * 자격증 합격률 응답
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@Getter
@AllArgsConstructor
public class CertificationPassRateResponse {

	private String certificationName; // 자격증 이름
	private Long examYear; // 시험연도
	private Long examRound; // 시험 회처
	private ExamType examType; // 시험 구분
	private Double passRate; // 합격률
}
