package pulleydoreurae.careerquestbackend.certification.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.certification.domain.ExamType;

/**
 * 자격증 접수 기간을 담아 전달해줄 Response
 *
 * @author : parkjihyeok
 * @since : 2024/05/19
 */
@Getter
@Setter
@AllArgsConstructor
public class CertificationPeriodResponse {

	private String name; // 자격증 이름
	private ExamType examType; // 시험 구분
	private Long examRound; // 회차 정보
	private LocalDate startDate; // 접수 시작일
	private LocalDate endDate; // 접수 마감일
}
