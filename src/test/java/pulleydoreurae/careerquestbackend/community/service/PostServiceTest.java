package pulleydoreurae.careerquestbackend.community.service;

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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.service.FileManagementService;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostImage;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostViewCheck;
import pulleydoreurae.careerquestbackend.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostViewCheckRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/03/31
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {
	@InjectMocks
	PostService postService;
	@Mock
	PostRepository postRepository;
	@Mock
	PostLikeRepository postLikeRepository;
	@Mock
	PostViewCheckRepository postViewCheckRepository;
	@Mock
	PostImageRepository postImageRepository;
	@Mock
	FileManagementService fileManagementService;
	@Mock
	CommonCommunityService commonCommunityService;

	@Test
	@DisplayName("게시글 불러오기 실패")
	void findByPostIdNullTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		given(commonCommunityService.findPost(any())).willReturn(null);

		// When
		PostResponse result = postService.findByPostId(request, response, 100L);

		// Then
		assertNull(result);
		verify(commonCommunityService, never()).postToPostResponse(new Post(), 0);
	}

	@Test
	@DisplayName("게시글 불러오기 성공")
	void findByPostIdNotNullTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Post post = Post.builder().title("제목").content("내용").hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(post);
		given(commonCommunityService.postToPostResponse(post, 0)).willReturn(
				new PostResponse("A", "A", "A", List.of(), 1L, 1L, 1L, 1L, 1, "A", "A"));

		// When
		PostResponse result = postService.findByPostId(request, response, 100L);

		// Then
		assertNotNull(result);
		verify(commonCommunityService).postToPostResponse(post, 0);
	}

	@Test
	@DisplayName("조회수 증가 테스트")
	void checkViewTest1() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Post post = Post.builder().title("제목").content("내용").hit(1L).category(1L).build();
		// Authentication Mocking
		Authentication authentication = new UsernamePasswordAuthenticationToken("testId", null,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		// SecurityContext 에 Authentication 객체 설정
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
		given(commonCommunityService.findPostViewCheck("testId")).willReturn(null);

		// When
		String result = postService.checkView(request, response, 100L, post);

		// Then
		assertEquals("testId", result);
		assertEquals(2, post.getHit());
		verify(postViewCheckRepository).save(any());
	}

	@Test
	@DisplayName("조회수 증가X 테스트")
	void checkViewTest2() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		Post post = Post.builder().title("제목").content("내용").hit(1L).category(1L).build();
		// Authentication Mocking
		Authentication authentication = new UsernamePasswordAuthenticationToken("testId", null,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		// SecurityContext 에 Authentication 객체 설정
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);
		PostViewCheck postViewCheck = new PostViewCheck("testId", 100L);
		given(commonCommunityService.findPostViewCheck("testId")).willReturn(postViewCheck);

		// When
		String result = postService.checkView(request, response, 100L, post);

		// Then
		assertEquals("testId", result);
		assertEquals(1, post.getHit());
		verify(postViewCheckRepository, never()).save(any());
	}

	@Test
	@DisplayName("좋아요 상태 0")
	void getIsLikedTest1() {
		// Given
		given(postLikeRepository.existsByPostAndUserAccount(any(), any())).willReturn(false);

		// When
		int result = postService.getIsLiked("testId", new Post());

		// Then
		assertEquals(0, result);
	}

	@Test
	@DisplayName("좋아요 상태 1")
	void getIsLikedTest2() {
		// Given
		given(postLikeRepository.existsByPostAndUserAccount(any(), any())).willReturn(true);

		// When
		int result = postService.getIsLiked("testId", new Post());

		// Then
		assertEquals(1, result);
	}

	@Test
	@DisplayName("사진 서버 저장 테스트 (성공)")
	void saveImageSuccessTest() {
		// Given
		given(fileManagementService.saveFile(any(), any())).willReturn("anyString()");

		// When
		MockMultipartFile file1 = new MockMultipartFile("test1", "Test1.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("test2", "Test2.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file3 = new MockMultipartFile("test3", "Test3.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file4 = new MockMultipartFile("test4", "Test4.png", "image/png", "사진내용".getBytes());

		// Then
		assertDoesNotThrow(() -> postService.saveImage(List.of(file1, file2, file3, file4)));
	}

	@Test
	@DisplayName("사진 서버 저장 테스트 (실패)")
	void saveImageFailTest() {
		// Given
		given(fileManagementService.saveFile(any(), any())).willReturn(null);

		// When
		MockMultipartFile file1 = new MockMultipartFile("test1", "Test1.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("test2", "Test2.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file3 = new MockMultipartFile("test3", "Test3.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file4 = new MockMultipartFile("test4", "Test4.png", "image/png", "사진내용".getBytes());

		// Then
		assertThrows(RuntimeException.class, () -> postService.saveImage(List.of(file1, file2, file3, file4)));
	}

	@Test
	@DisplayName("게시글 등록 테스트 (성공)")
	void savePostSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		given(commonCommunityService.findUserAccount("testId")).willReturn(user);

		// When
		boolean result = postService.savePost(new PostRequest("testId", "제목", "내용", 1L, null));

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("게시글 등록 테스트 (실패)")
	void savePostFailTest() {
		// Given
		given(commonCommunityService.findUserAccount("testId")).willReturn(null);

		// When
		boolean result = postService.savePost(new PostRequest("testId", "제목", "내용", 1L, null));

		// Then
		assertFalse(result);
	}

	@Test
	@DisplayName("게시글 사진과 함께 등록 테스트 (성공)")
	void savePostWithImageSuccessTest() {
		// Given
		String image1 = "image1.png";
		String image2 = "image2.png";
		String image3 = "image3.png";
		String image4 = "image4.png";
		String image5 = "image5.png";
		List<String> images = List.of(image1, image2, image3, image4, image5);
		UserAccount user = UserAccount.builder().userId("testId").build();
		given(commonCommunityService.findUserAccount("testId")).willReturn(user);

		// When
		boolean result = postService.savePost(new PostRequest("testId", "제목", "내용", 1L, images));

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("게시글 사진과 함께 등록 테스트 (실패)")
	void savePostWithImageFailTest() {
		// Given
		String image1 = "image1.png";
		String image2 = "image2.png";
		String image3 = "image3.png";
		String image4 = "image4.png";
		String image5 = "image5.png";
		List<String> images = List.of(image1, image2, image3, image4, image5);
		given(commonCommunityService.findUserAccount("testId")).willReturn(null);

		// When
		boolean result = postService.savePost(new PostRequest("testId", "제목", "내용", 1L, images));

		// Then
		assertFalse(result);
		// 저장된 이미지를 삭제하는 메서드가 동작했는지 검증
		verify(fileManagementService).deleteFile(anyList(), any());
	}

	@Test
	@DisplayName("게시글 수정 실패 (게시글 찾을 수 없음)")
	void updatePostFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(null);
		given(commonCommunityService.findUserAccount(any())).willReturn(user);

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L, null));

		// Then
		assertFalse(result);
		verify(commonCommunityService, never()).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 수정 실패 (사용자 찾을 수 없음)")
	void updatePostFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(post);
		given(commonCommunityService.findUserAccount(any())).willReturn(null);

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L, null));

		// Then
		assertFalse(result);
		verify(commonCommunityService, never()).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 수정 실패 (작성자, 수정자 다름)")
	void updatePostFail3Test() {
		// Given
		UserAccount user1 = UserAccount.builder().userId("testId1").build();
		UserAccount user2 = UserAccount.builder().userId("testId2").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user1).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(post);
		given(commonCommunityService.findUserAccount(any())).willReturn(user2);

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId2", "제목", "내용", 1L, null));

		// Then
		assertFalse(result);
		verify(commonCommunityService, never()).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository, never()).save(any());
	}

	@Test
	@DisplayName("게시글 수정 성공")
	void updatePostSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(post);
		given(commonCommunityService.findUserAccount(any())).willReturn(user);

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L, null));

		// Then
		assertTrue(result);
		verify(commonCommunityService).postRequestToPostForUpdate(any(), any(), any());
		verify(postRepository).save(any());
	}

	@Test
	@DisplayName("이미지 수정 테스트")
	void updateImagesTest() {
		// Given
		PostImage postImage1 = PostImage.builder().post(new Post()).fileName("image1.png").build();
		PostImage postImage2 = PostImage.builder().post(new Post()).fileName("image2.png").build();
		PostImage postImage3 = PostImage.builder().post(new Post()).fileName("image3.png").build();
		PostImage postImage4 = PostImage.builder().post(new Post()).fileName("image4.png").build();
		PostImage postImage5 = PostImage.builder().post(new Post()).fileName("image5.png").build();
		List<PostImage> images = List.of(postImage1, postImage2, postImage3, postImage4, postImage5);
		given(postImageRepository.findAllByPost(any())).willReturn(images);
		given(postImageRepository.existsByFileName("image2.png")).willReturn(true);
		given(postImageRepository.existsByFileName("image3.png")).willReturn(true);

		String image2 = "image2.png";
		String image3 = "image3.png";
		String image6 = "image6.png";
		List<String> input = List.of(image2, image3, image6);
		// When
		postService.updateImages(input, new Post());

		// Then
		verify(fileManagementService).deleteFile(anyList(), any());
		// 2, 3 을 제외한 1, 4, 5 가 삭제됨
		verify(postImageRepository, times(3)).deleteByFileName(any());
		// 2, 3, 6이 데이터베이스에 있는지 각각 확인
		verify(postImageRepository, times(3)).existsByFileName(any());
		// 데이터베이스에 없는 6만 저장하므로 1번만 실행
		verify(postImageRepository).save(any());
	}

	@Test
	@DisplayName("게시글 삭제 실패 (게시글 찾을 수 없음)")
	void deletePostFail1Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(null);
		given(commonCommunityService.findUserAccount("testId")).willReturn(user);

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertFalse(result);
		verify(postRepository, never()).deleteById(100L);
		verify(postImageRepository, never()).findAllByPost(any());
		verify(fileManagementService, never()).deleteFile(anyList(), anyString());
	}

	@Test
	@DisplayName("게시글 삭제 실패 (사용자 찾을 수 없음)")
	void deletePostFail2Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(post);
		given(commonCommunityService.findUserAccount("testId")).willReturn(null);

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertFalse(result);
		verify(postRepository, never()).deleteById(100L);
		verify(postImageRepository, never()).findAllByPost(any());
		verify(fileManagementService, never()).deleteFile(anyList(), anyString());
	}

	@Test
	@DisplayName("게시글 수정 실패 (작성자, 요청자 다름)")
	void deletePostFail3Test() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(post);

		// When
		boolean result = postService.deletePost(100L, "testId1");

		// Then
		assertFalse(result);
		verify(postRepository, never()).deleteById(100L);
		verify(postImageRepository, never()).findAllByPost(any());
		verify(fileManagementService, never()).deleteFile(anyList(), anyString());
	}

	@Test
	@DisplayName("게시글 삭제 성공")
	void deletePostSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(any())).willReturn(post);
		given(commonCommunityService.findUserAccount("testId")).willReturn(user);
		given(postImageRepository.findAllByPost(any())).willReturn(List.of());

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertTrue(result);
		verify(postRepository).deleteById(100L);
		verify(postImageRepository).findAllByPost(post);
		verify(fileManagementService, never()).deleteFile(anyList(), any());
	}

	@Test
	@DisplayName("게시글 사진과 함께 삭제 테스트 (성공)")
	void deletePostWithImagesSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().title("제목").content("내용").userAccount(user).hit(1L).category(1L).build();
		given(commonCommunityService.findPost(100L)).willReturn(post);
		given(commonCommunityService.findUserAccount("testId")).willReturn(user);
		given(postImageRepository.findAllByPost(any())).willReturn(List.of(new PostImage()));

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertTrue(result);
		verify(postRepository).deleteById(100L);
		verify(postImageRepository).findAllByPost(post);
		verify(fileManagementService).deleteFile(anyList(), any());
	}
}
