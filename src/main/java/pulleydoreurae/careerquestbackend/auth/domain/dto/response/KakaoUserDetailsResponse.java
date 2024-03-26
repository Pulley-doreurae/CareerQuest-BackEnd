package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.KakaoAccountRequest;

/**
 * 카카오 로그인에 성공한 토큰으로 사용자 정보를 받아올 dto
 *
 * @author : parkjihyeok
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-request-body">카카오 개발자센터</a>
 * @since : 2024/01/22
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserDetailsResponse {

	private Long id;
	private KakaoAccountRequest kakao_account;
}
