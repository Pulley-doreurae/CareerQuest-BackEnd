package pulleydoreurae.chwijunjindan.auth.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입 요청 시 전달받는 dto 클래스
 */
@Getter
@Setter
public class UserAccountRegisterRequest {

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId;    // 사용자의 id 를 입력받는다.

	@NotBlank(message = "사용자 이름은 필수입니다.")
	private String userName;    // 사용자의 이름을 입력받는다.

	@Email(message = "이메일 형식만 가능합니다.")    // 이메일 형식만 지정
	@NotBlank(message = "이메일은 필수입니다.")
	private String email;

	@NotBlank
	@Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "휴대폰 번호 형식에 맞춰 입력해주세요.")	// 전화번호 형식만 받기
	private String phoneNum;

	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 8, message = "비밀번호는 8자 이상으로 입력해주세요.")    // 비밀번호의 길이는 최소 8자
	private String password;
}
