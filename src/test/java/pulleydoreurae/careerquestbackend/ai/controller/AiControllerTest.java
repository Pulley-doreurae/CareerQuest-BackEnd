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
		mockMvc.perform(get("/api/ai")
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("질의할 ID"),
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
		mockMvc.perform(get("/api/ai")
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("질의할 ID"),
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
		String[] names = {"정보처리기사", "정보처리산업기사", "정보보안기사", "병아리 감별사"};
		AiResponse aiResponse = new AiResponse(names);
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
								fieldWithPath("userId").description("질의할 ID"),
								fieldWithPath("database").description("질의할 데이터베이스")
						),
						responseFields(
								fieldWithPath("name.[]").description("AI 질의결과(결과가 높은 순서부터 출력됨)")
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
		mockMvc.perform(get("/api/ai/aboutMe")
				.contentType(MediaType.APPLICATION_JSON)
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
		UserIdRequest request = new UserIdRequest();
		request.setUserId("testId");
		String[] names = {"안녕하세요 저는 꿈꾸는 개발자를 목표로 하고있는 창의력 있는 예술가 김아무개 입니다."};
		AiResponse aiResponse = new AiResponse(names);
		given(aiService.findResult(any())).willReturn(aiResponse);

		// When
		mockMvc.perform(post("/api/ai/aboutMe")
				.contentType(MediaType.APPLICATION_JSON)
				.with(csrf())
				.content(gson.toJson(request)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("질의할 ID")
				),
				responseFields(
					fieldWithPath("name.[]").description("AI 질의결과")
				)));

		// Then
		verify(aiService).findResult(any());


	}
}
