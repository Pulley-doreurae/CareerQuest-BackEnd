package pulleydoreurae.careerquestbackend.certification.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 자격증 Response
 *
 * @author : parkjihyeok
 * @since : 2024/05/22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CertificationResponse {

	private Long certificationCode; // 자격증을 구분할 코드
	private String certificationName; // 자격증명
	private String qualification; // 응시자격
	private String organizer; // 주관처
	private String registrationLink; // 접수링크
	private String aiSummary; // AI 요약
}
