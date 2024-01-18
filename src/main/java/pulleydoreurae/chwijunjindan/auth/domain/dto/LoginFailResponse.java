package pulleydoreurae.chwijunjindan.auth.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인에 실패하면 전달할 dto
 *
 * @author : parkjihyeok
 * @since : 2024/01/18
 */
@Getter
@Setter
@Builder
public class LoginFailResponse {

	private String code;
	private String error;
}
