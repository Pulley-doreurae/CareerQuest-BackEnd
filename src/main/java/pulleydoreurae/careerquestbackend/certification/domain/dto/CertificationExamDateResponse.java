package pulleydoreurae.careerquestbackend.certification.domain.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.certification.domain.ExamType;

/**
 * 자격증 시험일정을 담아 전달해줄 Response
 *
 * @author : parkjihyeok
 * @since : 2024/05/19
 */
@Getter
@Setter
@AllArgsConstructor
public class CertificationExamDateResponse {

	private String name; // 자격증 이름
	private ExamType examType; // 시험 구분
	private Long examRound; // 회차 정보
	private LocalDate examDate; // 시험일자
}
