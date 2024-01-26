package pulleydoreurae.chwijunjindan.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카카오 사용자정보를 담을 서브클래스
 *
 * @author : parkjihyeok
 * @see <a href="https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#kakaoaccount">카카오 개발자센터</a>
 * @since : 2024/01/23
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: 2024/01/24 카카오로부터 받아올 정보를 정해 추가해야함
public class KakaoAccount {

	private String email;
}
