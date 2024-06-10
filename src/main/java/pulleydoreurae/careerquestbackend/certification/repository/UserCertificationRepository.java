package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pulleydoreurae.careerquestbackend.certification.domain.entity.UserCertification;

/**
 * 사용자가 취득한 자격증 정보를 담당하는 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
public interface UserCertificationRepository extends JpaRepository<UserCertification, Long> {

	@Query("select uc from UserCertification uc "
			+ "join fetch uc.certification c "
			+ "where uc.userAccount.userId = :userId")
	List<UserCertification> findByUserId(String userId);

	@Query("select uc from UserCertification uc where uc.certification.certificationName = :certificationName")
	List<UserCertification> findByCertificationName(String certificationName);

	@Query("select uc from UserCertification uc where uc.certification.certificationName = :certificationName and uc.userAccount.userId = :userId")
	Optional<UserCertification> findByCertificationNameAndUserId(@Param("certificationName") String certificationName,
			@Param("userId") String userId);
}
