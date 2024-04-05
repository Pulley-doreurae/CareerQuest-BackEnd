package pulleydoreurae.careerquestbackend.community.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostViewCheck;
import pulleydoreurae.careerquestbackend.community.repository.CommentRepository;
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
	UserAccountRepository userAccountRepository;
	@Mock
	CommentRepository commentRepository;
	@Mock
	PostLikeRepository postLikeRepository;
	@Mock
	PostViewCheckRepository postViewCheckRepository;

	@Test
	@DisplayName("1. 게시글 리스트를 불러오는 테스트")
	void getPostListTest() {
		// Given
		insertUserAccount();
		Post post1 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		Post post2 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.build();
		Post post3 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.build();
		Post post4 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.build();
		Post post5 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		Page<Post> list = new PageImpl<>(List.of(post3, post4, post5), pageable, 3);

		given(postRepository.findAllByOrderByIdDesc(pageable)).willReturn(list);

		// When
		List<PostResponse> result = postService.getPostResponseList(pageable);

		// Then
		Page<Post> findAll = postRepository.findAllByOrderByIdDesc(pageable);
		assertEquals(3, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.getContent().get(0)),
				postToPostResponse(findAll.getContent().get(1)),
				postToPostResponse(findAll.getContent().get(2)));
	}

	@Test
	@DisplayName("2. 게시글 단건 조회 (실패)")
	void findByPostIdFailTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		given(postRepository.findById(100L)).willReturn(Optional.empty());

		// When
		PostResponse result = postService.findByPostId(request, response, 100L);

		// Then
		assertNull(result);
	}

	@Test
	@DisplayName("3. 게시글 단건 조회 (성공 - 조회수 증가 X)")
	void findByPostIdSuccessTest() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		insertUserAccount();
		Post post = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));
		given(postViewCheckRepository.findById(any())).willReturn(Optional.of(new PostViewCheck("user", post.getId())));

		// When
		// 이미 해당 게시글이 같은 사용자에 의해 방문되었다면 조회수는 증가하지 않음을 테스트한다.
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		PostResponse result = postService.findByPostId(request, response, 100L);
		PostResponse expect = postToPostResponse(post);

		// Then
		assertAll(
				() -> assertEquals(expect.getUserId(), result.getUserId()),
				() -> assertEquals(expect.getTitle(), result.getTitle()),
				() -> assertEquals(expect.getContent(), result.getContent()),
				() -> assertEquals(expect.getCategory(), result.getCategory()),
				() -> assertEquals(0, result.getHit())
		);
	}

	@Test
	@DisplayName("4. 게시글 등록 테스트 (실패)")
	void savePostFailTest() {
		// Given
		given(userAccountRepository.findByUserId(any())).willReturn(Optional.empty());

		// When
		boolean result = postService.savePost(new PostRequest("testId", "제목", "내용", 1L));

		// Then
		assertFalse(result);
	}

	@Test
	@DisplayName("5. 게시글 등록 테스트 (성공)")
	void savePostSuccessTest() {
		// Given
		insertUserAccount();

		// When
		boolean result = postService.savePost(new PostRequest("testId", "제목", "내용", 1L));

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("6. 게시글 수정 테스트 (실패 / 회원정보 없음)")
	void updatePostFailTest1() {
		// Given
		given(userAccountRepository.findByUserId(any())).willReturn(Optional.empty());
		Post post = Post.builder()
				.userAccount(UserAccount.builder()
						.userId("testId")
						.build())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L));

		// Then
		assertFalse(result);
	}

	@Test
	@DisplayName("7. 게시글 수정 테스트 (실패 / 작성자, 수정자 다름)")
	void updatePostFailTest2() {
		// Given
		insertUserAccount();
		Post post = Post.builder()
				.userAccount(UserAccount.builder()
						.userId("test")
						.build())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L));

		// Then
		assertFalse(result);
	}

	@Test
	@DisplayName("8. 게시글 수정 테스트 (성공)")
	void updatePostSuccessTest() {
		// Given
		insertUserAccount();
		Post post = Post.builder()
				.userAccount(UserAccount.builder()
						.userId("testId")
						.build())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));

		// When
		boolean result = postService.updatePost(100L, new PostRequest("testId", "제목", "내용", 1L));

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("9. 게시글 삭제 테스트 (실패 / 회원정보 없음)")
	void deletePostFailTest1() {
		// Given
		given(userAccountRepository.findByUserId(any())).willReturn(Optional.empty());
		Post post = Post.builder()
				.userAccount(UserAccount.builder()
						.userId("testId")
						.build())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertFalse(result);
	}

	@Test
	@DisplayName("10. 게시글 삭제 테스트 (실패 / 작성자, 요청자 다름)")
	void deletePostFailTest2() {
		// Given
		insertUserAccount();
		Post post = Post.builder()
				.userAccount(UserAccount.builder()
						.userId("test")
						.build())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertFalse(result);
	}

	@Test
	@DisplayName("11. 게시글 삭제 테스트 (성공)")
	void deletePostSuccessTest() {
		// Given
		insertUserAccount();
		Post post = Post.builder()
				.userAccount(UserAccount.builder()
						.userId("testId")
						.build())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));

		// When
		boolean result = postService.deletePost(100L, "testId");

		// Then
		assertTrue(result);
	}

	@Test
	@DisplayName("12. 게시글 리스트를 카테고리로 불러오는 테스트")
	void getPostListByCategoryTest() {
		// Given
		insertUserAccount();
		Post post1 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		Post post2 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.build();
		Post post3 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.build();
		Post post4 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.build();
		Post post5 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.build();
		Post post6 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목6")
				.content("내용6")
				.category(2L)
				.hit(0L)
				.build();
		Post post7 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목7")
				.content("내용7")
				.category(2L)
				.hit(0L)
				.build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		Page<Post> list = new PageImpl<>(List.of(post3, post5, post7), pageable, 3);

		given(postRepository.findAllByCategoryOrderByIdDesc(1L, pageable)).willReturn(list);

		// When
		List<PostResponse> result = postService.getPostResponseListByCategory(1L, pageable);

		// Then
		Page<Post> findAll = postRepository.findAllByCategoryOrderByIdDesc(1L, pageable);
		assertEquals(3, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.getContent().get(0)),
				postToPostResponse(findAll.getContent().get(1)),
				postToPostResponse(findAll.getContent().get(2)));
	}

	@Test
	@DisplayName("13. 한 사용자가 작성한 게시글 리스트를 불러오는 테스트")
	void getPostListByUserAccountTest() {
		// Given
		insertUserAccount();
		UserAccount user2 = UserAccount.builder()
				.userId("testId2")
				.build();

		given(userAccountRepository.findByUserId("testId2")).willReturn(Optional.ofNullable(user2));

		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		Post post2 = Post.builder()
				.userAccount(user)
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.build();
		Post post3 = Post.builder()
				.userAccount(user)
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.build();
		Post post4 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId2").get())
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.build();
		Post post5 = Post.builder()
				.userAccount(user)
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.build();
		Post post6 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId2").get())
				.title("제목6")
				.content("내용6")
				.category(2L)
				.hit(0L)
				.build();
		Post post7 = Post.builder()
				.userAccount(user)
				.title("제목7")
				.content("내용7")
				.category(2L)
				.hit(0L)
				.build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		Page<Post> list = new PageImpl<>(List.of(post3, post5, post7), pageable, 3); // 3개씩 자른다면 마지막 3개가 반환되어야 함

		given(postRepository.findAllByUserAccountOrderByIdDesc(user, pageable)).willReturn(list);

		// When
		List<PostResponse> result = postService.getPostListByUserAccount("testId", pageable);

		// Then
		Page<Post> findAll = postRepository.findAllByUserAccountOrderByIdDesc(user, pageable);
		assertEquals(3, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.getContent().get(0)),
				postToPostResponse(findAll.getContent().get(1)),
				postToPostResponse(findAll.getContent().get(2)));
	}

	@Test
	@DisplayName("14. 게시글 단건 조회 (성공 - 조회수 증가 O 모두 다른 사용자의 요청)")
	void findByPostIdSuccess2Test() {
		// Given
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		insertUserAccount();
		Post post = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));
		given(postViewCheckRepository.findById(any())).willReturn(Optional.of(new PostViewCheck("user", 1L)));

		// When
		// 게시글이 모두 다른 사용자들의 요청에 의해 호출되는 경우
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		postService.findByPostId(request, response, 100L);
		PostResponse result = postService.findByPostId(request, response, 100L);
		PostResponse expect = postToPostResponse(post);

		// Then
		assertAll(
				() -> assertEquals(expect.getUserId(), result.getUserId()),
				() -> assertEquals(expect.getTitle(), result.getTitle()),
				() -> assertEquals(expect.getContent(), result.getContent()),
				() -> assertEquals(expect.getCategory(), result.getCategory()),
				() -> assertEquals(6, result.getHit())
		);
	}

	@Test
	@DisplayName("15. UUID 값이 정상적으로 생성되는지 테스트")
	public void testGetUUIDWithNoExistingCookie() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// getUUID 메서드 실행
		String uuid = postService.getUUID(request, response);

		// UUID 값 검증
		assertThat(uuid).isNotNull();

		// 새로 생성된 쿠키 검증
		assertThat(response.getCookies()).isNotNull();
		assertThat(response.getCookies()[0].getName()).isEqualTo("UUID");
		assertThat(response.getCookies()[0].getValue()).isEqualTo(uuid);
	}

	@Test
	@DisplayName("검색어로만 검색하는 테스트")
	void searchPostsTest1() {
	    // Given
		insertUserAccount();
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = Post.builder().userAccount(user).title("검검색어어").content("내용1").category(1L).hit(0L).build();
		Post post2 = Post.builder().userAccount(user).title("제목2").content("검검색어어").category(2L).hit(0L).build();
		Post post3 = Post.builder().userAccount(user).title("검색어어어").content("내용3").category(1L).hit(0L).build();
		Post post4 = Post.builder().userAccount(user).title("제목4").content("검검검색어어").category(2L).hit(0L).build();
		Post post5 = Post.builder().userAccount(user).title("검색어").content("내용5").category(1L).hit(0L).build();
		Post post6 = Post.builder().userAccount(user).title("검색어").content("검색어").category(2L).hit(0L).build();
		Post post7 = Post.builder().userAccount(user).title("제목7").content("검색어").category(2L).hit(0L).build();

		// When

		Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending()); // 한 페이지에 3개씩 자르기
		Page<Post> list = new PageImpl<>(List.of(post5, post6, post7), pageable, 3); // 3개씩 자른다면 마지막 3개가 반환되어야 함
		given(postRepository.searchByKeyword("검색어", pageable)).willReturn(list);

		// When
		List<PostResponse> result = postService.searchPosts("검색어", null, pageable);

		// Then
		Page<Post> findAll = postRepository.searchByKeyword("검색어", pageable);
		assertEquals(3, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.getContent().get(0)),
				postToPostResponse(findAll.getContent().get(1)),
				postToPostResponse(findAll.getContent().get(2)));
	}

	@Test
	@DisplayName("검색어와 카테고리로 검색하는 테스트")
	void searchPostsTest2() {
	    // Given
		insertUserAccount();
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = Post.builder().userAccount(user).title("검검색어어").content("내용1").category(1L).hit(0L).build();
		Post post2 = Post.builder().userAccount(user).title("제목2").content("검검색어어").category(2L).hit(0L).build();
		Post post3 = Post.builder().userAccount(user).title("검색어어어").content("내용3").category(1L).hit(0L).build();
		Post post4 = Post.builder().userAccount(user).title("제목4").content("검검검색어어").category(2L).hit(0L).build();
		Post post5 = Post.builder().userAccount(user).title("검색어").content("내용5").category(1L).hit(0L).build();
		Post post6 = Post.builder().userAccount(user).title("검색어").content("검색어").category(2L).hit(0L).build();
		Post post7 = Post.builder().userAccount(user).title("제목7").content("검색어").category(2L).hit(0L).build();

		Pageable pageable = PageRequest.of(0, 3, Sort.by("id").descending()); // 한 페이지에 3개씩 자르기
		Page<Post> list = new PageImpl<>(List.of(post4, post6, post7), pageable, 3); // 3개씩 자른다면 마지막 3개가 반환되어야 함
		given(postRepository.searchByKeywordAndCategory("검색어", 2L, pageable)).willReturn(list);

		// When
		List<PostResponse> result = postService.searchPosts("검색어", 2L, pageable);

		// Then
		Page<Post> findAll = postRepository.searchByKeywordAndCategory("검색어", 2L, pageable);
		assertEquals(3, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.getContent().get(0)),
				postToPostResponse(findAll.getContent().get(1)),
				postToPostResponse(findAll.getContent().get(2)));
	}

	// 사용자 정보 저장 메서드
	public void insertUserAccount() {
		UserAccount user = UserAccount.builder()
				.userId("testId")
				.build();

		given(userAccountRepository.findByUserId("testId"))
				.willReturn(Optional.ofNullable(user));
	}

	// Post -> PostResponse 변환 메서드
	PostResponse postToPostResponse(Post post) {
		return PostResponse.builder()
				.userId(post.getUserAccount().getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.hit(post.getHit())
				.commentCount((long)commentRepository.findAllByPost(post).size())
				.postLikeCount((long)postLikeRepository.findAllByPost(post).size())
				.category(post.getCategory())
				.build();
	}
}
