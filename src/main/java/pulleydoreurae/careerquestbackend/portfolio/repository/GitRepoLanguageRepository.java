package pulleydoreurae.careerquestbackend.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.GitRepoLanguage;

public interface GitRepoLanguageRepository extends JpaRepository<GitRepoLanguage, Long> {
	boolean existsByUserAccount(UserAccount userAccount);
	void deleteAllByUserAccount(UserAccount userAccount);
	List<GitRepoLanguage> findAllByUserAccount(UserAccount user);
}
