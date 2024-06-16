package pulleydoreurae.careerquestbackend.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.community.domain.entity.JoinedContest;

/**
 * 참여했던 공모전 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/06/16
 */
public interface JoinedContestRepository extends JpaRepository<JoinedContest, Long> {

	@Query("select jc from JoinedContest jc "
			+ "join fetch jc.contest c "
			+ "join fetch jc.contest.post p "
			+ "join fetch jc.contest.post.userAccount ua "
			+ "where jc.userAccount.userId = :userId "
			+ "order by jc.id desc ")
	List<JoinedContest> findByUserId(String userId);

	void deleteByContestIdAndUserAccount(Long contestId, UserAccount userAccount);
}
