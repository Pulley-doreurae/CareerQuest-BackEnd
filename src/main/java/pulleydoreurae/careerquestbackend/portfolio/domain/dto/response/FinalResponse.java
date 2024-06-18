package pulleydoreurae.careerquestbackend.portfolio.domain.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.GitRepoInfo;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalResponse {
	private List<GitRepoInfo> gitRepoInfoList;
	private Map<String, Double> languages;
}
