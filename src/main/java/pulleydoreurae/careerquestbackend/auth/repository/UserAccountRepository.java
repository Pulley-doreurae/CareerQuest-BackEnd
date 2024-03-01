package pulleydoreurae.careerquestbackend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

	Optional<UserAccount> findByUserId(String userId);

	Optional<UserAccount> findByEmail(String email);

	boolean existsByUserId(String userId);

	boolean existsByEmail(String email);
}
