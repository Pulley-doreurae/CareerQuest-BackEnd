package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserCareerDetails;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserTechnologyStack;

/**
 * 사용자 기술스택 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
public interface UserTechnologyStackRepository extends JpaRepository<UserTechnologyStack, Long> {

	void deleteAllByUserAccount(UserAccount userAccount);
}
