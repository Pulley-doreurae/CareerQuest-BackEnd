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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.service.PostService;

/**
 * @author : parkjihyeok
 * @since : 2024/03/31
 */
@WebMvcTest(PostController.class)
@AutoConfigureRestDocs
class PostControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	PostService postService;
	Gson gson = new Gson();

	@Test
	@DisplayName("1. 게시글 리스트 조회 테스트")
	@WithMockUser
	void getPostListTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId")
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.commentCount(0L)
				.postLikeCount(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId")
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.commentCount(0L)
				.postLikeCount(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId")
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.commentCount(0L)
				.postLikeCount(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId")
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.commentCount(0L)
				.postLikeCount(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId")
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.commentCount(0L)
				.postLikeCount(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
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
								fieldWithPath("[].category").description("카테고리"),
								fieldWithPath("[].hit").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
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
				.userId("testId")
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.commentCount(0L)
				.postLikeCount(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();
		given(postService.findByPostId(100L)).willReturn(post);

		// When
		mockMvc.perform(
						get("/api/posts/{postId}", "100")
								.with(csrf()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.hit").exists())
				.andExpect(jsonPath("$.commentCount").exists())
				.andExpect(jsonPath("$.postLikeCount").exists())
				.andExpect(jsonPath("$.category").exists())
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
								fieldWithPath("category").description("카테고리"),
								fieldWithPath("hit").description("조회수"),
								fieldWithPath("commentCount").description("댓글 수"),
								fieldWithPath("postLikeCount").description("좋아요 수"),
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
		PostRequest request = PostRequest.builder()
				.userId("testId")
				.title("제목1")
				.content("내용1")
				.category(1L)
				.build();

		given(postService.savePost(any())).willReturn(false);

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
								fieldWithPath("category").description("카테고리 번호")
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
		PostRequest request = PostRequest.builder()
				.userId("testId")
				.title("제목1")
				.content("내용1")
				.category(1L)
				.build();

		given(postService.savePost(any())).willReturn(true);

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
								fieldWithPath("category").description("카테고리 번호")
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
		PostRequest request = PostRequest.builder()
				.userId("testId")
				.title("수정할 제목")
				.content("수정할 내용")
				.category(1L)
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
								fieldWithPath("category").description("카테고리 번호")
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
		PostRequest request = PostRequest.builder()
				.userId("testId")
				.title("수정할 제목")
				.content("수정할 내용")
				.category(1L)
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
								fieldWithPath("category").description("카테고리 번호")
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

	@Test
	@DisplayName("9. 게시글 리스트 카테고리로 조회 테스트")
	@WithMockUser
	void getPostListByCategoryTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId")
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId")
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId")
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId")
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId")
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
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
								fieldWithPath("[].category").description("카테고리"),
								fieldWithPath("[].hit").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("10. 한 사용자가 작성한 게시글 리스트 조회 테스트")
	@WithMockUser
	void getPostListByUserAccountTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId")
				.title("제목1")
				.content("내용1")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId")
				.title("제목2")
				.content("내용2")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId")
				.title("제목3")
				.content("내용3")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId")
				.title("제목4")
				.content("내용4")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId")
				.title("제목5")
				.content("내용5")
				.category(1L)
				.hit(0L)
				.createdAt("2024.04.01 15:37")
				.modifiedAt("2024.04.01 15:37")
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
								fieldWithPath("[].category").description("카테고리"),
								fieldWithPath("[].hit").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}
}
