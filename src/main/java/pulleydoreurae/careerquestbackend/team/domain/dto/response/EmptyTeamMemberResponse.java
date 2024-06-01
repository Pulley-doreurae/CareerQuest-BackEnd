package pulleydoreurae.careerquestbackend.team.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 팀장이 선호하는 팀원 정보를 담은 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class EmptyTeamMemberResponse {

	private String position; // 선호 포지션
}
