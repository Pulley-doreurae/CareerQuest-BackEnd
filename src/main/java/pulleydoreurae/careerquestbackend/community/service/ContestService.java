package pulleydoreurae.careerquestbackend.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestSearchRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.ContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.community.exception.PostSaveException;
import pulleydoreurae.careerquestbackend.community.repository.ContestRepository;
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

		return ContestResponse.builder()
				.contestCategory(contest.getContestCategory())
				.target(contest.getTarget())
				.region(contest.getRegion())
				.organizer(contest.getOrganizer())
				.totalPrize(contest.getTotalPrize())
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
					.contestCategory(contest.getContestCategory())
					.target(contest.getTarget())
					.region(contest.getRegion())
					.organizer(contest.getOrganizer())
					.totalPrize(contest.getTotalPrize())
					.build());
		});

		return response;
	}
}