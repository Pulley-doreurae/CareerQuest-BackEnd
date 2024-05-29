package pulleydoreurae.careerquestbackend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserCareerDetails;

/**
 * 사용자 직무 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
public interface UserCareerDetailsRepository extends JpaRepository<UserCareerDetails, Long> {

	Optional<UserCareerDetails> findByUserAccount(UserAccount userAccount);

	boolean existsByUserAccount(UserAccount userAccount);
}
