package pulleydoreurae.careerquestbackend.certification.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReview;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;

/**
 * 자격증 후기 Repository 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@DataJpaTest
@DisplayName("자격증 후기 구현체 DB 테스트")
public class CertificationReviewRepositoryTest {

	@Autowired
	PostRepository postRepository;
	@Autowired
	UserAccountRepository userAccountRepository;

	@BeforeEach
	void beforeEach() {
		UserAccount user = UserAccount.builder().userId("testId").userName("testName").email("test@email.com")
				.phoneNum("010-1111-2222").password("testPassword").role(UserRole.ROLE_TEMPORARY_USER).build();
		userAccountRepository.save(user);
	}

	@Test
	@DisplayName("자격증 후기 저장 테스트")
	void saveReviewTest() {
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		CertificationReview review = CertificationReview.builder().title("자격증후기").content("내용").userAccount(user)
				.category(1L).view(0L).build();

		// When
		postRepository.save(review);

		// Then
		Post savedPost = postRepository.findById(review.getId()).get();
		assertAll(
				() -> assertEquals(review.getUserAccount(), savedPost.getUserAccount()),
				() -> assertEquals(review.getTitle(), savedPost.getTitle()),
				() -> assertEquals(review.getContent(), savedPost.getContent()),
				() -> assertEquals(review.getCategory(), savedPost.getCategory()),
				() -> assertEquals(review.getView(), savedPost.getView()),
				() -> assertEquals(review.getPostLikes(), savedPost.getPostLikes()),
				() -> assertEquals(review.getCreatedAt(), savedPost.getCreatedAt()),
				() -> assertEquals(review.getModifiedAt(), savedPost.getModifiedAt())
		);
	}

	@Test
	@DisplayName("게시글 리스트 테스트")
	void findListTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = CertificationReview.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L)
				.build();
		Post post2 = CertificationReview.builder().userAccount(user).title("제목2").content("내용2").category(1L).view(0L)
				.build();
		Post post3 = CertificationReview.builder().userAccount(user).title("제목3").content("내용3").category(1L).view(0L)
				.build();
		Post post4 = CertificationReview.builder().userAccount(user).title("제목4").content("내용4").category(1L).view(0L)
				.build();
		Post post5 = CertificationReview.builder().userAccount(user).title("제목5").content("내용5").category(1L).view(0L)
				.build();

		// When
		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);

		// Then
		Pageable pageable = PageRequest.of(0, 3);
		assertEquals(3, postRepository.findAllByOrderByIdDesc(pageable).getSize());
		assertEquals(5, postRepository.findAllByOrderByIdDesc(pageable).getTotalElements());
		assertEquals(2, postRepository.findAllByOrderByIdDesc(pageable).getTotalPages());
		assertThat(postRepository.findAllByOrderByIdDesc(pageable)).contains(post3, post4, post5);
	}

	@Test
	@DisplayName("후기 수정 테스트")
	void postUpdateTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post = CertificationReview.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L)
				.build();
		postRepository.save(post);

		// When
		Post getPost = postRepository.findById(post.getId()).get();
		Post updatePost = CertificationReview.builder().id(getPost.getId()) // id 를 덮어써 갱신하기
				.userAccount(user).title("수정된 제목1").content("수정된 내용1").category(1L).view(0L).build();

		postRepository.save(updatePost);

		// Then
		Post updateAndSavedPost = postRepository.findById(updatePost.getId()).get();
		assertEquals(1, postRepository.findAll().size()); // 게시글이 갱신되어 게시글이 추가되지 않음을 확인
		assertAll(
				() -> assertEquals(updatePost.getUserAccount(), updateAndSavedPost.getUserAccount()),
				() -> assertEquals(updatePost.getTitle(), updateAndSavedPost.getTitle()),
				() -> assertEquals(updatePost.getContent(), updateAndSavedPost.getContent()),
				() -> assertEquals(updatePost.getCategory(), updateAndSavedPost.getCategory()),
				() -> assertEquals(updatePost.getView(), updateAndSavedPost.getView()),
				() -> assertEquals(updatePost.getPostLikes(), updateAndSavedPost.getPostLikes()),
				() -> assertEquals(updatePost.getCreatedAt(), updateAndSavedPost.getCreatedAt())
		);
	}

	@Test
	@DisplayName("게시글 삭제 테스트")
	void deletePostTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post = CertificationReview.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L)
				.build();
		postRepository.save(post);

		// When
		Post savedPost = postRepository.findById(post.getId()).get();
		postRepository.deleteById(savedPost.getId());

		// Then
		assertEquals(Optional.empty(), postRepository.findById(savedPost.getId()));
		assertEquals(0, postRepository.findAll().size());
	}

	@Test
	@DisplayName("게시글 카테고리로 리스트를 불러오는 테스트")
	void findListByCategoryTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = CertificationReview.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L)
				.build();
		Post post2 = CertificationReview.builder().userAccount(user).title("제목2").content("내용2").category(1L).view(0L)
				.build();
		Post post3 = CertificationReview.builder().userAccount(user).title("제목3").content("내용3").category(1L).view(0L)
				.build();
		Post post4 = CertificationReview.builder().userAccount(user).title("제목4").content("내용4").category(1L).view(0L)
				.build();
		Post post5 = CertificationReview.builder().userAccount(user).title("제목5").content("내용5").category(1L).view(0L)
				.build();
		Post post6 = CertificationReview.builder().userAccount(user).title("제목6").content("내용6").category(2L).view(0L)
				.build();
		Post post7 = CertificationReview.builder().userAccount(user).title("제목7").content("내용7").category(2L).view(0L)
				.build();

		// When
		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);
		postRepository.save(post6);
		postRepository.save(post7);

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기

		// Then
		assertEquals(2, postRepository.findAllByCategoryOrderByIdDesc(1L, pageable).getTotalPages());
		assertEquals(5, postRepository.findAllByCategoryOrderByIdDesc(1L, pageable).getTotalElements());
		assertEquals(3, postRepository.findAllByCategoryOrderByIdDesc(1L, pageable).getSize());
		assertThat(postRepository.findAllByCategoryOrderByIdDesc(1L, pageable))
				.contains(post3, post4, post5); // 3개씩 자른다면 맨 뒤에 입력된 3개가 출력되어야 함
	}

	@Test
	@DisplayName("한 사용자가 작성한 게시글 리스트를 불러오는 테스트")
	void findListByUserAccountTest() {
		// Given
		UserAccount user1 = UserAccount.builder().userId("testId1").userName("testName").email("test@email.com")
				.phoneNum("010-1111-2222").password("testPassword").role(UserRole.ROLE_TEMPORARY_USER).build();

		userAccountRepository.save(user1);

		UserAccount user2 = UserAccount.builder().userId("testId2").userName("testName").email("test@email.com")
				.phoneNum("010-1111-2222").password("testPassword").role(UserRole.ROLE_TEMPORARY_USER).build();

		userAccountRepository.save(user2);

		user1 = userAccountRepository.findByUserId("testId1").get();
		user2 = userAccountRepository.findByUserId("testId2").get();

		Post post1 = CertificationReview.builder().userAccount(user1).title("제목1").content("내용1").category(1L).view(0L)
				.build();
		Post post2 = CertificationReview.builder().userAccount(user1).title("제목2").content("내용2").category(1L).view(0L)
				.build();
		Post post3 = CertificationReview.builder().userAccount(user1).title("제목3").content("내용3").category(1L).view(0L)
				.build();
		Post post4 = CertificationReview.builder().userAccount(user2).title("제목4").content("내용4").category(1L).view(0L)
				.build();
		Post post5 = CertificationReview.builder().userAccount(user2).title("제목5").content("내용5").category(1L).view(0L)
				.build();
		Post post6 = CertificationReview.builder().userAccount(user1).title("제목6").content("내용6").category(2L).view(0L)
				.build();
		Post post7 = CertificationReview.builder().userAccount(user2).title("제목7").content("내용7").category(2L).view(0L)
				.build();

		// When
		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);
		postRepository.save(post6);
		postRepository.save(post7);
		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		// Then
		assertEquals(3, postRepository.findAllByUserAccountOrderByIdDesc(user1, pageable).getSize());
		assertEquals(4, postRepository.findAllByUserAccountOrderByIdDesc(user1, pageable).getTotalElements());
		assertEquals(2, postRepository.findAllByUserAccountOrderByIdDesc(user1, pageable).getTotalPages());
		assertThat(postRepository.findAllByUserAccountOrderByIdDesc(user1, pageable)).contains(post2, post3, post6);
	}

	@Test
	@DisplayName("검색어로 검색한 리스트를 정상적으로 불러오는지 테스트")
	void searchByKeywordTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = CertificationReview.builder().userAccount(user).title("검검색어어").content("내용1").category(1L)
				.view(0L).build();
		Post post2 = CertificationReview.builder().userAccount(user).title("제목2").content("검검색어어").category(1L)
				.view(0L).build();
		Post post3 = CertificationReview.builder().userAccount(user).title("검색어어어").content("내용3").category(1L)
				.view(0L).build();
		Post post4 = CertificationReview.builder().userAccount(user).title("제목4").content("검검검색어어").category(1L)
				.view(0L).build();
		Post post5 = CertificationReview.builder().userAccount(user).title("제목5").content("내용5").category(1L).view(0L)
				.build();
		Post post6 = CertificationReview.builder().userAccount(user).title("제목6").content("내용6").category(2L).view(0L)
				.build();
		Post post7 = CertificationReview.builder().userAccount(user).title("제목7").content("내용7").category(2L).view(0L)
				.build();

		// When
		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);
		postRepository.save(post6);
		postRepository.save(post7);
		Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

		// Then
		Page<Post> result = postRepository.searchByKeyword("검색어", pageable);
		assertEquals(2, result.getTotalPages());
		assertEquals(4, result.getTotalElements());
		assertEquals(3, result.getSize());
		assertThat(result).contains(post2, post3, post4); // 3개씩 자른다면 맨 뒤에 입력된 3개가 출력되어야 함
	}

	@Test
	@DisplayName("검색어와 카테고리로 검색한 리스트를 정상적으로 불러오는지 테스트")
	void searchByKeywordAndCategoryTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = CertificationReview.builder().userAccount(user).title("검검색어어").content("내용1").category(1L)
				.view(0L).build();
		Post post2 = CertificationReview.builder().userAccount(user).title("제목2").content("검검색어어").category(2L)
				.view(0L).build();
		Post post3 = CertificationReview.builder().userAccount(user).title("검색어어어").content("내용3").category(1L)
				.view(0L).build();
		Post post4 = CertificationReview.builder().userAccount(user).title("제목4").content("검검검색어어").category(2L)
				.view(0L).build();
		Post post5 = CertificationReview.builder().userAccount(user).title("검색어").content("내용5").category(1L)
				.view(0L).build();
		Post post6 = CertificationReview.builder().userAccount(user).title("검색어").content("검색어").category(2L)
				.view(0L).build();
		Post post7 = CertificationReview.builder().userAccount(user).title("제목7").content("검색어").category(2L)
				.view(0L).build();

		// When
		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);
		postRepository.save(post6);
		postRepository.save(post7);
		Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

		// Then
		Page<Post> result = postRepository.searchByKeywordAndCategory("검색어", 2L, pageable);
		assertEquals(2, result.getTotalPages());
		assertEquals(4, result.getTotalElements());
		assertEquals(3, result.getSize());
		assertThat(result).contains(post4, post6, post7); // 3개씩 자른다면 맨 뒤에 입력된 3개가 출력되어야 함
	}
}
