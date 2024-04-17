package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChangeEmailRequest {

	private String userId;

	@Email(message = "이메일 형식만 가능합니다.")    // 이메일 형식만 지정
	@NotBlank(message = "이메일은 필수입니다.")
	private String email;
}
