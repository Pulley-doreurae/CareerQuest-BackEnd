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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.service.CommonService;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.KickRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamDeleteRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamMemberRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamRequest;
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
		TeamRequest request = new TeamRequest(100L, "leaderId", "백엔드 개발자", "해파리", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions);
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
		TeamRequest request = new TeamRequest(100L, "leaderId", "백엔드 개발자", "해파리", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions);
		UserAccount user = UserAccount.builder().userId("leaderId").build();
		given(commonService.findUserAccount("leaderId", true)).willReturn(user);

		// When

		// Then
		assertDoesNotThrow(() -> teamService.makeTeam(request));
		verify(teamRepository).save(any());
	}

	@Test
	@DisplayName("팀 수정 테스트 - 실패(팀 정보를 찾을 수 없음)")
	void updateTeamTest1() {
	    // Given
		List<String> positions = List.of("백엔드 개발자", "프론트엔드 개발자", "프론트엔드 개발자", "디자이너");
		TeamRequest request = new TeamRequest(100L, "leaderId", "백엔드 개발자", "해파리", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions);
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
		TeamRequest request = new TeamRequest(100L, "leaderId", "백엔드 개발자", "해파리", TeamType.STUDY, 5, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 15), positions);
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
		verify(emptyTeamMemberRepository).findAllByTeamId(any());
		verify(emptyTeamMemberRepository).deleteAll(any());
		verify(teamMemberRepository).findAllByTeamId(any());
		verify(teamMemberRepository).deleteAll(any());
		verify(teamRepository).delete(any());
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
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).build();
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
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).build();
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
		Team team = Team.builder().id(100L).teamName("팀 이름").teamType(TeamType.STUDY).build();
		TeamMember teamLeader = TeamMember.builder().userAccount(leader).isTeamLeader(false).team(team).position("백엔드 개발자").build();
		given(commonService.findUserAccount("leaderId", true)).willReturn(leader);
		given(commonService.findUserAccount("memberId", false)).willReturn(member);
		given(teamRepository.findById(100L)).willReturn(Optional.ofNullable(team));
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
		given(teamRepository.findById(100L)).willReturn(Optional.ofNullable(team));
		given(teamMemberRepository.findByUserIdAndTeamId("leaderId", 100L)).willReturn(Optional.ofNullable(teamLeader));
		given(teamMemberRepository.findByUserIdAndTeamId("memberId", 100L)).willReturn(Optional.ofNullable(teamMember));

		// When

		// Then
		assertDoesNotThrow(() -> teamService.kickMember(kickRequest));
		verify(teamMemberRepository).findByUserIdAndTeamId("memberId", 100L);
		verify(teamMemberRepository).delete(any());
	}
}
