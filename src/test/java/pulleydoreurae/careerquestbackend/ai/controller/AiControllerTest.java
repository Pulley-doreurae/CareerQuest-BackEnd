package pulleydoreurae.careerquestbackend.ai.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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

import pulleydoreurae.careerquestbackend.ai.dto.AiRequest;
import pulleydoreurae.careerquestbackend.ai.dto.AiResponse;
import pulleydoreurae.careerquestbackend.ai.dto.Item;
import pulleydoreurae.careerquestbackend.ai.service.AiService;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserIdRequest;

/**
 * @author : parkjihyeok
 * @since : 2024/06/06
 */
@WebMvcTest(AiController.class)
@AutoConfigureRestDocs
@DisplayName("AI 컨트롤러 테스트")
class AiControllerTest {

	@Autowired MockMvc mockMvc;
	@MockBean AiService aiService;
	Gson gson = new Gson();

	@Test
	@DisplayName("AI 질의 테스트 - 실패 (userId없음)")
	@WithMockUser
	void findResultTest1() throws Exception {
		// Given
		AiRequest request = new AiRequest("", "study_vector");

		// When
		mockMvc.perform(post("/api/ai")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("content").description("질의할 내용"),
								fieldWithPath("database").description("질의할 데이터베이스")
						),
						responseFields(
								fieldWithPath("msg").description("실패 사유")
						)));

		// Then
		verify(aiService, never()).findResult(any());
	}

	@Test
	@DisplayName("AI 질의 테스트 - 실패 (database없음)")
	@WithMockUser
	void findResultTest2() throws Exception {
		// Given
		AiRequest request = new AiRequest("testId", "");

		// When
		mockMvc.perform(post("/api/ai")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("content").description("질의할 내용"),
								fieldWithPath("database").description("질의할 데이터베이스")
						),
						responseFields(
								fieldWithPath("msg").description("실패 사유")
						)));

		// Then
		verify(aiService, never()).findResult(any());
	}

	@Test
	@DisplayName("AI 질의 테스트 - 성공")
	@WithMockUser
	void findResultTest3() throws Exception {
		// Given
		AiRequest request = new AiRequest("testId", "cert_vector");
		List<Item> items = List.of(new Item(1L, null, "정보처리기사", "43%"), new Item(2L, null, "정보보안기사", "27%"), new Item(1L, null, "정보처리기사", "43%"), new Item(1L, null, "정보산업기사", "63%"), new Item(1L, null, "빅데이터분석기사", "80%"));
		AiResponse aiResponse = new AiResponse(items);
		given(aiService.findResult(any())).willReturn(aiResponse);

		// When
		mockMvc.perform(post("/api/ai")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("content").description("질의할 내용"),
								fieldWithPath("database").description("질의할 데이터베이스")
						),
						responseFields(
								fieldWithPath("items.[].id").description("엔티티 ID"),
								fieldWithPath("items.[].image").description("이미지 경로"),
								fieldWithPath("items.[].name").description("AI 질의내용"),
								fieldWithPath("items.[].value").description("AI 질의결과")
						)));

		// Then
		verify(aiService).findResult(any());


	}

	@Test
	@DisplayName("AI 자기소개 질의 테스트 - 실패 (유효성 검사 실패)")
	@WithMockUser
	void findResultTest4() throws Exception {
		// Given

		UserIdRequest request = new UserIdRequest();
		request.setUserId("tet ");

		// When
		mockMvc.perform(post("/api/ai")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("질의할 ID")
				),
				responseFields(
					fieldWithPath("msg").description("실패 사유")
				)));

		// Then
		verify(aiService, never()).findResult(any());

	}

	@Test
	@DisplayName("AI 자기소개 질의 테스트 - 성공")
	@WithMockUser
	void findResultTest5() throws Exception {
		// Given
		AiRequest request = new AiRequest("testId", "portfolio_summary");
		List<Item> items = List.of(new Item(0L, null, null, "문채원 님은 정보통신 및 정보기술 분야에 관심이 많으며, Spring 프레임워크를 활용하여 Java 기반 애플리케이션 개발에 능숙합니다. 또한, 정보처리기사 자격증을 목표로 공부하고 있으며, 관련 스터디 팀에서 백엔드 역할을 담당하여 팀의 기술적 성장을 도모하고 있습니다."));
		AiResponse aiResponse = new AiResponse(items);
		given(aiService.findResult(any())).willReturn(aiResponse);

		// When
		mockMvc.perform(post("/api/ai")
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
				.content(gson.toJson(request)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
						fieldWithPath("content").description("질의할 내용"),
						fieldWithPath("database").description("질의할 데이터베이스")
				),
				responseFields(
						fieldWithPath("items.[].id").description("엔티티 ID"),
						fieldWithPath("items.[].image").description("이미지 경로"),
						fieldWithPath("items.[].name").description("AI 질의내용"),
						fieldWithPath("items.[].value").description("AI 질의결과")
				)));

		// Then
		verify(aiService).findResult(any());
	}
}
