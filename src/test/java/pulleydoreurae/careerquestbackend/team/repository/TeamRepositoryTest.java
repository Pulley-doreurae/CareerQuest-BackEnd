package pulleydoreurae.careerquestbackend.team.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;
import pulleydoreurae.careerquestbackend.team.domain.entity.Team;

/**
 * @author : parkjihyeok
 * @since : 2024/05/31
 */
@DataJpaTest
@Transactional
@Import(QueryDSLConfig.class)
class TeamRepositoryTest {

	@Autowired TeamRepository teamRepository;

	@Test
	@DisplayName("팀 이름으로 검색")
	void findByTeamNameTest() {
		// Given
		Team team1 = Team.builder().teamName("정보처리기사1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team2 = Team.builder().teamName("정처기모여라!").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team3 = Team.builder().teamName("정처기모여라!").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		teamRepository.save(team1);
		teamRepository.save(team2);
		teamRepository.save(team3);

		// When
		List<Team> result = teamRepository.findByTeamName("정처기모여라!");

		// Then
		assertEquals(2, result.size());
		assertEquals(TeamType.STUDY, result.get(0).getTeamType());
	}

	@Test
	@DisplayName("팀 이름과 팀 타입으로 검색")
	void findByTeamNameAndTeamTypeTest() {
	    // Given
		Team team1 = Team.builder().teamName("정보처리기사1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team2 = Team.builder().teamName("정처기모여라!").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team3 = Team.builder().teamName("정처기모여라!").teamType(TeamType.CONTEST).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		teamRepository.save(team1);
		teamRepository.save(team2);
		teamRepository.save(team3);

	    // When
		List<Team> result = teamRepository.findByTeamNameAndTeamType("정처기모여라!", TeamType.STUDY);

		// Then
		assertEquals(1, result.size());
		assertEquals("정처기모여라!", result.get(0).getTeamName());
		assertEquals(TeamType.STUDY, result.get(0).getTeamType());
	}
}
