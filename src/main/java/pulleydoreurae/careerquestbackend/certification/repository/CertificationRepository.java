package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;

/**
 * 자격증 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
public interface CertificationRepository extends JpaRepository<Certification, Long> {
	List<Certification> findAllByCertificationCode(Long code);

	List<Certification> findAllByCertificationName(String name);
}
