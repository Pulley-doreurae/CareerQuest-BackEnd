package pulleydoreurae.careerquestbackend.community.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.common.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.service.PostService;

/**
 * @author : parkjihyeok
 * @since : 2024/03/31
 */
@WebMvcTest(BasicPostController.class)
@AutoConfigureRestDocs
class PostControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	@Qualifier("basicPostService")
	PostService postService;
	Gson gson = new Gson();

	@Test
	@DisplayName("1. 게시글 리스트 조회 테스트")
	@WithMockUser
	void getPostListTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId").title("제목2").content("내용2").postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId").title("제목3").content("내용3").postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId").title("제목4").content("내용4").postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId").title("제목5").content("내용5").postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(postService.getPostResponseList(any())).willReturn(List.of(post1, post2, post3, post4, post5));

		// When
		mockMvc.perform(
						get("/api/posts")
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("게시글 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].images").description("사진 리스트"),
								fieldWithPath("[].postCategory").description("카테고리"),
								fieldWithPath("[].certificationName").description("자격증 이름 (자격증 후기가 아닌 경우 무시)"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("2. 게시글 단건 조회 테스트")
	@WithMockUser
	void getPostTest() throws Exception {
		// Given
		PostResponse post = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();
		given(postService.findByPostId(any(), any(), any())).willReturn(post);

		// When
		mockMvc.perform(
						get("/api/posts/{postId}", "100")
								.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.view").exists())
				.andExpect(jsonPath("$.commentCount").exists())
				.andExpect(jsonPath("$.postLikeCount").exists())
				.andExpect(jsonPath("$.postCategory").exists())
				.andExpect(jsonPath("$.isLiked").exists())
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.modifiedAt").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("조회할 게시글 id")
						),
						responseFields(
								fieldWithPath("userId").description("게시글 작성자"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("images").description("사진 리스트"),
								fieldWithPath("postCategory").description("카테고리"),
								fieldWithPath("certificationName").description("자격증 이름 (자격증 후기가 아닌 경우 무시)"),
								fieldWithPath("view").description("조회수"),
								fieldWithPath("commentCount").description("댓글 수"),
								fieldWithPath("postLikeCount").description("좋아요 수"),
								fieldWithPath("isLiked").description("좋아요 상태"),
								fieldWithPath("createdAt").description("작성일자"),
								fieldWithPath("modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("3. 게시글 저장 테스트 (실패)")
	@WithMockUser
	void savePostFailTest() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD)
				.build();

		doThrow(new UsernameNotFoundException("게시글 저장에 실패했습니다.")).when(postService).savePost(any());

		// When
		mockMvc.perform(
						post("/api/posts")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("4. 게시글 저장 테스트 (성공)")
	@WithMockUser
	void savePostSuccessTest() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						post("/api/posts")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("5. 게시글 수정 테스트 (실패)")
	@WithMockUser
	void updatePostFailTest() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("수정할 제목").content("수정할 내용").postCategory(PostCategory.FREE_BOARD)
				.build();

		given(postService.updatePost(any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}", 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("수정할 게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("6. 게시글 수정 테스트 (성공)")
	@WithMockUser
	void updatePostSuccessTest() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("수정할 제목").content("수정할 내용").postCategory(PostCategory.FREE_BOARD)
				.build();

		given(postService.updatePost(any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}", 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("수정할 게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("7. 게시글 삭제 테스트 (실패)")
	@WithMockUser
	void deletePostFailTest() throws Exception {
		// Given
		given(postService.deletePost(any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						delete("/api/posts/{postId}", 100)
								.with(csrf())
								.queryParam("userId", "testId"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("삭제할 게시글 id")
						),
						queryParameters( // 쿼리파라미터
								parameterWithName("userId").description("작성자 id")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("8. 게시글 삭제 테스트 (성공)")
	@WithMockUser
	void deletePostSuccessTest() throws Exception {
		// Given
		given(postService.deletePost(any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						delete("/api/posts/{postId}", 100)
								.with(csrf())
								.queryParam("userId", "testId"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("삭제할 게시글 id")
						),
						queryParameters( // 쿼리파라미터
								parameterWithName("userId").description("작성자 id")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	/*@Test
	@DisplayName("9. 게시글 리스트 카테고리로 조회 테스트")
	@WithMockUser
	void getPostListByCategoryTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId").title("제목2").content("내용2").postCategory(PostCategory.FREE_BOARD).view(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId").title("제목3").content("내용3").postCategory(PostCategory.FREE_BOARD).view(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId").title("제목4").content("내용4").postCategory(PostCategory.FREE_BOARD).view(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId").title("제목5").content("내용5").postCategory(PostCategory.FREE_BOARD).view(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(postService.getPostResponseListByCategory(any(), any()))
				.willReturn(List.of(post1, post2, post3, post4, post5));

		// When
		mockMvc.perform(
						get("/api/posts/category/{category}", 1L)
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("게시글 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].images").description("사진 리스트"),
								fieldWithPath("[].postCategory").description("카테고리"),
								fieldWithPath("[].certificationName").description("자격증 이름 (자격증 후기가 아닌 경우 무시)"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}*/

	@Test
	@DisplayName("10. 한 사용자가 작성한 게시글 리스트 조회 테스트")
	@WithMockUser
	void getPostListByUserAccountTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId").title("제목2").content("내용2").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId").title("제목3").content("내용3").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId").title("제목4").content("내용4").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId").title("제목5").content("내용5").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(postService.getPostListByUserAccount(any(), any()))
				.willReturn(List.of(post1, post2, post3, post4, post5));

		// When
		mockMvc.perform(
						get("/api/posts/user/{userId}", "testId")
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("게시글 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].images").description("사진 리스트"),
								fieldWithPath("[].postCategory").description("카테고리"),
								fieldWithPath("[].certificationName").description("자격증 이름 (자격증 후기가 아닌 경우 무시)"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("검색어로 검색한 게시글 리스트 조회 테스트")
	@WithMockUser
	void searchPostsTest1() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder().userId("testId").title("제목1").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post2 = PostResponse.builder().userId("testId").title("제목2").content("내용2").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post3 = PostResponse.builder().userId("testId").title("제목3").content("내용3").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post4 = PostResponse.builder().userId("testId").title("검색어").content("내용4").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post5 = PostResponse.builder().userId("testId").title("제목5").content("검색어").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		given(postService.searchPosts(any(), any(), any()))
				.willReturn(List.of(post4, post5));

		// When
		mockMvc.perform(
						get("/api/posts/search")
								.queryParam("keyword", "검색어")
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("keyword").description("검색어"),
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("게시글 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].images").description("사진 리스트"),
								fieldWithPath("[].postCategory").description("카테고리"),
								fieldWithPath("[].certificationName").description("자격증 이름 (자격증 후기가 아닌 경우 무시)"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	/*@Test
	@DisplayName("검색어와 카테고리로 검색한 게시글 리스트 조회 테스트")
	@WithMockUser
	void searchPostsTest2() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder().userId("testId").title("검색어").content("내용1").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post2 = PostResponse.builder().userId("testId").title("검색어").content("내용2").postCategory(2L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post3 = PostResponse.builder().userId("testId").title("제목3").content("검색어").postCategory(PostCategory.FREE_BOARD).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post4 = PostResponse.builder().userId("testId").title("검색어").content("내용4").postCategory(2L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		PostResponse post5 = PostResponse.builder().userId("testId").title("제목5").content("검색어").postCategory(2L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37").build();

		given(postService.searchPosts(any(), any(), any()))
				.willReturn(List.of(post2, post4, post5));

		// When
		mockMvc.perform(
						get("/api/posts/search")
								.queryParam("keyword", "검색어")
								.queryParam("category", "2")
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("keyword").description("검색어"),
								parameterWithName("category").description("카테고리 id"),
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("게시글 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].images").description("사진 리스트"),
								fieldWithPath("[].category").description("카테고리"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}*/

	@Test
	@DisplayName("게시글 작성 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void savePostValidFail1Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("").title("수정할 제목").content("수정할 내용").postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						post("/api/posts")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("요청한 카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 작성 테스트 (검증 실패 - 제목 없음)")
	@WithMockUser
	void savePostValidFail2Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("").content("수정할 내용").postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						post("/api/posts")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("요청한 카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 작성 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void savePostValidFail3Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("수정할 제목").content("").postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						post("/api/posts")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("요청한 카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 작성 테스트 (검증 실패 - 카테고리 없음)")
	@WithMockUser
	void savePostValidFail4Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("수정할 제목").content("수정할 내용")
				.build();

		// When
		mockMvc.perform(
						post("/api/posts")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").doesNotExist())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 수정 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void updatePostValidFail1Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("").title("수정할 제목").content("수정할 내용").postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}", 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("수정할 게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("요청한 카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 수정 테스트 (검증 실패 - 제목 없음)")
	@WithMockUser
	void updatePostValidFail2Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("").content("수정할 내용").postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}", 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("수정할 게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("요청한 카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 수정 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void updatePostValidFail3Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("수정할 제목").content("").postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}", 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("수정할 게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("요청한 카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 수정 테스트 (검증 실패 - 카테고리 없음)")
	@WithMockUser
	void updatePostValidFail4Test() throws Exception {
		// Given
		PostRequest request = PostRequest.builder().userId("testId").title("수정할 제목").content("수정할 내용")
				.build();

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}", 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.postCategory").doesNotExist())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("수정할 게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("postCategory").description("카테고리"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("사진 저장 테스트")
	@WithMockUser
	void saveImageSuccessTest() throws Exception {
		// Given
		MockMultipartFile file1 = new MockMultipartFile("images", "Test1.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file2 = new MockMultipartFile("images", "Test2.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file3 = new MockMultipartFile("images", "Test3.png", "image/png", "사진내용".getBytes());
		MockMultipartFile file4 = new MockMultipartFile("images", "Test4.png", "image/png", "사진내용".getBytes());
		List<MultipartFile> images = List.of(file1, file2, file3, file4);
		given(postService.saveImage(images))
				.willReturn(List.of("UUID:Test1.png", "UUID:Test2.png", "UUID:Test3.png", "UUID:Test4.png"));

		// When
		mockMvc.perform(
						multipart("/api/posts/images")
								.file(file1)
								.file(file2)
								.file(file3)
								.file(file4)
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						responseFields(
								fieldWithPath("[]").description("업로드된 이미지 파일의 이름 목록")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 저장 테스트 (사진 포함)")
	@WithMockUser
	void savePostWithImageSuccessTest() throws Exception {
		// Given
		String image1 = "image1.png";
		String image2 = "image2.png";
		String image3 = "image3.png";
		String image4 = "image4.png";
		String image5 = "image5.png";
		List<String> images = List.of(image1, image2, image3, image4, image5);
		PostRequest request = PostRequest.builder()
				.userId("testId").title("제목1").content("내용1").images(images).postCategory(PostCategory.FREE_BOARD)
				.build();

		// When
		mockMvc.perform(
						post("/api/posts")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("images").description("사진 저장 요청 후 서버로 부터 전달받은 이미지 파일의 이름 목록"),
								fieldWithPath("postCategory").description("카테고리")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 단건 조회 테스트 (사진 포함)")
	@WithMockUser
	void getPostWithImageTest() throws Exception {
		// Given
		String image1 = "/api/posts/images/UUID:image1.png";
		String image2 = "/api/posts/images/UUID:image2.png";
		String image3 = "/api/posts/images/UUID:image3.png";
		String image4 = "/api/posts/images/UUID:image4.png";
		String image5 = "/api/posts/images/UUID:image5.png";
		List<String> images = List.of(image1, image2, image3, image4, image5);

		PostResponse post = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").images(images).
				postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L).isLiked(0)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();
		given(postService.findByPostId(any(), any(), any())).willReturn(post);

		// When
		mockMvc.perform(
						get("/api/posts/{postId}", "100")
								.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.images").exists())
				.andExpect(jsonPath("$.view").exists())
				.andExpect(jsonPath("$.commentCount").exists())
				.andExpect(jsonPath("$.postLikeCount").exists())
				.andExpect(jsonPath("$.isLiked").exists())
				.andExpect(jsonPath("$.createdAt").exists())
				.andExpect(jsonPath("$.modifiedAt").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("조회할 게시글 id")
						),
						responseFields(
								fieldWithPath("userId").description("게시글 작성자"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("images").description("사진 리스트"),
								fieldWithPath("postCategory").description("카테고리"),
								fieldWithPath("certificationName").description("자격증 이름 (자격증 후기가 아닌 경우 무시)"),
								fieldWithPath("view").description("조회수"),
								fieldWithPath("commentCount").description("댓글 수"),
								fieldWithPath("postLikeCount").description("좋아요 수"),
								fieldWithPath("isLiked").description("좋아요 상태"),
								fieldWithPath("createdAt").description("작성일자"),
								fieldWithPath("modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 리스트 조회 테스트 (사진 포함)")
	@WithMockUser
	void getPostWithImageListTest() throws Exception {
		// Given
		String image1 = "/api/posts/images/UUID:image1.png";
		String image2 = "/api/posts/images/UUID:image2.png";
		String image3 = "/api/posts/images/UUID:image3.png";
		String image4 = "/api/posts/images/UUID:image4.png";
		String image5 = "/api/posts/images/UUID:image5.png";
		List<String> images = List.of(image1, image2, image3, image4, image5);
		// 편의상 모두 동일한 사진을 가진다고 가정

		PostResponse post1 = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").images(images)
				.postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId").title("제목2").content("내용2").images(images)
				.postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId").title("제목3").content("내용3").images(images)
				.postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId").title("제목4").content("내용4").images(images)
				.postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId").title("제목5").content("내용5").images(images)
				.postCategory(PostCategory.FREE_BOARD).view(0L).commentCount(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(postService.getPostResponseList(any())).willReturn(List.of(post1, post2, post3, post4, post5));

		// When
		mockMvc.perform(
						get("/api/posts")
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("게시글 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].images").description("사진 리스트"),
								fieldWithPath("[].postCategory").description("카테고리"),
								fieldWithPath("[].certificationName").description("자격증 이름 (자격증 후기가 아닌 경우 무시)"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("이미지 요청 테스트")
	@WithMockUser
	void getImageResourceTest() throws Exception {
		// Given
		String fileName = "testImage.png";
		given(postService.getImageResource(fileName)).willReturn(mock(UrlResource.class));

		// When
		mockMvc.perform(
						get("/api/posts/images/{fileName}", fileName)
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("fileName").description("요청할 파일명")
						)));
		// Then
	}
}