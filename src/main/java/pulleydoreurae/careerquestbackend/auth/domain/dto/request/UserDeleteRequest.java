package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 삭제할 userId를 담는 Response
 *
 * @author : hanjaeseong
 * @since : 2024/04/03
 */

@Getter
@Setter
public class UserDeleteRequest {

	private String userId;      // 삭제할 user

	@NotBlank(message = "삭제 시 비밀번호은 필수입니다.")
	private String password;    // 본인확인을 위한 비밀번호

}
