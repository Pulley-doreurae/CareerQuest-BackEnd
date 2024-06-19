package pulleydoreurae.careerquestbackend.team.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.service.CommonService;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.KickRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamDeleteRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamMemberRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamDetailResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamMemberHistoryResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponseWithPageInfo;
import pulleydoreurae.careerquestbackend.team.domain.entity.EmptyTeamMember;
import pulleydoreurae.careerquestbackend.team.domain.entity.Team;
import pulleydoreurae.careerquestbackend.team.domain.entity.TeamMember;
import pulleydoreurae.careerquestbackend.team.repository.EmptyTeamMemberRepository;
import pulleydoreurae.careerquestbackend.team.repository.TeamMemberRepository;
import pulleydoreurae.careerquestbackend.team.repository.TeamRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("팀 매칭 서비스 테스트")
class TeamServiceTest {

	@InjectMocks TeamService teamService;
	@Mock TeamRepository teamRepository;
	@Mock EmptyTeamMemberRepository emptyTeamMemberRepository;
	@Mock TeamMemberRepository teamMemberRepository;
	@Mock CommonService commonService;

	@Test
	@DisplayName("팀 생성 테스트 - 실패(팀장의 정보를 찾을 수 없음)")
	void makeTeamTest1() {
	    // Given
		List<String> positions = List.of("백엔드 개발자", "프론트엔드 개발자", "프론트엔드 개발자", "디자이너");
		TeamRequest request = new TeamRequest("leaderId", "백엔드 개발자", "해파리", "캡스톤 프로젝트를 위한 팀", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions);
		given(commonService.findUserAccount("leaderId", true)).willThrow(new UsernameNotFoundException("회원 정보를 찾을 수 없습니다."));

	    // When

	    // Then
		assertThrows(UsernameNotFoundException.class, () -> teamService.makeTeam(request));
		verify(teamRepository, never()).save(any());
	}

	@Test
	@DisplayName("팀 생성 테스트 - 성공")
	void makeTeamTest2() {
		// Given
		List<String> positions = List.of("백엔드 개발자", "프론트엔드 개발자", "프론트엔드 개발자", "디자이너");
		TeamRequest request = new TeamRequest("leaderId", "백엔드 개발자", "해파리", "캡스톤 프로젝트를 위한 팀", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions);
		UserAccount user = UserAccount.builder().userId("leaderId").build();
		given(commonService.findUserAccount("leaderId", true)).willReturn(user);

		// When

		// Then
		assertDoesNotThrow(() -> teamService.makeTeam(request));
		verify(teamRepository).save(any());
		verify(emptyTeamMemberRepository, times(4)).save(any());
	}

	@Test
	@DisplayName("팀 수정 테스트 - 실패(팀 정보를 찾을 수 없음)")
	void updateTeamTest1() {
	    // Given
		List<String> positions = List.of("백엔드 개발자", "프론트엔드 개발자", "프론트엔드 개발자", "디자이너");
		TeamRequest request = new TeamRequest(100L, "leaderId", "백엔드 개발자", "해파리", "캡스톤 프로젝트를 위한 팀", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions, true);
		given(teamRepository.findById(request.getTeamId())).willThrow(new IllegalArgumentException("팀에 대한 정보를 찾을 수 없습니다."));

	    // When

	    // Then
		assertThrows(IllegalArgumentException.class, () -> teamService.updateTeam(request));
		verify(teamRepository, never()).save(any());
		verify(emptyTeamMemberRepository, never()).findAllByTeamId(request.getTeamId());
		verify(emptyTeamMemberRepository, never()).deleteAll(any());
	}

	@Test
	@DisplayName("팀 수정 테스트 - 성공")
	void updateTeamTest2() {
		// Given
		List<String> positions = List.of("백엔드 개발자", "프론트엔드 개발자", "프론트엔드 개발자", "디자이너");
		TeamRequest request = new TeamRequest(100L, "leaderId", "백엔드 개발자", "해파리", "캡스톤 프로젝트를 위한 팀", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions, true);
		Team team = Team.builder().id(request.getTeamId()).teamName(request.getTeamName()).teamType(request.getTeamType()).maxMember(request.getMaxMember()).startDate(request.getStartDate()).endDate(request.getEndDate()).build();
		given(teamRepository.findById(request.getTeamId())).willReturn(Optional.ofNullable(team));

		// When

		// Then
		assertDoesNotThrow(() -> teamService.updateTeam(request));
		verify(teamRepository).save(any());
		verify(emptyTeamMemberRepository).findAllByTeamId(request.getTeamId());
		verify(emptyTeamMemberRepository).deleteAll(any());
	}

	@Test
	@DisplayName("팀 제거 테스트 - 실패(요청자의 권한을 확인할 수 없음)")
	void deleteTeam1() {
		// Given
		TeamDeleteRequest request = new TeamDeleteRequest(100L, "leaderId");
		doThrow(new IllegalAccessError("요청자의 권한을 확인할 수 없습니다.")).when(commonService).checkAuth("leaderId");

		// When

		// Then
		assertThrows(IllegalAccessError.class, () -> teamService.deleteTeam(request));
		verify(emptyTeamMemberRepository, never()).findAllByTeamId(any());
		verify(emptyTeamMemberRepository, never()).deleteAll(any());
		verify(teamMemberRepository, never()).findAllByTeamId(any());
		verify(teamMemberRepository, never()).deleteAll(any());
		verify(teamRepository, never()).delete(any());
	}

	@Test
	@DisplayName("팀 제거 테스트 - 실패(팀정보를 찾을 수 없음)")
	void deleteTeam2() {
		// Given
		TeamDeleteRequest request = new TeamDeleteRequest(100L, "leaderId");
		given(teamRepository.findById(100L)).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> teamService.deleteTeam(request));
		verify(emptyTeamMemberRepository, never()).findAllByTeamId(any());
		verify(emptyTeamMemberRepository, never()).deleteAll(any());
		verify(teamMemberRepository, never()).findAllByTeamId(any());
		verify(teamMemberRepository, never()).deleteAll(any());
		verify(teamRepository, never()).delete(any());
	}

	@Test
	@DisplayName("팀 제거 테스트 - 성공")
	void deleteTeam3() {
		// Given
		TeamDeleteRequest request = new TeamDeleteRequest(100L, "leaderId");
		Team team = Team.builder().id(request.getTeamId()).teamName("팀 이름").teamType(TeamType.STUDY).build();
		given(teamRepository.findById(request.getTeamId())).willReturn(Optional.ofNullable(team));
		// When

		// Then
		assertDoesNotThrow(() -> teamService.deleteTeam(request));
	}

	@Test
	@DisplayName("팀 참가 테스트 - 실패(정원초과)")
	void joinTeamTest1() {
		// Given
		TeamMemberRequest request = new TeamMemberRequest(100L, "joinId", "백엔드 개발자");

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> teamService.joinTeam(request));
		verify(emptyTeamMemberRepository, never()).delete(any());
	}

	@Test
	@DisplayName("팀 참가 테스트 - 성공")
	void joinTeamTest2() {
		// Given
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).endDate(LocalDate.now().plusDays(3)).isOpened(true).build();
		TeamMemberRequest request = new TeamMemberRequest(100L, "joinId", "백엔드 개발자");
		EmptyTeamMember emptyTeamMember = EmptyTeamMember.builder().team(team).position("백엔드 개발자").build();
		given(teamRepository.findById(100L)).willReturn(Optional.ofNullable(team));
		given(emptyTeamMemberRepository.findAllByTeamIdAndPosition(100L, "백엔드 개발자")).willReturn(List.of(emptyTeamMember));

		// When

		// Then
		assertDoesNotThrow(() -> teamService.joinTeam(request));
		verify(emptyTeamMemberRepository).delete(any());
		verify(teamMemberRepository).save(any());
	}

	@Test
	@DisplayName("팀 참가 테스트 - 실패 (날짜 지남)")
	void joinTeamTest3() {
		// Given
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).endDate(LocalDate.now().plusDays(-1)).isOpened(true).build();
		TeamMemberRequest request = new TeamMemberRequest(100L, "joinId", "백엔드 개발자");
		EmptyTeamMember emptyTeamMember = EmptyTeamMember.builder().team(team).position("백엔드 개발자").build();
		given(teamRepository.findById(100L)).willReturn(Optional.ofNullable(team));

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> teamService.joinTeam(request));
		verify(emptyTeamMemberRepository, never()).delete(any());
		verify(teamMemberRepository, never()).save(any());
	}

	@Test
	@DisplayName("팀 참가 테스트 - 실패 (팀 닫힘)")
	void joinTeamTest4() {
		// Given
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).endDate(LocalDate.now().plusDays(3)).isOpened(false).build();
		TeamMemberRequest request = new TeamMemberRequest(100L, "joinId", "백엔드 개발자");
		EmptyTeamMember emptyTeamMember = EmptyTeamMember.builder().team(team).position("백엔드 개발자").build();
		given(teamRepository.findById(100L)).willReturn(Optional.ofNullable(team));

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> teamService.joinTeam(request));
		verify(emptyTeamMemberRepository, never()).delete(any());
		verify(teamMemberRepository, never()).save(any());
	}

	@Test
	@DisplayName("팀에서 나가기 테스트 - 실패(팀에서 회원 정보를 찾을 수 없음)")
	void leaveTeamTest1() {
		// Given
		TeamMemberRequest request = new TeamMemberRequest(100L, "memberId", "백엔드 개발자");
		UserAccount user = UserAccount.builder().userId("memberId").build();
		given(commonService.findUserAccount("memberId", true)).willReturn(user);
		given(teamMemberRepository.findByUserIdAndTeamId("memberId", 100L)).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(IllegalArgumentException.class, () -> teamService.leaveTeam(request));
		verify(emptyTeamMemberRepository, never()).save(any());
		verify(teamMemberRepository, never()).delete(any());
	}

	@Test
	@DisplayName("팀에서 나가기 테스트 - 성공")
	void leaveTeamTest2() {
		// Given
		TeamMemberRequest request = new TeamMemberRequest(100L, "memberId", "백엔드 개발자");
		UserAccount user = UserAccount.builder().userId("memberId").build();
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).isOpened(true).build();
		TeamMember teamMember = TeamMember.builder().userAccount(user).isTeamLeader(false).team(team).position("백엔드 개발자").build();
		given(commonService.findUserAccount("memberId", true)).willReturn(user);
		given(teamMemberRepository.findByUserIdAndTeamId("memberId", 100L)).willReturn(Optional.ofNullable(teamMember));

		// When

		// Then
		assertDoesNotThrow(() -> teamService.leaveTeam(request));
		verify(emptyTeamMemberRepository).save(any());
		verify(teamMemberRepository).delete(any());
	}

	@Test
	@DisplayName("팀원 추방 테스트 - 실패(추방 권한 없음)")
	void kickMemberTest1() {
		// Given
		KickRequest kickRequest = new KickRequest(100L, "leaderId", "memberId", "디자이너");
		UserAccount leader = UserAccount.builder().userId("leaderId").build();
		UserAccount member = UserAccount.builder().userId("memberId").build();
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).isOpened(true).build();
		TeamMember teamLeader = TeamMember.builder().userAccount(leader).isTeamLeader(false).team(team).position("백엔드 개발자").build();
		given(commonService.findUserAccount("leaderId", true)).willReturn(leader);
		given(commonService.findUserAccount("memberId", false)).willReturn(member);
		given(teamMemberRepository.findByUserIdAndTeamId("leaderId", 100L)).willReturn(Optional.ofNullable(teamLeader));

		// When

		// Then
		assertThrows(IllegalAccessError.class, () -> teamService.kickMember(kickRequest));
		verify(teamMemberRepository, never()).findByUserIdAndTeamId("memberId", 100L);
		verify(teamMemberRepository, never()).delete(any());
	}

	@Test
	@DisplayName("팀원 추방 테스트 - 성공")
	void kickMemberTest2() {
		// Given
		KickRequest kickRequest = new KickRequest(100L, "leaderId", "memberId", "디자이너");
		UserAccount leader = UserAccount.builder().userId("leaderId").build();
		UserAccount member = UserAccount.builder().userId("memberId").build();
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).build();
		TeamMember teamLeader = TeamMember.builder().userAccount(leader).isTeamLeader(true).team(team).position("백엔드 개발자").build();
		TeamMember teamMember = TeamMember.builder().userAccount(member).isTeamLeader(false).team(team).position("백엔드 개발자").build();
		given(commonService.findUserAccount("leaderId", true)).willReturn(leader);
		given(commonService.findUserAccount("memberId", false)).willReturn(member);
		given(teamMemberRepository.findByUserIdAndTeamId("leaderId", 100L)).willReturn(Optional.ofNullable(teamLeader));
		given(teamMemberRepository.findByUserIdAndTeamId("memberId", 100L)).willReturn(Optional.ofNullable(teamMember));

		// When

		// Then
		assertDoesNotThrow(() -> teamService.kickMember(kickRequest));
		verify(teamMemberRepository).findByUserIdAndTeamId("memberId", 100L);
		verify(teamMemberRepository).delete(any());
	}

	@Test
	@DisplayName("전체 팀 조회 테스트")
	void findAllTest() {
	    // Given
		Team team1 = Team.builder().id(100L).teamName("팀1").teamType(TeamType.STUDY).build();
		Team team2 = Team.builder().id(101L).teamName("팀2").teamType(TeamType.STUDY).build();
		Team team3 = Team.builder().id(102L).teamName("팀3").teamType(TeamType.STUDY).build();
		Team team4 = Team.builder().id(103L).teamName("팀4").teamType(TeamType.STUDY).build();
		Team team5 = Team.builder().id(104L).teamName("팀5").teamType(TeamType.STUDY).build();
		Team team6 = Team.builder().id(105L).teamName("팀6").teamType(TeamType.STUDY).build();
		Team team7 = Team.builder().id(106L).teamName("팀7").teamType(TeamType.STUDY).build();
		Team team8 = Team.builder().id(107L).teamName("팀8").teamType(TeamType.STUDY).build();
		Team team9 = Team.builder().id(108L).teamName("팀9").teamType(TeamType.STUDY).build();
		Team team10 = Team.builder().id(110L).teamName("팀10").teamType(TeamType.STUDY).build();
		Page<Team> page = new PageImpl<>(List.of(team10, team9, team8));
		Pageable pageable = PageRequest.of(0, 3);
		given(teamRepository.findAllByOrderByIdDesc(pageable)).willReturn(page);

	    // When
		TeamResponseWithPageInfo result = teamService.findAll(pageable);

		// Then
		assertEquals(3, result.getTeamResponse().size());
		assertEquals("팀10", result.getTeamResponse().get(0).getTeamName());
	}

	@Test
	@DisplayName("팀 타입으로 팀을 검색하는 테스트")
	void findAllByTeamTypeTest() {
	    // Given
		Team team1 = Team.builder().id(100L).teamName("팀1").teamType(TeamType.STUDY).build();
		Team team2 = Team.builder().id(101L).teamName("팀2").teamType(TeamType.STUDY).build();
		Team team3 = Team.builder().id(102L).teamName("팀3").teamType(TeamType.STUDY).build();
		Team team4 = Team.builder().id(103L).teamName("팀4").teamType(TeamType.STUDY).build();
		Team team5 = Team.builder().id(104L).teamName("팀5").teamType(TeamType.CONTEST).build();
		Team team6 = Team.builder().id(105L).teamName("팀6").teamType(TeamType.STUDY).build();
		Team team7 = Team.builder().id(106L).teamName("팀7").teamType(TeamType.CONTEST).build();
		Team team8 = Team.builder().id(107L).teamName("팀8").teamType(TeamType.CONTEST).build();
		Team team9 = Team.builder().id(108L).teamName("팀9").teamType(TeamType.CONTEST).build();
		Team team10 = Team.builder().id(110L).teamName("팀10").teamType(TeamType.STUDY).build();
		Page<Team> page = new PageImpl<>(List.of(team9, team8, team7));
		Pageable pageable = PageRequest.of(0, 3);
		given(teamRepository.findAllByTeamTypeOrderByIdDesc(TeamType.CONTEST, pageable)).willReturn(page);

	    // When
		TeamResponseWithPageInfo result = teamService.findAllByTeamType(TeamType.CONTEST, pageable);

		// Then
		assertEquals(3, result.getTeamResponse().size());
		assertEquals("팀9", result.getTeamResponse().get(0).getTeamName());
	}

	@Test
	@DisplayName("한 팀에 대한 세부정보를 받아오는 테스트 - 실패 (팀에 대한 정보를 찾을 수 없음)")
	void findByTeamIdTest1() {
	    // Given

	    // When

	    // Then
		assertThrows(IllegalArgumentException.class, () -> teamService.findByTeamId(100L));
	}

	@Test
	@DisplayName("한 팀에 대한 세부정보를 받아오는 테스트 - 성공")
	void findByTeamIdTest2() {
		// Given
		Team team = Team.builder().id(100L).teamName("팀1").teamType(TeamType.STUDY).build();
		UserAccount user1 = UserAccount.builder().userId("user1").build();
		UserAccount user2 = UserAccount.builder().userId("user2").build();
		UserAccount user3 = UserAccount.builder().userId("user3").build();
		TeamMember teamMember1 = TeamMember.builder().userAccount(user1).isTeamLeader(true).team(team).position("백엔드 개발자").build();
		TeamMember teamMember2 = TeamMember.builder().userAccount(user2).isTeamLeader(false).team(team).position("프론트엔드 개발자").build();
		TeamMember teamMember3 = TeamMember.builder().userAccount(user3).isTeamLeader(false).team(team).position("디자이너").build();
		EmptyTeamMember emptyTeamMember1 = EmptyTeamMember.builder().team(team).position("AI 개발자").build();
		EmptyTeamMember emptyTeamMember2 = EmptyTeamMember.builder().team(team).position("백엔드 개발자").build();

		given(teamRepository.findById(100L)).willReturn(Optional.ofNullable(team));
		given(teamMemberRepository.findAllByTeamId(100L)).willReturn(List.of(teamMember1, teamMember2, teamMember3));
		given(emptyTeamMemberRepository.findAllByTeamId(100L)).willReturn(List.of(emptyTeamMember1, emptyTeamMember2));

		// When
		TeamDetailResponse result = teamService.findByTeamId(100L);

		// Then
		assertDoesNotThrow(() -> teamService.findByTeamId(100L));
		assertEquals(3, result.getTeamMemberResponses().size());
		assertEquals(2, result.getEmptyTeamMemberResponses().size());
	}

	@Test
	@DisplayName("회원ID로 참여한 팀 정보 불러오기")
	void findByUserIdTest() {
	    // Given
		Team team1 = Team.builder().id(100L).teamName("팀1").teamType(TeamType.STUDY).build();
		Team team2 = Team.builder().id(101L).teamName("팀2").teamType(TeamType.CONTEST).isOpened(true).build();
		Team team3 = Team.builder().id(102L).teamName("팀3").teamType(TeamType.STUDY).isOpened(true).build();
		Team team4 = Team.builder().id(103L).teamName("팀4").teamType(TeamType.CONTEST).isDeleted(true).build();
		Team team5 = Team.builder().id(104L).teamName("팀5").teamType(TeamType.STUDY).build();
		UserAccount user = UserAccount.builder().userId("testId").build();
		TeamMember teamMember1 = TeamMember.builder().userAccount(user).isTeamLeader(true).team(team1).position("백엔드 개발자").build();
		TeamMember teamMember2 = TeamMember.builder().userAccount(user).isTeamLeader(false).team(team2).position("프론트엔드 개발자").build();
		TeamMember teamMember3 = TeamMember.builder().userAccount(user).isTeamLeader(true).team(team3).position("DevOps").build();
		TeamMember teamMember4 = TeamMember.builder().userAccount(user).isTeamLeader(false).team(team4).position("DBA").build();
		TeamMember teamMember5 = TeamMember.builder().userAccount(user).isTeamLeader(false).team(team5).position("백엔드 개발자").build();
		given(teamMemberRepository.findByUserId("testId")).willReturn(List.of(teamMember1, teamMember2, teamMember3, teamMember4, teamMember5));

	    // When
		List<TeamMemberHistoryResponse> result = teamService.findMemberHistory("testId");

		// Then
		assertEquals(5, result.size());
		assertEquals(teamMemberToTeamMemberHistoryResponse(teamMember1, "testId"), result.get(0));
		assertEquals(teamMemberToTeamMemberHistoryResponse(teamMember2, "testId"), result.get(1));
		assertEquals(teamMemberToTeamMemberHistoryResponse(teamMember3, "testId"), result.get(2));
		assertEquals(teamMemberToTeamMemberHistoryResponse(teamMember4, "testId"), result.get(3));
		assertEquals(teamMemberToTeamMemberHistoryResponse(teamMember5, "testId"), result.get(4));
	}

	private TeamMemberHistoryResponse teamMemberToTeamMemberHistoryResponse(TeamMember teamMember, String userId) {
		return TeamMemberHistoryResponse.builder()
				.userId(userId)
				.isTeamLeader(teamMember.isTeamLeader())
				.position(teamMember.getPosition())
				.teamId(teamMember.getTeam().getId())
				.teamName(teamMember.getTeam().getTeamName())
				.teamType(teamMember.getTeam().getTeamType())
				.build();
	}
}
