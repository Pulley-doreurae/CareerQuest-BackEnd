package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 비밀번호를 찾기로 들어간 링크로 변경할 비밀번호를 담는 Request
 *
 * @author : hanjaeseong
 * @since : 2024/04/03
 */

@Getter
@Setter
public class UserFindPasswordChangeRequest {

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")    // 비밀번호의 길이는 최소 8자
	private String password1;   // 변경할 비밀번호

	@NotBlank(message = "비밀번호 확인은 필수입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")    // 비밀번호 확인의 길이는 최소 8자
	private String password2;   // 변경할 비밀번호 확인

}
