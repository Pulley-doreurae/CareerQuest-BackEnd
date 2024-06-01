package pulleydoreurae.careerquestbackend.team.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

	@Test
	@DisplayName("전체 팀 조회 테스트")
	void findAllByOrderByIdDescTest() {
	    // Given
		Team team1 = Team.builder().teamName("정보처리기사1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team2 = Team.builder().teamName("정처기모여라!1").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team3 = Team.builder().teamName("정처기모여라!2").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team4 = Team.builder().teamName("정보처리기사2팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team5 = Team.builder().teamName("공모전에 나가보자").teamType(TeamType.CONTEST).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team6 = Team.builder().teamName("공모전에 참여하자").teamType(TeamType.CONTEST).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		teamRepository.save(team1);
		teamRepository.save(team2);
		teamRepository.save(team3);
		teamRepository.save(team4);
		teamRepository.save(team5);
		teamRepository.save(team6);

		// When
		Pageable pageable = PageRequest.of(0, 3);
		Page<Team> result = teamRepository.findAllByOrderByIdDesc(pageable);

		// Then
		assertEquals(2, result.getTotalPages());
		assertEquals(6, result.getTotalElements());
		assertEquals(3, result.getSize());
		assertEquals("공모전에 참여하자", result.getContent().get(0).getTeamName());
	}

	@Test
	@DisplayName("팀 타입에 맞는 팀 전체 조회")
	void findAllByTeamTypeOrderByIdDescTest() {
	    // Given
		Team team1 = Team.builder().teamName("정보처리기사1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team2 = Team.builder().teamName("정처기모여라!1").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team3 = Team.builder().teamName("정처기모여라!2").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team4 = Team.builder().teamName("정보처리기사2팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team5 = Team.builder().teamName("공모전에 나가보자").teamType(TeamType.CONTEST).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		Team team6 = Team.builder().teamName("공모전에 참여하자").teamType(TeamType.CONTEST).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		teamRepository.save(team1);
		teamRepository.save(team2);
		teamRepository.save(team3);
		teamRepository.save(team4);
		teamRepository.save(team5);
		teamRepository.save(team6);

	    // When
		Pageable pageable = PageRequest.of(0, 3);
		Page<Team> result = teamRepository.findAllByTeamTypeOrderByIdDesc(TeamType.STUDY, pageable);

	    // Then
		assertEquals(2, result.getTotalPages());
		assertEquals(4, result.getTotalElements());
		assertEquals(3, result.getSize());
		assertEquals("정보처리기사2팀", result.getContent().get(0).getTeamName());
	}
}
