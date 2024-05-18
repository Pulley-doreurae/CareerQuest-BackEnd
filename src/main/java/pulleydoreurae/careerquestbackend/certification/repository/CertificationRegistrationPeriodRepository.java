package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationRegistrationPeriod;

/**
 * 자격증 접수일정 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
public interface CertificationRegistrationPeriodRepository
		extends JpaRepository<CertificationRegistrationPeriod, Long> {

	@Query("select crp from CertificationRegistrationPeriod crp join crp.certification c where c.certificationName like :name and crp.examRound = :examRound")
	Optional<CertificationRegistrationPeriod> findByNameAndExamRound(String name, Long examRound);
}
