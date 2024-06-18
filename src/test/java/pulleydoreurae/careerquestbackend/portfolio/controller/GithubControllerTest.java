package pulleydoreurae.careerquestbackend.portfolio.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import pulleydoreurae.careerquestbackend.portfolio.domain.dto.response.FinalResponse;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.GitRepoInfo;
import pulleydoreurae.careerquestbackend.portfolio.service.GithubService;

@WebMvcTest(GithubController.class)
@AutoConfigureRestDocs
public class GithubControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	GithubService githubService;

	@Test
	@DisplayName("깃허브 로그인창으로 정상적으로 리다이렉트하는지 확인하는 테스트")
	@WithMockUser
	void githubGetRedirectTest() throws Exception {
		// Given

		// When
		mockMvc.perform(get("/api/login-github")
				.queryParam("userId", "testId"))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("userId").description("현재 요청을 보낸 유저의 아이디")
				)));

		// Then
	}

	@Test
	@DisplayName("깃허브 로그인 테스트 (유효하지 않은 코드)")
	@WithMockUser
	void githubInvalidCodeLoginTest() throws Exception {
		// Given
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user", "testId");

		// When
		mockMvc.perform(get("/api/login-github/code")
				.queryParam("code", "1234"))
			.andDo(print())
			.andExpect(status().is4xxClientError())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("code").description("깃허브 로그인에 성공하고 받은 코드가 유효하지 않은 경우")
				)));

		// Then
	}

	@Test
	@DisplayName("깃허브 로그인 테스트 (유효한 코드)")
	@WithMockUser
	void githubValidCodeLoginTest() throws Exception {
		// Given
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user", "testId");

		FinalResponse response = FinalResponse.builder()
			.gitRepoInfoList(List.of(
				GitRepoInfo.builder().name("레포이름1").project_url("레포이름1_링크").build(),
				GitRepoInfo.builder().name("레포이름2").project_url("레포이름2_링크").build(),
				GitRepoInfo.builder().name("레포이름3").project_url("레포이름3_링크").build()
			))
			.languages(Map.of("C", 44.1, "Java", 39.9, "Html", 16.0))
			.build();

		when(githubService.getToken(anyString(), anyString())).thenReturn("ValidAccessToken");
		when(githubService.getUserDetails(anyString(), anyString())).thenReturn("GithubUser");
		when(githubService.getRepoLists(any(), any(), any())).thenReturn(response);

		// When
		mockMvc.perform(get("/api/login-github/code")
				.queryParam("code", "123123")
				.session(session))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("code").description("구글 로그인에 성공하고 받은 코드")
				)
			));

		// Then
		verify(githubService).getToken(anyString(), anyString());
		verify(githubService).getUserDetails(anyString(), anyString());
	}

}
