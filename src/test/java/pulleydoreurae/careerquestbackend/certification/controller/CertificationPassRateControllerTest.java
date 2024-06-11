package pulleydoreurae.careerquestbackend.certification.controller;

import static org.junit.jupiter.api.Assertions.*;
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
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.PassRateSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationPassRateResponse;
import pulleydoreurae.careerquestbackend.certification.service.CertificationPassRateService;

/**
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@WebMvcTest(CertificationPassRateController.class)
@AutoConfigureRestDocs
@DisplayName("자격증 합격률 Controller")
class CertificationPassRateControllerTest {

	@Autowired MockMvc mockMvc;
	@MockBean CertificationPassRateService service;
	Gson gson = new Gson();

	@Test
	@DisplayName("자격증 이름으로 자격증 합격률 검색")
	@WithMockUser
	void findByCertificationName() throws Exception {
	    // Given
		CertificationPassRateResponse response1 = new CertificationPassRateResponse("정보처리기사", 2020L, 1L, ExamType.FIRST_STAGE, 30.0);
		CertificationPassRateResponse response2 = new CertificationPassRateResponse("정보처리기사", 2020L, 1L, ExamType.FIRST_STAGE, 28.0);
		CertificationPassRateResponse response3 = new CertificationPassRateResponse("정보처리기사", 2022L, 2L, ExamType.FIRST_STAGE, 37.0);
		CertificationPassRateResponse response4 = new CertificationPassRateResponse("정보처리기사", 2022L, 2L, ExamType.LAST_STAGE, 17.0);
		CertificationPassRateResponse response5 = new CertificationPassRateResponse("정보처리기사", 2023L, 1L, ExamType.FIRST_STAGE, 37.0);
		given(service.findByCertificationName("정보처리기사")).willReturn(List.of(response1, response2, response3, response4, response5));

	    // When
		mockMvc.perform(get("/api/certifications/pass-rate/{certificationName}", "정보처리기사"))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("certificationName").description("자격증 이름")
						),
						responseFields(
								fieldWithPath("[].certificationName").description("자격증 이름"),
								fieldWithPath("[].examYear").description("시행연도"),
								fieldWithPath("[].examRound").description("시험 회차"),
								fieldWithPath("[].examType").description("시험 구분"),
								fieldWithPath("[].passRate").description("합격률")
						)));

	    // Then
	}

	@Test
	@DisplayName("검색조건으로 자격증 합격률 검색")
	@WithMockUser
	void findBySearchRequestTest() throws Exception {
		// Given
		CertificationPassRateResponse response1 = new CertificationPassRateResponse("정보처리기사", 2020L, 1L, ExamType.FIRST_STAGE, 30.0);
		CertificationPassRateResponse response2 = new CertificationPassRateResponse("정보처리기사", 2020L, 1L, ExamType.FIRST_STAGE, 48.0);
		CertificationPassRateResponse response3 = new CertificationPassRateResponse("정보처리기사", 2023L, 1L, ExamType.FIRST_STAGE, 37.0);
		PassRateSearchRequest request = new PassRateSearchRequest("정보처리기사", 2020L, 2024L, 1L, ExamType.FIRST_STAGE, 30.0, 50.0);
		given(service.findBySearchRequest(any())).willReturn(List.of(response1, response2, response3));

		// When
		mockMvc.perform(post("/api/certifications/pass-rate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request))
						.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("certificationName").description("자격증 이름"),
								fieldWithPath("startYear").description("시작 연도"),
								fieldWithPath("endYear").description("종료 연도"),
								fieldWithPath("examRound").description("시험 회차"),
								fieldWithPath("examType").description("시험 구분"),
								fieldWithPath("minPassRate").description("최소 합격률"),
								fieldWithPath("maxPassRate").description("최대 합격률")
						),
						responseFields(
								fieldWithPath("[].certificationName").description("자격증 이름"),
								fieldWithPath("[].examYear").description("시행연도"),
								fieldWithPath("[].examRound").description("시험 회차"),
								fieldWithPath("[].examType").description("시험 구분"),
								fieldWithPath("[].passRate").description("합격률")
						)));

		// Then
	}
}
