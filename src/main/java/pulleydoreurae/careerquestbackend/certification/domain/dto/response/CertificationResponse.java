package pulleydoreurae.careerquestbackend.certification.domain.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
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

	List<CertificationPeriodResponse> periodResponse = new ArrayList<>(); // 신청기간
	List<CertificationExamDateResponse> examDateResponses = new ArrayList<>(); // 시험일정

	@Builder
	public CertificationResponse(Long certificationCode, String certificationName, String qualification,
			String organizer,
			String registrationLink, String aiSummary) {
		this.certificationCode = certificationCode;
		this.certificationName = certificationName;
		this.qualification = qualification;
		this.organizer = organizer;
		this.registrationLink = registrationLink;
		this.aiSummary = aiSummary;
	}
}
