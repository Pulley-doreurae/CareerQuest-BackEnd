package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 요청한 직무를 리스트에 담아 반환하는 Response
 *
 * @author : hanjaeseong
 */
@Getter
@Setter
@Builder
public class ShowCareersResponse {
	private String categoryName;
	private String categoryImage;
}
