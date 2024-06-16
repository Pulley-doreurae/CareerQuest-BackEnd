package pulleydoreurae.careerquestbackend.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.service.CommonService;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestSearchRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.JoinContestRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.ContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.JoinContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.JoinedContest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.community.exception.PostSaveException;
import pulleydoreurae.careerquestbackend.community.repository.ContestRepository;
import pulleydoreurae.careerquestbackend.community.repository.JoinedContestRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;

/**
 * 공모전 서비스
 *
 * @author : parkjihyeok
 * @since : 2024/05/26
 */
@Service
@RequiredArgsConstructor
public class ContestService {

	private final PostService postService;
	private final PostRepository postRepository;
	private final ContestRepository contestRepository;
	private final CommonCommunityService commonCommunityService;
	private final JoinedContestRepository joinedContestRepository;
	private final CommonService commonService;

	/**
	 * 게시글 + 공모전정보를 함께 저장하는 메서드 (하나의 트랜잭션으로 묶어 게시글 저장에 실패하면 공모전 저장에도 실패한다.)
	 *
	 * @param postRequest    게시글 요청정보
	 * @param contestRequest 공모전 요청정보
	 */
	@Transactional
	public void save(PostRequest postRequest, ContestRequest contestRequest) {
		Long postId = postService.savePost(postRequest);

		Optional<Post> findByPostId = postRepository.findById(postId);
		if (findByPostId.isEmpty()) {
			throw new PostSaveException("게시글(공모전) 저장에 실패했습니다.");
		}
		Post post = findByPostId.get();

		Contest contest = Contest.builder()
				.post(post)
				.contestCategory(contestRequest.getContestCategory())
				.target(contestRequest.getTarget())
				.region(contestRequest.getRegion())
				.organizer(contestRequest.getOrganizer())
				.totalPrize(contestRequest.getTotalPrize())
				.build();

		contestRepository.save(contest);
	}

	/**
	 * 공모전 수정 메서드
	 *
	 * @param postId         게시글 id
	 * @param postRequest    게시글 수정요청
	 * @param contestRequest 공모전 정보 수정요청
	 */
	@Transactional
	public void update(Long postId, PostRequest postRequest, ContestRequest contestRequest) {
		postService.updatePost(postId, postRequest);

		Optional<Contest> findByPostId = contestRepository.findByPostId(postId);
		if (findByPostId.isEmpty()) {
			throw new PostNotFoundException("공모전을 불러오는데 실패했습니다.");
		}
		Contest contest = findByPostId.get();

		Contest updatedContest = Contest.builder()
				.id(contest.getId())
				.post(contest.getPost())
				.contestCategory(contestRequest.getContestCategory())
				.target(contestRequest.getTarget())
				.region(contestRequest.getRegion())
				.organizer(contestRequest.getOrganizer())
				.totalPrize(contestRequest.getTotalPrize())
				.build();

		contestRepository.save(updatedContest);
	}

	/**
	 * 게시글과 공모전을 함께 삭제하는 메서드
	 *
	 * @param postId 게시글 정보
	 * @param userId 작성자 정보
	 */
	@Transactional
	public void delete(Long postId, String userId) {
		postService.deletePost(postId, userId);
		contestRepository.deleteByPostId(postId);
	}

	/**
	 * postId로 공모전정보를 불러오는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 공모전 정보
	 */
	public ContestResponse findByPostId(Long postId) {

		Optional<Contest> byPostId = contestRepository.findByPostId(postId);

		if (byPostId.isEmpty()) {
			throw new PostNotFoundException("공모전을 불러오는데 실패했습니다.");
		}

		Contest contest = byPostId.get();
		Post post = contest.getPost();

		return ContestResponse.builder()
				.contestId(contest.getId())
				.title(post.getTitle())
				.content(post.getContent())
				.images(commonCommunityService.postImageToStringList(post))
				.view(post.getView())
				.contestCategory(contest.getContestCategory())
				.target(contest.getTarget())
				.region(contest.getRegion())
				.organizer(contest.getOrganizer())
				.totalPrize(contest.getTotalPrize())
				.startDate(contest.getStartDate())
				.endDate(contest.getEndDate())
				.build();
	}

	/**
	 * 공모전 검색 조건으로 검색한 결과를 반환하는 메서드
	 *
	 * @param request 검색조건
	 * @return 수행결과
	 */
	public List<ContestResponse> findBySearchRequest(ContestSearchRequest request, Pageable pageable) {
		Page<Contest> bySearchRequest = contestRepository.findAllBySearchRequest(request, pageable);

		List<ContestResponse> response = new ArrayList<>();
		bySearchRequest.forEach(contest -> {
			response.add(ContestResponse.builder()
					.contestId(contest.getId())
					.contestCategory(contest.getContestCategory())
					.target(contest.getTarget())
					.region(contest.getRegion())
					.organizer(contest.getOrganizer())
					.totalPrize(contest.getTotalPrize())
					.startDate(contest.getStartDate())
					.endDate(contest.getEndDate())
					.build());
		});

		return response;
	}

	/**
	 * 공모전 참여 메서드
	 *
	 * @param request 참여 요청
	 */
	public void joinContest(JoinContestRequest request) {
		Contest contest = findContest(request.getContestId());
		UserAccount user = commonService.findUserAccount(request.getUserId(), true);
		JoinedContest joinedContest = JoinedContest.builder().contest(contest).userAccount(user).build();
		joinedContestRepository.save(joinedContest);
	}

	/**
	 * 공모전 참여에서 제거
	 *
	 * @param request 참여 요청
	 */
	public void removeFromJoinContest(JoinContestRequest request) {
		Contest contest = findContest(request.getContestId());
		UserAccount user = commonService.findUserAccount(request.getUserId(), true);
		joinedContestRepository.deleteByContestIdAndUserAccount(contest.getId(), user);
	}

	/**
	 * 참여 공모전 리스트 조회 메서드
	 *
	 * @param userId 회원ID
	 * @return 결과 리스트
	 */
	public List<JoinContestResponse> findJoinContest(String userId) {
		return joinedContestRepository.findByUserId(userId)
				.stream()
				.map(jc ->
						JoinContestResponse.builder()
								.postId(jc.getContest().getPost().getId())
								.contestId(jc.getContest().getId())
								.joinContestId(jc.getId())
								.userId(jc.getUserAccount().getUserId())
								.title(jc.getContest().getPost().getTitle())
								.build()
				).toList();
	}

	/**
	 * 공모전을 가져오는 메서드
	 * @param contestId 공모전 ID
	 * @return 공모전 엔티티
	 */
	private Contest findContest(Long contestId) {
		Optional<Contest> byId = contestRepository.findById(contestId);
		if (byId.isEmpty()) {
			throw new PostNotFoundException("공모전 정보를 찾을 수 없습니다.");
		}
		return byId.get();
	}
}
