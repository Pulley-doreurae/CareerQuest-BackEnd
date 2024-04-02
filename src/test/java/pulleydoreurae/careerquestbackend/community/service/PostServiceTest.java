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

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;

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
				.likeCount(0L)
				.build();
		Post post2 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post3 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post4 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post5 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();

		given(postRepository.findAll()).willReturn(List.of(post1, post2, post3, post4, post5));

		// When
		List<PostResponse> result = postService.getPostResponseList();

		// Then
		List<Post> findAll = postRepository.findAll();
		assertEquals(5, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.get(0)),
				postToPostResponse(findAll.get(1)),
				postToPostResponse(findAll.get(2)),
				postToPostResponse(findAll.get(3)),
				postToPostResponse(findAll.get(4)));
	}

	@Test
	@DisplayName("2. 게시글 단건 조회 (실패)")
	void findByPostIdFailTest() {
		// Given
		given(postRepository.findById(100L)).willReturn(Optional.empty());

		// When
		PostResponse result = postService.findByPostId(100L);

		// Then
		assertNull(result);
	}

	@Test
	@DisplayName("3. 게시글 단건 조회 (성공)")
	void findByPostIdSuccessTest() {
		// Given
		insertUserAccount();
		Post post = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.id(100L)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.ofNullable(post));

		// When
		PostResponse result = postService.findByPostId(100L);
		PostResponse expect = postToPostResponse(post);

		// Then
		assertAll(
				() -> assertEquals(expect.getUserId(), result.getUserId()),
				() -> assertEquals(expect.getTitle(), result.getTitle()),
				() -> assertEquals(expect.getContent(), result.getContent()),
				() -> assertEquals(expect.getCategory(), result.getCategory()),
				() -> assertEquals(expect.getHit(), result.getHit()),
				() -> assertEquals(expect.getLikeCount(), result.getLikeCount())
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
				.likeCount(0L)
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
				.likeCount(0L)
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
				.likeCount(0L)
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
				.likeCount(0L)
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
				.likeCount(0L)
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
				.likeCount(0L)
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
				.likeCount(0L)
				.build();
		Post post2 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post3 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post4 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post5 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post6 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목6")
				.content("내용6")
				.category(2L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post7 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId").get())
				.title("제목7")
				.content("내용7")
				.category(2L)
				.hit(0L)
				.likeCount(0L)
				.build();

		given(postRepository.findAllByCategory(1L)).willReturn(List.of(post1, post2, post3, post4, post5));

		// When
		List<PostResponse> result = postService.getPostResponseListByCategory(1L);

		// Then
		List<Post> findAll = postRepository.findAllByCategory(1L);
		assertEquals(5, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.get(0)),
				postToPostResponse(findAll.get(1)),
				postToPostResponse(findAll.get(2)),
				postToPostResponse(findAll.get(3)),
				postToPostResponse(findAll.get(4)));
	}

	@Test
	@DisplayName("13. 한 사용자가 작성한 게시글 리스트를 불러오는 테스트")
	void getPostListByUserAccountTest() {
		// Given
		insertUserAccount();
		UserAccount user2 = UserAccount.builder()
				.userId("testId2")
				.build();

		given(userAccountRepository.findByUserId("testId2"))
				.willReturn(Optional.ofNullable(user2));

		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post1 = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post2 = Post.builder()
				.userAccount(user)
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post3 = Post.builder()
				.userAccount(user)
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post4 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId2").get())
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post5 = Post.builder()
				.userAccount(user)
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post6 = Post.builder()
				.userAccount(userAccountRepository.findByUserId("testId2").get())
				.title("제목6")
				.content("내용6")
				.category(2L)
				.hit(0L)
				.likeCount(0L)
				.build();
		Post post7 = Post.builder()
				.userAccount(user)
				.title("제목7")
				.content("내용7")
				.category(2L)
				.hit(0L)
				.likeCount(0L)
				.build();

		given(postRepository.findAllByUserAccount(user))
				.willReturn(List.of(post1, post2, post3, post5, post7));

		// When
		List<PostResponse> result = postService.getPostListByUserAccount("testId");

		// Then
		List<Post> findAll = postRepository.findAllByUserAccount(user);
		assertEquals(5, result.size());
		assertThat(result).contains(
				postToPostResponse(findAll.get(0)),
				postToPostResponse(findAll.get(1)),
				postToPostResponse(findAll.get(2)),
				postToPostResponse(findAll.get(3)),
				postToPostResponse(findAll.get(4)));
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
				.likeCount(post.getLikeCount())
				.category(post.getCategory())
				.build();
	}
}