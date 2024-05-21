package pulleydoreurae.careerquestbackend.certification.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 자격증 기본 정보를 담을 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "certification_id")
	private Long id;

	@Column(nullable = false)
	private Long certificationCode; // 자격증을 구분할 코드

	@Column(nullable = false)
	private String certificationName; // 자격증명

	@Column(nullable = false)
	private String Qualification; // 응시자격

	@Column(nullable = false)
	private String organizer; // 주관처
	private String registrationLink; // 접수링크
	private String AiSummary; // AI 요약
}
