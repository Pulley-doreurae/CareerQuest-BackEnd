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

import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.InterestedCertificationRequest;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;
import pulleydoreurae.careerquestbackend.certification.service.InterestedCertificationService;

/**
 * 관심자격증 Controller 테스트
 *
 * @author : parkjihyeok
 * @since : 2024/05/22
 */
@WebMvcTest(InterestedCertificationController.class)
@AutoConfigureRestDocs
@DisplayName("관심자격증 컨트롤러 테스트")
class InterestedCertificationControllerTest {

	@Autowired
	MockMvc mockMvc;
	@MockBean
	InterestedCertificationService service;
	@MockBean
	UserAccountRepository userAccountRepository;
	@MockBean
	CertificationRepository certificationRepository;

	Gson gson = new Gson();

	@Test
	@DisplayName("회원정보에 해당하는 관심자격증 리스트를 정상 출력하는지 테스트")
	@WithMockUser
	void findAllByUserId() throws Exception {
	    // Given
		Certification certification1 = Certification.builder().id(100L).certificationCode(1L).certificationName("정보처리기사").qualification("4년제").organizer("A").registrationLink("Link").aiSummary("Summary").build();
		Certification certification2 = Certification.builder().id(101L).certificationCode(2L).certificationName("정보보안기사").qualification("4년제").organizer("A").registrationLink("Link").aiSummary("Summary").build();
		given(service.findAllByUserId("testId")).willReturn(List.of(certification1, certification2));

	    // When
		mockMvc.perform(get("/api/certification/interest/{userId}", "testId"))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("userId").description("회원id")
						),
						responseFields(
								fieldWithPath("[].certificationCode").description("자격증 코드"),
								fieldWithPath("[].certificationName").description("자격증 이름"),
								fieldWithPath("[].qualification").description("응시 자격"),
								fieldWithPath("[].organizer").description("주관처"),
								fieldWithPath("[].registrationLink").description("접수링크"),
								fieldWithPath("[].aiSummary").description("AI요약")
						)));

	    // Then
	}

	@Test
	@DisplayName("관심자격증 추가")
	@WithMockUser
	void changeInterestedCertification1() throws Exception {
	    // Given
		InterestedCertificationRequest request = new InterestedCertificationRequest("testId", "정보처리기사", false);

		// When
		mockMvc.perform(post("/api/certification/interest")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("회원id"),
								fieldWithPath("certificationName").description("자격증이름"),
								fieldWithPath("isInterested").description("관심자격증 여부 (관심상태라면 true, 아니라면 false)")
						),
						responseFields(
								fieldWithPath("msg").description("처리결과")
						)));

	    // Then
		verify(service).saveInterestedCertification("testId", "정보처리기사");
	}

	@Test
	@DisplayName("관심자격증 제거")
	@WithMockUser
	void changeInterestedCertification2() throws Exception {
		// Given
		InterestedCertificationRequest request = new InterestedCertificationRequest("testId", "정보처리기사", true);

		// When
		mockMvc.perform(post("/api/certification/interest")
						.contentType(MediaType.APPLICATION_JSON)
						.with(csrf())
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("userId").description("회원id"),
								fieldWithPath("certificationName").description("자격증이름"),
								fieldWithPath("isInterested").description("관심자격증 여부 (관심상태라면 true, 아니라면 false)")
						),
						responseFields(
								fieldWithPath("msg").description("처리결과")
						)));

		// Then
		verify(service).deleteInterestedCertification("testId", "정보처리기사");
	}
}
