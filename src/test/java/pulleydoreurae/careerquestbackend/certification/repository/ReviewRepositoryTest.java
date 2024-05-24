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
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;

/**
 * 자격증 후기 Repository 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@DataJpaTest
@DisplayName("자격증 후기 DB 테스트")
public class ReviewRepositoryTest {

	@Autowired
	ReviewRepository reviewRepository;
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
		Review review = Review.builder()
				.title("자격증후기")
				.content("내용")
				.userAccount(user)
				.certificationName("정보처리기사")
				.view(0L)
				.build();

		// When
		reviewRepository.save(review);

		// Then
		Review savedPost = reviewRepository.findById(review.getId()).get();
		assertAll(
				() -> assertEquals(review.getUserAccount(), savedPost.getUserAccount()),
				() -> assertEquals(review.getTitle(), savedPost.getTitle()),
				() -> assertEquals(review.getContent(), savedPost.getContent()),
				() -> assertEquals(review.getCertificationName(), savedPost.getCertificationName()),
				() -> assertEquals(review.getView(), savedPost.getView()),
				() -> assertEquals(review.getPostLikes(), savedPost.getPostLikes()),
				() -> assertEquals(review.getCreatedAt(), savedPost.getCreatedAt()),
				() -> assertEquals(review.getModifiedAt(), savedPost.getModifiedAt())
		);
	}

	@Test
	@DisplayName("후기 리스트 테스트")
	void findListTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Review review1 = Review.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review2 = Review.builder()
				.userAccount(user)
				.title("제목2")
				.content("내용2")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review3 = Review.builder()
				.userAccount(user)
				.title("제목3")
				.content("내용3")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review4 = Review.builder()
				.userAccount(user)
				.title("제목4")
				.content("내용4")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review5 = Review.builder()
				.userAccount(user)
				.title("제목5")
				.content("내용5")
				.certificationName("정보처리기사")
				.view(0L)
				.build();

		// When
		reviewRepository.save(review1);
		reviewRepository.save(review2);
		reviewRepository.save(review3);
		reviewRepository.save(review4);
		reviewRepository.save(review5);

		// Then
		Pageable pageable = PageRequest.of(0, 3);
		assertEquals(3, reviewRepository.findAllByOrderByIdDesc(pageable).getSize());
		assertEquals(5, reviewRepository.findAllByOrderByIdDesc(pageable).getTotalElements());
		assertEquals(2, reviewRepository.findAllByOrderByIdDesc(pageable).getTotalPages());
		assertThat(reviewRepository.findAllByOrderByIdDesc(pageable)).contains(review3, review4, review5);
	}

	@Test
	@DisplayName("후기 수정 테스트")
	void postUpdateTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Review review = Review.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		reviewRepository.save(review);

		// When
		Review getPost = reviewRepository.findById(review.getId()).get();
		Review updatePost = Review.builder().id(getPost.getId()) // id 를 덮어써 갱신하기
				.userAccount(user).title("수정된 제목1").content("수정된 내용1").certificationName("정보처리기사").view(0L).build();

		reviewRepository.save(updatePost);

		// Then
		Review updateAndSavedReview = reviewRepository.findById(updatePost.getId()).get();
		assertEquals(1, reviewRepository.findAll().size()); // 게시글이 갱신되어 게시글이 추가되지 않음을 확인
		assertAll(
				() -> assertEquals(updatePost.getUserAccount(), updateAndSavedReview.getUserAccount()),
				() -> assertEquals(updatePost.getTitle(), updateAndSavedReview.getTitle()),
				() -> assertEquals(updatePost.getContent(), updateAndSavedReview.getContent()),
				() -> assertEquals(updatePost.getCertificationName(), updateAndSavedReview.getCertificationName()),
				() -> assertEquals(updatePost.getView(), updateAndSavedReview.getView()),
				() -> assertEquals(updatePost.getPostLikes(), updateAndSavedReview.getPostLikes()),
				() -> assertEquals(updatePost.getCreatedAt(), updateAndSavedReview.getCreatedAt())
		);
	}

	@Test
	@DisplayName("후기 삭제 테스트")
	void deletePostTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Review review = Review.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		reviewRepository.save(review);

		// When
		Review savedPost = reviewRepository.findById(review.getId()).get();
		reviewRepository.deleteById(savedPost.getId());

		// Then
		assertEquals(Optional.empty(), reviewRepository.findById(savedPost.getId()));
		assertEquals(0, reviewRepository.findAll().size());
	}

	@Test
	@DisplayName("후기 자격증명으로 리스트를 불러오는 테스트")
	void findListByCategoryTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Review review1 = Review.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review2 = Review.builder()
				.userAccount(user)
				.title("제목2")
				.content("내용2")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review3 = Review.builder()
				.userAccount(user)
				.title("제목3")
				.content("내용3")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review4 = Review.builder()
				.userAccount(user)
				.title("제목4")
				.content("내용4")
				.certificationName("정보보안기사")
				.view(0L)
				.build();
		Review review5 = Review.builder()
				.userAccount(user)
				.title("제목5")
				.content("내용5")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review6 = Review.builder()
				.userAccount(user)
				.title("제목6")
				.content("내용6")
				.certificationName("정보처리산업기사")
				.view(0L)
				.build();
		Review review7 = Review.builder()
				.userAccount(user)
				.title("제목7")
				.content("내용7")
				.certificationName("정보처리기사")
				.view(0L)
				.build();

		// When
		reviewRepository.save(review1);
		reviewRepository.save(review2);
		reviewRepository.save(review3);
		reviewRepository.save(review4);
		reviewRepository.save(review5);
		reviewRepository.save(review6);
		reviewRepository.save(review7);

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기

		// Then
		assertEquals(2, reviewRepository.findAllByCertificationNameOrderByIdDesc("정보처리기사", pageable).getTotalPages());
		assertEquals(5,
				reviewRepository.findAllByCertificationNameOrderByIdDesc("정보처리기사", pageable).getTotalElements());
		assertEquals(3, reviewRepository.findAllByCertificationNameOrderByIdDesc("정보처리기사", pageable).getSize());
		assertThat(reviewRepository.findAllByCertificationNameOrderByIdDesc("정보처리기사", pageable))
				.contains(review3, review5, review7); // 3개씩 자른다면 맨 뒤에 입력된 3개가 출력되어야 함
	}

	@Test
	@DisplayName("한 사용자가 작성한 후기 리스트를 불러오는 테스트")
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

		Review review1 = Review.builder()
				.userAccount(user1)
				.title("제목1")
				.content("내용1")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review2 = Review.builder()
				.userAccount(user1)
				.title("제목2")
				.content("내용2")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review3 = Review.builder()
				.userAccount(user1)
				.title("제목3")
				.content("내용3")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review4 = Review.builder()
				.userAccount(user2)
				.title("제목4")
				.content("내용4")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review5 = Review.builder()
				.userAccount(user2)
				.title("제목5")
				.content("내용5")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review6 = Review.builder()
				.userAccount(user1)
				.title("제목6")
				.content("내용6")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review7 = Review.builder()
				.userAccount(user2)
				.title("제목7")
				.content("내용7")
				.certificationName("정보처리기사")
				.view(0L)
				.build();

		// When
		reviewRepository.save(review1);
		reviewRepository.save(review2);
		reviewRepository.save(review3);
		reviewRepository.save(review4);
		reviewRepository.save(review5);
		reviewRepository.save(review6);
		reviewRepository.save(review7);
		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		// Then
		assertEquals(3, reviewRepository.findAllByUserAccountOrderByIdDesc(user1, pageable).getSize());
		assertEquals(4, reviewRepository.findAllByUserAccountOrderByIdDesc(user1, pageable).getTotalElements());
		assertEquals(2, reviewRepository.findAllByUserAccountOrderByIdDesc(user1, pageable).getTotalPages());
		assertThat(reviewRepository.findAllByUserAccountOrderByIdDesc(user1, pageable)).contains(review2, review3,
				review6);
	}

	@Test
	@DisplayName("검색어로 검색한 리스트를 정상적으로 불러오는지 테스트")
	void searchByKeywordTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Review review1 = Review.builder()
				.userAccount(user)
				.title("검검색어어")
				.content("내용1")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review2 = Review.builder()
				.userAccount(user)
				.title("제목2")
				.content("검검색어어")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review3 = Review.builder()
				.userAccount(user)
				.title("검색어어어")
				.content("내용3")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review4 = Review.builder()
				.userAccount(user)
				.title("제목4")
				.content("검검검색어어")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review5 = Review.builder()
				.userAccount(user)
				.title("제목5")
				.content("내용5")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review6 = Review.builder()
				.userAccount(user)
				.title("제목6")
				.content("내용6")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review7 = Review.builder()
				.userAccount(user)
				.title("제목7")
				.content("내용7")
				.certificationName("정보처리기사")
				.view(0L)
				.build();

		// When
		reviewRepository.save(review1);
		reviewRepository.save(review2);
		reviewRepository.save(review3);
		reviewRepository.save(review4);
		reviewRepository.save(review5);
		reviewRepository.save(review6);
		reviewRepository.save(review7);
		Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

		// Then
		Page<Review> result = reviewRepository.searchByKeyword("검색어", pageable);
		assertEquals(2, result.getTotalPages());
		assertEquals(4, result.getTotalElements());
		assertEquals(3, result.getSize());
		assertThat(result).contains(review2, review3, review4); // 3개씩 자른다면 맨 뒤에 입력된 3개가 출력되어야 함
	}

	@Test
	@DisplayName("검색어와 자격증명으로 검색한 리스트를 정상적으로 불러오는지 테스트")
	void searchByKeywordAndCategoryTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Review review1 = Review.builder()
				.userAccount(user)
				.title("검검색어어")
				.content("내용1")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review2 = Review.builder()
				.userAccount(user)
				.title("제목2")
				.content("검검색어어")
				.certificationName("정보보안기사")
				.view(0L)
				.build();
		Review review3 = Review.builder()
				.userAccount(user)
				.title("검색어어어")
				.content("내용3")
				.certificationName("정보보안기사")
				.view(0L)
				.build();
		Review review4 = Review.builder()
				.userAccount(user)
				.title("제목4")
				.content("검검검색어어")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review5 = Review.builder()
				.userAccount(user)
				.title("검색어")
				.content("내용5")
				.certificationName("정보보안기사")
				.view(0L)
				.build();
		Review review6 = Review.builder()
				.userAccount(user)
				.title("검색어")
				.content("검색어")
				.certificationName("정보처리기사")
				.view(0L)
				.build();
		Review review7 = Review.builder()
				.userAccount(user)
				.title("제목7")
				.content("검색어")
				.certificationName("정보처리기사")
				.view(0L)
				.build();

		// When
		reviewRepository.save(review1);
		reviewRepository.save(review2);
		reviewRepository.save(review3);
		reviewRepository.save(review4);
		reviewRepository.save(review5);
		reviewRepository.save(review6);
		reviewRepository.save(review7);
		Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending());

		// Then
		Page<Review> result = reviewRepository.searchByKeywordAndCertificationName("검색어", "정보처리기사", pageable);
		assertEquals(2, result.getTotalPages());
		assertEquals(4, result.getTotalElements());
		assertEquals(3, result.getSize());
		assertThat(result).contains(review4, review6, review7); // 3개씩 자른다면 맨 뒤에 입력된 3개가 출력되어야 함
	}
}
