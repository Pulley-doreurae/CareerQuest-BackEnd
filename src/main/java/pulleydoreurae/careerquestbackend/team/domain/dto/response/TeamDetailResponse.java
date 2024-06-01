package pulleydoreurae.careerquestbackend.team.domain.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 한 팀에 대한 전체 세부정보를 담은 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@Setter
@EqualsAndHashCode
public class TeamDetailResponse {

	private TeamResponse teamResponse;
	private List<TeamMemberResponse> teamMemberResponses = new ArrayList<>();
	private List<EmptyTeamMemberResponse> emptyTeamMemberResponses = new ArrayList<>();

	public TeamDetailResponse(TeamResponse teamResponse) {
		this.teamResponse = teamResponse;
	}
}
