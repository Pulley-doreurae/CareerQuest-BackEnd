package pulleydoreurae.careerquestbackend.team.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;

/**
 * 한 회원이 참여했던 팀에 대한 정보를 담은 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/15
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class TeamMemberHistoryResponse {

	private String userId;
	private boolean isTeamLeader; // 팀장인지 여부
	private String position; // 포지션
	private Long teamId; // 팀 상세 정보를 조회하기 위한 팀 ID
	private String teamName; // 팀 이름
	private TeamType teamType; // 팀 유형
}
