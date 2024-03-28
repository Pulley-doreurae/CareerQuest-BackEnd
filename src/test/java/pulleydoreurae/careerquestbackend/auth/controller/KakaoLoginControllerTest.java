package pulleydoreurae.careerquestbackend.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import pulleydoreurae.careerquestbackend.auth.service.UserAccessLogService;

/**
 * 카카오 로그인 테스트 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/01/24
 */
@WebMvcTest(KakaoLoginController.class)
@AutoConfigureRestDocs    // REST Docs 를 사용하기 위해 추가
class KakaoLoginControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private KakaoLoginService kakaoLoginService;

	@MockBean
	UserAccessLogService userAccessLogService;

	@Test
	@DisplayName("카카오 로그인창으로 정상적으로 리다이렉트하는지 확인하는 테스트")
	@WithMockUser
	void GetRedirectTest() throws Exception {
		// Given

		// When
		mockMvc.perform(get("/api/login-kakao"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint())));

		// Then
	}

	@Test
	@DisplayName("카카오 로그인 테스트 (유효하지 않은 코드)")
	@WithMockUser
	void KakaoInvalidCodeLoginTest() throws Exception {
		// Given

		// When
		mockMvc.perform(get("/api/login-kakao/code")
						.queryParam("code", "1234"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("code").description("카카오 로그인에 성공하고 받은 코드가 유효하지 않은 경우")
						)));

		// Then
	}

	@Test
	@DisplayName("카카오 로그인 테스트 (유효한 코드)")
	@WithMockUser
	void KakaoValidCodeLoginTest() throws Exception {
		// Given
		JwtTokenResponse response = JwtTokenResponse.builder()
				.token_type("bearer")
				.access_token("accessToken")
				.expires_in(1111L)
				.refresh_token("refreshToken")
				.refresh_token_expires_in(3333L)
				.build();
		when(kakaoLoginService.getToken(anyString(), anyString())).thenReturn("ValidAccessToken");
		when(kakaoLoginService.getUserDetails(anyString(), anyString())).thenReturn("ValidUserEmail");
		when(kakaoLoginService.login("ValidUserEmail"))
				.thenReturn(ResponseEntity.status(HttpStatus.OK)
						.body(response));

		// When
		mockMvc.perform(get("/api/login-kakao/code")
						.queryParam("code", "123123"))
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
								parameterWithName("code").description("카카오 로그인에 성공하고 받은 코드")
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
		verify(kakaoLoginService).getToken(anyString(), anyString());
		verify(kakaoLoginService).getUserDetails(anyString(), anyString());
		verify(kakaoLoginService).login("ValidUserEmail");
	}
}
