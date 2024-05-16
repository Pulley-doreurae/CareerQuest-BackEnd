package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.*;

/**
 * 사용자가 네이버 로그인에 성공하면 받아올 dto
 *
 * @see <a href="https://developers.naver.com/docs/login/api/api.md#4-2--%EC%A0%91%EA%B7%BC-%ED%86%A0%ED%81%B0-%EB%B0%9C%EA%B8%89-%EC%9A%94%EC%B2%AD">네이버 개발자센터</a>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverLoginResponse {

    private String access_token;
    private String refresh_token;
    private String token_type;
    private Integer expires_in;

}
