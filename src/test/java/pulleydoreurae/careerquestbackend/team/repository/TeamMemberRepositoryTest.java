package pulleydoreurae.careerquestbackend.team.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;
import pulleydoreurae.careerquestbackend.team.domain.entity.Team;
import pulleydoreurae.careerquestbackend.team.domain.entity.TeamMember;

/**
 * @author : parkjihyeok
 * @since : 2024/05/31
 */
@DataJpaTest
@Transactional
@Import(QueryDSLConfig.class)
class TeamMemberRepositoryTest {

	@Autowired UserAccountRepository userAccountRepository;
	@Autowired TeamRepository teamRepository;
	@Autowired TeamMemberRepository teamMemberRepository;

	@BeforeEach
	void setUp() {
		UserAccount user1 = UserAccount.builder().userId("testId1").build();
		UserAccount user2 = UserAccount.builder().userId("testId2").build();
		UserAccount user3 = UserAccount.builder().userId("testId3").build();
		UserAccount user4 = UserAccount.builder().userId("testId4").build();
		userAccountRepository.save(user1);
		userAccountRepository.save(user2);
		userAccountRepository.save(user3);
		userAccountRepository.save(user4);

		Team team1 = Team.builder().teamName("정보처리기사1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team2 = Team.builder().teamName("정처기모여라!").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team3 = Team.builder().teamName("정처기모여라!").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		teamRepository.save(team1);
		teamRepository.save(team2);
		teamRepository.save(team3);

		TeamMember teamMember1 = TeamMember.builder().userAccount(user1).isTeamLeader(true).team(team1).position("백엔드 개발자").build();
		TeamMember teamMember2 = TeamMember.builder().userAccount(user2).isTeamLeader(false).team(team1).position("프론트 개발자").build();
		TeamMember teamMember3 = TeamMember.builder().userAccount(user3).isTeamLeader(false).team(team1).position("프론트 개발자").build();
		TeamMember teamMember4 = TeamMember.builder().userAccount(user4).isTeamLeader(false).team(team1).position("디자이너").build();
		teamMemberRepository.save(teamMember1);
		teamMemberRepository.save(teamMember2);
		teamMemberRepository.save(teamMember3);
		teamMemberRepository.save(teamMember4);
	}

	@Test
	@DisplayName("팀에 소속된 팀원들 불러오기")
	void findAllByTeamIdTest() {
	    // Given

	    // When
		List<Team> teams = teamRepository.findByTeamName("정보처리기사1팀");
		List<TeamMember> result = teamMemberRepository.findAllByTeamId(teams.get(0).getId());

		// Then
		assertEquals(4, result.size());
		assertEquals("정보처리기사1팀", result.get(0).getTeam().getTeamName());
		assertEquals(5, result.get(0).getTeam().getMaxMember());
		assertEquals("프론트 개발자", result.get(1).getPosition());
		assertTrue(result.get(0).isTeamLeader());
	}

	@Test
	@DisplayName("userId와 팀정보로 팀원 찾아오기")
	void findByUserIdAndTeamIdTest() {
	    // Given

	    // When
		List<Team> teams = teamRepository.findByTeamName("정보처리기사1팀");
		TeamMember result = teamMemberRepository.findByUserIdAndTeamId("testId2", teams.get(0).getId()).get();

		// Then
		assertEquals("정보처리기사1팀", result.getTeam().getTeamName());
		assertEquals(TeamType.STUDY, result.getTeam().getTeamType());
		assertEquals("프론트 개발자", result.getPosition());
		assertFalse(result.isTeamLeader());
	}
}
