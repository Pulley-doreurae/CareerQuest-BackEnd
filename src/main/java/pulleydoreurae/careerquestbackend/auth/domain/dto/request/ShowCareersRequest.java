package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 직무 리스트를 요청하는 Request
 *
 * @author : hanjaeseong
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowCareersRequest {

	private String major;
	private String middle;
}
