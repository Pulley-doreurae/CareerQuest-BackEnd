package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "팀ID는 null일 수 없습니다.")
	private Long teamId; // 팀 ID
	@NotBlank(message = "팀장ID는 공백일 수 없습니다.")
	private String teamLeaderId; // 팀장ID
	@NotBlank(message = "추방할 대상은 공백일 수 없습니다.")
	private String targetId; // 추방할 대상ID
	@NotBlank(message = "빈자리에 추가할 포지션은 공백일 수 없습니다.")
	private String position; // 추방하고 빈자리에 추가할 포지션
}
