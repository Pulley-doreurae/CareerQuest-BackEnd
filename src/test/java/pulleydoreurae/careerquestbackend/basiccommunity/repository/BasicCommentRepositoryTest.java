package pulleydoreurae.careerquestbackend.basiccommunity.repository;

import static org.assertj.core.api.Assertions.*;
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
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicComment;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPost;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@DataJpaTest
@DisplayName("댓글 Repository 테스트")
class BasicCommentRepositoryTest {

	@Autowired
	CommentRepository commentRepository;
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
		commentRepository.deleteAll();
		userAccountRepository.deleteAll();
		postRepository.deleteAll();
	}

	@Test
	@DisplayName("1. 댓글 저장 테스트")
	void saveCommentTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L).build();
		postRepository.save(post);

		// When
		Comment comment = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		commentRepository.save(comment);

		// Then
		Comment result = commentRepository.findById(comment.getId()).get();

		assertEquals(1, commentRepository.findAll().size());
		assertAll(
				() -> assertEquals(comment.getId(), result.getId()),
				() -> assertEquals(comment.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(comment.getPost(), result.getPost()),
				() -> assertEquals(comment.getContent(), result.getContent()),
				() -> assertEquals(comment.getCreatedAt(), result.getCreatedAt()),
				() -> assertEquals(comment.getModifiedAt(), result.getModifiedAt())
		);
	}

	@Test
	@DisplayName("2. 댓글 수정 테스트")
	void updateCommentTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L).build();
		postRepository.save(post);

		Comment comment = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		commentRepository.save(comment);

		// When
		Comment updateComment = BasicComment.builder()
				.id(comment.getId())
				.userAccount(user)
				.post(post)
				.content("댓글 내용")
				.build();
		commentRepository.save(comment);

		// Then
		Comment result = commentRepository.findById(updateComment.getId()).get();

		assertEquals(1, commentRepository.findAll().size());
		assertAll(
				() -> assertEquals(updateComment.getId(), result.getId()),
				() -> assertEquals(updateComment.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(updateComment.getPost(), result.getPost()),
				() -> assertEquals(updateComment.getContent(), result.getContent())
		);
	}

	@Test
	@DisplayName("3. 댓글 삭제 테스트")
	void deleteCommentTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L).build();
		postRepository.save(post);

		Comment comment = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		commentRepository.save(comment);

		// When
		commentRepository.deleteById(comment.getId());

		// Then
		assertEquals(0, commentRepository.findAll().size());
		assertEquals(Optional.empty(), commentRepository.findById(comment.getId()));
	}

	@Test
	@DisplayName("4. 한 게시글의 댓글들을 불러오는 테스트")
	void findListByPostTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L).build();
		postRepository.save(post);

		Comment comment1 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용1").build();
		Comment comment2 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용2").build();
		Comment comment3 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용3").build();
		Comment comment4 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용4").build();
		Comment comment5 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용5").build();

		commentRepository.save(comment1);
		commentRepository.save(comment2);
		commentRepository.save(comment3);
		commentRepository.save(comment4);
		commentRepository.save(comment5);

		// When
		Pageable pageable = PageRequest.of(0, 3);
		Page<Comment> result = commentRepository.findAllByPostOrderByIdDesc(post, pageable);

		// Then
		assertEquals(2, result.getTotalPages());
		assertEquals(5, result.getTotalElements());
		assertEquals(3, result.getSize());
		assertThat(result).contains(comment3, comment4, comment5);
	}

	@Test
	@DisplayName("5. 한 사용자가 작성한 댓글들을 불러오는 테스트")
	void findListByUserAccountTest() {
		// Given
		UserAccount user = userAccountRepository.findByUserId("testId").get();

		Post post = BasicPost.builder().userAccount(user).title("제목1").content("내용1").category(1L).view(0L).build();
		postRepository.save(post);

		Comment comment1 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용1").build();
		Comment comment2 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용2").build();
		Comment comment3 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용3").build();
		Comment comment4 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용4").build();
		Comment comment5 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용5").build();

		commentRepository.save(comment1);
		commentRepository.save(comment2);
		commentRepository.save(comment3);
		commentRepository.save(comment4);
		commentRepository.save(comment5);

		// When
		Pageable pageable = PageRequest.of(0, 3);
		Page<Comment> result = commentRepository.findAllByUserAccountOrderByIdDesc(user, pageable);

		// Then
		assertEquals(3, result.getSize());
		assertEquals(2, result.getTotalPages());
		assertEquals(5, result.getTotalElements());
		assertThat(result).contains(comment3, comment4, comment5);
	}
}
