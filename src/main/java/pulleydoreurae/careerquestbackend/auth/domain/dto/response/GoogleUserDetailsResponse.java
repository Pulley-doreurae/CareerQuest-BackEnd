package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 구글 로그인에 성공한 토큰으로 사용자 정보를 받아올 dto
 *
 * @see <a href="https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko#httprest_1">Google Cloud</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GoogleUserDetailsResponse {
    public String id;
    public String email;
    public Boolean verifiedEmail;
    public String name;
    public String givenName;
    public String familyName;
    public String picture;
    public String locale;
}
