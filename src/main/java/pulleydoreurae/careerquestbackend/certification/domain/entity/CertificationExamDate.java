package pulleydoreurae.careerquestbackend.certification.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 자격증 시험일정 Entity
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
@Entity
@Getter
public class CertificationExamDate extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "certification_id", nullable = false)
	private Certification certification; // 자격증 정보

	@Column(nullable = false)
	private Long examRound; // 시험 정보(몇회차인지)

	@Column(nullable = false)
	private LocalDate examDate; // 시험일정
}
