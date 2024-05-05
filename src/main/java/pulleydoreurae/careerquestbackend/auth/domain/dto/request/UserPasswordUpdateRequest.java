package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordUpdateRequest {

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId;

	@NotBlank(message = "회원정보 수정 시 비밀번호는 필수입니다.")
	private String currentPassword;

	@NotBlank(message = "변경할 비밀번호를 입력해주세요.")
	@Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")    // 비밀번호의 길이는 최소 8자
	private String newPassword1;   // 변경할 비밀번호

	@NotBlank(message = "비밀번호 확인은 필수입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")    // 비밀번호 확인의 길이는 최소 8자
	private String newPassword2;   // 변경할 비밀번호 확인
}
