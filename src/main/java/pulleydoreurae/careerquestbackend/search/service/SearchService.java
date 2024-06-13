package pulleydoreurae.careerquestbackend.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;
import pulleydoreurae.careerquestbackend.certification.service.CertificationService;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.ContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.repository.ContestRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.community.service.CommonCommunityService;
import pulleydoreurae.careerquestbackend.community.service.ContestService;
import pulleydoreurae.careerquestbackend.search.domain.response.SearchRankResponse;
import pulleydoreurae.careerquestbackend.search.domain.response.SearchResultResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponseWithPageInfo;
import pulleydoreurae.careerquestbackend.team.domain.entity.Team;
import pulleydoreurae.careerquestbackend.team.repository.TeamRepository;
import pulleydoreurae.careerquestbackend.team.service.TeamService;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {

	private final CertificationRepository certificationRepository;
	private final ContestRepository contestRepository;
	private final PostRepository postRepository;
	private final TeamRepository teamRepository;

	private final CommonCommunityService commonCommunityService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Transactional(readOnly = true)
	public SearchResultResponse findAllByKeyword(String keyword, Pageable pageable){

		// 자격증 조회
		Page<Certification> certifications = certificationRepository.searchByKeyword(keyword, pageable);
		List<CertificationResponse> certificationResponses = new ArrayList<>();
		certifications.forEach(certification -> {
			certificationResponses.add(CertificationResponse.builder()
					.certificationName(certification.getCertificationName())
					.certificationCode(certification.getCertificationCode())
					.qualification(certification.getQualification())
					.registrationLink(certification.getRegistrationLink())
					.organizer(certification.getOrganizer())
					.aiSummary(certification.getAiSummary())
				.build());
		});

		// 공모전 조회
		Page<Contest> contests = contestRepository.findByKeyword(keyword, pageable);
		List<ContestResponse> contestResponses = new ArrayList<>();
		contests.forEach(contest -> {
			contestResponses.add(ContestResponse.builder()
				.contestId(contest.getId())
				.title(contest.getPost().getTitle())
				.content(contest.getPost().getContent())
				.contestCategory(contest.getContestCategory())
				.target(contest.getTarget())
				.region(contest.getRegion())
				.organizer(contest.getOrganizer())
				.totalPrize(contest.getTotalPrize())
				.startDate(contest.getStartDate())
				.endDate(contest.getEndDate())
				.build());
		});

		// 커뮤니티 조회
		Page<Post> posts = postRepository.searchByKeyword(keyword, pageable);
		List<PostResponse> postResponses = commonCommunityService.postListToPostResponseList(posts);

		// 스터디 조회
		Page<Team> teams = teamRepository.searchByKeyword(keyword, pageable);
		int totalPages = teams.getTotalPages();

		TeamResponseWithPageInfo teamResponses = new TeamResponseWithPageInfo(totalPages);
		teams.forEach(team -> {
			TeamResponse detail = TeamResponse.builder()
				.teamId(team.getId())
				.teamName(team.getTeamName())
				.teamType(team.getTeamType())
				.maxMember(team.getMaxMember())
				.startDate(team.getStartDate())
				.endDate(team.getEndDate())
				.build();
			teamResponses.getTeamResponse().add(detail);
		});

		if( !certifications.isEmpty() || !contests.isEmpty() || !posts.isEmpty() || !teams.isEmpty() ) incrementSearchCount(keyword);

		return SearchResultResponse.builder()
			.certificationList(certificationResponses)
			.contestList(contestResponses)
			.postList(postResponses)
			.teamList(teamResponses)
			.msg("검색한 키워드 : " + keyword)
			.build();
	}

	private void incrementSearchCount(String keyword) {
		redisTemplate.opsForZSet().incrementScore("hitRanking", keyword, 1);
	}

	private Set<String> getTopKeywords(String key) {
		int topN = 9;
		return redisTemplate.opsForZSet().reverseRange(key, 0, topN);
	}

	public List<SearchRankResponse> getShowRanking() {
		Set<String> currentShowRankings = getTopKeywords("showRanking");
		List<SearchRankResponse> responses = new ArrayList<>();

		int rank = 1;
		for (String keyword : currentShowRankings) {
			String rankChange = (String) redisTemplate.opsForHash().get("showRankingRankChanges", keyword);
			responses.add(new SearchRankResponse(keyword, rankChange, rank));
			rank++;
		}

		return responses;
	}

	private void updateShowRanking(List<String> keywords, List<String> rankChanges) {
		redisTemplate.delete("showRanking");
		redisTemplate.delete("showRankingRankChanges");

		for (int i = 0; i < keywords.size(); i++) {
			// 각 키워드의 점수를 유지하면서 추가
			redisTemplate.opsForZSet().add("showRanking", keywords.get(i), keywords.size() - i);
			redisTemplate.opsForHash().put("showRankingRankChanges", keywords.get(i), rankChanges.get(i));
		}
	}

	@Scheduled(cron = "0 0 0/1 * * *")
	public void updateRankings() {
		Set<String> topHitKeywords = getTopKeywords("hitRanking");
		Set<String> currentShowRankings = getTopKeywords("showRanking");

		List<String> topHitList = new ArrayList<>(topHitKeywords);
		List<String> currentShowList = new ArrayList<>(currentShowRankings);
		List<String> rankChanges = new ArrayList<>();

		for (int i = 0; i < topHitList.size(); i++) {
			String keyword = topHitList.get(i);
			int currentIndex = currentShowList.indexOf(keyword);
			String change = (currentIndex == -1) ? "N" : String.valueOf(currentIndex - i);
			rankChanges.add(change);
		}

		updateShowRanking(topHitList, rankChanges);
	}

}
