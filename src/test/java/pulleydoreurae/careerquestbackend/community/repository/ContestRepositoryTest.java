package pulleydoreurae.careerquestbackend.community.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestSearchRequest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
@DataJpaTest
@DisplayName("공모전 RepositoryTest")
@Transactional
@Import(QueryDSLConfig.class)
class ContestRepositoryTest {

	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	ContestRepository contestRepository;
	@Autowired
	EntityManager em;

	@Test
	@DisplayName("공모전 저장")
	void contestSaveTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post);
		Contest contest = new Contest(100L, post, "정부주관", "대학생", "서울", "서울시청", 100000L);

	    // When
		contestRepository.save(contest);

	    // Then
		Contest result = contestRepository.findById(contest.getId()).get();
		assertEquals("공모전", result.getPost().getTitle());
		assertEquals("대학생", result.getTarget());
		assertEquals("서울", result.getRegion());
		assertEquals("서울시청", result.getOrganizer());
		assertEquals(100000L, result.getTotalPrize());
	}

	@Test
	@DisplayName("공모전 삭제")
	void contestDeleteTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post);
		Contest contest = new Contest(100L, post, "정부주관", "대학생", "서울", "서울시청", 100000L);

		// When
		contestRepository.save(contest);
		contestRepository.delete(contest);

	    // Then
		assertEquals(Optional.empty(), contestRepository.findById(contest.getId()));
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (공모전 분야)")
	void findAllBySearchRequestTest1() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 100000L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100000L);
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory("정부주관").build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findAny().get();

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getTotalElements());
		assertEquals("서울", getOne.getRegion());
		assertEquals("대학생", getOne.getTarget());
		assertEquals("보건복지부", getOne.getOrganizer());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (대상)")
	void findAllBySearchRequestTest2() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 100000L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100000L);
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().target("대학생").build();
		Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 부산주관 공모전이 꺼내져야함.

		assertEquals(2, result.getNumberOfElements());
		assertEquals(2, result.getTotalPages());
		assertEquals(3, result.getTotalElements());
		assertEquals("부산", getOne.getRegion());
		assertEquals("부산주관", getOne.getContestCategory());
		assertEquals("대학생", getOne.getTarget());
		assertEquals("부산시청", getOne.getOrganizer());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (지역)")
	void findAllBySearchRequestTest3() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 100000L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100000L);
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().region("서울").build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 서울주관이 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(2, result.getTotalPages());
		assertEquals(2, result.getTotalElements());
		assertEquals("서울", getOne.getRegion());
		assertEquals("서울주관", getOne.getContestCategory());
		assertEquals("대학생", getOne.getTarget());
		assertEquals("서울시청", getOne.getOrganizer());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (주관처)")
	void findAllBySearchRequestTest4() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 100000L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100000L);
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().organizer("보건복지부").build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 정부주관 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getTotalElements());
		assertEquals("서울", getOne.getRegion());
		assertEquals("정부주관", getOne.getContestCategory());
		assertEquals("대학생", getOne.getTarget());
		assertEquals("보건복지부", getOne.getOrganizer());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (최소상금)")
	void findAllBySearchRequestTest5() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 99999L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100001L);
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().totalPrize(100000L).build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 부산주관이 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(2, result.getTotalPages());
		assertEquals(2, result.getTotalElements());
		assertEquals("부산", getOne.getRegion());
		assertEquals("부산주관", getOne.getContestCategory());
		assertEquals("대학생", getOne.getTarget());
		assertEquals("부산시청", getOne.getOrganizer());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (분야 + 대상)")
	void findAllBySearchRequestTest6() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 99999L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100001L);
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory("부산주관").target("대학생").build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 부산주관 + 대학생 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getTotalElements());
		assertEquals("부산", getOne.getRegion());
		assertEquals("부산주관", getOne.getContestCategory());
		assertEquals("대학생", getOne.getTarget());
		assertEquals("부산시청", getOne.getOrganizer());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (분야 + 대상 + 주관처)")
	void findAllBySearchRequestTest7() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Post post4 = new Post(103L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post4);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 99999L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100001L);
		contestRepository.save(contest3);
		Contest contest4 = new Contest(103L, post4, "부산주관", "대학생", "부산", "병무청", 100001L);
		contestRepository.save(contest4);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory("부산주관").target("대학생").organizer("부산시청").build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 부산주관 + 대학생 + 부산시청 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalElements());
		assertEquals("부산", getOne.getRegion());
		assertEquals("부산주관", getOne.getContestCategory());
		assertEquals("대학생", getOne.getTarget());
		assertEquals("부산시청", getOne.getOrganizer());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (분야 + 대상 + 주관처 + 지역)")
	void findAllBySearchRequestTest8() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Post post4 = new Post(103L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post4);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 99999L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100001L);
		contestRepository.save(contest3);
		Contest contest4 = new Contest(103L, post4, "부산주관", "대학생", "부산", "병무청", 100001L);
		contestRepository.save(contest4);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory("부산주관").target("대학생").organizer("부산시청").region("철원").build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);

		assertEquals(Optional.empty(), result.get().findFirst()); // 검색결과는 없다.
		assertEquals(0, result.getNumberOfElements());
		assertEquals(0, result.getTotalPages());
		assertEquals(0, result.getTotalElements());
	}

	@Test
	@DisplayName("공모전 정보 게시글로 찾기")
	void findByPostId() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, "정부주관", "대학생", "서울", "보건복지부", 99999L);
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, "서울주관", "대학생", "서울", "서울시청", 100000L);
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, "부산주관", "대학생", "부산", "부산시청", 100001L);
		contestRepository.save(contest3);

	    // When
		Contest result = contestRepository.findByPostId(post1.getId()).get();

		// Then
		assertEquals("정부주관", result.getContestCategory());
		assertEquals("대학생", result.getTarget());
		assertEquals("서울", result.getRegion());
		assertEquals("보건복지부", result.getOrganizer());
		assertEquals(99999L, result.getTotalPrize());
	}
}
