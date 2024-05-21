package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;

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

	// 본인이 등록한 관심 자격증만 보여주면 되므로 userId검색만 존재
	@Query("select ic from InterestedCertification ic join ic.userAccount u where u.userId = :userId")
	List<InterestedCertification> findAllByUserId(String userId);
}
