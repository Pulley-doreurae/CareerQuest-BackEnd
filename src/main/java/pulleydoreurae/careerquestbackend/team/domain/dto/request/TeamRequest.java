package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;

/**
 * 팀 생성 및 수정 Request
 *
 * @author : parkjihyeok
 * @since : 2024/05/31
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {

	private Long teamId; // 팀ID (팀 수정시에만 전달)
	private String teamLeaderId; // 팀장ID
	private String position; // 팀장의 포지션
	private String teamName; // 팀 이름
	private TeamType teamType; // 팀 구분
	private Integer maxMember; // 팀 최대 인원
	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일
	private List<String> positions; // 팀장이 원하는 팀원들의 포지션 (빈자리의 포지션만 입력)
}
