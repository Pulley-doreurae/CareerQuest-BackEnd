package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 비밀번호를 찾기를 요청한 userId를 담는 Request
 *
 * @author : hanjaeseong
 * @since : 2024/04/05
 */

@Getter
@Setter
public class UserIdRequest {

	private String userId;
}
