package pulleydoreurae.careerquestbackend.certification.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.ReviewRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewLike;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewViewCheck;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewLikeRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewViewCheckRepository;
import pulleydoreurae.careerquestbackend.community.exception.PostLikeNotFoundException;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증에서 공통으로 사용되는 메서드 테스트")
class CommonReviewServiceTest {

	@InjectMocks
	CommonReviewService commonReviewService;
	@Mock
	UserAccountRepository userAccountRepository;
	@Mock
	ReviewRepository reviewRepository;
	@Mock
	ReviewLikeRepository reviewLikeRepository;
	@Mock
	ReviewViewCheckRepository reviewViewCheckRepository;

	@Test
	@DisplayName("후기 Entity -> 후기 Response 변환 메서드 테스트")
	void reviewToReviewResponseTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().userAccount(user).id(100L).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		given(reviewRepository.findById(100L)).willReturn(Optional.of(review));

		ReviewLike reviewLike1 = ReviewLike.builder().userAccount(user).review(review).build();
		ReviewLike reviewLike2 = ReviewLike.builder().userAccount(user).review(review).build();
		ReviewLike reviewLike3 = ReviewLike.builder().userAccount(user).review(review).build();
		ReviewLike reviewLike4 = ReviewLike.builder().userAccount(user).review(review).build();
		ReviewLike reviewLike5 = ReviewLike.builder().userAccount(user).review(review).build();
		List<ReviewLike> reviewLikes = List.of(reviewLike1, reviewLike2, reviewLike3, reviewLike4, reviewLike5);
		given(reviewLikeRepository.findAllByReview(review)).willReturn(reviewLikes);

		// When
		ReviewResponse expect = ReviewResponse.builder()
				.userId(review.getUserAccount().getUserId())
				.title(review.getTitle())
				.content(review.getContent())
				.view(review.getView())
				.postLikeCount(commonReviewService.countReviewLike(review.getId()))
				.certificationName(review.getCertificationName())
				.isLiked(false)
				.createdAt(review.getCreatedAt())
				.modifiedAt(review.getModifiedAt())
				.build();
		ReviewResponse result = commonReviewService.reviewToReviewResponse(review, false);

		// Then
		assertAll(
				() -> assertEquals(expect.getUserId(), result.getUserId()),
				() -> assertEquals(expect.getTitle(), result.getTitle()),
				() -> assertEquals(expect.getContent(), result.getContent()),
				() -> assertEquals(expect.getView(), result.getView()),
				() -> assertEquals(expect.getPostLikeCount(), result.getPostLikeCount()),
				() -> assertEquals(expect.getCertificationName(), result.getCertificationName()),
				() -> assertEquals(expect.getIsLiked(), result.getIsLiked()),
				() -> assertEquals(expect.getCreatedAt(), result.getCreatedAt()),
				() -> assertEquals(expect.getModifiedAt(), result.getModifiedAt())
		);
	}

	@Test
	@DisplayName("UUID 값이 정상적으로 생성되는지 테스트")
	public void testGetUUIDWithNoExistingCookie() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// getUUID 메서드 실행
		String uuid = commonReviewService.getUUID(request, response);

		// UUID 값 검증
		assertThat(uuid).isNotNull();

		// 새로 생성된 쿠키 검증
		assertThat(response.getCookies()).isNotNull();
		assertThat(response.getCookies()[0].getName()).isEqualTo("UUID");
		assertThat(response.getCookies()[0].getValue()).isEqualTo(uuid);
	}

	@Test
	@DisplayName("후기 찾기 실패")
	void findReviewFailTest() {
		// Given
		given(reviewRepository.findById(100L)).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> commonReviewService.findReview(100L));
	}

	@Test
	@DisplayName("후기 찾기 성공")
	void findReviewSuccessTest() {
		// Given
		Review review = Review.builder()
				.userAccount(new UserAccount()).id(100L).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		given(reviewRepository.findById(100L)).willReturn(Optional.of(review));

		// When
		Review result = commonReviewService.findReview(100L);

		// Then
		assertAll(
				() -> assertEquals(review.getId(), result.getId()),
				() -> assertEquals(review.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(review.getTitle(), result.getTitle()),
				() -> assertEquals(review.getContent(), result.getContent()),
				() -> assertEquals(review.getView(), result.getView()),
				() -> assertEquals(review.getCertificationName(), result.getCertificationName()),
				() -> assertEquals(review.getPostLikes(), result.getPostLikes()),
				() -> assertEquals(review.getCreatedAt(), result.getCreatedAt()),
				() -> assertEquals(review.getModifiedAt(), result.getModifiedAt())
		);
	}

	@Test
	@DisplayName("사용자 찾기 실패")
	void findUserAccountFailTest() {
		// Given
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> commonReviewService.findUserAccount("testId"));
	}

	@Test
	@DisplayName("사용자 찾기 성공")
	void findUserAccountSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.of(user));

		// When
		UserAccount result = commonReviewService.findUserAccount("testId");

		// Then
		assertEquals(user, result);
	}

	@Test
	@DisplayName("조회수 중복이 아닌 경우")
	void findReviewViewCheckNullTest() {
		// Given
		given(reviewViewCheckRepository.findById(anyString())).willReturn(Optional.empty());

		// When
		ReviewViewCheck result = commonReviewService.findReviewViewCheck("testId");

		// Then
		assertNull(result);
	}

	@Test
	@DisplayName("조회수 중복인 경우")
	void findReviewViewCheckNotNullTest() {
		// Given
		ReviewViewCheck postViewCheck = new ReviewViewCheck("testId", 100L);
		given(reviewViewCheckRepository.findById("testId")).willReturn(Optional.of(postViewCheck));

		// When
		ReviewViewCheck result = commonReviewService.findReviewViewCheck("testId");

		// Then
		assertAll(
				() -> assertEquals(postViewCheck.getReviewId(), result.getReviewId()),
				() -> assertEquals(postViewCheck.getUserId(), result.getUserId())

		);
	}

	@Test
	@DisplayName("후기 Request -> 후기 Entity 변환 테스트")
	void reviewRequestToReviewTest() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		ReviewRequest reviewRequest = ReviewRequest.builder().userId("testId").title("제목").content("내용").build();

		// When
		Review result = commonReviewService.reviewRequestToReview(reviewRequest, userAccount);

		// Then
		assertAll(
				() -> assertEquals(reviewRequest.getUserId(), result.getUserAccount().getUserId()),
				() -> assertEquals(reviewRequest.getTitle(), result.getTitle()),
				() -> assertEquals(reviewRequest.getContent(), result.getContent()),
				() -> assertEquals(reviewRequest.getCertificationName(), result.getCertificationName())
		);
	}

	@Test
	@DisplayName("후기 수정 Request -> 후기 Entity")
	void reviewRequestToReviewForUpdateTest() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		ReviewRequest reviewRequest = ReviewRequest.builder().userId("testId").title("제목").content("내용").certificationName("정보처리기사").build();
		Review review = Review.builder().userAccount(userAccount).title("제목").content("내용").certificationName("정보처리기사").build();

		// When
		Review result = commonReviewService.reviewRequestToReviewForUpdate(review, reviewRequest, userAccount);

		// Then
		assertAll(
				() -> assertEquals(reviewRequest.getTitle(), result.getTitle()),
				() -> assertEquals(reviewRequest.getContent(), result.getContent()),
				() -> assertEquals(reviewRequest.getCertificationName(), result.getCertificationName()),
				() -> assertEquals(review.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(review.getView(), result.getView()),
				() -> assertEquals(review.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(review.getPostLikes(), result.getPostLikes())
		);
	}

	@Test
	@DisplayName("후기 좋아요 수 테스트")
	void countReviewLikeTest() {
		// Given
		Review review = Review.builder().userAccount(new UserAccount()).id(100L).title("제목1").content("내용1").certificationName("정보처리기사").view(0L).build();
		ReviewLike reviewLike1 = ReviewLike.builder().review(review).userAccount(new UserAccount()).build();
		ReviewLike reviewLike2 = ReviewLike.builder().review(review).userAccount(new UserAccount()).build();
		ReviewLike reviewLike3 = ReviewLike.builder().review(review).userAccount(new UserAccount()).build();
		ReviewLike reviewLike4 = ReviewLike.builder().review(review).userAccount(new UserAccount()).build();
		ReviewLike reviewLike5 = ReviewLike.builder().review(review).userAccount(new UserAccount()).build();
		given(reviewRepository.findById(100L)).willReturn(Optional.of(review));
		given(reviewLikeRepository.findAllByReview(any()))
				.willReturn(List.of(reviewLike1, reviewLike2, reviewLike3, reviewLike4, reviewLike5));

		// When
		Long result = commonReviewService.countReviewLike(100L);

		// Then
		assertEquals(5, result);
	}

	@Test
	@DisplayName("후기와 사용자 정보로 좋아요 정보 가져오기 실패")
	void findReviewLikeFailTest() {
		// Given
		given(reviewLikeRepository.findByReviewAndUserAccount(any(), any())).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(PostLikeNotFoundException.class, () -> commonReviewService.findReviewLike(any(), any()));
	}

	@Test
	@DisplayName("게시글과 사용자 정보로 좋아요 정보 가져오기 성공")
	void findReviewLikeSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().title("제목").content("내용").userAccount(user).build();
		ReviewLike reviewLike = ReviewLike.builder().userAccount(user).review(review).build();
		given(reviewLikeRepository.findByReviewAndUserAccount(any(), any())).willReturn(Optional.of(reviewLike));

		// When
		ReviewLike result = commonReviewService.findReviewLike(review, user);

		// Then
		assertAll(
				() -> assertEquals(reviewLike.getReview(), result.getReview()),
				() -> assertEquals(reviewLike.getUserAccount(), result.getUserAccount())
		);
	}
}
