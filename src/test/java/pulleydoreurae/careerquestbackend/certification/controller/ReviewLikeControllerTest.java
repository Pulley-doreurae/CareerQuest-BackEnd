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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.certification.domain.dto.ReviewLikeRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.service.ReviewLikeService;

/**
 * 자격증 좋아요 컨트롤러 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/13
 */
@WebMvcTest(ReviewLikeController.class)
@AutoConfigureRestDocs
class ReviewLikeControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	ReviewLikeService reviewLikeService;
	Gson gson = new Gson();

	@Test
	@DisplayName("좋아요 상태 변환 성공")
	@WithMockUser
	void changeReviewLikeSuccessTest() throws Exception {
		// Given
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").isLiked(false).build();

		// When
		mockMvc.perform(post("/api/certifications/reviews/likes")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("reviewId").description("좋아요 상태를 변환할 후기 id"),
								fieldWithPath("userId").description("요청자 id"),
								fieldWithPath("isLiked").description("현재 좋아요 상태")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("한 사용자가 좋아요 누른 후기 리스트 반환 테스트")
	@WithMockUser
	void findAllReviewLikeByUserAccountTest() throws Exception {
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

		given(reviewLikeService.findAllReviewLikeByUserAccount(any(), any())).willReturn(
				List.of(review1, review2, review3, review4, review5));

		// When
		mockMvc.perform(get("/api/certifications/likes/{userId}", "testId")
						.queryParam("page", "0")
						.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("userId").description("요청자 id")
						),
						queryParameters(
								parameterWithName("page").description("페이지 정보 (0부터 시작)")
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
	@DisplayName("좋아요 상태 변환 검증 실패(reviewId 없음)")
	@WithMockUser
	void changeReviewLikeValidFail1Test() throws Exception {
		// Given
		ReviewLikeRequest request = ReviewLikeRequest.builder().userId("testId").isLiked(false).build();

		// When
		mockMvc.perform(post("/api/certifications/reviews/likes")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("요청자 id"),
								fieldWithPath("isLiked").description("현재 좋아요 상태")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("좋아요 상태 변환 검증 실패(userId 없음)")
	@WithMockUser
	void changeReviewLikeValidFail2Test() throws Exception {
		// Given
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).isLiked(false).build();

		// When
		mockMvc.perform(post("/api/certifications/reviews/likes")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("reviewId").description("좋아요 상태를 변환할 후기 id"),
								fieldWithPath("isLiked").description("현재 좋아요 상태")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("좋아요 상태 변환 검증 실패(isLiked 없음)")
	@WithMockUser
	void changePostLikeValidFail3Test() throws Exception {
		// Given
		ReviewLikeRequest request = ReviewLikeRequest.builder().reviewId(10000L).userId("testId").build();

		// When
		mockMvc.perform(post("/api/certifications/reviews/likes")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("reviewId").description("좋아요 상태를 변환할 후기 id"),
								fieldWithPath("userId").description("요청자 id")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 처리 결과")
						)));

		// Then
	}
}
