package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 간단한 메시지만 담은 response
 *
 * @author : parkjihyeok
 * @since : 2024/03/27
 */
@Getter
@Setter
@Builder
public class SimpleResponse {

	private String msg;
}
