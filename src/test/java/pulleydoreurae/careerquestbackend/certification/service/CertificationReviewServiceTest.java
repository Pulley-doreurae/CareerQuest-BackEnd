package pulleydoreurae.careerquestbackend.certification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

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
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReview;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReviewViewCheck;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostViewCheck;
import pulleydoreurae.careerquestbackend.common.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostViewCheckRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증 후기 Service 테스트")
class CertificationReviewServiceTest {

	@InjectMocks
	CertificationReviewService postService;
	@Mock
	PostRepository postRepository;
	@Mock
	PostLikeRepository postLikeRepository;
	@Mock
	PostViewCheckRepository postViewCheckRepository;
	@Mock
	PostImageRepository postImageRepository;
	@Mock
	CommonCertificationService commonCertificationService;

	@Test
	@DisplayName("게시글 불러오기 실패")
	void findByPostIdNullTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		given(commonCertificationService.findPost(100L)).willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> postService.findByPostId(request, response, 100L));
		verify(commonCertificationService, never()).postToPostResponse(new CertificationReview(), 0);
	}

	@Test
	@DisplayName("게시글 불러오기 성공")
	void findByPostIdNotNullTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Post post = CertificationReview.builder().title("제목").content("내용").view(1L).category(1L).build();
		given(commonCertificationService.findPost(any())).willReturn(post);
		given(commonCertificationService.postToPostResponse(post, 0)).willReturn(
				new PostResponse("A", "A", "A", List.of(), 1L, 1L, 1L, 1L, 1, "A", "A"));

		// When
		PostResponse result = postService.findByPostId(request, response, 100L);

		// Then
		assertNotNull(result);
		verify(commonCertificationService).postToPostResponse(post, 0);
	}

	@Test
	@DisplayName("조회수 증가 테스트")
	void checkViewTest1() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Post post = CertificationReview.builder().title("제목").content("내용").view(1L).category(1L).build();
		// Authentication Mocking
		Authentication authentication = new UsernamePasswordAuthenticationToken("testId", null,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		// SecurityContext 에 Authentication 객체 설정
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
		given(commonCertificationService.findPostViewCheck("testId")).willReturn(null);

		// When
		String result = postService.checkView(request, response, 100L, post);

		// Then
		assertEquals("testId", result);
		assertEquals(2, post.getView());
		verify(postViewCheckRepository).save(any());
	}

	@Test
	@DisplayName("조회수 증가X 테스트")
	void checkViewTest2() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Post post = CertificationReview.builder().title("제목").content("내용").view(1L).category(1L).build();
		// Authentication Mocking
		Authentication authentication = new UsernamePasswordAuthenticationToken("testId", null,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		// SecurityContext 에 Authentication 객체 설정
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
		PostViewCheck postViewCheck = new CertificationReviewViewCheck("testId", 100L);
		given(commonCertificationService.findPostViewCheck("testId")).willReturn(postViewCheck);

		// When
		String result = postService.checkView(request, response, 100L, post);

		// Then
		assertEquals("testId", result);
		assertEquals(1, post.getView());
		verify(postViewCheckRepository, never()).save(any());
	}

	@Test
	@DisplayName("좋아요 상태 0")
	void getIsLikedTest1() {
		// Given
		given(postLikeRepository.existsByPostAndUserAccount(any(), any())).willReturn(false);

		// When
		int result = postService.getIsLiked("testId", new CertificationReview());

		// Then
		assertEquals(0, result);
	}

	@Test
	@DisplayName("좋아요 상태 1")
	void getIsLikedTest2() {
		// Given
		given(postLikeRepository.existsByPostAndUserAccount(any(), any())).willReturn(true);

		// When
		int result = postService.getIsLiked("testId", new CertificationReview());

		// Then
		assertEquals(1, result);
	}

	@Test
	@DisplayName("게시글 등록 테스트 (성공)")
	void savePostSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		given(commonCertificationService.findUserAccount("testId")).willReturn(user);

		// When

		// Then
		assertDoesNotThrow(() -> postService.savePost(new PostRequest("testId", "제목", "내용", 1L, null)));
	}

	@Test
	@DisplayName("게시글 등록 테스트 (실패)")
	void savePostFailTest() {
		// Given
		given(commonCertificationService.findUserAccount("testId")).willThrow(UsernameNotFoundException.class);

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () ->
				postService.savePost(new PostRequest("testId", "제목", "내용", 1L, null)));
	}

	@Test
	@DisplayName("게시글 수정 실패 (게시글 찾을 수 없음)")
	void updatePostFail1Test() {
		// Given
		given(commonCertificationService.findPost(any())).willThrow(new PostNotFoundException("게시글을 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(PostNotFoundException.class,
				() -> postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L, null)));
		verify(commonCertificationService, never()).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 수정 실패 (사용자 찾을 수 없음)")
	void updatePostFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = CertificationReview.builder()
				.title("제목")
				.content("내용")
				.userAccount(user)
				.view(1L)
				.category(1L)
				.build();
		given(commonCertificationService.findPost(any())).willReturn(post);
		given(commonCertificationService.findUserAccount(any()))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(UsernameNotFoundException.class,
				() -> postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L, null)));
		verify(commonCertificationService, never()).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 수정 실패 (작성자, 수정자 다름)")
	void updatePostFail3Test() {
		// Given
		UserAccount user1 = UserAccount.builder().userId("testId1").build();
		UserAccount user2 = UserAccount.builder().userId("testId2").build();
		Post post = CertificationReview.builder()
				.title("제목")
				.content("내용")
				.userAccount(user1)
				.view(1L)
				.category(1L)
				.build();
		given(commonCertificationService.findPost(any())).willReturn(post);
		given(commonCertificationService.findUserAccount(any())).willReturn(user2);

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId2", "제목", "내용", 1L, null));

		// Then
		assertFalse(result);
		verify(commonCertificationService, never()).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 수정 성공")
	void updatePostSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = CertificationReview.builder()
				.title("제목")
				.content("내용")
				.userAccount(user)
				.view(1L)
				.category(1L)
				.build();
		given(commonCertificationService.findPost(any())).willReturn(post);
		given(commonCertificationService.findUserAccount(any())).willReturn(user);

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L, null));

		// Then
		assertTrue(result);
		verify(commonCertificationService).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository).save(any());
	}

	@Test
	@DisplayName("게시글 삭제 실패 (게시글 찾을 수 없음)")
	void deletePostFail1Test() {
		// Given
		given(commonCertificationService.findPost(any())).willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> postService.deletePost(100L, "testId"));
		verify(postRepository, never()).deleteById(100L);
	}

	@Test
	@DisplayName("게시글 삭제 실패 (사용자 찾을 수 없음)")
	void deletePostFail2Test() {
		// Given
		given(commonCertificationService.findUserAccount("testId"))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> postService.deletePost(100L, "testId"));
		verify(postRepository, never()).deleteById(100L);
	}

	@Test
	@DisplayName("게시글 수정 실패 (작성자, 요청자 다름)")
	void deletePostFail3Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = CertificationReview.builder()
				.title("제목")
				.content("내용")
				.userAccount(user)
				.view(1L)
				.category(1L)
				.build();
		given(commonCertificationService.findPost(100L)).willReturn(post);
		given(commonCertificationService.findUserAccount("testId1"))
				.willReturn(UserAccount.builder().userId("testId1").build());

		// When
		boolean result = postService.deletePost(100L, "testId1");

		// Then
		assertFalse(result);
		verify(postRepository, never()).deleteById(100L);
	}

	@Test
	@DisplayName("게시글 삭제 성공")
	void deletePostSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = CertificationReview.builder()
				.title("제목")
				.content("내용")
				.userAccount(user)
				.view(1L)
				.category(1L)
				.build();
		given(commonCertificationService.findPost(any())).willReturn(post);
		given(commonCertificationService.findUserAccount("testId")).willReturn(user);

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertTrue(result);
		verify(postRepository).deleteById(100L);
	}
}
