package pulleydoreurae.careerquestbackend.certification.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.ToString;
import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 자격증 합격률
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CertificationPassRate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "certification_name", nullable = false)
	private Certification certification;

	private Long examYear; // 년도
	private Long examRound; // 시험 정보(몇회차인지)
	@Enumerated(value = EnumType.STRING)
	private ExamType examType; // 시험 구분
	private Double passRate; // 합격률
}
