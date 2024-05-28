package pulleydoreurae.careerquestbackend.community.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
import pulleydoreurae.careerquestbackend.common.service.CommonService;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.exception.CommentNotFoundException;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.community.repository.CommentRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@InjectMocks
	CommentService commentService;
	@Mock
	CommentRepository commentRepository;
	@Mock
	CommonCommunityService commonCommunityService;
	@Mock
	CommonService commonService;

	@Test
	@DisplayName("1. 댓글 저장 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void saveCommentFailTest1() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L) // 해당 번호까지는 테스트에서 올라갈 일이 없으므로 특정한 값으로 지정해서 정확한 테스트 시도
				.userAccount(user).title("제목1").build();

		given(commonService.findUserAccount(user.getUserId(), true))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		CommentRequest request = CommentRequest.builder()
				.userId(user.getUserId()).postId(post.getId()).content("댓글 내용").build();

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> commentService.saveComment(request));
		verify(commentRepository, never()).save(any()); // 해당 메서드가 한번도 호출되지 않았는지 확인
	}

	@Test
	@DisplayName("2. 댓글 저장 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void saveCommentFailTest2() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId()))
				.willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		CommentRequest request = CommentRequest.builder()
				.userId(user.getUserId()).postId(post.getId()).content("댓글 내용").build();

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> commentService.saveComment(request));
		verify(commentRepository, never()).save(any()); // 해당 메서드가 한번도 호출되지 않았는지 확인
	}

	@Test
	@DisplayName("3. 댓글 저장 테스트 (성공)")
	void saveCommentSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId())).willReturn(post);

		CommentRequest request = CommentRequest.builder()
				.userId(user.getUserId()).postId(post.getId()).content("댓글 내용").build();

		// When

		// Then
		assertDoesNotThrow(() -> commentService.saveComment(request));
		verify(commentRepository).save(any()); // 해당 메서드가 호출되었는지 확인
	}

	@Test
	@DisplayName("4. 댓글 수정 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void updateCommentFailTest1() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		CommentRequest request = CommentRequest.builder()
				.userId(user.getUserId()).postId(post.getId()).content("댓글 내용").build();

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> commentService.updateComment(request, post.getId()));
		verify(commentRepository, never()).save(any());
	}

	@Test
	@DisplayName("5. 댓글 수정 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void updateCommentFailTest2() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId()))
				.willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		CommentRequest request = CommentRequest.builder()
				.userId(user.getUserId()).postId(post.getId()).content("댓글 내용").build();

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> commentService.updateComment(request, post.getId()));
		verify(commentRepository, never()).save(any());
	}

	@Test
	@DisplayName("6. 댓글 수정 테스트 (실패 - 댓글정보를 찾을 수 없음)")
	void updateCommentFailTest3() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId())).willReturn(post);
		given(commonCommunityService.findComment(comment.getId()))
				.willThrow(new CommentNotFoundException("댓글정보를 찾을 수 없습니다."));

		CommentRequest request = CommentRequest.builder()
				.userId(user.getUserId()).postId(post.getId()).content("댓글 내용").build();

		// When

		// Then
		assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(request, post.getId()));
		verify(commentRepository, never()).save(any());
	}

	@Test
	@DisplayName("7. 댓글 수정 테스트 (실패 - 게시글과 댓글이 연결되지 않음)")
	void updateCommentFailTest4() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(100L))
				.willReturn(Post.builder().id(100L).userAccount(user).title("제목1").build());
		given(commonCommunityService.findComment(comment.getId())).willReturn(comment);

		CommentRequest request = CommentRequest.builder().userId(user.getUserId()).postId(100L) // 잘못된 게시글 정보로 요청
				.content("댓글 내용").build();

		// When
		boolean result = commentService.updateComment(request, post.getId());

		// Then
		assertFalse(result);
		verify(commentRepository, never()).save(any());
	}

	@Test
	@DisplayName("8. 댓글 수정 테스트 (실패 - 작성자와 수정자가 다름)")
	void updateCommentFailTest5() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount("test", true))
				.willReturn(UserAccount.builder().userId("test").build());
		given(commonCommunityService.findPost(post.getId())).willReturn(post);
		given(commonCommunityService.findComment(comment.getId())).willReturn(comment);

		CommentRequest request = CommentRequest.builder()
				.userId("test").postId(post.getId()).content("댓글 내용").build();

		// When
		boolean result = commentService.updateComment(request, post.getId());

		// Then
		assertFalse(result);
		verify(commentRepository, never()).save(any());
	}

	@Test
	@DisplayName("9. 게시글 수정 테스트 (성공)")
	void updateCommentSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId())).willReturn(post);
		given(commonCommunityService.findComment(comment.getId())).willReturn(comment);

		CommentRequest request = CommentRequest.builder()
				.userId(user.getUserId()).postId(post.getId()).content("댓글 내용").build();

		// When
		boolean result = commentService.updateComment(request, post.getId());

		// Then
		assertTrue(result);
		verify(commentRepository).save(any());
	}

	@Test
	@DisplayName("10. 댓글 삭제 테스트 (실패 - 회원정보를 찾을 수 없음)")
	void deleteCommentFailTest1() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true))
				.willThrow(new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(UsernameNotFoundException.class,
				() -> commentService.deleteComment(comment.getId(), post.getId(), user.getUserId()));
		verify(commentRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("11. 댓글 삭제 테스트 (실패 - 게시글 정보를 찾을 수 없음)")
	void deleteCommentFailTest2() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId()))
				.willThrow(new PostNotFoundException("게시글 정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(PostNotFoundException.class,
				() -> commentService.deleteComment(comment.getId(), post.getId(), user.getUserId()));
		verify(commentRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("12. 댓글 삭제 테스트 (실패 - 댓글정보를 찾을 수 없음)")
	void deleteCommentFailTest3() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId())).willReturn(post);
		given(commonCommunityService.findComment(comment.getId()))
				.willThrow(new CommentNotFoundException("댓글정보를 찾을 수 없습니다."));

		// When

		// Then
		assertThrows(CommentNotFoundException.class,
				() -> commentService.deleteComment(comment.getId(), post.getId(), user.getUserId()));
		verify(commentRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("13. 댓글 삭제 테스트 (실패 - 게시글과 댓글이 연결되지 않음)")
	void deleteCommentFailTest4() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(100L))
				.willReturn(Post.builder().id(100L).userAccount(user).title("제목1").build());
		given(commonCommunityService.findComment(comment.getId())).willReturn(comment);

		// When
		boolean result = commentService.deleteComment(comment.getId(), 100L, user.getUserId());

		// Then
		assertFalse(result);
		verify(commentRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("14. 댓글 삭제 테스트 (실패 - 작성자와 수정자가 다름)")
	void deleteCommentFailTest5() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount("test", true))
				.willReturn(UserAccount.builder().userId("test").build());
		given(commonCommunityService.findPost(post.getId())).willReturn(post);
		given(commonCommunityService.findComment(comment.getId())).willReturn(comment);

		// When
		boolean result = commentService.deleteComment(comment.getId(), post.getId(), "test");

		// Then
		assertFalse(result);
		verify(commentRepository, never()).deleteById(any());
	}

	@Test
	@DisplayName("15. 댓글 삭제 테스트 (성공)")
	void deleteCommentSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment = Comment.builder().id(10000L).userAccount(user).post(post).content("댓글 내용").build();

		given(commonService.findUserAccount(user.getUserId(), true)).willReturn(user);
		given(commonCommunityService.findPost(post.getId())).willReturn(post);
		given(commonCommunityService.findComment(comment.getId())).willReturn(comment);

		// When
		boolean result = commentService.deleteComment(comment.getId(), post.getId(), user.getUserId());

		// Then
		assertTrue(result);
		verify(commentRepository).deleteById(any());
	}

	@Test
	@DisplayName("16. 한 게시글에 작성된 댓글리스트 테스트")
	void findListByPostIdTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment1 = Comment.builder().userAccount(user).post(post).content("댓글 내용1").build();
		Comment comment2 = Comment.builder().userAccount(user).post(post).content("댓글 내용2").build();
		Comment comment3 = Comment.builder().userAccount(user).post(post).content("댓글 내용3").build();
		Comment comment4 = Comment.builder().userAccount(user).post(post).content("댓글 내용4").build();
		Comment comment5 = Comment.builder().userAccount(user).post(post).content("댓글 내용5").build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		// 3개씩 자른다면 마지막 3개가 반환되어야 함
		Page<Comment> pageList = new PageImpl<>(
				List.of(comment3, comment4, comment5), pageable, 3);

		given(commonCommunityService.findPost(post.getId())).willReturn(post);
		given(commentRepository.findAllByPostOrderByIdDesc(post, pageable)).willReturn(pageList);

		// When
		Page<Comment> list = commentRepository.findAllByPostOrderByIdDesc(
				post, pageable);
		List<CommentResponse> result = commentService.findListByPostId(post.getId(), pageable);

		// Then
		assertEquals(3, result.size());
		assertThat(result).contains(
				commentToCommentResponse(list.getContent().get(0)),
				commentToCommentResponse(list.getContent().get(1)),
				commentToCommentResponse(list.getContent().get(2))
		);
	}

	@Test
	@DisplayName("17. 한 사용자가 작성한 댓글리스트 테스트")
	void findListByUserAccountTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = Post.builder().id(10000L).userAccount(user).title("제목1").build();
		Comment comment1 = Comment.builder().userAccount(user).post(post).content("댓글 내용1").build();
		Comment comment2 = Comment.builder().userAccount(user).post(post).content("댓글 내용2").build();
		Comment comment3 = Comment.builder().userAccount(user).post(post).content("댓글 내용3").build();
		Comment comment4 = Comment.builder().userAccount(user).post(post).content("댓글 내용4").build();
		Comment comment5 = Comment.builder().userAccount(user).post(post).content("댓글 내용5").build();

		Pageable pageable = PageRequest.of(0, 3); // 한 페이지에 3개씩 자르기
		// 3개씩 자른다면 마지막 3개가 반환되어야 함
		Page<Comment> pageList = new PageImpl<>(
				List.of(comment3, comment4, comment5), pageable, 3);

		given(commonService.findUserAccount(user.getUserId(), false)).willReturn(user);
		given(commentRepository.findAllByUserAccountOrderByIdDesc(user, pageable)).willReturn(pageList);

		// When
		Page<Comment> list = commentRepository.findAllByUserAccountOrderByIdDesc(
				user, pageable);
		List<CommentResponse> result = commentService.findListByUserAccount(user.getUserId(), pageable);

		// Then
		assertEquals(3, result.size());
		assertThat(result).contains(
				commentToCommentResponse(list.getContent().get(0)),
				commentToCommentResponse(list.getContent().get(1)),
				commentToCommentResponse(list.getContent().get(2))
		);
	}

	CommentResponse commentToCommentResponse(Comment comment) {
		return CommentResponse.builder()
				.userId(comment.getUserAccount().getUserId())
				.postId(comment.getPost().getId())
				.content(comment.getContent())
				.createdAt(comment.getCreatedAt())
				.modifiedAt(comment.getModifiedAt())
				.build();
	}
}
