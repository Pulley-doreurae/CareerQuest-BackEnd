package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;

/**
 * 자격증 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
public interface CertificationRepository extends JpaRepository<Certification, Long> {
	Optional<Certification> findByCertificationCode(Long code);

	Optional<Certification> findByCertificationName(String name);

	@Query("SELECT c FROM Certification c WHERE c.certificationName LIKE concat('%', :keyword, '%')")
	Page<Certification> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
