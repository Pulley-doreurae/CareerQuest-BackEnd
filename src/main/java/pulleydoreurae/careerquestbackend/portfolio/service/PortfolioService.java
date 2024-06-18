package pulleydoreurae.careerquestbackend.portfolio.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.AboutMeResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.GitRepoInfoResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.GitRepoLanguageResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.RepoInfoListResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.AboutMe;
import pulleydoreurae.careerquestbackend.portfolio.repository.AboutMeRepository;
import pulleydoreurae.careerquestbackend.portfolio.repository.GitRepoInfoRepository;
import pulleydoreurae.careerquestbackend.portfolio.repository.GitRepoLanguageRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

	private final AboutMeRepository aboutMeRepository;
	private final GitRepoInfoRepository gitRepoInfoRepository;
	private final GitRepoLanguageRepository gitRepoLanguageRepository;

	public void updateUserAboutMe(UserAccount user, String content) {

		Optional<AboutMe> aboutMe = aboutMeRepository.findByUserId(user.getUserId());

		if (aboutMe.isPresent()) { // 있으면 내용 업데이트
			aboutMe.get().updateContent(content);
		} else {                    // 없으면 새로 생성해서 저장
			AboutMe newAboutMe = AboutMe.builder().content(content).userAccount(user).build();
			aboutMeRepository.save(newAboutMe);
		}
	}

	public AboutMeResponse getUserAboutMe(String user) {
		return new AboutMeResponse().aboutMeFrom(aboutMeRepository.findByUserId(user).orElseThrow(() -> new UsernameNotFoundException("저장된 자기소개가 없습니다")));
	}

	@Transactional
	public RepoInfoListResponse getUserRepos(UserAccount user) {

		List<GitRepoInfoResponse> gitRepoInfos = gitRepoInfoRepository.findAllByUserAccount(user).stream().map(
			gitRepoInfo -> {
				GitRepoInfoResponse response = GitRepoInfoResponse.builder()
					.repoName(gitRepoInfo.getName())
					.repoUrl(gitRepoInfo.getProject_url())
					.build();
				return response;
			}).toList();

		if (gitRepoInfos.isEmpty()) {
			return null;
		}

		List<GitRepoLanguageResponse> gitRepoLanguageResponses = gitRepoLanguageRepository.findAllByUserAccount(user)
			.stream()
			.map(
				gitRepoLanguage -> {
					GitRepoLanguageResponse response = GitRepoLanguageResponse.builder()
						.language(gitRepoLanguage.getName())
						.count(gitRepoLanguage.getCount())
						.build();
					return response;
				})
			.toList();

		return RepoInfoListResponse.builder()
			.gitRepoInfoList(gitRepoInfos)
			.gitRepoLanguageList(gitRepoLanguageResponses)
			.build();

	}
}
