package pulleydoreurae.careerquestbackend.portfolio.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.LoginFailResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.FinalResponse;
import pulleydoreurae.careerquestbackend.portfolio.service.GithubService;

@RequestMapping("/api")
@Slf4j
@Controller
public class GithubController {

	private final GithubService githubService;
	private final String authUrl;
	private final String tokenUrl;
	private final String getUserDetailUrl;
	private final String getRepoUrl;

	public GithubController(GithubService githubService,
		@Value("${LOGIN.GITHUB_AUTH_URL}") String authUrl,
		@Value("${LOGIN.GITHUB_TOKEN_URL}") String tokenUrl,
		@Value("${LOGIN.GITHUB_GET_USERDETAIL_URL}") String getUserDetailUrl,
		@Value("${LOGIN.GITHUB_GET_REPO_URL}") String getRepoUrl,
		@Value("${LOGIN.GITHUB_GET_LANGUAGE_URL}") String getLanguageUrl) {
		this.githubService = githubService;
		this.authUrl = authUrl;
		this.tokenUrl = tokenUrl;
		this.getUserDetailUrl = getUserDetailUrl;
		this.getRepoUrl = getRepoUrl;
	}

	@GetMapping("/login-github")
	public String githubLogin(HttpSession session, @RequestParam("userId") String userId) {
		session.setAttribute("user", userId);
		String url = githubService.getRedirectUrl(authUrl);
		log.info("[로그인-깃허브] : 로그인창으로 이동");

		return "redirect:" + url;
	}

	@GetMapping("/login-github/code")
	@ResponseBody
	public ResponseEntity<?> googleLoginCode(@RequestParam(value = "code") String code, HttpSession session) {
		// code로 accessToken 발급
		String userId = (String)session.getAttribute("user");
		log.info("[로그인-깃허브] : 깃허브 로그인 성공, 요청한 유저 {}", userId);
		String token = githubService.getToken(code, tokenUrl);
		if (token != null) {
			// 발급한 accessToken으로 유저의 이름 가져오기
			String gitUser = githubService.getUserDetails(token, getUserDetailUrl);
			if (gitUser != null) {
				// 유저 아이디와 발급한 accessToken으로 유저 레포 및 언어 가져오기
				FinalResponse response = githubService.getRepoLists(token, gitUser, getRepoUrl);
				// 유저에 저장
				githubService.saveRepoLists(userId, response);

				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(URI.create("/repoSaveOk.html"));
				return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(LoginFailResponse.builder()
				.code(HttpStatus.BAD_REQUEST.toString())
				.error("유효하지 않은 인증코드")
				.build());
	}

}
