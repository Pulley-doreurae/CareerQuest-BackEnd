package pulleydoreurae.careerquestbackend.certification.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 자격증 접수기간 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationRegistrationPeriod extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "certification_id", nullable = false)
	private Certification certification; // 자격증 정보

	@Enumerated(value = EnumType.STRING)
	private ExamType examType; // 시험 구분 (필기, 실기)

	@Column(nullable = false)
	private Long examRound; // 시험 정보(몇회차인지)

	@Column(nullable = false)
	private LocalDate startDate; // 접수 시작일

	@Column(nullable = false)
	private LocalDate endDate; // 접수 종료일
}
