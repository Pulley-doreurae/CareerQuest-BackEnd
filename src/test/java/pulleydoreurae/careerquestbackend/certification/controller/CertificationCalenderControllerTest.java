package pulleydoreurae.careerquestbackend.certification.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationDateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationExamDateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationPeriodResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.CertificationSearchRequest;
import pulleydoreurae.careerquestbackend.certification.service.CertificationCalenderService;

/**
 *
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
@WebMvcTest(CertificationCalenderController.class)
@AutoConfigureRestDocs
@DisplayName("자격증 달력 컨트롤러 테스트")
class CertificationCalenderControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	CertificationCalenderService service;

	/**
	 * Gson으로 LocalDate를 전송하기 위한 직렬화
	 */
	static class LocalDateTimeSerializer implements JsonSerializer<LocalDate> {
		private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		@Override
		public JsonElement serialize(LocalDate localDate, Type srcType, JsonSerializationContext context) {
			return new JsonPrimitive(formatter.format(localDate));
		}

	}

	GsonBuilder gsonBuilder = new GsonBuilder();
	Gson gson;

	@BeforeEach
	void setUp() {
		gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateTimeSerializer());
		gson = gsonBuilder.setPrettyPrinting().create();
	}

	@Test
	@DisplayName("입력한 날짜에 맞는 자격증 정보 전달하기")
	@WithMockUser
	void findByDateTest() throws Exception {
		// Given
		CertificationExamDateResponse examDate = new CertificationExamDateResponse("정보처리기사", ExamType.FIRST_STAGE, 100L,
				LocalDate.of(2024, 5, 21));
		CertificationPeriodResponse period = new CertificationPeriodResponse("정보처리기사", ExamType.FIRST_STAGE, 100L,
				LocalDate.of(2024, 3, 1), LocalDate.of(2024, 6, 10));

		CertificationSearchRequest request = new CertificationSearchRequest();
		request.setDate(LocalDate.of(2024, 5, 21));
		CertificationDateResponse willReturn = new CertificationDateResponse();
		willReturn.getPeriodResponse().add(period);
		willReturn.getExamDateResponses().add(examDate);
		given(service.findByDate(request.getDate())).willReturn(willReturn);

		// When
		mockMvc.perform(get("/api/certifications/dates")
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.periodResponse").exists())
				.andExpect(jsonPath("$.examDateResponses").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("date").description("검색할 날짜")
						),
						responseFields(
								fieldWithPath("periodResponse").description("접수 기간 정보 배열"),
								fieldWithPath("periodResponse[].name").description("자격증 이름"),
								fieldWithPath("periodResponse[].examType").description("시험 유형"),
								fieldWithPath("periodResponse[].examRound").description("시험 회차"),
								fieldWithPath("periodResponse[].startDate").description("접수 시작일"),
								fieldWithPath("periodResponse[].endDate").description("접수 종료일"),
								fieldWithPath("examDateResponses").description("시험 날짜 정보 배열"),
								fieldWithPath("examDateResponses[].name").description("자격증 이름"),
								fieldWithPath("examDateResponses[].examType").description("시험 유형"),
								fieldWithPath("examDateResponses[].examRound").description("시험 회차"),
								fieldWithPath("examDateResponses[].examDate").description("시험 날짜")
						)));

		// Then
	}
}