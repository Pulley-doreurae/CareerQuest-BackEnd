package pulleydoreurae.careerquestbackend.mail.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.careerquestbackend.mail.entity.UserInfoUserId;

/**
 * userId 를 선점하기 위한 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/03/03
 */
public interface UserInfoUserIdRepository extends CrudRepository<UserInfoUserId, String> {
}
