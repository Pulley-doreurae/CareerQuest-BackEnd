package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.*;

/**
 * 사용자가 구글 로그인에 성공하면 받아올 dto
 *
 * @see <a href="https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko#httprest_1">카카오 개발자센터</a>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginResponse {

    private String token_type;
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String scope;
}
