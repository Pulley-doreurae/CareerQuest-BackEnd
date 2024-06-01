package pulleydoreurae.careerquestbackend.team.domain.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;

/**
 * 팀의 정보를 담은 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class TeamResponse {

	private Long teamId;
	private String teamName; // 팀 이름
	private TeamType teamType; // 팀 구분
	private Integer maxMember; // 팀 최대 인원
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
}
