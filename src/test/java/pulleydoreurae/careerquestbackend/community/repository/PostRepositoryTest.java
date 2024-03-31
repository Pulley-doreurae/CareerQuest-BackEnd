package pulleydoreurae.careerquestbackend.community.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;

/**
 * @author : parkjihyeok
 * @since : 2024/03/31
 */
@DataJpaTest
@DisplayName("게시글 Repository 테스트")
class PostRepositoryTest {

	@Autowired
	PostRepository postRepository;
	@Autowired
	UserAccountRepository userAccountRepository;

	@BeforeEach
	void beforeEach() {
		UserAccount user = UserAccount.builder()
				.userId("testId")
				.userName("testName")
				.email("test@email.com")
				.phoneNum("010-1111-2222")
				.password("testPassword")
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();
		userAccountRepository.save(user);
	}

	@AfterEach
	void afterEach() {
		postRepository.deleteAll();
		userAccountRepository.deleteAll();
	}

	@Test
	@DisplayName("1. 게시글 저장 테스트")
	void savePostTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();

		// When
		postRepository.save(post);

		// Then
		Post savedPost = postRepository.findById(post.getId()).get();
		assertAll(
				() -> assertEquals(post.getUserAccount(), savedPost.getUserAccount()),
				() -> assertEquals(post.getTitle(), savedPost.getTitle()),
				() -> assertEquals(post.getContent(), savedPost.getContent()),
				() -> assertEquals(post.getCategory(), savedPost.getCategory()),
				() -> assertEquals(post.getHit(), savedPost.getHit()),
				() -> assertEquals(post.getLikeCount(), savedPost.getLikeCount())
		);
	}

	@Test
	@DisplayName("2. 게시글 리스트 테스트")
	void findListTest() {
		// Given
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
				.userAccount(user)
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

		// When
		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);

		// Then
		assertEquals(5, postRepository.findAll().size());
		assertThat(postRepository.findAll()).contains(post1, post2, post3, post4, post5);
	}

	@Test
	@DisplayName("3. 게시글 수정 테스트")
	void postUpdateTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		postRepository.save(post);

		// When
		Post getPost = postRepository.findById(post.getId()).get();
		Post updatePost = Post.builder()
				.id(getPost.getId()) // id 를 덮어써 갱신하기
				.userAccount(user)
				.title("수정된 제목1")
				.content("수정된 내용1")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		postRepository.save(updatePost);

		// Then
		Post updateAndSavedPost = postRepository.findById(updatePost.getId()).get();
		assertEquals(1, postRepository.findAll().size()); // 게시글이 갱신되어 게시글이 추가되지 않음을 확인
		assertAll(
				() -> assertEquals(updatePost.getUserAccount(), updateAndSavedPost.getUserAccount()),
				() -> assertEquals(updatePost.getTitle(), updateAndSavedPost.getTitle()),
				() -> assertEquals(updatePost.getContent(), updateAndSavedPost.getContent()),
				() -> assertEquals(updatePost.getCategory(), updateAndSavedPost.getCategory()),
				() -> assertEquals(updatePost.getHit(), updateAndSavedPost.getHit()),
				() -> assertEquals(updatePost.getLikeCount(), updateAndSavedPost.getLikeCount())
		);
	}

	@Test
	@DisplayName("4. 게시글 삭제 테스트")
	void deletePostTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.likeCount(0L)
				.build();
		postRepository.save(post);

		// When
		Post savedPost = postRepository.findById(post.getId()).get();
		postRepository.deleteById(savedPost.getId());

		// Then
		assertEquals(Optional.empty(), postRepository.findById(savedPost.getId()));
		assertEquals(0, postRepository.findAll().size());
	}
}