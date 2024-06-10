package pulleydoreurae.careerquestbackend.certification.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.UserCertificationRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.UserCertificationInfo;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.UserCertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.UserCertification;
import pulleydoreurae.careerquestbackend.certification.service.UserCertificationService;

/**
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
@WebMvcTest(UserCertificationController.class)
@AutoConfigureRestDocs
class UserCertificationControllerTest {

	@Autowired MockMvc mockMvc;
	@MockBean UserCertificationService service;

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
		gsonBuilder.registerTypeAdapter(LocalDate.class, new CertificationCalenderControllerTest.LocalDateTimeSerializer());
		gson = gsonBuilder.setPrettyPrinting().create();
	}

	@Test
	@DisplayName("취득 자격증 조회 테스트")
	@WithMockUser
	void findAllByUserIdTest() throws Exception {
	    // Given
		UserCertificationResponse response = new UserCertificationResponse("testId", List.of(new UserCertificationInfo("정보처리기사", LocalDate.of(2024, 1, 1)), new UserCertificationInfo("정보처리산업기사", LocalDate.of(2024, 1, 2)), new UserCertificationInfo("정보보안기사", LocalDate.of(2024, 1, 3))));
		given(service.findAllByUserId("testId")).willReturn(response);

	    // When
		mockMvc.perform(
						get("/api/certifications/user-certification/{userId}", "testId")
								.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("userId").description("사용자 ID")
						),
						responseFields(
								fieldWithPath("userId").description("사용자 ID"),
								fieldWithPath("certificationInfos[].certificationName").description("자격증 이름"),
								fieldWithPath("certificationInfos[].acqDate").description("취득 날짜")
						)));

	    // Then
	}

	@Test
	@DisplayName("취득 자격증 저장 테스트 -실패(유효하지 않은 요청 / 회원 ID 없음)")
	@WithMockUser
	void saveUserCertificationTest1() throws Exception {
		// Given
		UserCertificationRequest request = new UserCertificationRequest("", "정보처리기사", LocalDate.of(2024, 1, 1));

		// When
		mockMvc.perform(
						post("/api/certifications/user-certification")
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request))
								.with(csrf()))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("요청자 ID"),
								fieldWithPath("certificationName").description("취득 자격증 이름"),
								fieldWithPath("acqDate").description("자격증 취득일자")
						),
						responseFields(
								fieldWithPath("msg").description("처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("취득 자격증 저장 테스트 -실패(유효하지 않은 요청 / 자격증 이름 없음)")
	@WithMockUser
	void saveUserCertificationTest2() throws Exception {
		// Given
		UserCertificationRequest request = new UserCertificationRequest("testId", "", LocalDate.of(2024, 1, 1));

		// When
		mockMvc.perform(
						post("/api/certifications/user-certification")
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request))
								.with(csrf()))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("요청자 ID"),
								fieldWithPath("certificationName").description("취득 자격증 이름"),
								fieldWithPath("acqDate").description("자격증 취득일자")
						),
						responseFields(
								fieldWithPath("msg").description("처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("취득 자격증 저장 테스트 -실패(유효하지 않은 요청 / 취득일자없음)")
	@WithMockUser
	void saveUserCertificationTest3() throws Exception {
		// Given
		UserCertificationRequest request = new UserCertificationRequest("testId", "정보처리기사", null);

		// When
		mockMvc.perform(
						post("/api/certifications/user-certification")
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request))
								.with(csrf()))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("요청자 ID"),
								fieldWithPath("certificationName").description("취득 자격증 이름")
						),
						responseFields(
								fieldWithPath("msg").description("처리 결과")
						)));

		// Then
	}

	@Test
	@DisplayName("취득 자격증 저장 테스트 -성공")
	@WithMockUser
	void saveUserCertificationTest4() throws Exception {
		// Given
		UserCertificationRequest request = new UserCertificationRequest("testId", "정보처리기사", LocalDate.of(2024, 1, 1));

		// When
		mockMvc.perform(
						post("/api/certifications/user-certification")
								.contentType(MediaType.APPLICATION_JSON)
								.content(gson.toJson(request))
								.with(csrf()))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("요청자 ID"),
								fieldWithPath("certificationName").description("취득 자격증 이름"),
								fieldWithPath("acqDate").description("자격증 취득일자")
						),
						responseFields(
								fieldWithPath("msg").description("처리 결과")
						)));

		// Then
	}
}
