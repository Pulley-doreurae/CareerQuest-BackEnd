package pulleydoreurae.careerquestbackend.community.service;

import static org.assertj.core.api.Assertions.*;
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

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.service.CommonService;
import pulleydoreurae.careerquestbackend.community.domain.ContestCategory;
import pulleydoreurae.careerquestbackend.community.domain.Organizer;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.community.domain.Region;
import pulleydoreurae.careerquestbackend.community.domain.Target;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestSearchRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.JoinContestRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.ContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.JoinContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.JoinedContest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.exception.PostDeleteException;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.community.exception.PostSaveException;
import pulleydoreurae.careerquestbackend.community.exception.PostUpdateException;
import pulleydoreurae.careerquestbackend.community.repository.ContestRepository;
import pulleydoreurae.careerquestbackend.community.repository.JoinedContestRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/05/26
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("공모전 서비스 테스트")
class ContestServiceTest {

	@InjectMocks ContestService contestService;
	@Mock ContestRepository contestRepository;
	@Mock PostRepository postRepository;
	@Mock PostService postService;
	@Mock CommonCommunityService commonCommunityService;
	@Mock JoinedContestRepository joinedContestRepository;
	@Mock CommonService commonService;

	@Test
	@DisplayName("게시글 + 공모전 저장 테스트 -실패")
	void saveTest1() {
	    // Given
		PostRequest postRequest = PostRequest.builder().build();
		ContestRequest contestRequest = ContestRequest.builder().build();
		given(postService.savePost(postRequest)).willReturn(1L);
		given(postRepository.findById(1L)).willReturn(Optional.empty());

	    // When

	    // Then
		assertThrows(PostSaveException.class, () -> contestService.save(postRequest, contestRequest));
		verify(postRepository).findById(any());
	}

	@Test
	@DisplayName("게시글 + 공모전 저장 테스트 -성공")
	void saveTest2() {
		// Given
		PostRequest postRequest = PostRequest.builder().build();
		ContestRequest contestRequest = ContestRequest.builder().build();
		Post post = Post.builder().build();
		given(postService.savePost(postRequest)).willReturn(1L);
		given(postRepository.findById(1L)).willReturn(Optional.ofNullable(post));

		// When

		// Then
		assertDoesNotThrow(() -> contestService.save(postRequest, contestRequest));
		verify(postRepository).findById(any());
		verify(contestRepository).save(any());
	}

	@Test
	@DisplayName("게시글 + 공모전 수정 -실패 (게시글 업데이트 실패)")
	void updateTest1() {
	    // Given
		PostRequest postRequest = PostRequest.builder().build();
		ContestRequest contestRequest = ContestRequest.builder().build();
		doThrow(new PostUpdateException("")).when(postService).updatePost(any(), any());

	    // When

	    // Then
		assertThrows(PostUpdateException.class, () -> contestService.update(1L, postRequest, contestRequest));
		verify(contestRepository, never()).findByPostId(any());
	}

	@Test
	@DisplayName("게시글 + 공모전 수정 -실패 (공모전을 찾지 못함)")
	void updateTest3() {
		// Given
		PostRequest postRequest = PostRequest.builder().build();
		ContestRequest contestRequest = ContestRequest.builder().build();
		given(contestRepository.findByPostId(1L)).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> contestService.update(1L, postRequest, contestRequest));
		verify(contestRepository).findByPostId(1L);
	}

	@Test
	@DisplayName("게시글 + 공모전 수정 -성공")
	void updateTest4() {
		// Given
		PostRequest postRequest = PostRequest.builder().build();
		ContestRequest contestRequest = ContestRequest.builder().build();
		Contest contest = Contest.builder().build();
		given(contestRepository.findByPostId(1L)).willReturn(Optional.ofNullable(contest));

		// When

		// Then
		assertDoesNotThrow(() -> contestService.update(1L, postRequest, contestRequest));
		verify(contestRepository).findByPostId(1L);
		verify(contestRepository).save(any());
	}

	@Test
	@DisplayName("게시글 + 공모전 삭제 -실패")
	void deleteTest1() {
	    // Given
		doThrow(new PostDeleteException("게시글 삭제 실패")).when(postService).deletePost(1L, "testId");

	    // When

	    // Then
		assertThrows(PostDeleteException.class, () -> contestService.delete(1L, "testId"));
		verify(contestRepository, never()).deleteByPostId(1L);
	}

	@Test
	@DisplayName("게시글 + 공모전 삭제 -성공")
	void deleteTest2() {
		// Given

		// When

		// Then
		assertDoesNotThrow(() -> contestService.delete(1L, "testId"));
		verify(postService).deletePost(1L, "testId");
		verify(contestRepository).deleteByPostId(1L);
	}

	@Test
	@DisplayName("postId로 공모전정보 불러오기 테스트 -실패")
	void findByPostIdTest1() {
	    // Given
		given(contestRepository.findByPostId(1L)).willReturn(Optional.empty());

	    // When

	    // Then
		assertThrows(PostNotFoundException.class, () -> contestService.findByPostId(1L));
	}

	@Test
	@DisplayName("postId로 공모전정보 불러오기 테스트 -성공")
	void findByPostIdTest2() {
		// Given
		Post post = Post.builder().build();
		Contest contest = Contest.builder().post(post).build();
		given(contestRepository.findByPostId(1L)).willReturn(Optional.ofNullable(contest));

		// When

		// Then
		assertDoesNotThrow(() -> contestService.findByPostId(1L));
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 테스트")
	void findBySearchRequestTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST, null, null);
		postRepository.save(post3);

		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		Page<Contest> list = new PageImpl<>(
				List.of(contest1, contest2, contest3), pageable, 3); // 3개씩 자른다면 마지막 3개가 반환되어야 함

		ContestSearchRequest request = ContestSearchRequest.builder().build();

		given(contestRepository.findAllBySearchRequest(request, pageable)).willReturn(list);

		// When
		List<ContestResponse> result = contestService.findBySearchRequest(request, pageable);

		// Then
		assertThat(result).contains(
				contestToContestResponse(contest1),
				contestToContestResponse(contest2),
				contestToContestResponse(contest3)
		);
	}

	@Test
	@DisplayName("공모전 참여 테스트")
	void joinContestTest1() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		Post post = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST, null, null);
		Contest contest = new Contest(100L, post, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		JoinContestRequest request = new JoinContestRequest(100L, "testId");
		given(contestRepository.findById(request.getContestId())).willReturn(Optional.of(contest));

	    // When
		assertDoesNotThrow(() -> contestService.joinContest(request));

	    // Then
		verify(commonService).findUserAccount(any(), anyBoolean());
		verify(joinedContestRepository).save(any());
	}

	@Test
	@DisplayName("공모전 참여 테스트 -실패")
	void joinContestTest2() {
		// Given
		JoinContestRequest request = new JoinContestRequest(100L, "testId");
		given(contestRepository.findById(request.getContestId())).willReturn(Optional.empty());

		// When
		assertThrows(PostNotFoundException.class, () -> contestService.joinContest(request));

		// Then
		verify(commonService, never()).findUserAccount(any(), anyBoolean());
		verify(joinedContestRepository, never()).save(any());
	}

	@Test
	@DisplayName("공모전 참여에서 제거")
	void removeFromJoinContestTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		Post post = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST, null, null);
		Contest contest = new Contest(100L, post, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		JoinContestRequest request = new JoinContestRequest(100L, "testId");
		given(contestRepository.findById(request.getContestId())).willReturn(Optional.of(contest));

	    // When
		assertDoesNotThrow(() -> contestService.removeFromJoinContest(request));

	    // Then
		verify(joinedContestRepository).deleteByContestIdAndUserAccount(any(), any());
	}

	@Test
	@DisplayName("참여 공모전 리스트 조회 테스트")
	void findJoinContestTest() {
	    // Given
		JoinedContest jc1 = JoinedContest.builder().contest(Contest.builder().post(Post.builder().title("공모전1").build()).build()).userAccount(UserAccount.builder().userId("testId").build()).build();
		JoinedContest jc2 = JoinedContest.builder().contest(Contest.builder().post(Post.builder().title("공모전2").build()).build()).userAccount(UserAccount.builder().userId("testId").build()).build();
		JoinedContest jc3 = JoinedContest.builder().contest(Contest.builder().post(Post.builder().title("공모전3").build()).build()).userAccount(UserAccount.builder().userId("testId").build()).build();
		JoinedContest jc4 = JoinedContest.builder().contest(Contest.builder().post(Post.builder().title("공모전4").build()).build()).userAccount(UserAccount.builder().userId("testId").build()).build();
		given(joinedContestRepository.findByUserId("testId")).willReturn(List.of(jc1, jc2, jc3, jc4));

	    // When
		List<JoinContestResponse> result = contestService.findJoinContest("testId");

		// Then
		assertEquals(4, result.size());
		assertEquals(joinContestToResponse(jc1), result.get(0));
		assertEquals(joinContestToResponse(jc2), result.get(1));
		assertEquals(joinContestToResponse(jc3), result.get(2));
		assertEquals(joinContestToResponse(jc4), result.get(3));
	}

	private ContestResponse contestToContestResponse(Contest contest) {
		return ContestResponse.builder()
				.contestId(contest.getId())
				.contestCategory(contest.getContestCategory())
				.target(contest.getTarget())
				.region(contest.getRegion())
				.organizer(contest.getOrganizer())
				.totalPrize(contest.getTotalPrize())
				.startDate(contest.getStartDate())
				.endDate(contest.getEndDate())
				.build();
	}

	private JoinContestResponse joinContestToResponse(JoinedContest jc) {
		return JoinContestResponse.builder()
				.postId(jc.getContest().getPost().getId())
				.contestId(jc.getContest().getId())
				.joinContestId(jc.getId())
				.userId(jc.getUserAccount().getUserId())
				.title(jc.getContest().getPost().getTitle())
				.build();
	}
}
