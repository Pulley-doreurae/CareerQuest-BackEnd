package pulleydoreurae.careerquestbackend.portfolio.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.service.UserAccountService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.request.UpdateAboutMeRequest;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.RepoInfoListResponse;
import pulleydoreurae.careerquestbackend.portfolio.service.PortfolioService;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PortfolioController {

	private final PortfolioService portfolioService;
	private final UserAccountService userAccountService;

	@GetMapping("/portfolio/selfIntro")
	public ResponseEntity<?> getUserAboutMe(@RequestParam("userId") String userId) {
		return ResponseEntity.status(HttpStatus.OK).body(portfolioService.getUserAboutMe(userId));
	}

	@PostMapping("/portfolio/selfIntro")
	public ResponseEntity<?> updateUserAboutMe(UpdateAboutMeRequest request) {
		UserAccount user = userAccountService.findUserByUserId(request.getUserId());
		portfolioService.updateUserAboutMe(user, request.getAboutMe());

		return ResponseEntity.status(HttpStatus.OK).body(SimpleResponse.builder().msg("새로운 자기소개로 변경했습니다.").build());
	}

	@GetMapping("/portfolio/repos")
	private ResponseEntity<?> getUserRepos(@RequestParam("userId") String userId) {
		UserAccount user = userAccountService.findUserByUserId(userId);
		RepoInfoListResponse response = portfolioService.getUserRepos(user);
		if(response == null) return ResponseEntity.status(HttpStatus.OK).body(SimpleResponse.builder().msg("리스트가 없습니다.").build());
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
