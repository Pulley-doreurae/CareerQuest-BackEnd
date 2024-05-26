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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.certification.domain.dto.request.ReviewRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.service.ReviewService;

/**
 * 자격증 컨트롤러 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/13
 */
@WebMvcTest(ReviewController.class)
@AutoConfigureRestDocs
class ReviewControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	ReviewService reviewService;
	Gson gson = new Gson();

	@Test
	@DisplayName("자격증 후기 리스트 조회 테스트")
	@WithMockUser
	void getReviewListTest() throws Exception {
		// Given
		ReviewResponse review1 = ReviewResponse.builder()
				.userId("testId").title("제목1").content("내용1").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review2 = ReviewResponse.builder()
				.userId("testId").title("제목2").content("내용2").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review3 = ReviewResponse.builder()
				.userId("testId").title("제목3").content("내용3").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review4 = ReviewResponse.builder()
				.userId("testId").title("제목4").content("내용4").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review5 = ReviewResponse.builder()
				.userId("testId").title("제목5").content("내용5").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(reviewService.getPostResponseList(any())).willReturn(
				List.of(review1, review2, review3, review4, review5));

		// When
		mockMvc.perform(
						get("/api/certifications/reviews", 1)
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
								fieldWithPath("[].userId").description("후기 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].certificationName").description("자격증이름"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 리스트 자격증명으로 조회 테스트")
	@WithMockUser
	void getReviewListByCertificationNameTest() throws Exception {
		// Given
		ReviewResponse review1 = ReviewResponse.builder()
				.userId("testId").title("제목1").content("내용1").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review2 = ReviewResponse.builder()
				.userId("testId").title("제목2").content("내용2").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review3 = ReviewResponse.builder()
				.userId("testId").title("제목3").content("내용3").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review4 = ReviewResponse.builder()
				.userId("testId").title("제목4").content("내용4").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review5 = ReviewResponse.builder()
				.userId("testId").title("제목5").content("내용5").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		Pageable pageable = PageRequest.of(0, 15);
		given(reviewService.getReviewResponseListByCertificationName("정보처리기사", pageable)).willReturn(
				List.of(review1, review2, review3, review4, review5));

		// When
		mockMvc.perform(
						get("/api/certifications/reviews/{certificationName}", "정보처리기사")
								.queryParam("page", "0")
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("certificationName").description("자격증이름")
						),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("후기 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].certificationName").description("자격증이름"),
								fieldWithPath("[].view").description("조회수"),
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
	void saveReviewFailTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "제목", "내용");

		doThrow(new UsernameNotFoundException("후기 저장에 실패했습니다.")).when(reviewService).saveReview(any());

		// When
		mockMvc.perform(
						post("/api/certifications/reviews")
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
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 저장 테스트 (성공)")
	@WithMockUser
	void saveReviewSuccessTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "제목", "내용");

		// When
		mockMvc.perform(
						post("/api/certifications/reviews")
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
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 수정 테스트 (실패)")
	@WithMockUser
	void updateReviewFailTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "수정할 제목", "수정할 내용");

		given(reviewService.updatePost(any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						patch("/api/certifications/reviews/{reviewId}",100)
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
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 수정 테스트 (성공)")
	@WithMockUser
	void updateReviewSuccessTest() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "수정할 제목", "수정할 내용");

		given(reviewService.updatePost(any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						patch("/api/certifications/reviews/{reviewId}", 1, 100)
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
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("자격증 후기 삭제 테스트 (실패)")
	@WithMockUser
	void deleteReviewFailTest() throws Exception {
		// Given
		given(reviewService.deleteReview(any(), any())).willReturn(false);

		// When
		mockMvc.perform(
						delete("/api/certifications/reviews/{postId}", 100)
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
	void deleteReviewSuccessTest() throws Exception {
		// Given
		given(reviewService.deleteReview(any(), any())).willReturn(true);

		// When
		mockMvc.perform(
						delete("/api/certifications/reviews/{postId}", 100)
								.with(csrf())
								.queryParam("userId", "testId"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("postId").description("삭제할 후기 id")
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
	@DisplayName("한 사용자가 작성한 후기 리스트 조회 테스트")
	@WithMockUser
	void getReviewListByUserAccountTest() throws Exception {
		// Given
		ReviewResponse review1 = ReviewResponse.builder()
				.userId("testId").title("제목1").content("내용1").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review2 = ReviewResponse.builder()
				.userId("testId").title("제목2").content("내용2").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review3 = ReviewResponse.builder()
				.userId("testId").title("제목3").content("내용3").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review4 = ReviewResponse.builder()
				.userId("testId").title("제목4").content("내용4").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		ReviewResponse review5 = ReviewResponse.builder()
				.userId("testId").title("제목5").content("내용5").certificationName("정보처리기사").view(0L).postLikeCount(0L)
				.isLiked(false).createdAt("2024.04.01 15:37").modifiedAt("2024.04.01 15:37")
				.build();

		given(reviewService.getReviewListByUserAccount(any(), any()))
				.willReturn(List.of(review1, review2, review3, review4, review5));

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
						pathParameters(
								parameterWithName("userId").description("작성자 id")
						),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("[].userId").description("후기 작성자"),
								fieldWithPath("[].title").description("제목"),
								fieldWithPath("[].content").description("내용"),
								fieldWithPath("[].certificationName").description("자격증이름"),
								fieldWithPath("[].view").description("조회수"),
								fieldWithPath("[].postLikeCount").description("좋아요 수"),
								fieldWithPath("[].isLiked").description("좋아요 상태 (리스트에선 상관 X)"),
								fieldWithPath("[].createdAt").description("작성일자"),
								fieldWithPath("[].modifiedAt").description("수정일자")
						)));

		// Then
	}

	@Test
	@DisplayName("후기 작성 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void saveReviewValidFail1Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("", "정보처리기사", "제목", "내용");

		// When
		mockMvc.perform(
						post("/api/certifications/reviews")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.certificationName").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("certificationName").description("요청한 자격증이름"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("후기 작성 테스트 (검증 실패 - 제목 없음)")
	@WithMockUser
	void saveReviewValidFail2Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "", "내용");

		// When
		mockMvc.perform(
						post("/api/certifications/reviews")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.certificationName").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("certificationName").description("요청한 자격증이름"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("후기 작성 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void saveReviewValidFail3Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "제목", "");

		// When
		mockMvc.perform(
						post("/api/certifications/reviews")
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.certificationName").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("certificationName").description("요청한 자격증이름"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("후기 수정 테스트 (검증 실패 - userId 없음)")
	@WithMockUser
	void updateReviewValidFail1Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("", "정보처리기사", "제목", "내용");

		// When
		mockMvc.perform(
						patch("/api/certifications/reviews/{reviewId}", 1, 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.certificationName").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("certificationName").description("요청한 자격증이름"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("후기 수정 테스트 (검증 실패 - 제목 없음)")
	@WithMockUser
	void updateReviewValidFail2Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "", "내용");

		// When
		mockMvc.perform(
						patch("/api/certifications/reviews/{reviewId}", 1, 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.certificationName").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("certificationName").description("요청한 자격증이름"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("후기 수정 테스트 (검증 실패 - 내용 없음)")
	@WithMockUser
	void updateReviewValidFail3Test() throws Exception {
		// Given
		ReviewRequest request = new ReviewRequest("testId", "정보처리기사", "제목", "");

		// When
		mockMvc.perform(
						patch("/api/certifications/reviews/{reviewId}", 100)
								.with(csrf())
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.title").exists())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.certificationName").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters( // PathVariable 방식
								parameterWithName("reviewId").description("수정할 자격증 후기 id")
						),
						requestFields(
								fieldWithPath("userId").description("작성자 id"),
								fieldWithPath("title").description("제목"),
								fieldWithPath("content").description("내용"),
								fieldWithPath("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("title").description("요청한 제목"),
								fieldWithPath("content").description("요청한 내용"),
								fieldWithPath("certificationName").description("요청한 자격증이름"),
								fieldWithPath("errors").description("요청에 대한 검증 결과")
						)));

		// Then
	}
}
