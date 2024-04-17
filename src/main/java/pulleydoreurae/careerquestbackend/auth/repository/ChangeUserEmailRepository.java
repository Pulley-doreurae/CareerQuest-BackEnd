package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.repository.CrudRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.ChangeUserEmail;

/**
 * 이메일 변경에 관환 Repository
 */
public interface ChangeUserEmailRepository extends CrudRepository<ChangeUserEmail, String> {
}
