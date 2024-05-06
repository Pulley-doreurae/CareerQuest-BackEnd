package pulleydoreurae.careerquestbackend.basiccommunity.controller;

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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.basiccommunity.domain.dto.request.CommentRequest;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.dto.response.CommentResponse;
import pulleydoreurae.careerquestbackend.basiccommunity.service.CommentService;

/**
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@WebMvcTest(CommentController.class)
@AutoConfigureRestDocs
class CommentControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	CommentService commentService;

	Gson gson = new Gson();

	@Test
	@DisplayName("1. 한 사용자가 작성한 댓글리스트 불러오기")
	@WithMockUser
	void findAllByUserIdTest() throws Exception {
		// Given
		CommentResponse comment1 = CommentResponse.builder().userId("testId").postId(10000L).content("내용1")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();
		CommentResponse comment2 = CommentResponse.builder().userId("testId").postId(10000L).content("내용2")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		CommentResponse comment3 = CommentResponse.builder().userId("testId").postId(10000L).content("내용3")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		CommentResponse comment4 = CommentResponse.builder().userId("testId").postId(10000L).content("내용4")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		CommentResponse comment5 = CommentResponse.builder().userId("testId").postId(10000L).content("내용5")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(commentService.findListByUserAccount(any(), any())).willReturn(
				List.of(comment1, comment2, comment3, comment4, comment5));

		// When
		mockMvc.perform(
						get("/api/comments/{userId}", "testId")
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("페이지 정보 (0부터 시작)")
						),
						pathParameters(
								parameterWithName("userId").description("회원아이디")
						),
						responseFields(
								fieldWithPath("[].userId").description("댓글 작성자"),
								fieldWithPath("[].postId").description("게시글 id"),
								fieldWithPath("[].content").description("댓글 내용"),
								fieldWithPath("[].createdAt").description("작성일"),
								fieldWithPath("[].modifiedAt").description("수정일")
						)));

		// Then
	}

	@Test
	@DisplayName("2. 게시글로 댓글리스트 불러오기")
	@WithMockUser
	void findAllByPostIdTest() throws Exception {
		// Given
		CommentResponse comment1 = CommentResponse.builder().userId("testId").postId(10000L).content("내용1")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();
		CommentResponse comment2 = CommentResponse.builder().userId("testId").postId(10000L).content("내용2")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		CommentResponse comment3 = CommentResponse.builder().userId("testId").postId(10000L).content("내용3")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		CommentResponse comment4 = CommentResponse.builder().userId("testId").postId(10000L).content("내용4")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		CommentResponse comment5 = CommentResponse.builder().userId("testId").postId(10000L).content("내용5")
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(commentService.findListByPostId(any(), any())).willReturn(
				List.of(comment1, comment2, comment3, comment4, comment5));

		// When
		mockMvc.perform(
						get("/api/posts/{postId}/comments", 10000L)
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id")
						),
						queryParameters(
								parameterWithName("page").description("페이지 정보 (0부터 시작)")
						),
						responseFields(
								fieldWithPath("[].userId").description("댓글 작성자"),
								fieldWithPath("[].postId").description("게시글 id"),
								fieldWithPath("[].content").description("댓글 내용"),
								fieldWithPath("[].createdAt").description("작성일"),
								fieldWithPath("[].modifiedAt").description("수정일")
						)));

		// Then
	}

	@Test
	@DisplayName("3. 댓글 등록 테스트 (실패)")
	@WithMockUser
	void saveCommentFailTest() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("testId").content("댓글내용").build();
		doThrow(new UsernameNotFoundException("댓글 등록 실패")).when(commentService).saveComment(any());

		// When
		mockMvc.perform(
						post("/api/posts/{postId}/comments", 10000L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("댓글 작성자"),
								fieldWithPath("content").description("댓글 내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("4. 댓글 등록 테스트 (성공)")
	@WithMockUser
	void saveCommentSuccessTest() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("testId").content("댓글내용").build();

		// When
		mockMvc.perform(
						post("/api/posts/{postId}/comments", 10000L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("댓글 작성자"),
								fieldWithPath("content").description("댓글 내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("5. 댓글 수정 테스트 (실패)")
	@WithMockUser
	void updateCommentFailTest() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("testId").content("수정할 댓글 내용").build();
		given(commentService.updateComment(any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}/comments/{commentId}", 10000L, 100L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id"),
								parameterWithName("commentId").description("댓글 id")
						),
						requestFields(
								fieldWithPath("userId").description("수정 요청자"),
								fieldWithPath("content").description("수정할 내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("6. 댓글 수정 테스트 (성공)")
	@WithMockUser
	void updateCommentSuccessTest() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("testId").content("수정할 댓글 내용").build();
		given(commentService.updateComment(any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}/comments/{commentId}", 10000L, 100L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id"),
								parameterWithName("commentId").description("댓글 id")
						),
						requestFields(
								fieldWithPath("userId").description("수정 요청자"),
								fieldWithPath("content").description("수정할 내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("7. 댓글 삭제 테스트 (실패)")
	@WithMockUser
	void deleteCommentFailTest() throws Exception {
		// Given
		given(commentService.deleteComment(any(), any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						delete("/api/posts/{postId}/comments/{commentId}", 10000L, 100L)
								.queryParam("userId", "testId")
								.with(csrf()))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id"),
								parameterWithName("commentId").description("댓글 id")
						),
						queryParameters(
								parameterWithName("userId").description("요청자 아이디")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("8. 댓글 삭제 테스트 (성공)")
	@WithMockUser
	void deleteCommentSuccessTest() throws Exception {
		// Given
		given(commentService.deleteComment(any(), any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						delete("/api/posts/{postId}/comments/{commentId}", 10000L, 100L)
								.queryParam("userId", "testId")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id"),
								parameterWithName("commentId").description("댓글 id")
						),
						queryParameters(
								parameterWithName("userId").description("요청자 아이디")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("댓글 등록 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void saveCommentValidFail1Test() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("").content("댓글내용").build();

		// When
		mockMvc.perform(
						post("/api/posts/{postId}/comments", 10000L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("댓글 작성자"),
								fieldWithPath("content").description("댓글 내용")
						),
						responseFields(
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("댓글 등록 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void saveCommentValidFail2Test() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("testId").content("").build();

		// When
		mockMvc.perform(
						post("/api/posts/{postId}/comments", 10000L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id")
						),
						requestFields(
								fieldWithPath("userId").description("댓글 작성자"),
								fieldWithPath("content").description("댓글 내용")
						),
						responseFields(
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("댓글 수정 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void updateCommentValidFail1Test() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("").content("댓글내용").build();

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}/comments/{commentId}", 10000L, 100L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id"),
								parameterWithName("commentId").description("댓글 id")
						),
						requestFields(
								fieldWithPath("userId").description("댓글 작성자"),
								fieldWithPath("content").description("댓글 내용")
						),
						responseFields(
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));
		// Then
	}

	@Test
	@DisplayName("댓글 수정 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void updateCommentValidFail2Test() throws Exception {
		// Given
		CommentRequest request = CommentRequest.builder().userId("testId").content("").build();

		// When
		mockMvc.perform(
						patch("/api/posts/{postId}/comments/{commentId}", 10000L, 100L)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("postId").description("게시글 id"),
								parameterWithName("commentId").description("댓글 id")
						),
						requestFields(
								fieldWithPath("userId").description("댓글 작성자"),
								fieldWithPath("content").description("댓글 내용")
						),
						responseFields(
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));
		// Then
	}
}
