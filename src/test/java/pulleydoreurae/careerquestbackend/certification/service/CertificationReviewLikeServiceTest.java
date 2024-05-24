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
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReview;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReviewLike;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostLikeRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.common.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증 후기 좋아요 Service 테스트")
class CertificationReviewLikeServiceTest {

	@InjectMocks
	CertificationReviewLikeService postLikeService;
	@Mock
	PostLikeRepository postLikeRepository;
	@Mock
	CommonCertificationService commonCertificationService;

	@Test
	@DisplayName("좋아요 증가 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void postLikePlusFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCertificationService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(0).build();

		// Then
		assertThrows(UsernameNotFoundException.class, () -> postLikeService.changePostLike(request));
		verify(postLikeRepository, never()).save(any());
		verify(postLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 증가 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void postLikePlusFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCertificationService.findUserAccount("testId")).willReturn(user);
		given(commonCertificationService.findPost(10000L))
				.willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		// When
		PostLikeRequest request = PostLikeRequest.builder()
				.postId(10000L)
				.userId("testId")
				.isLiked(0)
				.build();

		// Then
		assertThrows(PostNotFoundException.class, () -> postLikeService.changePostLike(request));
		verify(postLikeRepository, never()).save(any());
		verify(postLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 증가 테스트 (성공)")
	void postLikePlusSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = CertificationReview.builder().userAccount(user).id(10000L).title("제목1").build();

		given(commonCertificationService.findUserAccount("testId")).willReturn(user);
		given(commonCertificationService.findPost(10000L)).willReturn(post);

		// When
		PostLikeRequest request = PostLikeRequest.builder()
				.postId(10000L)
				.userId("testId")
				.isLiked(0)
				.build();

		// Then
		boolean result = postLikeService.changePostLike(request);

		assertTrue(result);
		verify(postLikeRepository).save(any());
		verify(postLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 감소 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void postLikeMinusFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCertificationService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(1).build();

		// Then
		assertThrows(UsernameNotFoundException.class, () -> postLikeService.changePostLike(request));
		verify(postLikeRepository, never()).save(any());
		verify(postLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("좋아요 감소 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void postLikeMinusFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCertificationService.findUserAccount("testId")).willReturn(user);
		given(commonCertificationService.findPost(10000L))
				.willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(1).build();

		// Then
		assertThrows(PostNotFoundException.class, () -> postLikeService.changePostLike(request));
		verify(postLikeRepository, never()).save(any());
	}

	@Test
	@DisplayName("좋아요 감소 테스트 (성공)")
	void postLikeMinusSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = CertificationReview.builder().userAccount(user).id(10000L).title("제목1").build();
		PostLike postLike = CertificationReviewLike.builder().userAccount(user).post(post).build();

		given(commonCertificationService.findUserAccount("testId")).willReturn(user);
		given(commonCertificationService.findPost(10000L)).willReturn(post);
		given(commonCertificationService.findPostLike(post, user)).willReturn(postLike);

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(1).build();

		// Then
		boolean result = postLikeService.changePostLike(request);

		assertTrue(result);
		verify(postLikeRepository, never()).save(any());
		verify(postLikeRepository).delete(any());
	}

	@Test
	@DisplayName("한 회원이 좋아요 누른 게시글 불러오기")
	void findAllPostLikeByUserAccountTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post1 = CertificationReview.builder().userAccount(user).id(10001L).title("제목1").build();
		Post post2 = CertificationReview.builder().userAccount(user).id(10002L).title("제목2").build();
		Post post3 = CertificationReview.builder().userAccount(user).id(10003L).title("제목3").build();
		Post post4 = CertificationReview.builder().userAccount(user).id(10004L).title("제목4").build();
		Post post5 = CertificationReview.builder().userAccount(user).id(10005L).title("제목5").build();
		PostLike postLike1 = CertificationReviewLike.builder().userAccount(user).post(post1).build();
		PostLike postLike2 = CertificationReviewLike.builder().userAccount(user).post(post2).build();
		PostLike postLike3 = CertificationReviewLike.builder().userAccount(user).post(post3).build();
		PostLike postLike4 = CertificationReviewLike.builder().userAccount(user).post(post4).build();
		PostLike postLike5 = CertificationReviewLike.builder().userAccount(user).post(post5).build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		Page<PostLike> list = new PageImpl<>(
				List.of(postLike3, postLike4, postLike5), pageable, 3); // 3개씩 자른다면 마지막 3개가 반환되어야 함

		given(commonCertificationService.findUserAccount("testId")).willReturn(user);
		given(commonCertificationService.postToPostResponse(post3, 0)).willReturn(postToPostResponse(post3));
		given(commonCertificationService.postToPostResponse(post4, 0)).willReturn(postToPostResponse(post4));
		given(commonCertificationService.postToPostResponse(post5, 0)).willReturn(postToPostResponse(post5));
		given(postLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable))
				.willReturn(list);

		// When
		List<PostResponse> result = postLikeService.findAllPostLikeByUserAccount(user.getUserId(), pageable);

		// Then
		assertEquals(3, result.size());
		System.out.println(result.get(0));
		assertThat(result).contains(
				postToPostResponse(post3),
				postToPostResponse(post4),
				postToPostResponse(post5)
		);
		verify(postLikeRepository).findAllByUserAccountOrderByIdDesc(user, pageable);
		verify(commonCertificationService).postToPostResponse(post3, 0);
		verify(commonCertificationService).postToPostResponse(post4, 0);
		verify(commonCertificationService).postToPostResponse(post5, 0);
	}

	// Post -> PostResponse 변환 메서드
	PostResponse postToPostResponse(Post post) {
		return PostResponse.builder()
				.userId(post.getUserAccount().getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.view(post.getView())
				.commentCount(0L)
				.postLikeCount(0L)
				.postCategory(post.getPostCategory())
				.build();
	}
}
