package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀원의 정보를 담을 Request
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberRequest {

	private Long teamId; // 팀ID
	private String userId; // 회원ID
	private String position; // 포지션
}
