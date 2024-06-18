package pulleydoreurae.careerquestbackend.portfolio.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class GitRepoInfoResponse {
	private String repoName;
	private String repoUrl;
}
