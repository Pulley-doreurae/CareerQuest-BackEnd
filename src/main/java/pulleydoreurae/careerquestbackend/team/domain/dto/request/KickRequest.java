package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀원을 추방하기 위한 Request
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KickRequest {

	private Long teamId; // 팀ID
	private String teamLeaderId; // 팀장ID
	private String targetId; // 추방할 대상ID
	private String position; // 추방하고 빈자리에 추가할 포지션
}
