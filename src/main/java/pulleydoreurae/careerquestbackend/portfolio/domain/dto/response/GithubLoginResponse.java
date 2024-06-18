package pulleydoreurae.careerquestbackend.portfolio.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자가 깃허브 로그인에 성공하면 받아올 dto
 *
 * @see <a href="https://docs.github.com/ko/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps">깃허브 API</a>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubLoginResponse {

	private String access_token;
	private String scope;
	private String token_type;

}
