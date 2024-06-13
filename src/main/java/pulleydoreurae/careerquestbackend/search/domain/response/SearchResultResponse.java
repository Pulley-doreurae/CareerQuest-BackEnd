package pulleydoreurae.careerquestbackend.search.domain.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.ContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponseWithPageInfo;
import pulleydoreurae.careerquestbackend.team.domain.entity.Team;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultResponse {

	private List<CertificationResponse> certificationList;
	private List<ContestResponse> contestList;
	private	List<PostResponse> postList;
	private TeamResponseWithPageInfo teamList;
	private String msg;

}
