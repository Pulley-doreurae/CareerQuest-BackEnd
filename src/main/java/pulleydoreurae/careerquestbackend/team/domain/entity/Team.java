package pulleydoreurae.careerquestbackend.team.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;

/**
 * 팀 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String teamName; // 팀 이름

	@Column(nullable = false)
	private String teamContent; // 팀 설명

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private TeamType teamType; // 팀 구분

	private Integer maxMember; // 팀 최대 인원

	private LocalDate startDate; // 시작일
	private LocalDate endDate; // 종료일

	private boolean isOpened; // 팀 활성화 상태여부
	private boolean isDeleted; // 팀 삭제 여부

	public void changeStatus(boolean isOpened) {
		this.isOpened = isOpened;
	}

	public void delete() {
		isDeleted = true;
	}
}
