package pulleydoreurae.careerquestbackend.team.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pulleydoreurae.careerquestbackend.team.domain.entity.EmptyTeamMember;

/**
 * 팀장이 지정한 팀의 포지션을 미리 선점하는 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/31
 */
public interface EmptyTeamMemberRepository extends JpaRepository<EmptyTeamMember, Long> {

	@Query("select etm from EmptyTeamMember etm where etm.team.id = :teamId")
	List<EmptyTeamMember> findAllByTeamId(Long teamId);

	@Query("select etm from EmptyTeamMember etm where etm.team.id = :teamId and etm.position = :position and etm.index = :index")
	Optional<EmptyTeamMember> findByTeamIdAndPositionAndIndex(@Param("teamId") Long teamId,
			@Param("position") String position, @Param("index") Integer index);
}
