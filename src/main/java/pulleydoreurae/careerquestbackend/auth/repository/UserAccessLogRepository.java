package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccessLog;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;

import java.util.Collection;
import java.util.List;

/**
 * 접속기록 Repository
 *
 * @author : hanjaeseong
 * @since : 2024/03/28
 */
public interface UserAccessLogRepository extends JpaRepository<UserAccessLog, Long> {
    List<UserAccessLog> findAllByUserAccount(UserAccount userAccount);
}
