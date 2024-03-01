package pulleydoreurae.careerquestbackend.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자가 카카오 로그인에 성공하면 받아올 dto
 *
 * @author : parkjihyeok
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-response-body">카카오 개발자센터</a>
 * @since : 2024/01/15
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginResponse {

	private String token_type;
	private String access_token;
	private Integer expires_in;
	private String refresh_token;
	private Integer refresh_token_expires_in;
}
