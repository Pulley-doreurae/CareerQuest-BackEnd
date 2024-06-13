package pulleydoreurae.careerquestbackend.community.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.ContestCategory;
import pulleydoreurae.careerquestbackend.community.domain.Organizer;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.community.domain.Region;
import pulleydoreurae.careerquestbackend.community.domain.Target;
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
		Contest contest = new Contest(100L, post, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));

	    // When
		contestRepository.save(contest);

	    // Then
		Contest result = contestRepository.findById(contest.getId()).get();
		assertEquals("공모전", result.getPost().getTitle());
		assertEquals(Target.UNIVERSITY, result.getTarget());
		assertEquals(Region.SEOUL, result.getRegion());
		assertEquals(Organizer.LOCAL_GOVERNMENT, result.getOrganizer());
		assertEquals(100000L, result.getTotalPrize());
	}

	@Test
	@DisplayName("공모전 삭제")
	void contestDeleteTest1() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post);
		Contest contest = new Contest(100L, post, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));

		// When
		contestRepository.save(contest);
		contestRepository.delete(contest);

	    // Then
		assertEquals(Optional.empty(), contestRepository.findById(contest.getId()));
	}

	@Test
	@DisplayName("공모전 postId로 삭제")
	void contestDeleteTest2() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post = new Post(100L, userAccount, "공모전", "내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post);
		Contest contest = new Contest(100L, post, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest);

		// When
		contestRepository.deleteByPostId(post.getId());

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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.SEOUL, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.UNIVERSITY, Region.SEOUL, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory(ContestCategory.CONTEST).build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findAny().get();

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getTotalElements());
		assertEquals(Region.SEOUL, getOne.getRegion());
		assertEquals(Target.UNIVERSITY, getOne.getTarget());
		assertEquals(Organizer.GOVERNMENT, getOne.getOrganizer());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.UNIVERSITY, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().target(Target.UNIVERSITY).build();
		Pageable pageable = PageRequest.of(0, 2, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get();

		assertEquals(2, result.getNumberOfElements());
		assertEquals(2, result.getTotalPages());
		assertEquals(3, result.getTotalElements());
		assertEquals(Region.BUSAN, getOne.getRegion());
		assertEquals(ContestCategory.ARCHITECTURE, getOne.getContestCategory());
		assertEquals(Target.UNIVERSITY, getOne.getTarget());
		assertEquals(Organizer.LOCAL_GOVERNMENT, getOne.getOrganizer());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.SEOUL, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().region(Region.SEOUL).build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get();

		assertEquals(1, result.getNumberOfElements());
		assertEquals(2, result.getTotalPages());
		assertEquals(2, result.getTotalElements());
		assertEquals(Region.SEOUL, getOne.getRegion());
		assertEquals(ContestCategory.ART, getOne.getContestCategory());
		assertEquals(Target.UNIVERSITY, getOne.getTarget());
		assertEquals(Organizer.PUBLIC_INSTITUTION, getOne.getOrganizer());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().organizer(Organizer.GOVERNMENT).build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 정부주관 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getTotalElements());
		assertEquals(Region.SEOUL, getOne.getRegion());
		assertEquals(ContestCategory.CONTEST, getOne.getContestCategory());
		assertEquals(Target.HIGH_SCHOOL, getOne.getTarget());
		assertEquals(Organizer.GOVERNMENT, getOne.getOrganizer());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 99999L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().totalPrize(100000L).build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 부산주관이 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(2, result.getTotalPages());
		assertEquals(2, result.getTotalElements());
		assertEquals(Region.BUSAN, getOne.getRegion());
		assertEquals(ContestCategory.ARCHITECTURE, getOne.getContestCategory());
		assertEquals(Target.EVERYONE, getOne.getTarget());
		assertEquals(Organizer.LOCAL_GOVERNMENT, getOne.getOrganizer());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.GOVERNMENT, 99999L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.UNIVERSITY, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory(ContestCategory.ARCHITECTURE).target(Target.UNIVERSITY).build();
		Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 부산주관 + 대학생 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getTotalElements());
		assertEquals(Region.BUSAN, getOne.getRegion());
		assertEquals(ContestCategory.ARCHITECTURE, getOne.getContestCategory());
		assertEquals(Target.UNIVERSITY, getOne.getTarget());
		assertEquals(Organizer.LOCAL_GOVERNMENT, getOne.getOrganizer());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 99999L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);
		Contest contest4 = new Contest(103L, post4, ContestCategory.EMPLOYMENT_STARTUP, Target.FOREIGNER, Region.DAEGU, Organizer.ALL, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest4);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory(ContestCategory.ART).target(Target.UNIVERSITY).organizer(Organizer.PUBLIC_INSTITUTION).build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);
		Contest getOne = result.get().findFirst().get(); // 역순으로 가져오므로 마지막에 입력한 부산주관 + 대학생 + 부산시청 꺼내짐

		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalElements());
		assertEquals(Region.ULSAN, getOne.getRegion());
		assertEquals(ContestCategory.ART, getOne.getContestCategory());
		assertEquals(Target.UNIVERSITY, getOne.getTarget());
		assertEquals(Organizer.PUBLIC_INSTITUTION, getOne.getOrganizer());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 99999L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);
		Contest contest4 = new Contest(103L, post4, ContestCategory.EMPLOYMENT_STARTUP, Target.FOREIGNER, Region.DAEGU, Organizer.ALL, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest4);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory(ContestCategory.EMPLOYMENT_STARTUP).target(Target.FOREIGNER).organizer(Organizer.ALL).region(Region.DAEJEON).build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);

		assertEquals(Optional.empty(), result.get().findFirst()); // 검색결과는 없다.
		assertEquals(0, result.getNumberOfElements());
		assertEquals(0, result.getTotalPages());
		assertEquals(0, result.getTotalElements());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (분야 + 대상 + 주관처 + 지역 + 날짜)")
	void findAllBySearchRequestTest9() {
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 99999L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);
		Contest contest4 = new Contest(103L, post4, ContestCategory.EMPLOYMENT_STARTUP, Target.FOREIGNER, Region.DAEGU, Organizer.ALL, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest4);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory(ContestCategory.EMPLOYMENT_STARTUP).target(Target.FOREIGNER).organizer(Organizer.ALL).region(Region.DAEGU).startDate(LocalDate.of(2020, 1, 10)).endDate(LocalDate.of(2020, 1, 10)).build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);

		assertEquals(Optional.empty(), result.get().findFirst()); // 검색결과는 없다.
		assertEquals(0, result.getNumberOfElements());
		assertEquals(0, result.getTotalPages());
		assertEquals(0, result.getTotalElements());
	}

	@Test
	@DisplayName("공모전 검색조건으로 검색 (분야 + 대상 + 주관처 + 지역 + 날짜)")
	void findAllBySearchRequestTest10() {
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.HIGH_SCHOOL, Region.SEOUL, Organizer.GOVERNMENT, 99999L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.EVERYONE, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);
		Contest contest4 = new Contest(103L, post4, ContestCategory.EMPLOYMENT_STARTUP, Target.FOREIGNER, Region.DAEGU, Organizer.ALL, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest4);

		// When
		ContestSearchRequest request = ContestSearchRequest.builder().contestCategory(ContestCategory.ARCHITECTURE).target(Target.EVERYONE).organizer(Organizer.LOCAL_GOVERNMENT).region(Region.BUSAN).startDate(LocalDate.of(2020, 1, 10)).endDate(LocalDate.of(2025, 1, 10)).build();
		Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
		Page<Contest> result = contestRepository.findAllBySearchRequest(request, pageable);

		assertEquals(1, result.getSize());
		assertEquals(1, result.getNumberOfElements());
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getTotalElements());
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
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.GOVERNMENT, 99999L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.ULSAN, Organizer.PUBLIC_INSTITUTION, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.UNIVERSITY, Region.BUSAN, Organizer.LOCAL_GOVERNMENT, 100001L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

	    // When
		Contest result = contestRepository.findByPostId(post1.getId()).get();

		// Then
		assertEquals(ContestCategory.CONTEST, result.getContestCategory());
		assertEquals(Target.UNIVERSITY, result.getTarget());
		assertEquals(Region.SEOUL, result.getRegion());
		assertEquals(Organizer.GOVERNMENT, result.getOrganizer());
		assertEquals(99999L, result.getTotalPrize());
	}

	@Test
	@DisplayName("공모전의 내용을 공모전을 검색하는 테스트")
	void findByKeywordTest() {
	    // Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		userAccountRepository.save(userAccount);
		Post post1 = new Post(100L, userAccount, "공모전제목", "내용공모전내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post1);
		Post post2 = new Post(101L, userAccount, "공모전공모전", "내용내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post2);
		Post post3 = new Post(102L, userAccount, "공공모모전전", "내용내용내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		postRepository.save(post3);
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest1);
		Contest contest2 = new Contest(101L, post2, ContestCategory.ART, Target.UNIVERSITY, Region.SEOUL, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest2);
		Contest contest3 = new Contest(102L, post3, ContestCategory.ARCHITECTURE, Target.UNIVERSITY, Region.SEOUL, Organizer.LOCAL_GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));
		contestRepository.save(contest3);

	    // When
		Pageable pageable = PageRequest.of(0, 2);
		Page<Contest> result = contestRepository.findByKeyword("공모", pageable);
		List<Contest> list = result.get().toList();

		// Then
		assertEquals(2, result.getTotalPages());
		assertEquals(3, result.getTotalElements());
		assertEquals(2, result.getSize());
		assertEquals(ContestCategory.ARCHITECTURE, list.get(0).getContestCategory());
		assertEquals(ContestCategory.ART, list.get(1).getContestCategory());
	}
}
