package pulleydoreurae.careerquestbackend.certification.controller;

import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.certification.domain.dto.ReviewRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.service.PostService;

/**
 * 자격증 컨트롤러 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/13
 */
@WebMvcTest(CertificationReviewController.class)
@AutoConfigureRestDocs
class CertificationReviewControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	@Qualifier("certificationReviewService")
	PostService postService;
	Gson gson = new Gson();

	@Test
	@DisplayName("자격증 후기 리스트 조회 테스트")
	@WithMockUser
	void getPostListTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").category(1L).view(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId").title("제목2").content("내용2").category(1L).view(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId").title("제목3").content("내용3").category(1L).view(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId").title("제목4").content("내용4").category(1L).view(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId").title("제목5").content("내용5").category(1L).view(0L).postLikeCount(0L)
				.isLiked(0).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(postService.getPostResponseListByCategory(any(), any())).willReturn(
				List.of(post1, post2, post3, post4, post5));

		// When
		mockMvc.perform(
						get("/api/certifications/{certificationId}/reviews", 1)
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)")
						),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("게시글 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].images").description("사진 리스트 (자격증 후기이므로 null전달)"),
								fieldWithPath("[].category").description("카테고리 (자격증 정보를 나타냄)"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수 (자격증 후기이므로 null전달)"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 저장 테스트 (실패)")
	@WithMockUser
	void savePostFailTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "제목", "내용");

		doThrow(new UsernameNotFoundException("게시글 저장에 실패했습니다.")).when(postService).savePost(any());

		// When
		mockMvc.perform(
						post("/api/certifications/{certificationId}/reviews", 1)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 저장 테스트 (성공)")
	@WithMockUser
	void savePostSuccessTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "제목", "내용");

		// When
		mockMvc.perform(
						post("/api/certifications/{certificationId}/reviews", 1)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 수정 테스트 (실패)")
	@WithMockUser
	void updatePostFailTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "수정할 제목", "수정할 내용");

		given(postService.updatePost(any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						patch("/api/certifications/{certificationId}/reviews/{reviewId}", 1, 100)
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
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)"),
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 수정 테스트 (성공)")
	@WithMockUser
	void updatePostSuccessTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "수정할 제목", "수정할 내용");

		given(postService.updatePost(any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						patch("/api/certifications/{certificationId}/reviews/{reviewId}", 1, 100)
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
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)"),
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 삭제 테스트 (실패)")
	@WithMockUser
	void deletePostFailTest() throws Exception {
		// Given
		given(postService.deletePost(any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						delete("/api/certifications/{postId}", 100)
								.with(csrf())
								.queryParam("userId", "testId"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("삭제할 자격증 후기 id")
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
	@DisplayName("자격증 후기 삭제 테스트 (성공)")
	@WithMockUser
	void deletePostSuccessTest() throws Exception {
		// Given
		given(postService.deletePost(any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						delete("/api/certifications/{postId}", 100)
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
	@DisplayName("한 사용자가 작성한 게시글 리스트 조회 테스트")
	@WithMockUser
	void getPostListByUserAccountTest() throws Exception {
		// Given
		PostResponse post1 = PostResponse.builder()
				.userId("testId").title("제목1").content("내용1").category(1L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post2 = PostResponse.builder()
				.userId("testId").title("제목2").content("내용2").category(1L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post3 = PostResponse.builder()
				.userId("testId").title("제목3").content("내용3").category(1L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post4 = PostResponse.builder()
				.userId("testId").title("제목4").content("내용4").category(1L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		PostResponse post5 = PostResponse.builder()
				.userId("testId").title("제목5").content("내용5").category(1L).view(0L)
				.createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(postService.getPostListByUserAccount(any(), any()))
				.willReturn(List.of(post1, post2, post3, post4, post5));

		// When
		mockMvc.perform(
						get("/api/certifications/reviews/user/{userId}", "testId")
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
								fieldWithPath("[].images").description("사진 리스트 (자격증 후기이므로 null전달)"),
								fieldWithPath("[].category").description("카테고리 (자격증 정보를 나타냄)"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].commentCount").description("댓글 수 (자격증 후기이므로 null전달)"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 작성 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void savePostValidFail1Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("", "제목", "내용");

		// When
		mockMvc.perform(
						post("/api/certifications/{certificationId}/reviews", 1)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.category").exists())
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
								fieldWithPath("category").description("요청한 카테고리 (자격증 id 정보)"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 작성 테스트 (검증 실패 - 제목 없음)")
	@WithMockUser
	void savePostValidFail2Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "", "내용");

		// When
		mockMvc.perform(
						post("/api/certifications/{certificationId}/reviews", 1)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.category").exists())
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
								fieldWithPath("category").description("요청한 카테고리 (자격증 id 정보)"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 작성 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void savePostValidFail3Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "제목", "");

		// When
		mockMvc.perform(
						post("/api/certifications/{certificationId}/reviews", 1)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.category").exists())
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
								fieldWithPath("category").description("요청한 카테고리 (자격증 id 정보)"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 수정 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void updatePostValidFail1Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("", "제목", "내용");

		// When
		mockMvc.perform(
						patch("/api/certifications/{certificationId}/reviews/{reviewId}", 1, 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.category").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)"),
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("category").description("요청한 카테고리 (자격증 id 정보)"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 수정 테스트 (검증 실패 - 제목 없음)")
	@WithMockUser
	void updatePostValidFail2Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "", "내용");

		// When
		mockMvc.perform(
						patch("/api/certifications/{certificationId}/reviews/{reviewId}", 1, 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.category").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)"),
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("category").description("요청한 카테고리 (자격증 id 정보)"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("게시글 수정 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void updatePostValidFail3Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "제목", "");

		// When
		mockMvc.perform(
						patch("/api/certifications/{certificationId}/reviews/{reviewId}", 1, 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.category").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("certificationId").description("자격증 정보 (자격증 id 번호)"),
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("category").description("요청한 카테고리 (자격증 id 정보)"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}
}
