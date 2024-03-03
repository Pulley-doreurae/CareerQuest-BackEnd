package pulleydoreurae.careerquestbackend.mail.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.careerquestbackend.mail.entity.EmailAuthentication;

/**
 * Redis 에 이메일인증에 대한 객체를 저장하는 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/03/02
 */
public interface EmailAuthenticationRepository extends CrudRepository<EmailAuthentication, String> {
}
