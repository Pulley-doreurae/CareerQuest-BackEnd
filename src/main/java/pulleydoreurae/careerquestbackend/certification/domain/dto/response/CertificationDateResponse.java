package pulleydoreurae.careerquestbackend.certification.domain.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * 자격증달력에 대한 응답을 담아 전달해줄 Response
 *
 * @author : parkjihyeok
 * @since : 2024/05/19
 */
@Getter
public class CertificationDateResponse {
	List<CertificationPeriodResponse> periodResponse = new ArrayList<>();
	List<CertificationExamDateResponse> examDateResponses = new ArrayList<>();
}
