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
import pulleydoreurae.careerquestbackend.team.domain.entity.EmptyTeamMember;
import pulleydoreurae.careerquestbackend.team.domain.entity.Team;

/**
 * @author : parkjihyeok
 * @since : 2024/05/31
 */
@DataJpaTest
@Transactional
@Import(QueryDSLConfig.class)
class EmptyTeamMemberRepositoryTest {

	@Autowired TeamRepository teamRepository;
	@Autowired EmptyTeamMemberRepository emptyTeamMemberRepository;

	@Test
	@DisplayName("팀 id로 팀장이 지정한 팀원 포지션 불러오기")
	void findByTeamIdTest() {
	    // Given
		Team team = Team.builder().teamName("정보처리기사1팀").teamContent("정보처리기사를 목표로 하는 스터디").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		teamRepository.save(team);

		EmptyTeamMember emptyTeamMember1 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember2 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember3 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember4 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember5 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		emptyTeamMemberRepository.save(emptyTeamMember1);
		emptyTeamMemberRepository.save(emptyTeamMember2);
		emptyTeamMemberRepository.save(emptyTeamMember3);
		emptyTeamMemberRepository.save(emptyTeamMember4);
		emptyTeamMemberRepository.save(emptyTeamMember5);

	    // When
		List<EmptyTeamMember> result = emptyTeamMemberRepository.findAllByTeamId(team.getId());

		// Then
		assertEquals(5, result.size());
		assertEquals("정보처리기사1팀", result.get(0).getTeam().getTeamName());
		assertEquals(TeamType.STUDY, result.get(0).getTeam().getTeamType());
	}

	@Test
	@DisplayName("팀장이 지정한 팀원 포지션 팀id, 포지션, 인덱스로 불러오기")
	void findAllByTeamIdAndPositionTest() {
		// Given
		Team team = Team.builder().teamName("정보처리기사1팀").teamContent("정보처리기사취득을 위한 팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 3, 10)).endDate(LocalDate.of(2024, 5, 20)).build();
		teamRepository.save(team);

		EmptyTeamMember emptyTeamMember1 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember2 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember3 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember4 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		EmptyTeamMember emptyTeamMember5 = EmptyTeamMember.builder().team(team).position("프론트 개발자").build();
		emptyTeamMemberRepository.save(emptyTeamMember1);
		emptyTeamMemberRepository.save(emptyTeamMember2);
		emptyTeamMemberRepository.save(emptyTeamMember3);
		emptyTeamMemberRepository.save(emptyTeamMember4);
		emptyTeamMemberRepository.save(emptyTeamMember5);

		// When
		EmptyTeamMember result = emptyTeamMemberRepository.findAllByTeamIdAndPosition(team.getId(), "프론트 개발자").stream().findAny().get();

		// Then
		assertEquals("정보처리기사1팀", result.getTeam().getTeamName());
		assertEquals(5, result.getTeam().getMaxMember());
	}
}
