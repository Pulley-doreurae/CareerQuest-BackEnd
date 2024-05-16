package pulleydoreurae.careerquestbackend.auth.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.careerquestbackend.auth.service.KakaoLoginService;
import pulleydoreurae.careerquestbackend.auth.service.NaverLoginService;
import pulleydoreurae.careerquestbackend.auth.service.UserAccessLogService;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NaverLoginController.class)
@AutoConfigureRestDocs    // REST Docs 를 사용하기 위해 추가
class NaverLoginControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private NaverLoginService naverLoginService;

	@MockBean
	UserAccessLogService userAccessLogService;

	@Test
	@DisplayName("네이버 로그인창으로 정상적으로 리다이렉트하는지 확인하는 테스트")
	@WithMockUser
	void NaverGetRedirectTest() throws Exception {
		// Given

		// When
		mockMvc.perform(get("/api/login-naver"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));

		// Then
	}

	@Test
	@DisplayName("네이버 로그인 테스트 (유효하지 않은 코드)")
	@WithMockUser
	void NaverInvalidCodeLoginTest() throws Exception {
		// Given

		// When
		mockMvc.perform(get("/api/login-naver/code")
						.queryParam("code", "1234")
						.queryParam("state", "RAMDOM_STATE"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("code").description("네이버 로그인에 성공하고 받은 코드가 유효하지 않은 경우"),
								parameterWithName("state").description("네이버 로그인에 성공하고 받은 상태")
						)));
		// Then
	}

	@Test
	@DisplayName("네이버 로그인 테스트 (유효하지 않은 상태)")
	@WithMockUser
	void NaverInvalidCodeLoginTest2() throws Exception {
		// Given

		// When
		mockMvc.perform(get("/api/login-naver/code")
						.queryParam("code", "1234")
						.queryParam("state", "non"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("code").description("네이버 로그인에 성공하고 받은 코드"),
								parameterWithName("state").description("네이버 로그인에 성공하고 받은 상태가 유효하지 않은 경우")
						)));
		// Then
	}

	@Test
	@DisplayName("네이버 로그인 테스트 (유효한 코드)")
	@WithMockUser
	void NaverValidCodeLoginTest() throws Exception {
		// Given
		JwtTokenResponse response = JwtTokenResponse.builder()
				.token_type("bearer")
				.access_token("accessToken")
				.expires_in(1111L)
				.refresh_token("refreshToken")
				.refresh_token_expires_in(3333L)
				.build();
		when(naverLoginService.getToken(anyString(), anyString(), anyString())).thenReturn("ValidAccessToken");
		when(naverLoginService.getUserDetails(anyString(), anyString())).thenReturn("ValidUserEmail");
		when(naverLoginService.login("ValidUserEmail"))
				.thenReturn(ResponseEntity.status(HttpStatus.OK)
						.body(response));

		// When
		mockMvc.perform(get("/api/login-naver/code")
						.queryParam("code", "123123")
						.queryParam("state", "RAMDOM_STATE"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token_type").exists())
				.andExpect(jsonPath("$.access_token").exists())
				.andExpect(jsonPath("$.expires_in").exists())
				.andExpect(jsonPath("$.refresh_token").exists())
				.andExpect(jsonPath("$.refresh_token_expires_in").exists())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("code").description("네이버 로그인에 성공하고 받은 코드"),
								parameterWithName("state").description("네이버 로그인에 성공하고 받은 상태")
						),
						responseFields(
								fieldWithPath("userId").description("로그인 시도한 id"),
								fieldWithPath("token_type").description("토큰 타입"),
								fieldWithPath("access_token").description("액세스 토큰 (현재는 임의의 값)"),
								fieldWithPath("expires_in").description("액세스 토큰 유효기간 (현재는 임의의 값)"),
								fieldWithPath("refresh_token").description("리프레시 토큰 (현재는 임의의 값)"),
								fieldWithPath("refresh_token_expires_in").description("리프레시 토큰 유효기간 (현재는 임의의 값)")
						)));
		;
		// Then
		verify(naverLoginService).getToken(anyString(), anyString(), anyString());
		verify(naverLoginService).getUserDetails(anyString(), anyString());
		verify(naverLoginService).login("ValidUserEmail");
	}
}
