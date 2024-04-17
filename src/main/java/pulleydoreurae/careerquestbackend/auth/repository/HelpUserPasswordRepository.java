package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.repository.CrudRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.HelpUserPassword;

/**
 * 비밀번호 찾기에 관환 Repository
 */
public interface HelpUserPasswordRepository extends CrudRepository<HelpUserPassword, String> {
}