package pulleydoreurae.careerquestbackend.team.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.KickRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamDeleteRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamMemberRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamDetailResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponseWithPageInfo;
import pulleydoreurae.careerquestbackend.team.service.TeamService;

/**
 * 팀을 담당할 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class TeamController {

	private final TeamService teamService;

	@GetMapping("/teams")
	public ResponseEntity<TeamResponseWithPageInfo> findAll(@PageableDefault(size = 15) Pageable pageable) {
		TeamResponseWithPageInfo response = teamService.findAll(pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}

	@GetMapping("/teams/{teamType}")
	public ResponseEntity<TeamResponseWithPageInfo> findAllByTeamType(@PathVariable TeamType teamType,
			@PageableDefault(size = 15) Pageable pageable) {
		TeamResponseWithPageInfo response = teamService.findAllByTeamType(teamType, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}

	@GetMapping("/teams-details/{teamId}")
	public ResponseEntity<TeamDetailResponse> findByTeamId(@PathVariable Long teamId) {
		TeamDetailResponse response = teamService.findByTeamId(teamId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(response);
	}

	@PostMapping("/teams")
	public ResponseEntity<SimpleResponse> makeTeam(@Valid @RequestBody TeamRequest request,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		teamService.makeTeam(request);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("팀 생성에 성공했습니다.")
						.build());
	}

	@PatchMapping("/teams")
	public ResponseEntity<SimpleResponse> updateTeam(@Valid @RequestBody TeamRequest request,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		teamService.updateTeam(request);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("팀 수정에 성공했습니다.")
						.build());
	}

	@DeleteMapping("/teams")
	public ResponseEntity<SimpleResponse> deleteTeam(@Valid @RequestBody TeamDeleteRequest request,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		teamService.deleteTeam(request);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("팀 삭제에 성공했습니다.")
						.build());
	}

	@PostMapping("/teams/members")
	public ResponseEntity<SimpleResponse> joinTeam(@Valid @RequestBody TeamMemberRequest request,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		teamService.joinTeam(request);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("팀 참가에 성공했습니다.")
						.build());
	}

	@PatchMapping("/teams/members")
	public ResponseEntity<SimpleResponse> leaveTeam(@Valid @RequestBody TeamMemberRequest request,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		teamService.leaveTeam(request);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("팀을 떠나는데 성공했습니다.")
						.build());
	}

	@DeleteMapping("/teams/members")
	public ResponseEntity<SimpleResponse> kickMember(@Valid @RequestBody KickRequest request,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		teamService.kickMember(request);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("팀에서 회원을 추방하는데 성공했습니다.")
						.build());
	}

	/**
	 * 검증 메서드
	 *
	 * @param bindingResult 검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 SimpleResponse 반환
	 */
	private ResponseEntity<SimpleResponse> validCheck(BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			bindingResult.getAllErrors().forEach(objectError -> {
				String message = objectError.getDefaultMessage();

				sb.append(message).append("\n");
			});

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg(sb.toString())
							.build());
		}
		return null;
	}
}
