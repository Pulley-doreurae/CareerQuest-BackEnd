package pulleydoreurae.careerquestbackend.basiccommunity.service;

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
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostLikeRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPost;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostLike;
import pulleydoreurae.careerquestbackend.common.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;

/**
 * @author : parkjihyeok
 * @since : 2024/04/03
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("좋아요 Service 테스트")
class BasicPostLikeServiceTest {

	@InjectMocks
	BasicPostLikeService postLikeService;
	@Mock
	PostLikeRepository postLikeRepository;
	@Mock
	CommonBasicCommunityService commonCommunityService;

	@Test
	@DisplayName("1. 좋아요 증가 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void postLikePlusFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCommunityService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(0).build();

		// Then
		assertThrows(UsernameNotFoundException.class, () -> postLikeService.changePostLike(request));
		verify(postLikeRepository, never()).save(any());
		verify(postLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("2. 좋아요 증가 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void postLikePlusFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCommunityService.findUserAccount("testId")).willReturn(user);
		given(commonCommunityService.findPost(10000L))
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
	@DisplayName("3. 좋아요 증가 테스트 (성공)")
	void postLikePlusSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = BasicPost.builder().userAccount(user).id(10000L).title("제목1").build();

		given(commonCommunityService.findUserAccount("testId")).willReturn(user);
		given(commonCommunityService.findPost(10000L)).willReturn(post);

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
	@DisplayName("4. 좋아요 감소 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void postLikeMinusFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCommunityService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(1).build();

		// Then
		assertThrows(UsernameNotFoundException.class, () -> postLikeService.changePostLike(request));
		verify(postLikeRepository, never()).save(any());
		verify(postLikeRepository, never()).delete(any());
	}

	@Test
	@DisplayName("5. 좋아요 감소 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void postLikeMinusFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();

		given(commonCommunityService.findUserAccount("testId")).willReturn(user);
		given(commonCommunityService.findPost(10000L))
				.willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(1).build();

		// Then
		assertThrows(PostNotFoundException.class, () -> postLikeService.changePostLike(request));
		verify(postLikeRepository, never()).save(any());
	}

	@Test
	@DisplayName("6. 좋아요 감소 테스트 (성공)")
	void postLikeMinusSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = BasicPost.builder().userAccount(user).id(10000L).title("제목1").build();
		PostLike postLike = BasicPostLike.builder().userAccount(user).post(post).build();

		given(commonCommunityService.findUserAccount("testId")).willReturn(user);
		given(commonCommunityService.findPost(10000L)).willReturn(post);
		given(commonCommunityService.findPostLike(post, user)).willReturn(postLike);

		// When
		PostLikeRequest request = PostLikeRequest.builder().postId(10000L).userId("testId").isLiked(1).build();

		// Then
		boolean result = postLikeService.changePostLike(request);

		assertTrue(result);
		verify(postLikeRepository, never()).save(any());
		verify(postLikeRepository).delete(any());
	}

	@Test
	@DisplayName("7. 한 회원이 좋아요 누른 게시글 불러오기")
	void findAllPostLikeByUserAccountTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post1 = BasicPost.builder().userAccount(user).id(10001L).title("제목1").build();
		Post post2 = BasicPost.builder().userAccount(user).id(10002L).title("제목2").build();
		Post post3 = BasicPost.builder().userAccount(user).id(10003L).title("제목3").build();
		Post post4 = BasicPost.builder().userAccount(user).id(10004L).title("제목4").build();
		Post post5 = BasicPost.builder().userAccount(user).id(10005L).title("제목5").build();
		PostLike postLike1 = BasicPostLike.builder().userAccount(user).post(post1).build();
		PostLike postLike2 = BasicPostLike.builder().userAccount(user).post(post2).build();
		PostLike postLike3 = BasicPostLike.builder().userAccount(user).post(post3).build();
		PostLike postLike4 = BasicPostLike.builder().userAccount(user).post(post4).build();
		PostLike postLike5 = BasicPostLike.builder().userAccount(user).post(post5).build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		Page<PostLike> list = new PageImpl<>(
				List.of(postLike3, postLike4, postLike5), pageable, 3); // 3개씩 자른다면 마지막 3개가 반환되어야 함

		given(commonCommunityService.findUserAccount("testId")).willReturn(user);
		given(commonCommunityService.postToPostResponse(post3, 0)).willReturn(postToPostResponse(post3));
		given(commonCommunityService.postToPostResponse(post4, 0)).willReturn(postToPostResponse(post4));
		given(commonCommunityService.postToPostResponse(post5, 0)).willReturn(postToPostResponse(post5));
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
		verify(commonCommunityService).postToPostResponse(post3, 0);
		verify(commonCommunityService).postToPostResponse(post4, 0);
		verify(commonCommunityService).postToPostResponse(post5, 0);
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
				.category(post.getCategory())
				.build();
	}
}
