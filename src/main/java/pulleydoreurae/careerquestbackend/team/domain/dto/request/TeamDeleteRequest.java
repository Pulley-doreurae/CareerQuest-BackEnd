package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀 삭제 Request
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamDeleteRequest {

	private Long teamId; // 팀 ID
	private String teamLeaderId; // 팀장 ID
}
