package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "팀ID는 null일 수 없습니다.")
	private Long teamId; // 팀 ID
	@NotBlank(message = "팀장ID는 공백일 수 없습니다.")
	private String teamLeaderId; // 팀장 ID
}
