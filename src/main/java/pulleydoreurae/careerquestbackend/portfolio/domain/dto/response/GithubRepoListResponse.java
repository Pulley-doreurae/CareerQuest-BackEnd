package pulleydoreurae.careerquestbackend.portfolio.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 사용자가 레포 리스트 성공하면 받아올 dto
 *
 * @see <a href="https://docs.github.com/ko/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps">깃허브 API</a>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GithubRepoListResponse {
	private String name;
	private String html_url;
	private String languages_url;
}
