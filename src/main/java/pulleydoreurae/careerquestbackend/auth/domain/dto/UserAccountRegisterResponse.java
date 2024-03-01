package pulleydoreurae.careerquestbackend.auth.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원가입에 대한 응답을 담은 dto 클래스
 */
@Getter
@Setter
@Builder
public class UserAccountRegisterResponse {
	private String userId;
	private String userName;
	private String email;
	private String phoneNum;
	private String msg;
}
