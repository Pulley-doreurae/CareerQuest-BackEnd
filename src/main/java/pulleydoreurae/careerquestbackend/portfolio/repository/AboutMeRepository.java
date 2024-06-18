package pulleydoreurae.careerquestbackend.portfolio.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pulleydoreurae.careerquestbackend.portfolio.domain.entity.AboutMe;

public interface AboutMeRepository extends JpaRepository<AboutMe, Long> {

	@Query("select am from AboutMe am where am.userAccount.userId = :userId")
	Optional<AboutMe> findByUserId(String userId);
}
