package pulleydoreurae.careerquestbackend.certification.service;

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
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReview;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReviewLike;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReviewViewCheck;
import pulleydoreurae.careerquestbackend.common.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostViewCheck;
import pulleydoreurae.careerquestbackend.common.community.exception.PostLikeNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.exception.PostNotFoundException;
import pulleydoreurae.careerquestbackend.common.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostViewCheckRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("자격증에서 공통으로 사용되는 메서드 테스트")
class CommonCertificationServiceTest {

	@Value("${IMAGES_PATH}")
	String IMAGES_PATH;

	@InjectMocks
	CommonCertificationService commonCommunityService;
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
		Post post = CertificationReview.builder()
				.userAccount(user)
				.id(100L)
				.title("제목1")
				.content("내용1")
				.postCategory(PostCategory.CERTIFICATION_REVIEW)
				.view(0L)
				.build();
		given(postRepository.findById(100L)).willReturn(Optional.of(post));

		PostLike postLike1 = CertificationReviewLike.builder().userAccount(user).post(post).build();
		PostLike postLike2 = CertificationReviewLike.builder().userAccount(user).post(post).build();
		PostLike postLike3 = CertificationReviewLike.builder().userAccount(user).post(post).build();
		PostLike postLike4 = CertificationReviewLike.builder().userAccount(user).post(post).build();
		PostLike postLike5 = CertificationReviewLike.builder().userAccount(user).post(post).build();
		List<PostLike> postLikes = List.of(postLike1, postLike2, postLike3, postLike4, postLike5);
		given(postLikeRepository.findAllByPost(post)).willReturn(postLikes);

		// When
		PostResponse expect = PostResponse.builder()
				.userId(post.getUserAccount().getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.view(post.getView())
				.postLikeCount(commonCommunityService.countPostLike(post.getId()))
				.postCategory(post.getPostCategory())
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
				() -> assertEquals(expect.getView(), result.getView()),
				() -> assertEquals(expect.getPostLikeCount(), result.getPostLikeCount()),
				() -> assertEquals(expect.getPostCategory(), result.getPostCategory()),
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
		Post post = CertificationReview.builder()
				.userAccount(new UserAccount()).id(100L).title("제목1").content("내용1").postCategory(PostCategory.CERTIFICATION_REVIEW).view(0L).build();
		given(postRepository.findById(100L)).willReturn(Optional.of(post));

		// When
		Post result = commonCommunityService.findPost(100L);

		// Then
		assertAll(
				() -> assertEquals(post.getId(), result.getId()),
				() -> assertEquals(post.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(post.getTitle(), result.getTitle()),
				() -> assertEquals(post.getContent(), result.getContent()),
				() -> assertEquals(post.getView(), result.getView()),
				() -> assertEquals(post.getPostCategory(), result.getPostCategory()),
				() -> assertEquals(post.getPostLikes(), result.getPostLikes()),
				() -> assertEquals(post.getCreatedAt(), result.getCreatedAt()),
				() -> assertEquals(post.getModifiedAt(), result.getModifiedAt())
		);
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
		PostViewCheck postViewCheck = new CertificationReviewViewCheck("testId", 100L);
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
				.userId("testId").title("제목").content("내용").postCategory(PostCategory.CERTIFICATION_REVIEW).images(List.of("image1.png", "image2.png"))
				.build();

		// When
		Post result = commonCommunityService.postRequestToPost(postRequest, userAccount);

		// Then
		assertAll(
				() -> assertEquals(postRequest.getUserId(), result.getUserAccount().getUserId()),
				() -> assertEquals(postRequest.getTitle(), result.getTitle()),
				() -> assertEquals(postRequest.getContent(), result.getContent()),
				() -> assertEquals(postRequest.getPostCategory(), result.getPostCategory())
		);
	}

	@Test
	@DisplayName("게시글 수정 Request -> 게시글 Entity")
	void postRequestToPostForUpdateTest() {
		// Given
		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		PostRequest postRequest = PostRequest.builder()
				.userId("testId").title("제목").content("내용").postCategory(PostCategory.CERTIFICATION_REVIEW).images(List.of("image1.png", "image2.png"))
				.build();
		Post post = CertificationReview.builder()
				.userAccount(userAccount)
				.title("제목")
				.content("내용")
				.postCategory(PostCategory.CERTIFICATION_REVIEW)
				.build();

		// When
		Post result = commonCommunityService.postRequestToPostForUpdate(post, postRequest, userAccount);

		// Then
		assertAll(
				() -> assertEquals(postRequest.getTitle(), result.getTitle()),
				() -> assertEquals(postRequest.getContent(), result.getContent()),
				() -> assertEquals(postRequest.getPostCategory(), result.getPostCategory()),
				() -> assertEquals(post.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(post.getView(), result.getView()),
				() -> assertEquals(post.getUserAccount(), result.getUserAccount()),
				() -> assertEquals(post.getPostLikes(), result.getPostLikes())
		);
	}

	@Test
	@DisplayName("게시글 좋아요 수 테스트")
	void countPostLikeTest() {
		// Given
		Post post = CertificationReview.builder()
				.userAccount(new UserAccount()).id(100L).title("제목1").content("내용1").postCategory(PostCategory.CERTIFICATION_REVIEW).view(0L).build();
		PostLike postLike1 = CertificationReviewLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike2 = CertificationReviewLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike3 = CertificationReviewLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike4 = CertificationReviewLike.builder().post(post).userAccount(new UserAccount()).build();
		PostLike postLike5 = CertificationReviewLike.builder().post(post).userAccount(new UserAccount()).build();
		given(postRepository.findById(100L)).willReturn(Optional.of(post));
		given(postLikeRepository.findAllByPost(any()))
				.willReturn(List.of(postLike1, postLike2, postLike3, postLike4, postLike5));

		// When
		Long result = commonCommunityService.countPostLike(100L);

		// Then
		assertEquals(5, result);
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
		Post post = CertificationReview.builder().title("제목").content("내용").userAccount(user).build();
		PostLike postLike = CertificationReviewLike.builder().userAccount(user).post(post).build();
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
