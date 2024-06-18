package pulleydoreurae.careerquestbackend.portfolio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.GitRepoInfo;

public interface GitRepoInfoRepository extends JpaRepository<GitRepoInfo, Long> {

	boolean existsByUserAccount(UserAccount userAccount);
	void deleteAllByUserAccount(UserAccount userAccount);

	List<GitRepoInfo> findAllByUserAccount(UserAccount user);
}
