package pulleydoreurae.careerquestbackend.certification.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.ReviewLikeRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewLike;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewLikeRepository;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증 후기 좋아요 Service 테스트")
class ReviewLikeServiceTest {

	@InjectMocks
	ReviewLikeService reviewLikeService;
	@Mock
	ReviewLikeRepository reviewLikeRepository;
	@Mock
	CommonReviewService commonReviewService;

	@Test
	@DisplayName("좋아요 증가 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void reviewLikePlusFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonReviewService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").isLiked(false).build();

		// Then
		assertThrows(UsernameNotFoundException.class, () -> reviewLikeService.changeReviewLike(request));
		verify(reviewLikeRepository, never()).save(any());
		verify(reviewLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 증가 테스트 (실패 - 후기 정보를 찾을 수 없음)")
	void reviewLikePlusFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonReviewService.findUserAccount("testId")).willReturn(user);
		given(commonReviewService.findReview(10000L))
				.willThrow(new PostNotFoundException("후기 정보를 찾을 수 없습니다."));

		// When
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").isLiked(false).build();

		// Then
		assertThrows(PostNotFoundException.class, () -> reviewLikeService.changeReviewLike(request));
		verify(reviewLikeRepository, never()).save(any());
		verify(reviewLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 증가 테스트 (성공)")
	void reviewLikePlusSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().userAccount(user).id(10000L).title("제목1").build();

		given(commonReviewService.findUserAccount("testId")).willReturn(user);
		given(commonReviewService.findReview(10000L)).willReturn(review);

		// When
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").isLiked(false).build();

		// Then
		reviewLikeService.changeReviewLike(request);

		verify(reviewLikeRepository).save(any());
		verify(reviewLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 감소 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void reviewLikeMinusFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonReviewService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").isLiked(true).build();

		// Then
		assertThrows(UsernameNotFoundException.class, () -> reviewLikeService.changeReviewLike(request));
		verify(reviewLikeRepository, never()).save(any());
		verify(reviewLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 감소 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void reviewLikeMinusFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonReviewService.findUserAccount("testId")).willReturn(user);
		given(commonReviewService.findReview(10000L))
				.willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		// When
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").isLiked(true).build();

		// Then
		assertThrows(PostNotFoundException.class, () -> reviewLikeService.changeReviewLike(request));
		verify(reviewLikeRepository, never()).save(any());
	}

	@Test
	@DisplayName("좋아요 감소 테스트 (성공)")
	void reviewLikeMinusSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().userAccount(user).id(10000L).title("제목1").build();
		ReviewLike reviewLike = ReviewLike.builder().userAccount(user).review(review).build();

		given(commonReviewService.findUserAccount("testId")).willReturn(user);
		given(commonReviewService.findReview(10000L)).willReturn(review);
		given(commonReviewService.findReviewLike(review, user)).willReturn(reviewLike);

		// When
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").isLiked(true).build();

		// Then
		reviewLikeService.changeReviewLike(request);

		verify(reviewLikeRepository, never()).save(any());
		verify(reviewLikeRepository).delete(any());
	}

	@Test
	@DisplayName("한 회원이 좋아요 누른 후기 불러오기")
	void findAllReviewLikeByUserAccountTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review1 = Review.builder().userAccount(user).id(10001L).title("제목1").build();
		Review review2 = Review.builder().userAccount(user).id(10002L).title("제목2").build();
		Review review3 = Review.builder().userAccount(user).id(10003L).title("제목3").build();
		Review review4 = Review.builder().userAccount(user).id(10004L).title("제목4").build();
		Review review5 = Review.builder().userAccount(user).id(10005L).title("제목5").build();
		ReviewLike reviewLike1 = ReviewLike.builder().userAccount(user).review(review1).build();
		ReviewLike reviewLike2 = ReviewLike.builder().userAccount(user).review(review2).build();
		ReviewLike reviewLike3 = ReviewLike.builder().userAccount(user).review(review3).build();
		ReviewLike reviewLike4 = ReviewLike.builder().userAccount(user).review(review4).build();
		ReviewLike reviewLike5 = ReviewLike.builder().userAccount(user).review(review5).build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		Page<ReviewLike> list = new PageImpl<>(
				List.of(reviewLike3, reviewLike4, reviewLike5), pageable, 3); // 3개씩 자른다면 마지막 3개가 반환되어야 함

		given(commonReviewService.findUserAccount("testId")).willReturn(user);
		given(commonReviewService.reviewToReviewResponse(review3, false)).willReturn(reviewToReviewResponse(review3));
		given(commonReviewService.reviewToReviewResponse(review4, false)).willReturn(reviewToReviewResponse(review4));
		given(commonReviewService.reviewToReviewResponse(review5, false)).willReturn(reviewToReviewResponse(review5));
		given(reviewLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable))
				.willReturn(list);

		// When
		List<ReviewResponse> result = reviewLikeService.findAllReviewLikeByUserAccount(user.getUserId(), pageable);

		// Then
		assertEquals(3, result.size());
		assertThat(result).contains(
				reviewToReviewResponse(review3),
				reviewToReviewResponse(review4),
				reviewToReviewResponse(review5)
		);
		verify(reviewLikeRepository).findAllByUserAccountOrderByIdDesc(user, pageable);
		verify(commonReviewService).reviewToReviewResponse(review3, false);
		verify(commonReviewService).reviewToReviewResponse(review4, false);
		verify(commonReviewService).reviewToReviewResponse(review5, false);
	}

	// Review -> ReviewResponse 변환 메서드
	ReviewResponse reviewToReviewResponse(Review review) {
		return ReviewResponse.builder()
				.userId(review.getUserAccount().getUserId())
				.title(review.getTitle())
				.content(review.getContent())
				.view(review.getView())
				.postLikeCount(0L)
				.certificationName(review.getCertificationName())
				.build();
	}
}
