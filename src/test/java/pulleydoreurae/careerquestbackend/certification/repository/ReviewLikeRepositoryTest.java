package pulleydoreurae.careerquestbackend.certification.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewLike;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@DataJpaTest
@DisplayName("자격증 후기 좋아요 DB 테스트")
class ReviewLikeRepositoryTest {

	@Autowired
	ReviewLikeRepository reviewLikeRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	ReviewRepository reviewRepository;

	@BeforeEach
	void beforeEach() {
		UserAccount user = UserAccount.builder().userId("testId").userName("testName").email("test@email.com")
				.phoneNum("010-1111-2222").password("testPassword").role(UserRole.ROLE_TEMPORARY_USER).build();
		userAccountRepository.save(user);
	}

	@AfterEach
	void afterEach() {
		reviewLikeRepository.deleteAll();
		userAccountRepository.deleteAll();
		reviewRepository.deleteAll();
	}

	@Test
	@DisplayName("좋아요 누름 테스트 (증가)")
	void postLikePlusTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Review review = Review.builder().userAccount(user).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		reviewRepository.save(review);

		// When
		ReviewLike reviewLike = ReviewLike.builder().userAccount(user).review(review).build();
		reviewLikeRepository.save(reviewLike);

		ReviewLike result = reviewLikeRepository.findByReviewAndUserAccount(review, user).get();

		// Then
		Pageable pageable = PageRequest.of(0, 3);
		assertEquals(1, reviewLikeRepository.findAllByReviewOrderByIdDesc(review, pageable).getTotalElements());
		assertEquals(1, reviewLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable).getTotalElements());

		assertAll(
				() -> assertEquals(reviewLike.getReview(), result.getReview()),
				() -> assertEquals(reviewLike.getUserAccount(), result.getUserAccount())
		);
	}

	@Test
	@DisplayName("좋아요 누름 테스트 (감소)")
	void postLikeMinusTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Review review = Review.builder().userAccount(user).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		reviewRepository.save(review);

		ReviewLike reviewLike = ReviewLike.builder().userAccount(user).review(review).build();
		reviewLikeRepository.save(reviewLike);

		// When
		ReviewLike get = reviewLikeRepository.findByReviewAndUserAccount(review, user).get();
		reviewLikeRepository.deleteById(get.getId());

		// Then
		Pageable pageable = PageRequest.of(0, 3);
		assertEquals(0, reviewLikeRepository.findAllByReviewOrderByIdDesc(review, pageable).getTotalElements());
		assertEquals(0, reviewLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable).getTotalElements());

		assertEquals(Optional.empty(), reviewLikeRepository.findByReviewAndUserAccount(review, user));
	}

	@Test
	@DisplayName("한 게시글의 좋아요 개수를 불러오는 테스트")
	void findListByPostTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		UserAccount user2 = UserAccount.builder().userId("testId2").build();
		UserAccount user3 = UserAccount.builder().userId("testId3").build();
		UserAccount user4 = UserAccount.builder().userId("testId4").build();
		UserAccount user5 = UserAccount.builder().userId("testId5").build();

		userAccountRepository.save(user2);
		userAccountRepository.save(user3);
		userAccountRepository.save(user4);
		userAccountRepository.save(user5);

		Review review = Review.builder().userAccount(user).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		reviewRepository.save(review);

		ReviewLike reviewLike1 = ReviewLike.builder().userAccount(user).review(review).build();
		ReviewLike reviewLike2 = ReviewLike.builder().userAccount(user2).review(review).build();
		ReviewLike reviewLike3 = ReviewLike.builder().userAccount(user3).review(review).build();
		ReviewLike reviewLike4 = ReviewLike.builder().userAccount(user4).review(review).build();
		ReviewLike reviewLike5 = ReviewLike.builder().userAccount(user5).review(review).build();

		reviewLikeRepository.save(reviewLike1);
		reviewLikeRepository.save(reviewLike2);
		reviewLikeRepository.save(reviewLike3);
		reviewLikeRepository.save(reviewLike4);
		reviewLikeRepository.save(reviewLike5);

		// When
		Pageable pageable = PageRequest.of(0, 3);
		Page<ReviewLike> result = reviewLikeRepository.findAllByReviewOrderByIdDesc(review, pageable);

		// Then
		assertEquals(3, result.getSize());
		assertEquals(5, result.getTotalElements());
		assertEquals(2, result.getTotalPages());
	}

	@Test
	@DisplayName("한 사용자가 좋아요 누른 후기를 불러오는 테스트")
	void findListByUserAccountTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Review review1 = Review.builder().userAccount(user).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		Review review2 = Review.builder().userAccount(user).title("제목2").content("내용2").certificationName("정보처리기사").view(0L).build();
		Review review3 = Review.builder().userAccount(user).title("제목3").content("내용3").certificationName("정보처리기사").view(0L).build();
		Review review4 = Review.builder().userAccount(user).title("제목4").content("내용4").certificationName("정보처리기사").view(0L).build();
		Review review5 = Review.builder().userAccount(user).title("제목5").content("내용5").certificationName("정보처리기사").view(0L).build();

		reviewRepository.save(review1);
		reviewRepository.save(review2);
		reviewRepository.save(review3);
		reviewRepository.save(review4);
		reviewRepository.save(review5);

		ReviewLike reviewLike1 = ReviewLike.builder().userAccount(user).review(review1).build();
		ReviewLike reviewLike2 = ReviewLike.builder().userAccount(user).review(review2).build();
		ReviewLike reviewLike3 = ReviewLike.builder().userAccount(user).review(review3).build();
		ReviewLike reviewLike4 = ReviewLike.builder().userAccount(user).review(review4).build();
		ReviewLike reviewLike5 = ReviewLike.builder().userAccount(user).review(review5).build();

		reviewLikeRepository.save(reviewLike1);
		reviewLikeRepository.save(reviewLike2);
		reviewLikeRepository.save(reviewLike3);
		reviewLikeRepository.save(reviewLike4);
		reviewLikeRepository.save(reviewLike5);

		// When
		Pageable pageable = PageRequest.of(0, 3);
		Page<ReviewLike> result = reviewLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable);

		// Then
		assertEquals(3, result.getSize());
		assertEquals(5, result.getTotalElements());
		assertEquals(2, result.getTotalPages());
	}

	@Test
	@DisplayName("좋아요가 존재하는지 테스트")
	void existTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Review review = Review.builder().userAccount(user).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		reviewRepository.save(review);
		ReviewLike postLike = ReviewLike.builder().review(review).userAccount(user).build();
		reviewLikeRepository.save(postLike);

		// When
		boolean result1 = reviewLikeRepository.existsByReviewAndUserAccount(null, null);
		boolean result2 = reviewLikeRepository.existsByReviewAndUserAccount(review, null);
		boolean result3 = reviewLikeRepository.existsByReviewAndUserAccount(null, user);
		boolean result4 = reviewLikeRepository.existsByReviewAndUserAccount(review, user);

		// Then
		assertAll(
				() -> assertFalse(result1),
				() -> assertFalse(result2),
				() -> assertFalse(result3),
				() -> assertTrue(result4)
		);
	}
}
