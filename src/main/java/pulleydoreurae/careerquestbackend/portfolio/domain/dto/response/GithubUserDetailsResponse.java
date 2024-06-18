package pulleydoreurae.careerquestbackend.portfolio.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 깃허브 로그인에 성공한 토큰으로 사용자 정보를 받아올 dto
 *
 * @see <a href="https://docs.github.com/ko/rest/users/users?apiVersion=2022-11-28#get-the-authenticated-user">깃허브 API</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GithubUserDetailsResponse {
	private String login;

}
