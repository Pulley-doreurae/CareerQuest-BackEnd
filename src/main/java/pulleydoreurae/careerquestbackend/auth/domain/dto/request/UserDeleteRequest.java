package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId;      // 삭제할 user

	@NotBlank(message = "삭제 시 비밀번호은 필수입니다.")
	private String password;    // 본인확인을 위한 비밀번호

}
