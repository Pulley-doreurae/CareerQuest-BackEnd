package pulleydoreurae.careerquestbackend.certification.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationExamDateResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationPeriodResponse;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.service.CertificationService;

/**
 * @author : parkjihyeok
 * @since : 2024/05/27
 */
@WebMvcTest(CertificationController.class)
@AutoConfigureRestDocs
@DisplayName("일반 자격증 컨트롤러 테스트")
class CertificationControllerTest {

	@Autowired MockMvc mockMvc;

	@MockBean CertificationService certificationService;

	@Test
	@DisplayName("자격증 검색에 실패 (자격증 이름이 맞지않음)")
	@WithMockUser
	void findByNameTest1() throws Exception {
	    // Given
		doThrow(new IllegalArgumentException("자격증을 찾을 수 없음")).when(certificationService).findByName("정보처리기사");

	    // When
		mockMvc.perform(get("/api/certification/{certificationName}", "정보처리기사"))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("msg").description("실패원인")
						)));

	    // Then
	}

	@Test
	@DisplayName("자격증 검색 성공")
	@WithMockUser
	void findByNameTest2() throws Exception {
		// Given
		Certification certification = Certification.builder().certificationCode(10L).certificationName("정보처리기사").qualification("4년제").organizer("한국산업인력공단").registrationLink("https://www.hrdkorea.or.kr/").aiSummary("정보처리기사에 대한 AI 요약입니다.").build();
		CertificationResponse response = CertificationResponse.builder().certificationCode(10L).certificationName("정보처리기사").qualification("4년제").organizer("한국산업인력공단").registrationLink("https://www.hrdkorea.or.kr/").aiSummary("정보처리기사에 대한 AI 요약입니다.").build();
		List<CertificationPeriodResponse> periodResponses = response.getPeriodResponse();
		periodResponses.add(new CertificationPeriodResponse("정보처리기사", ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 7, 10), LocalDate.of(2024, 8, 10)));
		periodResponses.add(new CertificationPeriodResponse("정보처리기사", ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 9, 10), LocalDate.of(2024, 10, 10)));
		periodResponses.add(new CertificationPeriodResponse("정보처리기사", ExamType.FIRST_STAGE, 1002L, LocalDate.of(2024, 9, 10), LocalDate.of(2024, 10, 10)));
		periodResponses.add(new CertificationPeriodResponse("정보처리기사", ExamType.LAST_STAGE, 1002L, LocalDate.of(2024, 10, 10), LocalDate.of(2024, 11, 10)));
		List<CertificationExamDateResponse> examDateResponses = response.getExamDateResponses();
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.FIRST_STAGE, 1000L, LocalDate.of(2024, 10, 1)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.FIRST_STAGE, 1000L, LocalDate.of(2024, 10, 2)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.FIRST_STAGE, 1000L, LocalDate.of(2024, 10, 3)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.LAST_STAGE, 1000L, LocalDate.of(2024, 11, 1)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.LAST_STAGE, 1000L, LocalDate.of(2024, 11, 2)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.LAST_STAGE, 1000L, LocalDate.of(2024, 11, 3)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 11, 1)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 11, 2)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.FIRST_STAGE, 1001L, LocalDate.of(2024, 11, 3)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 12, 1)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 12, 2)));
		examDateResponses.add(new CertificationExamDateResponse("정보처리기사", ExamType.LAST_STAGE, 1001L, LocalDate.of(2024, 12, 3)));

		given(certificationService.findByName("정보처리기사")).willReturn(response);

		// When
		mockMvc.perform(get("/api/certification/{certificationName}", "정보처리기사"))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("certificationName").description("자격증이름")
						),
						responseFields(
								fieldWithPath("certificationCode").description("자격증 고유번호"),
								fieldWithPath("certificationName").description("자격증 이름"),
								fieldWithPath("qualification").description("자격요건"),
								fieldWithPath("organizer").description("주관처"),
								fieldWithPath("registrationLink").description("접수링크"),
								fieldWithPath("aiSummary").description("AI요약"),
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
