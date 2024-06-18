package pulleydoreurae.careerquestbackend.portfolio.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.auth.service.UserAccountService;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.FinalResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.GithubLoginResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.GithubRepoListResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.GithubUserDetailsResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.GitRepoInfo;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.GitRepoLanguage;
import pulleydoreurae.careerquestbackend.portfolio.repository.GitRepoInfoRepository;
import pulleydoreurae.careerquestbackend.portfolio.repository.GitRepoLanguageRepository;

@Slf4j
@Service
public class GithubService {

	private final String clientId;
	private final String clientSecret;
	private final String redirect_uri;
	private final String response_type = "code";
	private final String host;
	private final UserAccountRepository userAccountRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserAccountService userAccountService;
	private final GitRepoInfoRepository gitRepoInfoRepository;
	private final GitRepoLanguageRepository gitRepoLanguageRepository;

	public GithubService(@Value("${LOGIN.GITHUB_CLIENT_ID}") String clientId,
		@Value("${LOGIN.GITHUB_CLIENT_SECRET}") String clientSecret,
		@Value("${LOGIN.GITHUB_REDIRECT_URL}") String redirect_uri,
		@Value("${LOGIN.HOST}") String host,
		UserAccountRepository userAccountRepository,
		BCryptPasswordEncoder bCryptPasswordEncoder, UserAccountService userAccountService,
		GitRepoInfoRepository gitRepoInfoRepository, GitRepoLanguageRepository gitRepoLanguageRepository) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.redirect_uri = redirect_uri;
		this.host = host;
		this.userAccountRepository = userAccountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userAccountService = userAccountService;
		this.gitRepoInfoRepository = gitRepoInfoRepository;
		this.gitRepoLanguageRepository = gitRepoLanguageRepository;
	}

	/**
	 * 리다이렉션할 주소를 반환하는 메서드
	 *
	 * @param authUrl API 의 기본 url 을 전달받는다.
	 * @return 구글 로그인 창으로 리다이렉션시켜줄 주소를 반환한다.
	 */
	public String getRedirectUrl(String authUrl) {

		URI uri = UriComponentsBuilder.fromUriString(authUrl)
			.queryParam("client_id", clientId)
			.queryParam("redirect_uri", redirect_uri)
			.build()
			.toUri();

		return uri.toString();
	}

	public String getToken(String code, String tokenUrl) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("code", code);
		body.add("client_id", clientId);
		body.add("client_secret", clientSecret);
		body.add("redirect_uri", redirect_uri);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		try {
			GithubLoginResponse response = new RestTemplate().exchange(
				tokenUrl,
				HttpMethod.POST,
				entity,
				GithubLoginResponse.class
			).getBody();

			return response.getAccess_token();
		} catch (Exception e) {
			log.error("[로그인-깃허브] : 예외 발생 --- {}", e.getMessage(), e);
			return null;
		}
	}

	// 깃허브에 접속 레포 리스트 가져오기
	public String getUserDetails(String token, String url) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

		try {
			GithubUserDetailsResponse googleUserDetailsResponse = new RestTemplate().exchange(
				url,
				HttpMethod.GET,
				entity,
				GithubUserDetailsResponse.class
			).getBody();

			return googleUserDetailsResponse.getLogin();
		} catch (Exception e) {
			log.error("[로그인-깃허브] : 유효하지않은 액세스토큰 --- {}", e.getMessage());
			return null;
		}
	}

	// Repo 가져오기
	public FinalResponse getRepoLists(String token, String userId, String getRepoUrl) {

		String url = getRepoUrl.replace("USER", userId);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		headers.add("Authorization", "Bearer " + token);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

		try {
			GithubRepoListResponse[] response = new RestTemplate().exchange(
				url,
				HttpMethod.GET,
				entity,
				GithubRepoListResponse[].class
			).getBody();

			List<GitRepoInfo> repositories = new ArrayList<>();
			Map<String, Integer> languageMap = new HashMap<>();

			// 각 리포지토리의 언어 정보를 가져와 합산
			for (GithubRepoListResponse repo : response) {
				ResponseEntity<Map> languagesResponse = new RestTemplate().exchange(
					repo.getLanguages_url(),
					HttpMethod.GET,
					entity,
					Map.class
				);
				Map<String, Integer> languages = languagesResponse.getBody();

				// 리포지토리 정보 추가
				GitRepoInfo repoInfo = GitRepoInfo.builder()
					.name(repo.getName())
					.project_url(repo.getHtml_url()).build();
				repositories.add(repoInfo);

				// 언어 합산
				if (languages != null) {
					for (Map.Entry<String, Integer> entry : languages.entrySet()) {
						languageMap.put(entry.getKey(), languageMap.getOrDefault(entry.getKey(), 0) + entry.getValue());
					}
				}
			}

			// 상위 3개 언어 추출
			Map<String, Integer> topLanguages = languageMap.entrySet()
				.stream()
				.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
				.limit(3)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			int totalLanguageCount = topLanguages.values().stream().mapToInt(Integer::intValue).sum();

			// 퍼센트 계산
			Map<String, Double> languagePercentages = topLanguages.entrySet().stream()
				.collect(Collectors.toMap(
					Map.Entry::getKey,
					entry -> (entry.getValue() * 100.0) / totalLanguageCount
				));

			FinalResponse finalResponse = FinalResponse.builder()
				.gitRepoInfoList(repositories)
				.languages(languagePercentages)
				.build();

			return finalResponse;
		} catch (Exception e) {
			log.error("[로그인-깃허브] : 유효하지않은 액세스토큰 --- {}", e.getMessage());
			return null;
		}

	}

	@Transactional
	public void saveRepoLists(String userId, FinalResponse response) {
		UserAccount user = userAccountService.findUserByUserId(userId);

		if (gitRepoInfoRepository.existsByUserAccount(user)) {
			gitRepoInfoRepository.deleteAllByUserAccount(user);
		}

		if (gitRepoLanguageRepository.existsByUserAccount(user)) {
			gitRepoLanguageRepository.deleteAllByUserAccount(user);
		}

		List<GitRepoInfo> repositories = response.getGitRepoInfoList().stream().map(repoInfo -> {
			GitRepoInfo repo = GitRepoInfo.builder()
				.name(repoInfo.getName())
				.project_url(repoInfo.getProject_url())
				.userAccount(user)
				.build();
			return repo;
		}).collect(Collectors.toList());

		// 리포지토리 저장
		gitRepoInfoRepository.saveAll(repositories);

		// 저장할 언어 목록
		List<GitRepoLanguage> languages = response.getLanguages().entrySet().stream().map(entry -> {
			GitRepoLanguage language = GitRepoLanguage.builder()
				.name(entry.getKey())
				.count(entry.getValue())
				.userAccount(user)
				.build();
			return language;
		}).collect(Collectors.toList());

		// 언어 저장
		gitRepoLanguageRepository.saveAll(languages);

	}
}
