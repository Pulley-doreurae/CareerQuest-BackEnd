package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

	@NotNull(message = "팀ID는 null일 수 없습니다.")
	private Long teamId; // 팀ID
	@NotBlank(message = "회원ID는 공백일 수 없습니다.")
	private String userId; // 회원ID
	@NotBlank(message = "포지션은 공백일 수 없습니다.")
	private String position; // 포지션
}
