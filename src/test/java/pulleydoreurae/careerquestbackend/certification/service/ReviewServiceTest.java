package pulleydoreurae.careerquestbackend.certification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.dto.ReviewRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewViewCheck;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewLikeRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewViewCheckRepository;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증 후기 Service 테스트")
class ReviewServiceTest {

	@InjectMocks
	ReviewService reviewService;
	@Mock
	ReviewRepository reviewRepository;
	@Mock
	ReviewLikeRepository reviewLikeRepository;
	@Mock
	ReviewViewCheckRepository reviewViewCheckRepository;
	@Mock
	CommonReviewService commonReviewService;

	@Test
	@DisplayName("후기 불러오기 실패")
	void findByReviewIdNullTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		given(commonReviewService.findReview(100L)).willThrow(new PostNotFoundException("후기 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> reviewService.findByReviewId(request, response, 100L));
		verify(commonReviewService, never()).reviewToReviewResponse(new Review(), false);
	}

	@Test
	@DisplayName("후기 불러오기 성공")
	void findByReviewIdNotNullTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Review review = Review.builder().title("제목").content("내용").view(1L).certificationName("정보처리기사").build();
		given(commonReviewService.findReview(any())).willReturn(review);
		given(commonReviewService.reviewToReviewResponse(review, false)).willReturn(
				new ReviewResponse("A", "A", "A","정보처리기사", 1L, 1L, false, "A", "A"));

		// When
		ReviewResponse result = reviewService.findByReviewId(request, response, 100L);

		// Then
		assertNotNull(result);
		verify(commonReviewService).reviewToReviewResponse(review, false);
	}

	@Test
	@DisplayName("조회수 증가 테스트")
	void checkViewTest1() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Review review = Review.builder().title("제목").content("내용").view(1L).certificationName("정보처리기사").build();
		// Authentication Mocking
		Authentication authentication = new UsernamePasswordAuthenticationToken("testId", null,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		// SecurityContext 에 Authentication 객체 설정
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
		given(commonReviewService.findReviewViewCheck("testId")).willReturn(null);

		// When
		String result = reviewService.checkView(request, response, 100L, review);

		// Then
		assertEquals("testId", result);
		assertEquals(2, review.getView());
		verify(reviewViewCheckRepository).save(any());
	}

	@Test
	@DisplayName("조회수 증가X 테스트")
	void checkViewTest2() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Review review = Review.builder().title("제목").content("내용").view(1L).certificationName("정보처리기사").build();
		// Authentication Mocking
		Authentication authentication = new UsernamePasswordAuthenticationToken("testId", null,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		// SecurityContext 에 Authentication 객체 설정
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
		ReviewViewCheck reviewViewCheck = new ReviewViewCheck("testId", 100L);
		given(commonReviewService.findReviewViewCheck("testId")).willReturn(reviewViewCheck);

		// When
		String result = reviewService.checkView(request, response, 100L, review);

		// Then
		assertEquals("testId", result);
		assertEquals(1, review.getView());
		verify(reviewViewCheckRepository, never()).save(any());
	}

	@Test
	@DisplayName("좋아요 상태 false")
	void getIsLikedTest1() {
		// Given
		given(reviewLikeRepository.existsByReviewAndUserAccount(any(), any())).willReturn(false);

		// When

		// Then
		assertFalse(reviewService.getIsLiked("testId", new Review()));
	}

	@Test
	@DisplayName("좋아요 상태 true")
	void getIsLikedTest2() {
		// Given
		given(reviewLikeRepository.existsByReviewAndUserAccount(any(), any())).willReturn(true);

		// When

		// Then
		assertTrue(reviewService.getIsLiked("testId", new Review()));
	}

	@Test
	@DisplayName("후기 등록 테스트 (성공)")
	void saveReviewSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		given(commonReviewService.findUserAccount("testId")).willReturn(user);

		// When

		// Then
		assertDoesNotThrow(() -> reviewService.saveReview(new ReviewRequest("testId", "정보처리기사", "제목", "내용")));
	}

	@Test
	@DisplayName("후기 등록 테스트 (실패)")
	void saveReviewFailTest() {
		// Given
		given(commonReviewService.findUserAccount("testId")).willThrow(UsernameNotFoundException.class);

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () ->
				reviewService.saveReview(new ReviewRequest("testId", "정보처리기사", "제목", "내용")));
	}

	@Test
	@DisplayName("후기 수정 실패 (후기 찾을 수 없음)")
	void updateReviewFail1Test() {
		// Given
		given(commonReviewService.findReview(any())).willThrow(new PostNotFoundException("후기을 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(PostNotFoundException.class,
				() -> reviewService.updatePost(100L, new ReviewRequest("testId", "정보처리기사", "제목", "내용")));
		verify(commonReviewService, never()).reviewRequestToReviewForUpdate(any(), any(), any());
		verify(reviewRepository, never()).save(any());
	}

	@Test
	@DisplayName("후기 수정 실패 (사용자 찾을 수 없음)")
	void updateReviewFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().title("제목").content("내용").userAccount(user).view(1L).certificationName("정보처리기사").build();
		given(commonReviewService.findReview(any())).willReturn(review);
		given(commonReviewService.findUserAccount(any()))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(UsernameNotFoundException.class,
				() -> reviewService.updatePost(100L, new ReviewRequest("testId", "정보처리기사", "제목", "내용")));
		verify(commonReviewService, never()).reviewRequestToReviewForUpdate(any(), any(), any());
		verify(reviewRepository, never()).save(any());
	}

	@Test
	@DisplayName("후기 수정 실패 (작성자, 수정자 다름)")
	void updateReviewFail3Test() {
		// Given
		UserAccount user1 = UserAccount.builder().userId("testId1").build();
		UserAccount user2 = UserAccount.builder().userId("testId2").build();
		Review review = Review.builder().title("제목").content("내용").userAccount(user1).view(1L).certificationName("정보처리기사").build();
		given(commonReviewService.findReview(any())).willReturn(review);
		given(commonReviewService.findUserAccount(any())).willReturn(user2);

		// When
		boolean result = reviewService.updatePost(100L, new ReviewRequest("testId", "정보처리기사", "제목", "내용"));

		// Then
		assertFalse(result);
		verify(commonReviewService, never()).reviewRequestToReviewForUpdate(any(), any(), any());
		verify(reviewRepository, never()).save(any());
	}

	@Test
	@DisplayName("후기 수정 성공")
	void updateReviewSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().title("제목").content("내용").userAccount(user).view(1L).certificationName("정보처리기사").build();
		given(commonReviewService.findReview(any())).willReturn(review);
		given(commonReviewService.findUserAccount(any())).willReturn(user);

		// When
		boolean result = reviewService.updatePost(100L, new ReviewRequest("testId", "정보처리기사", "제목", "내용"));

		// Then
		assertTrue(result);
		verify(commonReviewService).reviewRequestToReviewForUpdate(any(), any(), any());
		verify(reviewRepository).save(any());
	}

	@Test
	@DisplayName("후기 삭제 실패 (후기 찾을 수 없음)")
	void deleteReviewFail1Test() {
		// Given
		given(commonReviewService.findReview(any())).willThrow(new PostNotFoundException("후기 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> reviewService.deleteReview(100L, "testId"));
		verify(reviewRepository, never()).deleteById(100L);
	}

	@Test
	@DisplayName("후기 삭제 실패 (사용자 찾을 수 없음)")
	void deleteReviewFail2Test() {
		// Given
		given(commonReviewService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> reviewService.deleteReview(100L, "testId"));
		verify(reviewRepository, never()).deleteById(100L);
	}

	@Test
	@DisplayName("후기 수정 실패 (작성자, 요청자 다름)")
	void deletePostFail3Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().title("제목").content("내용").userAccount(user).view(1L).certificationName("정보처리기사").build();
		given(commonReviewService.findReview(100L)).willReturn(review);
		given(commonReviewService.findUserAccount("testId1"))
				.willReturn(UserAccount.builder().userId("testId1").build());

		// When
		boolean result = reviewService.deleteReview(100L, "testId1");

		// Then
		assertFalse(result);
		verify(reviewRepository, never()).deleteById(100L);
	}

	@Test
	@DisplayName("후기 삭제 성공")
	void deletePostSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Review review = Review.builder().title("제목").content("내용").userAccount(user).view(1L).certificationName("정보처리기사").build();

		given(commonReviewService.findReview(any())).willReturn(review);
		given(commonReviewService.findUserAccount("testId")).willReturn(user);

		// When
		boolean result = reviewService.deleteReview(100L, "testId");

		// Then
		assertTrue(result);
		verify(reviewRepository).deleteById(100L);
	}
}
