package pulleydoreurae.careerquestbackend.team.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 팀원 정보를 담은 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class TeamMemberResponse {

	private String userId;
	private boolean isTeamLeader; // 팀장인지 여부
	private String position; // 포지션
}
