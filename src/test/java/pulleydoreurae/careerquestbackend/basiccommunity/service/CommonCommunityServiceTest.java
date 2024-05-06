package pulleydoreurae.careerquestbackend.basiccommunity.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicComment;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPost;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostImage;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostLike;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostViewCheck;
import pulleydoreurae.careerquestbackend.basiccommunity.exception.CommentNotFoundException;
import pulleydoreurae.careerquestbackend.basiccommunity.exception.PostLikeNotFoundException;
import pulleydoreurae.careerquestbackend.basiccommunity.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostViewCheckRepository;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostImage;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostViewCheck;

/**
 * @author : parkjihyeok
 * @since : 2024/04/11
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("커뮤니티에서 공통으로 사용되는 메서드 테스트")
class CommonCommunityServiceTest {

	@Value("${IMAGES_PATH}")
	String IMAGES_PATH;

	@InjectMocks
	CommonCommunityService commonCommunityService;
	@Mock
	UserAccountRepository userAccountRepository;
	@Mock
	PostRepository postRepository;
	@Mock
	CommentRepository commentRepository;
	@Mock
	PostLikeRepository postLikeRepository;
	@Mock
	PostImageRepository postImageRepository;
	@Mock
	PostViewCheckRepository postViewCheckRepository;

	@Test
	@DisplayName("게시글 Entity -> 게시글 Response 변환 메서드 테스트")
	void postToPostResponseTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = BasicPost.builder().userAccount(user).id(100L).title("제목1").content("내용1").category(1L).view(0L).build();
		given(postRepository.findById(100L)).willReturn(Optional.of(post));

		PostImage postImage1 = BasicPostImage.builder().post(new BasicPost()).fileName("image1.png").build();
		PostImage postImage2 = BasicPostImage.builder().post(new BasicPost()).fileName("image2.png").build();
		PostImage postImage3 = BasicPostImage.builder().post(new BasicPost()).fileName("image3.png").build();
		PostImage postImage4 = BasicPostImage.builder().post(new BasicPost()).fileName("image4.png").build();
		PostImage postImage5 = BasicPostImage.builder().post(new BasicPost()).fileName("image5.png").build();
		List<PostImage> postImages = List.of(postImage1, postImage2, postImage3, postImage4, postImage5);
		given(postImageRepository.findAllByPost(post)).willReturn(postImages);

		Comment comment1 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		Comment comment2 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		Comment comment3 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		Comment comment4 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		Comment comment5 = BasicComment.builder().userAccount(user).post(post).content("댓글 내용").build();
		List<Comment> comments = List.of(comment1, comment2, comment3, comment4, comment5);
		given(commentRepository.findAllByPost(post)).willReturn(comments);

		PostLike postLike1 = BasicPostLike.builder().userAccount(user).post(post).build();
		PostLike postLike2 = BasicPostLike.builder().userAccount(user).post(post).build();
		PostLike postLike3 = BasicPostLike.builder().userAccount(user).post(post).build();
		PostLike postLike4 = BasicPostLike.builder().userAccount(user).post(post).build();
		PostLike postLike5 = BasicPostLike.builder().userAccount(user).post(post).build();
		List<PostLike> postLikes = List.of(postLike1, postLike2, postLike3, postLike4, postLike5);
		given(postLikeRepository.findAllByPost(post)).willReturn(postLikes);

		// When
		PostResponse expect = PostResponse.builder()
				.userId(post.getUserAccount().getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.images(commonCommunityService.postImageToStringList(post))
				.view(post.getView())
				.commentCount(commonCommunityService.countComment(post.getId()))
				.postLikeCount(commonCommunityService.countPostLike(post.getId()))
				.category(post.getCategory())
				.isLiked(0)
				.createdAt(post.getCreatedAt())
				.modifiedAt(post.getModifiedAt())
				.build();
		PostResponse result = commonCommunityService.postToPostResponse(post, 0);

		// Then
		assertAll(
				() -> assertEquals(expect.getUserId(), result.getUserId()),
				() -> assertEquals(expect.getTitle(), result.getTitle()),
				() -> assertEquals(expect.getContent(), result.getContent()),
				() -> assertEquals(expect.getImages(), result.getImages()),
				() -> assertEquals(expect.getView(), result.getView()),
				() -> assertEquals(expect.getCommentCount(), result.getCommentCount()),
				() -> assertEquals(expect.getPostLikeCount(), result.getPostLikeCount()),
				() -> assertEquals(expect.getCategory(), result.getCategory()),
				() -> assertEquals(expect.getIsLiked(), result.getIsLiked()),
				() -> assertEquals(expect.getCreatedAt(), result.getCreatedAt()),
				() -> assertEquals(expect.getModifiedAt(), result.getModifiedAt())
		);
	}

	@Test
	@DisplayName("UUID 값이 정상적으로 생성되는지 테스트")
	public void testGetUUIDWithNoExistingCookie() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// getUUID 메서드 실행
		String uuid = commonCommunityService.getUUID(request, response);

		// UUID 값 검증
		assertThat(uuid).isNotNull();

		// 새로 생성된 쿠키 검증
		assertThat(response.getCookies()).isNotNull();
		assertThat(response.getCookies()[0].getName()).isEqualTo("UUID");
		assertThat(response.getCookies()[0].getValue()).isEqualTo(uuid);
	}

	@Test
	@DisplayName("게시글 찾기 실패")
	void findPostFailTest() {
		// Given
		given(postRepository.findById(100L)).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(PostNotFoundException.class, () -> commonCommunityService.findPost(100L));
	}

	@Test
	@DisplayName("게시글 찾기 성공")
	void findPostSuccessTest() {
		// Given
		Post post = BasicPost.builder()
				.userAccount(new UserAccount()).id(100L).title("제목1").content("내용1").category(1L).view(0L).build();
		given(postRepository.findById(100L)).willReturn(Optional.of(post));

		// When
		Post result = commonCommunityService.findPost(100L);

		// Then
		assertAll(
				() -> assertEquals(post.getId(), result.getId()),
				() -> assertEquals(post.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(post.getTitle(), result.getTitle()),
				() -> assertEquals(post.getContent(), result.getContent()),
				() -> assertEquals(post.getComments(), result.getComments()),
				() -> assertEquals(post.getView(), result.getView()),
				() -> assertEquals(post.getCategory(), result.getCategory()),
				() -> assertEquals(post.getPostLikes(), result.getPostLikes()),
				() -> assertEquals(post.getCreatedAt(), result.getCreatedAt()),
				() -> assertEquals(post.getModifiedAt(), result.getModifiedAt())
		);
	}

	@Test
	@DisplayName("댓글 id 로 댓글을 찾기 실패")
	void findCommentFailTest() {
		// Given
		given(commentRepository.findById(100L)).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(CommentNotFoundException.class, () -> commonCommunityService.findComment(100L));
	}

	@Test
	@DisplayName("댓글 id 로 댓글을 찾기 성공")
	void findCommentSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = BasicPost.builder().userAccount(user).id(100L).title("제목1").content("내용1").category(1L).view(0L).build();
		Comment comment = BasicComment.builder().userAccount(user).post(post).content("내용").build();
		given(commentRepository.findById(100L)).willReturn(Optional.of(comment));

		// When
		Comment result = commonCommunityService.findComment(100L);

		// Then
		assertEquals(comment, result);
	}

	@Test
	@DisplayName("사용자 찾기 실패")
	void findUserAccountFailTest() {
		// Given
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> commonCommunityService.findUserAccount("testId"));
	}

	@Test
	@DisplayName("사용자 찾기 성공")
	void findUserAccountSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.of(user));

		// When
		UserAccount result = commonCommunityService.findUserAccount("testId");

		// Then
		assertEquals(user, result);
	}

	@Test
	@DisplayName("조회수 중복이 아닌 경우")
	void findPostViewCheckNullTest() {
		// Given
		given(postViewCheckRepository.findById(anyString())).willReturn(Optional.empty());

		// When
		PostViewCheck result = commonCommunityService.findPostViewCheck("testId");

		// Then
		assertNull(result);
	}

	@Test
	@DisplayName("조회수 중복인 경우")
	void findPostViewCheckNotNullTest() {
		// Given
		PostViewCheck postViewCheck = new BasicPostViewCheck("testId", 100L);
		given(postViewCheckRepository.findById("testId")).willReturn(Optional.of(postViewCheck));

		// When
		PostViewCheck result = commonCommunityService.findPostViewCheck("testId");

		// Then
		assertAll(
				() -> assertEquals(postViewCheck.getPostId(), result.getPostId()),
				() -> assertEquals(postViewCheck.getUserId(), result.getUserId())

		);
	}

	@Test
	@DisplayName("게시글 Request -> 게시글 Entity 변환 테스트")
	void postRequestToPostTest() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		PostRequest postRequest = PostRequest.builder()
				.userId("testId").title("제목").content("내용").category(1L).images(List.of("image1.png", "image2.png"))
				.build();

		// When
		Post result = commonCommunityService.postRequestToPost(postRequest, userAccount);

		// Then
		assertAll(
				() -> assertEquals(postRequest.getUserId(), result.getUserAccount().getUserId()),
				() -> assertEquals(postRequest.getTitle(), result.getTitle()),
				() -> assertEquals(postRequest.getContent(), result.getContent()),
				() -> assertEquals(postRequest.getCategory(), result.getCategory())
		);
	}

	@Test
	@DisplayName("게시글 수정 Request -> 게시글 Entity")
	void postRequestToPostForUpdateTest() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		PostRequest postRequest = PostRequest.builder()
				.userId("testId").title("제목").content("내용").category(1L).images(List.of("image1.png", "image2.png"))
				.build();
		Post post = BasicPost.builder().userAccount(userAccount).title("제목").content("내용").category(1L).build();

		// When
		Post result = commonCommunityService.postRequestToPostForUpdate(post, postRequest, userAccount);

		// Then
		assertAll(
				() -> assertEquals(postRequest.getTitle(), result.getTitle()),
				() -> assertEquals(postRequest.getContent(), result.getContent()),
				() -> assertEquals(postRequest.getCategory(), result.getCategory()),
				() -> assertEquals(post.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(post.getView(), result.getView()),
				() -> assertEquals(post.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(post.getComments(), result.getComments()),
				() -> assertEquals(post.getPostLikes(), result.getPostLikes())
		);
	}

	@Test
	@DisplayName("게시글 댓글 수 테스트")
	void countCommentTest() {
		// Given
		Post post = BasicPost.builder()
				.userAccount(new UserAccount()).id(100L).title("제목1").content("내용1").category(1L).view(0L).build();
		Comment comment1 = BasicComment.builder().content("내용").post(post).userAccount(new UserAccount()).build();
		Comment comment2 = BasicComment.builder().content("내용").post(post).userAccount(new UserAccount()).build();
		Comment comment3 = BasicComment.builder().content("내용").post(post).userAccount(new UserAccount()).build();
		Comment comment4 = BasicComment.builder().content("내용").post(post).userAccount(new UserAccount()).build();
		Comment comment5 = BasicComment.builder().content("내용").post(post).userAccount(new UserAccount()).build();
		given(postRepository.findById(100L)).willReturn(Optional.of(post));
		given(commentRepository.findAllByPost(any()))
				.willReturn(List.of(comment1, comment2, comment3, comment4, comment5));

		// When
		Long result = commonCommunityService.countComment(100L);

		// Then
		assertEquals(5, result);
	}

	@Test
	@DisplayName("게시글 좋아요 수 테스트")
	void countPostLikeTest() {
		// Given
		Post post = BasicPost.builder()
				.userAccount(new UserAccount()).id(100L).title("제목1").content("내용1").category(1L).view(0L).build();
		PostLike postLike1 = BasicPostLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike2 = BasicPostLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike3 = BasicPostLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike4 = BasicPostLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike5 = BasicPostLike.builder().post(post).userAccount(new UserAccount()).build();
		given(postRepository.findById(100L)).willReturn(Optional.of(post));
		given(postLikeRepository.findAllByPost(any()))
				.willReturn(List.of(postLike1, postLike2, postLike3, postLike4, postLike5));

		// When
		Long result = commonCommunityService.countPostLike(100L);

		// Then
		assertEquals(5, result);
	}

	@Test
	@DisplayName("저장한 사진 정보 가져오기")
	void postImageToStringListTest() {
		// Given
		PostImage postImage1 = BasicPostImage.builder().post(new BasicPost()).fileName("image1.png").build();
		PostImage postImage2 = BasicPostImage.builder().post(new BasicPost()).fileName("image2.png").build();
		PostImage postImage3 = BasicPostImage.builder().post(new BasicPost()).fileName("image3.png").build();
		PostImage postImage4 = BasicPostImage.builder().post(new BasicPost()).fileName("image4.png").build();
		PostImage postImage5 = BasicPostImage.builder().post(new BasicPost()).fileName("image5.png").build();
		given(postImageRepository.findAllByPost(any()))
				.willReturn(List.of(postImage1, postImage2, postImage3, postImage4, postImage5));

		// When
		List<String> result = commonCommunityService.postImageToStringList(new BasicPost());

		// Then
		assertThat(result)
				.contains(IMAGES_PATH + "image1.png")
				.contains(IMAGES_PATH + "image2.png")
				.contains(IMAGES_PATH + "image3.png")
				.contains(IMAGES_PATH + "image4.png")
				.contains(IMAGES_PATH + "image5.png");
	}

	@Test
	@DisplayName("게시글과 사용자 정보로 좋아요 정보 가져오기 실패")
	void findPostLikeFailTest() {
		// Given
		given(postLikeRepository.findByPostAndUserAccount(any(), any())).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(PostLikeNotFoundException.class, () -> commonCommunityService.findPostLike(any(), any()));
	}

	@Test
	@DisplayName("게시글과 사용자 정보로 좋아요 정보 가져오기 성공")
	void findPostLikeSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		Post post = BasicPost.builder().title("제목").content("내용").userAccount(user).build();
		PostLike postLike = BasicPostLike.builder().userAccount(user).post(post).build();
		given(postLikeRepository.findByPostAndUserAccount(any(), any())).willReturn(Optional.of(postLike));

		// When
		PostLike result = commonCommunityService.findPostLike(post, user);

		// Then
		assertAll(
				() -> assertEquals(postLike.getPost(), result.getPost()),
				() -> assertEquals(postLike.getUserAccount(), result.getUserAccount())
		);
	}
}
