package pulleydoreurae.careerquestbackend.team.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	@NotBlank(message = "팀장ID는 공백일 수 없습니다.")
	private String teamLeaderId; // 팀장ID
	@NotBlank(message = "팀장의 포지션은 공백일 수 없습니다.")
	private String position; // 팀장의 포지션
	@NotBlank(message = "팀 이름은 공백일 수 없습니다.")
	private String teamName; // 팀 이름
	@NotNull(message = "팀 구분은 null일 수 없습니다.")
	private TeamType teamType; // 팀 구분
	@NotNull(message = "팀 최대 인원은 null일 수 없습니다.")
	private Integer maxMember; // 팀 최대 인원
	@NotNull(message = "시작일은 null일 수 없습니다.")
	private LocalDate startDate; // 시작일
	@NotNull(message = "종료일은 null일 수 없습니다.")
	private LocalDate endDate; // 종료일
	@NotNull(message = "선호 포지션은 null일 수 없습니다.") // 포지션이 아무 상관없더라도 값을 전달해야한다.
	private List<String> positions; // 팀장이 원하는 팀원들의 포지션 (빈자리의 포지션만 입력)
	@NotNull(message = "팀 활성화 여부는 null일 수 없습니다.")
	private boolean isOpened; // 팀 활성화 여부

	public TeamRequest(String teamLeaderId, String position, String teamName, TeamType teamType, Integer maxMember,
			LocalDate startDate, LocalDate endDate, List<String> positions) {
		this.teamLeaderId = teamLeaderId;
		this.position = position;
		this.teamName = teamName;
		this.teamType = teamType;
		this.maxMember = maxMember;
		this.startDate = startDate;
		this.endDate = endDate;
		this.positions = positions;
	}

	public TeamRequest(String teamLeaderId, String position, String teamName, TeamType teamType, Integer maxMember,
			LocalDate startDate, LocalDate endDate, List<String> positions, boolean isOpened) {
		this.teamLeaderId = teamLeaderId;
		this.position = position;
		this.teamName = teamName;
		this.teamType = teamType;
		this.maxMember = maxMember;
		this.startDate = startDate;
		this.endDate = endDate;
		this.positions = positions;
		this.isOpened = isOpened;
	}
}
