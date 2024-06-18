package pulleydoreurae.careerquestbackend.portfolio.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.auth.service.UserAccountService;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.request.UpdateAboutMeRequest;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.AboutMeResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.GitRepoInfoResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.GitRepoLanguageResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.RepoInfoListResponse;
import pulleydoreurae.careerquestbackend.portfolio.service.PortfolioService;

@WebMvcTest(PortfolioController.class)
@AutoConfigureRestDocs    // REST Docs 를 사용하기 위해 추가
public class PortfolioControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	PortfolioService portfolioService;
	@MockBean
	UserAccountService userAccountService;

	Gson gson = new Gson();

	@Test
	@DisplayName("포트폴리오 자기소개 내용 불러오기 성공")
	@WithMockUser
	void getUserAboutMeSuccess() throws Exception {
		// Given

		given(portfolioService.getUserAboutMe(any())).willReturn(
			AboutMeResponse.builder().content("자기소개 전문").userId("testId").build());

		// When
		mockMvc.perform(get("/api/portfolio/selfIntro")
				.queryParam("userId", "testId")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				queryParameters( // path 요청 방식
					parameterWithName("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("content").description("자기소개 내용"),
					fieldWithPath("userId").description("요청한 유저")
				)));

		// Then
	}

	@Test
	@DisplayName("포트폴리오 자기소개 내용 불러오기 실패 (자기소개가 없음)")
	@WithMockUser
	void getUserAboutMeFailed() throws Exception {
		// Given
		doThrow(new UsernameNotFoundException("저장된 자기소개가 없습니다")).when(portfolioService).getUserAboutMe(any());

		// When
		mockMvc.perform(get("/api/portfolio/selfIntro")
				.queryParam("userId", "testId")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				queryParameters( // path 요청 방식
					parameterWithName("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("포트폴리오 자기소개 내용 업데이트 성공")
	@WithMockUser
	void updateUserAboutMeSuccess() throws Exception {
		// Given
		UpdateAboutMeRequest updateAboutMeRequest = UpdateAboutMeRequest.builder()
			.userId("testId")
			.aboutMe("새로운 자기소개 내용")
			.build();

		// When
		mockMvc.perform(post("/api/portfolio/selfIntro")
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(updateAboutMeRequest))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("요청한 유저"),
					fieldWithPath("aboutMe").description("요청한 자기소개 내용")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
		verify(portfolioService).updateUserAboutMe(any(), any());
	}

	@Test
	@DisplayName("포트폴리오 자기소개 내용 업데이트 실패 (유저가 없음)")
	@WithMockUser
	void updateUserAboutMeFailed() throws Exception {
		// Given
		UpdateAboutMeRequest updateAboutMeRequest = UpdateAboutMeRequest.builder()
			.userId("tes")
			.aboutMe("새로운 자기소개 내용")
			.build();

		doThrow(new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.")).when(userAccountService)
			.findUserByUserId(any());

		// When
		mockMvc.perform(post("/api/portfolio/selfIntro")
				.contentType(MediaType.APPLICATION_JSON)
				.content(gson.toJson(updateAboutMeRequest))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("요청한 유저"),
					fieldWithPath("aboutMe").description("요청한 자기소개 내용")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("유저의 레포지토리를 가져오기 성공 (내용 있음)")
	@WithMockUser
	void getUserReposSuccess1() throws Exception {
		// Given

		RepoInfoListResponse response = RepoInfoListResponse.builder()
			.gitRepoInfoList(List.of(
				GitRepoInfoResponse.builder().repoName("레포이름1").repoUrl("레포이름1_링크").build(),
				GitRepoInfoResponse.builder().repoName("레포이름2").repoUrl("레포이름2_링크").build(),
				GitRepoInfoResponse.builder().repoName("레포이름3").repoUrl("레포이름3_링크").build()))
			.gitRepoLanguageList(List.of(
				GitRepoLanguageResponse.builder().language("C").count(44.1).build(),
				GitRepoLanguageResponse.builder().language("Java").count(39.9).build(),
				GitRepoLanguageResponse.builder().language("Html").count(16.0).build()
			))
			.build();

		given(portfolioService.getUserRepos(any())).willReturn(response);

		// When
		mockMvc.perform(get("/api/portfolio/repos")
				.queryParam("userId", "testId")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				queryParameters( // path 요청 방식
					parameterWithName("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("gitRepoInfoList").description("깃허브 레포지토리 리스트"),
					fieldWithPath("gitRepoInfoList[].repoName").description("레포지토리 이름"),
					fieldWithPath("gitRepoInfoList[].repoUrl").description("레포지토리 링크"),
					fieldWithPath("gitRepoLanguageList").description("언어 상위 3개 리스트"),
					fieldWithPath("gitRepoLanguageList[].language").description("프로그래밍 언어"),
					fieldWithPath("gitRepoLanguageList[].count").description("3개의 언어 중 차지하는 비율(퍼센트)")
				)));

		// Then
	}

	@Test
	@DisplayName("유저의 레포지토리를 가져오기 성공 (내용 없음)")
	@WithMockUser
	void getUserReposSuccess2() throws Exception {
		// Given
		given(portfolioService.getUserRepos(any())).willReturn(null);

		// When
		mockMvc.perform(get("/api/portfolio/repos")
				.queryParam("userId", "testId")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				queryParameters( // path 요청 방식
					parameterWithName("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("유저의 레포지토리를 가져오기 실패 (유저가 없음)")
	@WithMockUser
	void getUserReposFailed() throws Exception {
		// Given

		doThrow(new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.")).when(userAccountService)
			.findUserByUserId(any());

		// When
		mockMvc.perform(get("/api/portfolio/repos")
				.queryParam("userId", "testId")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				queryParameters( // path 요청 방식
					parameterWithName("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

}
