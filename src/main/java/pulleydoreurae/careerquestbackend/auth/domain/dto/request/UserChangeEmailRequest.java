package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 변경할 이메일을 담는 Request
 *
 * @author : hanjaeseong
 */
@Getter
@Setter
public class UserChangeEmailRequest {

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId;

	@Email(message = "이메일 형식만 가능합니다.")    // 이메일 형식만 지정
	@NotBlank(message = "이메일은 필수입니다.")
	private String email;
}
