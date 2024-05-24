package pulleydoreurae.careerquestbackend.basiccommunity.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPost;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostLike;
import pulleydoreurae.careerquestbackend.common.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/04/02
 */
@DataJpaTest
@DisplayName("좋아요 Repository 테스트")
class BasicPostLikeRepositoryTest {

	@Autowired
	PostLikeRepository postLikeRepository;
	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	PostRepository postRepository;

	@BeforeEach
	void beforeEach() {
		UserAccount user = UserAccount.builder().userId("testId").userName("testName").email("test@email.com")
				.phoneNum("010-1111-2222").password("testPassword").role(UserRole.ROLE_TEMPORARY_USER).build();
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

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		postRepository.save(post);

		// When
		PostLike postLike = BasicPostLike.builder().userAccount(user).post(post).build();
		postLikeRepository.save(postLike);

		PostLike result = postLikeRepository.findByPostAndUserAccount(post, user).get();

		// Then
		Pageable pageable = PageRequest.of(0, 3);
		assertEquals(1, postLikeRepository.findAllByPostOrderByIdDesc(post, pageable).getTotalElements());
		assertEquals(1, postLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable).getTotalElements());

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

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		postRepository.save(post);

		PostLike postLike = BasicPostLike.builder().userAccount(user).post(post).build();
		postLikeRepository.save(postLike);

		// When
		PostLike get = postLikeRepository.findByPostAndUserAccount(post, user).get();
		postLikeRepository.deleteById(get.getId());

		// Then
		Pageable pageable = PageRequest.of(0, 3);
		assertEquals(0, postLikeRepository.findAllByPostOrderByIdDesc(post, pageable).getTotalElements());
		assertEquals(0, postLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable).getTotalElements());

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

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		postRepository.save(post);

		PostLike postLike1 = BasicPostLike.builder().userAccount(user).post(post).build();
		PostLike postLike2 = BasicPostLike.builder().userAccount(user2).post(post).build();
		PostLike postLike3 = BasicPostLike.builder().userAccount(user3).post(post).build();
		PostLike postLike4 = BasicPostLike.builder().userAccount(user4).post(post).build();
		PostLike postLike5 = BasicPostLike.builder().userAccount(user5).post(post).build();

		postLikeRepository.save(postLike1);
		postLikeRepository.save(postLike2);
		postLikeRepository.save(postLike3);
		postLikeRepository.save(postLike4);
		postLikeRepository.save(postLike5);

		// When
		Pageable pageable = PageRequest.of(0, 3);
		Page<PostLike> result = postLikeRepository.findAllByPostOrderByIdDesc(post, pageable);

		// Then
		assertEquals(3, result.getSize());
		assertEquals(5, result.getTotalElements());
		assertEquals(2, result.getTotalPages());
	}

	@Test
	@DisplayName("5. 한 사용자가 작성한 댓글들을 불러오는 테스트")
	void findListByUserAccountTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post1 = BasicPost.builder().userAccount(user).title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		Post post2 = BasicPost.builder().userAccount(user).title("제목2").content("내용2").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		Post post3 = BasicPost.builder().userAccount(user).title("제목3").content("내용3").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		Post post4 = BasicPost.builder().userAccount(user).title("제목4").content("내용4").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		Post post5 = BasicPost.builder().userAccount(user).title("제목5").content("내용5").postCategory(PostCategory.FREE_BOARD).view(0L).build();

		postRepository.save(post1);
		postRepository.save(post2);
		postRepository.save(post3);
		postRepository.save(post4);
		postRepository.save(post5);

		PostLike postLike1 = BasicPostLike.builder().userAccount(user).post(post1).build();
		PostLike postLike2 = BasicPostLike.builder().userAccount(user).post(post2).build();
		PostLike postLike3 = BasicPostLike.builder().userAccount(user).post(post3).build();
		PostLike postLike4 = BasicPostLike.builder().userAccount(user).post(post4).build();
		PostLike postLike5 = BasicPostLike.builder().userAccount(user).post(post5).build();

		postLikeRepository.save(postLike1);
		postLikeRepository.save(postLike2);
		postLikeRepository.save(postLike3);
		postLikeRepository.save(postLike4);
		postLikeRepository.save(postLike5);

		// When
		Pageable pageable = PageRequest.of(0, 3);
		Page<PostLike> result = postLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable);

		// Then
		assertEquals(3, result.getSize());
		assertEquals(5, result.getTotalElements());
		assertEquals(2, result.getTotalPages());
	}

	@Test
	@DisplayName("좋아요가 존재하는지 테스트")
	void existTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();
		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L).build();
		postRepository.save(post);
		PostLike postLike = BasicPostLike.builder().post(post).userAccount(user).build();
		postLikeRepository.save(postLike);

		// When
		boolean result1 = postLikeRepository.existsByPostAndUserAccount(null, null);
		boolean result2 = postLikeRepository.existsByPostAndUserAccount(post, null);
		boolean result3 = postLikeRepository.existsByPostAndUserAccount(null, user);
		boolean result4 = postLikeRepository.existsByPostAndUserAccount(post, user);

		// Then
		assertAll(
				() -> assertFalse(result1),
				() -> assertFalse(result2),
				() -> assertFalse(result3),
				() -> assertTrue(result4)
		);
	}
}
