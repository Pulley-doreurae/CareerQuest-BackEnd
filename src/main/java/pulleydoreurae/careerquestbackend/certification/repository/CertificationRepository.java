package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;

/**
 * 자격증 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
public interface CertificationRepository extends JpaRepository<Certification, Long> {
	Optional<Certification> findAllByCertificationCode(Long code);

	Optional<Certification> findAllByCertificationName(String name);
}
