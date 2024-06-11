package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationPassRate;

/**
 * 자격증 합격률 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
public interface CertificationPassRateRepository
		extends JpaRepository<CertificationPassRate, Long>, CertificationPassRateRepositoryCustom {

	@Query("select cpr from CertificationPassRate cpr "
			+ "join fetch cpr.certification "
			+ "where cpr.certification.certificationName = :certificationName")
	List<CertificationPassRate> findByCertification(String certificationName);
}
