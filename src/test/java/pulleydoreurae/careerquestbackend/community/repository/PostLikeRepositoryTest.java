package pulleydoreurae.careerquestbackend.community.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
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
import pulleydoreurae.careerquestbackend.community.domain.entity.PostLike;

/**
 * @author : parkjihyeok
 * @since : 2024/04/02
 */
@DataJpaTest
@DisplayName("좋아요 Repository 테스트")
class PostLikeRepositoryTest {

	@Autowired
	PostLikeRepository postLikeRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	PostRepository postRepository;

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
		postLikeRepository.deleteAll();
		userAccountRepository.deleteAll();
		postRepository.deleteAll();
	}

	@Test
	@DisplayName("1. 좋아요 누름 테스트 (증가)")
	void postLikePlusTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		postRepository.save(post);

		// When
		PostLike postLike = PostLike.builder()
				.userAccount(user)
				.post(post)
				.build();
		postLikeRepository.save(postLike);

		PostLike result = postLikeRepository.findByPostAndUserAccount(post, user).get();

		// Then
		assertEquals(1, postLikeRepository.findAllByPost(post).size());
		assertEquals(1, postLikeRepository.findAllByUserAccount(user).size());

		assertAll(
				() -> assertEquals(postLike.getPost(), result.getPost()),
				() -> assertEquals(postLike.getUserAccount(), result.getUserAccount())
		);
	}

	@Test
	@DisplayName("2. 좋아요 누름 테스트 (감소)")
	void postLikeMinusTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		postRepository.save(post);

		PostLike postLike = PostLike.builder()
				.userAccount(user)
				.post(post)
				.build();
		postLikeRepository.save(postLike);

		// When
		PostLike get = postLikeRepository.findByPostAndUserAccount(post, user).get();
		postLikeRepository.deleteById(get.getId());

		// Then
		assertEquals(0, postLikeRepository.findAllByUserAccount(user).size());
		assertEquals(0, postLikeRepository.findAllByPost(post).size());

		assertEquals(Optional.empty(), postLikeRepository.findByPostAndUserAccount(post, user));
	}

	@Test
	@DisplayName("3. 한 게시글의 좋아요 개수를 불러오는 테스트")
	void findListByPostTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		UserAccount user2 = UserAccount.builder().userId("testId2").build();
		UserAccount user3 = UserAccount.builder().userId("testId3").build();
		UserAccount user4 = UserAccount.builder().userId("testId4").build();
		UserAccount user5 = UserAccount.builder().userId("testId5").build();

		userAccountRepository.save(user2);
		userAccountRepository.save(user3);
		userAccountRepository.save(user4);
		userAccountRepository.save(user5);

		Post post = Post.builder()
				.userAccount(user)
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.build();
		postRepository.save(post);

		PostLike postLike1 = PostLike.builder().userAccount(user).post(post).build();
		PostLike postLike2 = PostLike.builder().userAccount(user2).post(post).build();
		PostLike postLike3 = PostLike.builder().userAccount(user3).post(post).build();
		PostLike postLike4 = PostLike.builder().userAccount(user4).post(post).build();
		PostLike postLike5 = PostLike.builder().userAccount(user5).post(post).build();
		postLikeRepository.save(postLike1);
		postLikeRepository.save(postLike2);
		postLikeRepository.save(postLike3);
		postLikeRepository.save(postLike4);
		postLikeRepository.save(postLike5);

		// When
		List<PostLike> result = postLikeRepository.findAllByPost(post);

		// Then
		assertEquals(5, result.size());
	}

	@Test
	@DisplayName("5. 한 사용자가 작성한 댓글들을 불러오는 테스트")
	void findListByUserAccountTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post1 = Post.builder().userAccount(user).title("제목1").content("내용1").category(1L).hit(0L).build();
		Post post2 = Post.builder().userAccount(user).title("제목2").content("내용2").category(1L).hit(0L).build();
		Post post3 = Post.builder().userAccount(user).title("제목3").content("내용3").category(1L).hit(0L).build();
		Post post4 = Post.builder().userAccount(user).title("제목4").content("내용4").category(1L).hit(0L).build();
		Post post5 = Post.builder().userAccount(user).title("제목5").content("내용5").category(1L).hit(0L).build();
		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);

		PostLike postLike1 = PostLike.builder().userAccount(user).post(post1).build();
		PostLike postLike2 = PostLike.builder().userAccount(user).post(post2).build();
		PostLike postLike3 = PostLike.builder().userAccount(user).post(post3).build();
		PostLike postLike4 = PostLike.builder().userAccount(user).post(post4).build();
		PostLike postLike5 = PostLike.builder().userAccount(user).post(post5).build();
		postLikeRepository.save(postLike1);
		postLikeRepository.save(postLike2);
		postLikeRepository.save(postLike3);
		postLikeRepository.save(postLike4);
		postLikeRepository.save(postLike5);

		// When
		List<PostLike> result = postLikeRepository.findAllByUserAccount(user);

		// Then
		assertEquals(5, result.size());
	}
}