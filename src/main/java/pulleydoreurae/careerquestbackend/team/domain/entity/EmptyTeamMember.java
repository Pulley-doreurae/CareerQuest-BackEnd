package pulleydoreurae.careerquestbackend.team.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 팀장이 지정한 팀의 포지션을 미리 선점하는 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/31
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmptyTeamMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team; // 팀 정보
	@Column(nullable = false)
	private String position; // 포지션
	@Column(nullable = false)
	private Integer index; // 인덱스
}
