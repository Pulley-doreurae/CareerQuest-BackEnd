package pulleydoreurae.chwijunjindan.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDTO, Long> {

	Optional<UserDTO> findByUserid(String userId);
}
