package pulleydoreurae.careerquestbackend.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pulleydoreurae.careerquestbackend.team.domain.entity.TeamMember;

/**
 * 팀원을 담당하는 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/31
 */
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

	@Query("select tm from TeamMember tm where tm.team.id = :teamId")
	List<TeamMember> findAllByTeamId(Long teamId);

	@Query("select tm from TeamMember tm where tm.userAccount.userId = :userId and tm.team.id = :teamId")
	Optional<TeamMember> findByUserIdAndTeamId(@Param("userId") String userId, @Param("teamId") Long teamId);
}
