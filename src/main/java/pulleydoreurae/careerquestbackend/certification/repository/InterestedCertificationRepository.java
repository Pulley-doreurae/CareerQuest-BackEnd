package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pulleydoreurae.careerquestbackend.certification.domain.entity.InterestedCertification;

/**
 * 관심 자격증 엔티티 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
public interface InterestedCertificationRepository extends JpaRepository<InterestedCertification, Long> {

	@Query("select ic from InterestedCertification ic join ic.userAccount u where u.userId = :userId")
	List<InterestedCertification> findAllByUserId(String userId);

	// 관심자격증에서 제거하기 직전 제거할 정보를 검색하기
	@Query("select ic from InterestedCertification ic join ic.userAccount u join ic.certification c where u.userId = :userId and c.certificationName = :certificationName")
	Optional<InterestedCertification> findByUserIdAndCertificationName(String userId, String certificationName);
}
