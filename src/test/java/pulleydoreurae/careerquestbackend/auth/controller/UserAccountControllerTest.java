package pulleydoreurae.careerquestbackend.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pulleydoreurae.careerquestbackend.auth.domain.MBTI;
import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.ShowUserDetailsToChangeRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserAccountRegisterRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserCareerDetailsRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserChangeEmailRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserDeleteRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserFindPasswordChangeRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserIdRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserMBTIRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserPasswordUpdateRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserTechnologyStackRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.ShowCareersResponse;
import pulleydoreurae.careerquestbackend.auth.domain.entity.Careers;
import pulleydoreurae.careerquestbackend.auth.domain.entity.ChangeUserEmail;
import pulleydoreurae.careerquestbackend.auth.domain.entity.TechnologyStack;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserCareerDetails;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserTechnologyStack;
import pulleydoreurae.careerquestbackend.auth.repository.TechnologyStackRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserTechnologyStackRepository;
import pulleydoreurae.careerquestbackend.auth.service.UserAccountService;
import pulleydoreurae.careerquestbackend.mail.repository.EmailAuthenticationRepository;
import pulleydoreurae.careerquestbackend.mail.repository.UserInfoUserIdRepository;
import pulleydoreurae.careerquestbackend.mail.service.MailService;

/**
 * 회원가입 컨트롤러를 테스트하는 클래스
 * UserAccountRepository 와 BCryptPasswordEncoder 를 MockBean 으로 설정해야 UserAccountController 를 정상적으로 불러와 테스트할 수 있다.
 * `@WebMvcTest` 를 사용하는 경우 시큐리티 설정파일도 전부 불러오지 않으므로 `@WithMockUser` 와 `csrf()` 설정을 해주어야 정상적으로 테스트할 수 있다.
 */
@WebMvcTest(UserAccountController.class)
@AutoConfigureRestDocs    // REST Docs 를 사용하기 위해 추가
class UserAccountControllerTest {

	Gson gson = new Gson();
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private UserAccountRepository userAccountRepository;
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@MockBean
	private MailService mailService;
	@MockBean
	private UserTechnologyStackRepository userTechnologyStackRepository;
	@MockBean
	private EmailAuthenticationRepository emailAuthenticationRepository;
	@MockBean
	private TechnologyStackRepository technologyStackRepository;
	@MockBean
	private UserAccountService userAccountService;
	@MockBean
	private UserInfoUserIdRepository userIdRepository;

	public static Attribute field(String key, String value) {
		return new Attribute(key, value);
	}

	@Test
	@DisplayName("회원가입 테스트")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest1() throws Exception {
		// Given
		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd123");
		request.setPassword("wjsansrk");
		request.setUserName("홍길동");
		request.setPhoneNum("010-1111-2222");
		request.setEmail("hgd123@naver.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.userName").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.birth").exists())
			.andExpect(jsonPath("$.gender").exists())
			.andExpect(jsonPath("$.isMarketed").exists())
			.andDo(print())
			// Spring REST Docs
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
		// 이메일 전송 메서드가 동작했는지 확인
		verify(mailService).emailAuthentication(request.getUserId(), request.getUserName(), request.getPhoneNum(),
			request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()), request.getBirth(),
			request.getGender(), request.getIsMarketed());
	}

	@Test
	@DisplayName("회원가입 중복 테스트 (id)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest2() throws Exception {
		// Given
		given(userAccountRepository.existsByUserId(any())).willReturn(true);

		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd123");
		request.setPassword("wjsansrk");
		request.setUserName("홍길동");
		request.setPhoneNum("010-1111-2222");
		request.setEmail("hgd123@naver.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.userName").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.birth").exists())
			.andExpect(jsonPath("$.gender").exists())
			.andExpect(jsonPath("$.isMarketed").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
		// id 로 검색하는 메서드가 동작했는지 확인
		verify(userAccountRepository).existsByUserId(any());
	}

	@Test
	@DisplayName("회원가입 중복 테스트 (email)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest3() throws Exception {
		// Given
		given(userAccountRepository.existsByEmail(any())).willReturn(true);

		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd123");
		request.setPassword("wjsansrk");
		request.setUserName("홍길동");
		request.setPhoneNum("010-1111-2222");
		request.setEmail("hgd123@naver.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.userName").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.birth").exists())
			.andExpect(jsonPath("$.gender").exists())
			.andExpect(jsonPath("$.isMarketed").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
		// email 로 검색하는 메서드가 동작했는지 확인
		verify(userAccountRepository).existsByEmail(any());
	}

	@Test
	@DisplayName("유효성 검사를 통과못하는 테스트케이스 (id)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest4() throws Exception {
		// Given
		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd1");
		request.setPassword("wjsansrk");
		request.setUserName("홍길동");
		request.setPhoneNum("010-1111-2222");
		request.setEmail("hgd123@naver.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.userName").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.birth").exists())
			.andExpect(jsonPath("$.gender").exists())
			.andExpect(jsonPath("$.isMarketed").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("유효성 검사를 통과못하는 테스트케이스 (password)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest5() throws Exception {
		// Given
		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd123");
		request.setPassword("aaa");
		request.setUserName("홍길동");
		request.setPhoneNum("010-1111-2222");
		request.setEmail("hgd123@naver.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.userName").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.birth").exists())
			.andExpect(jsonPath("$.gender").exists())
			.andExpect(jsonPath("$.isMarketed").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("유효성 검사를 통과못하는 테스트케이스 (email)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest6() throws Exception {
		// Given
		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd123");
		request.setPassword("wjsansrk");
		request.setUserName("홍길동");
		request.setPhoneNum("010-1111-2222");
		request.setEmail("hgd123.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.userName").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.birth").exists())
			.andExpect(jsonPath("$.gender").exists())
			.andExpect(jsonPath("$.isMarketed").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("유효성 검사를 통과못하는 테스트케이스 (phoneNum)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest7() throws Exception {
		// Given
		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd123");
		request.setPassword("wjsansrk");
		request.setUserName("홍길동");
		request.setPhoneNum("010-11-22");
		request.setEmail("hgd123@naver.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.userName").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.birth").exists())
			.andExpect(jsonPath("$.gender").exists())
			.andExpect(jsonPath("$.isMarketed").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("아이디 중복확인 (중복 X)")
	@WithMockUser
	void duplicateCheckIdSuccess() throws Exception {
		// Given

		// When
		mockMvc.perform(
				get("/api/users/username/{userId}", "testId")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.field").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("userId").description("확인할 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("field").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("아이디 중복확인 (중복 O)")
	@WithMockUser
	void duplicateCheckIdFail() throws Exception {
		// Given
		given(userAccountRepository.existsByUserId(any())).willReturn(true);

		// When
		mockMvc.perform(
				get("/api/users/username/{userId}", "testId")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.field").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("userId").description("확인할 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("field").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("이메일 중복확인 (중복 X)")
	@WithMockUser
	void duplicateCheckEmailSuccess() throws Exception {
		// Given

		// When
		mockMvc.perform(
				get("/api/users/email/{email}", "test@email.com")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.field").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("email").description("확인할 이메일")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("field").description("요청한 이메일"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("이메일 중복확인 (중복 O)")
	@WithMockUser
	void duplicateCheckEmailFail() throws Exception {
		// Given
		given(userAccountRepository.existsByEmail(any())).willReturn(true);

		// When
		mockMvc.perform(
				get("/api/users/email/{email}", "test@email.com")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.field").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("email").description("확인할 이메일")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("field").description("요청한 이메일"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("추가정보 입력하는 창 노출 확인 (노출 여부 : x)")
	@WithMockUser
	void addInfoCheckFalseTest() throws Exception {
		// Given
		given(userAccountService.isAddInfoShow(any())).willReturn(false);

		// When
		mockMvc.perform(
				get("/api/users/details/{username}", "testId")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("username").description("확인할 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));
		// Then

	}

	@Test
	@DisplayName("추가정보 입력하는 창 노출 확인 (노출 여부 : o)")
	@WithMockUser
	void addInfoCheckTrueTest() throws Exception {
		// Given
		given(userAccountService.isAddInfoShow(any())).willReturn(true);

		// When
		mockMvc.perform(
				get("/api/users/details/{username}", "testId")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("username").description("확인할 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));
		// Then

	}

	@Test
	@DisplayName("회원직무 종류 리스트 조회 성공 (대분류)")
	@WithMockUser
	void showMajorCareersSuccessTest() throws Exception {
		// Given
		Careers majorCareers1 = Careers.builder()
			.careerId(0L)
			.categoryName("사업관리")
			.categoryType("대분류")
			.categoryImage("/major/images/0")
			.build();
		Careers majorCareers2 = Careers.builder()
			.careerId(1L)
			.categoryName("경영·회계·사무")
			.categoryType("대분류")
			.categoryImage("/major/images/1")
			.build();

		List<ShowCareersResponse> majorList = Arrays.asList(ShowCareersResponse.builder()
				.categoryName(majorCareers1.getCategoryName())
				.categoryImage(majorCareers1.getCategoryImage())
				.build(),
			ShowCareersResponse.builder()
				.categoryName(majorCareers2.getCategoryName())
				.categoryImage(majorCareers2.getCategoryImage())
				.build());

		given(userAccountService.getCareerList(any(), any())).willReturn(majorList);

		// When
		mockMvc.perform(
				get("/api/users/details/careers")
					.queryParam("major", "")
					.queryParam("middle", "")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // Json 요청 방식
					parameterWithName("major").description("대분류"),
					parameterWithName("middle").description("중분류")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("lists").description("요청한 카테고리 리스트"),
					fieldWithPath("lists[].categoryName").description("카테고리 이름"),
					fieldWithPath("lists[].categoryImage").description("카테고리 이미지"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원직무 종류 리스트 조회 성공 (중분류)")
	@WithMockUser
	void showMiddleCareersSuccessTest() throws Exception {
		// Given
		Careers majorCareers1 = Careers.builder()
			.careerId(0L)
			.categoryName("사업관리")
			.categoryType("대분류")
			.categoryImage("/major/images/0")
			.build();

		Careers middleCareers1 = Careers.builder()
			.careerId(1L)
			.categoryName("사업관리")
			.categoryType("중분류")
			.categoryImage("/middle/images/0")
			.parent(majorCareers1)
			.build();
		Careers middleCareers2 = Careers.builder()
			.careerId(2L)
			.categoryName("기획사무")
			.categoryType("중분류")
			.categoryImage("/middle/images/1")
			.parent(majorCareers1)
			.build();

		List<ShowCareersResponse> middleList = Arrays.asList(ShowCareersResponse.builder()
				.categoryName(middleCareers1.getCategoryName())
				.categoryImage(middleCareers1.getCategoryImage())
				.build(),
			ShowCareersResponse.builder()
				.categoryName(middleCareers2.getCategoryName())
				.categoryImage(middleCareers2.getCategoryImage())
				.build());

		given(userAccountService.getCareerList(any(), any())).willReturn(middleList);

		// When
		mockMvc.perform(
				get("/api/users/details/careers")
					.queryParam("major", "사업관리")
					.queryParam("middle", "")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // query-parameters 요청 방식
					parameterWithName("major").description("대분류"),
					parameterWithName("middle").description("중분류")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("lists").description("요청한 카테고리 리스트"),
					fieldWithPath("lists[].categoryName").description("카테고리 이름"),
					fieldWithPath("lists[].categoryImage").description("카테고리 이미지"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원직무 종류 리스트 조회 성공 (소분류)")
	@WithMockUser
	void showSmallCareersSuccessTest() throws Exception {
		// Given
		Careers majorCareers1 = Careers.builder()
			.careerId(0L)
			.categoryName("사업관리")
			.categoryType("대분류")
			.categoryImage("/major/images/0")
			.build();

		Careers middleCareers1 = Careers.builder()
			.careerId(1L)
			.categoryName("사업관리")
			.categoryType("중분류")
			.categoryImage("/middle/images/0")
			.parent(majorCareers1)
			.build();

		Careers smallCareers1 = Careers.builder()
			.careerId(2L)
			.categoryName("경영기획")
			.categoryType("소분류")
			.categoryImage("/small/images/0")
			.parent(middleCareers1)
			.build();
		Careers smallCareers2 = Careers.builder()
			.careerId(3L)
			.categoryName("홍보·광고")
			.categoryType("소분류")
			.categoryImage("/small/images/1")
			.parent(middleCareers1)
			.build();

		List<ShowCareersResponse> smallList = Arrays.asList(ShowCareersResponse.builder()
				.categoryName(smallCareers1.getCategoryName())
				.categoryImage(smallCareers1.getCategoryImage())
				.build(),
			ShowCareersResponse.builder()
				.categoryName(smallCareers2.getCategoryName())
				.categoryImage(smallCareers2.getCategoryImage())
				.build());

		given(userAccountService.getCareerList(any(), any())).willReturn(smallList);

		// When
		mockMvc.perform(
				get("/api/users/details/careers")
					.queryParam("major", "사업관리")
					.queryParam("middle", "기획사무")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // query-parameters 요청 방식
					parameterWithName("major").description("대분류"),
					parameterWithName("middle").description("중분류")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("lists").description("요청한 카테고리 리스트"),
					fieldWithPath("lists[].categoryName").description("카테고리 이름"),
					fieldWithPath("lists[].categoryImage").description("카테고리 이미지"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));
		// Then

	}

	@Test
	@DisplayName("회원직무 종류 리스트 조회 실패 ( 잘못된 회원직무 종류 제목 전송 )")
	@WithMockUser
	void showCareersFailTest() throws Exception {
		// Given
		given(userAccountService.getCareerList(any(), any())).willReturn(List.of());

		// When
		mockMvc.perform(
				get("/api/users/details/careers")
					.queryParam("major", "hi")
					.queryParam("middle", "hi")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // query-parameters 요청 방식
					parameterWithName("major").description("대분류"),
					parameterWithName("middle").description("중분류")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("").ignored(),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원직무 추가 실패 (사용자 정보를 찾을 수 없음)")
	@WithMockUser
	void addCareerFailTest() throws Exception {
		// Given
		given(userAccountService.findUserByUserId(any())).willReturn(null);
		UserCareerDetailsRequest userCareerDetailsRequest = new UserCareerDetailsRequest();
		userCareerDetailsRequest.setUserId("testId");
		userCareerDetailsRequest.setSmallCategory("경영기획");
		// When
		mockMvc.perform(
				post("/api/users/details/careers")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userCareerDetailsRequest))
					.with(csrf())
			)
			.andExpect(status().isBadRequest())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(    // Json 형식
					fieldWithPath("userId").description("사용자 아이디")
						.attributes(field("constraints", "String")),
					fieldWithPath("smallCategory").description("소분류")
						.attributes(field("constraints", "String"))
				),
				responseFields(
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
		verify(userAccountService).findUserByUserId(any());
	}

	@Test
	@DisplayName("회원직무 추가 실패 (유효성 검사 실패)")
	@WithMockUser
	void addCareerFailTest2() throws Exception {
		// Given
		UserCareerDetailsRequest userCareerDetailsRequest = new UserCareerDetailsRequest();
		userCareerDetailsRequest.setUserId("Id ");
		userCareerDetailsRequest.setSmallCategory("경영기획");
		// When
		mockMvc.perform(
				post("/api/users/details/careers")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userCareerDetailsRequest))
					.with(csrf())
			)
			.andExpect(status().isBadRequest())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(    // Json 형식
					fieldWithPath("userId").description("사용자 아이디")
						.attributes(field("constraints", "String")),
					fieldWithPath("smallCategory").description("소분류")
						.attributes(field("constraints", "String"))
				),
				responseFields(
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원직무 추가 성공")
	@WithMockUser
	void addCareerSuccessTest() throws Exception {
		// Given
		UserCareerDetailsRequest userCareerDetailsRequest = new UserCareerDetailsRequest();
		userCareerDetailsRequest.setUserId("testId");
		userCareerDetailsRequest.setSmallCategory("경영기획");

		given(userAccountService.findUserByUserId(any())) // 사용자 id가 들어왔다면
			.willReturn(new UserAccount());

		// When
		mockMvc.perform(
				post("/api/users/details/careers")
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userCareerDetailsRequest))
					.with(csrf())
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(    // Json 형식
					fieldWithPath("userId").description("사용자 아이디")
						.attributes(field("constraints", "String")),
					fieldWithPath("smallCategory").description("소분류")
						.attributes(field("constraints", "String"))
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
		verify(userAccountService).findUserByUserId(any());
	}

	@Test
	@DisplayName("기술스택 검색 성공 (키워드 : Java)")
	@WithMockUser
	void showStacksSuccessTest() throws Exception {
		// Given
		TechnologyStack tech1 = TechnologyStack.builder()
			.id(0L)
			.stackName("Javascript")
			.description("자바스크립트")
			.stackImage("/src/image/tech/0")
			.build();
		TechnologyStack tech2 = TechnologyStack.builder()
			.id(1L)
			.stackName("Java")
			.description("자바")
			.stackImage("/src/image/tech/1")
			.build();

		List<TechnologyStack> technologyStackList = new ArrayList<>();
		technologyStackList.add(tech1);
		technologyStackList.add(tech2);

		given(userAccountService.getTechnologyStackByKeyword(anyString())).willReturn(technologyStackList);

		// When

		mockMvc.perform(
				get("/api/users/details/stacks")
					.queryParam("keyword", "Java")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("keyword").description("검색 키워드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("lists").description("요청한 기술스택 리스트"),
					fieldWithPath("lists[].id").description("기술스택 id"),
					fieldWithPath("lists[].stackName").description("기술스택 이름"),
					fieldWithPath("lists[].description").description("기술스택 설명"),
					fieldWithPath("lists[].stackImage").description("기술스택 이미지"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
		technologyStackRepository.deleteAll();
	}

	@Test
	@DisplayName("기술스택 검색 성공 (키워드에 들어있는 값이 없음)")
	@WithMockUser
	void showStacksFailTest() throws Exception {
		// Given
		given(userAccountService.getTechnologyStackByKeyword(anyString())).willReturn(null);

		// When
		mockMvc.perform(
				get("/api/users/details/stacks")
					.queryParam("keyword", "")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").isEmpty())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("keyword").description("검색 키워드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("lists").description("요청한 기술스택 리스트"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
		technologyStackRepository.deleteAll();
	}

	@Test
	@DisplayName("기술스택 추가 실패 (사용자 정보를 찾을 수 없음)")
	@WithMockUser
	void addStacksFailTest() throws Exception {
		// Given
		UserTechnologyStackRequest stackRequest = new UserTechnologyStackRequest();
		stackRequest.setUserId("testId");
		List<String> stacks = new ArrayList<>();
		stacks.add("1L");
		stacks.add("2L");
		stacks.add("3L");
		stacks.add("4L");
		stackRequest.setStacks(stacks);

		// When
		mockMvc.perform(
				post("/api/users/details/stacks")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(stackRequest))
			)
			.andExpect(status().isBadRequest())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(    // Json 형식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("stacks").description("추가할 스택들의 id 값 (List 형식)")
				),
				responseFields(        // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
		verify(userAccountService).findUserByUserId(any());

	}

	@Test
	@DisplayName("기술스택 추가 실패 (유효성 검사 실패)")
	@WithMockUser
	void addStacksFailTest2() throws Exception {
		// Given
		UserTechnologyStackRequest stackRequest = new UserTechnologyStackRequest();
		stackRequest.setUserId("Id ");
		List<String> stacks = new ArrayList<>();
		stacks.add("1L");
		stacks.add("2L");
		stacks.add("3L");
		stacks.add("4L");
		stackRequest.setStacks(stacks);

		// When
		mockMvc.perform(
				post("/api/users/details/stacks")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(stackRequest))
			)
			.andExpect(status().isBadRequest())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(    // Json 형식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("stacks").description("추가할 스택들의 id 값 (List 형식)")
				),
				responseFields(     // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then

	}

	@Test
	@DisplayName("기술스택 추가 성공")
	@WithMockUser
	void addStacksSuccessTest() throws Exception {
		// Given
		UserTechnologyStackRequest stackRequest = new UserTechnologyStackRequest();
		stackRequest.setUserId("testId");
		List<String> stacks = new ArrayList<>();
		stacks.add("1L");
		stacks.add("2L");
		stacks.add("3L");
		stacks.add("4L");
		stackRequest.setStacks(stacks);
		given(userAccountService.findUserByUserId(any())).willReturn(new UserAccount());

		// When
		mockMvc.perform(
				post("/api/users/details/stacks")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(stackRequest))
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(    // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("stacks").description("추가할 스택들의 id 값 (List 형식)")
				),
				responseFields(        // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
		verify(userAccountService).findUserByUserId(any());
		verify(userAccountRepository).save(any());
		// 4개의 리스트 내용이 전부 저장되는지 검사
		verify(userTechnologyStackRepository, times(4)).save(any());
	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크 발송 성공")
	@WithMockUser
	void sendFindPasswordLinkSuccess() throws Exception {
		// Given
		UserAccount userAccount = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		String userId = "user_1";
		UserIdRequest userIdRequest = new UserIdRequest();
		userIdRequest.setUserId(userId);

		given(userAccountService.findUserByUserId(userId)).willReturn(userAccount);

		// When
		mockMvc.perform(
				post("/api/users/help/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userIdRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
		verify(userAccountService).findPassword(userAccount.getUserId(), userAccount.getEmail());
	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크 발송 실패 (id에 해당하는 유저가 없음)")
	@WithMockUser
	void sendFindPasswordLinkFailed() throws Exception {
		// Given
		String userId = "user_1";
		UserIdRequest userIdRequest = new UserIdRequest();
		userIdRequest.setUserId(userId);

		given(userAccountService.findUserByUserId(userId)).willReturn(null);

		// When
		mockMvc.perform(
				post("/api/users/help/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userIdRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(  // Json 형식
					fieldWithPath("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크 발송 실패 (유효성 검사 실패)")
	@WithMockUser
	void sendFindPasswordLinkFailed2() throws Exception {
		// Given
		String userId = "id ";
		UserIdRequest userIdRequest = new UserIdRequest();
		userIdRequest.setUserId(userId);

		// When
		mockMvc.perform(
				post("/api/users/help/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userIdRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 형식
					fieldWithPath("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크 접속 성공")
	@WithMockUser
	void successPasswordLinkAccess() throws Exception {
		// Given
		String test_uuid = "0123-4567-89AB-CDEF";

		given(userAccountService.checkUserIdByUuid(test_uuid)).willReturn("user_1");

		// When
		mockMvc.perform(
				get("/api/users/help/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("uuid").description("사용자 식별 코드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then

	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크 접속 실패 (uuid가 없음)")
	@WithMockUser
	void failedPasswordLinkAccess() throws Exception {
		// Given
		String test_uuid = "0123-4567-89AB-CDEF";

		given(userAccountService.checkUserIdByUuid(test_uuid)).willReturn(null);

		// When
		mockMvc.perform(
				get("/api/users/help/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식 방식
					parameterWithName("uuid").description("사용자 식별 코드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크로 비밀번호 변경 성공")
	@WithMockUser
	void successPasswordChange() throws Exception {
		// Given
		String test_uuid = "0123-4567-89AB-CDEF";

		UserAccount userAccount = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserFindPasswordChangeRequest userFindPasswordChangeRequest = new UserFindPasswordChangeRequest();

		userFindPasswordChangeRequest.setPassword1("changePassword");
		userFindPasswordChangeRequest.setPassword2("changePassword");

		given(userAccountService.checkUserIdByUuid(test_uuid)).willReturn(userAccount.getUserId());

		// When
		mockMvc.perform(
				post("/api/users/help/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userFindPasswordChangeRequest))
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("uuid").description("사용자 식별 코드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
		verify(userAccountService).updatePassword(userAccount.getUserId(), bCryptPasswordEncoder.encode(
			userFindPasswordChangeRequest.getPassword1()));
		verify(userAccountService).deleteHelpUser(test_uuid);
	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크로 비밀번호 변경 실패 (요청한 uuid에 맞는 userId가 없음)")
	@WithMockUser
	void failedPasswordChange1() throws Exception {
		// Given
		String test_uuid = "0123-4567-89AB-CDEF";

		UserFindPasswordChangeRequest userFindPasswordChangeRequest = new UserFindPasswordChangeRequest();

		userFindPasswordChangeRequest.setPassword1("changePassword");
		userFindPasswordChangeRequest.setPassword2("changePassword");

		given(userAccountService.checkUserIdByUuid(test_uuid)).willReturn(null);

		// When
		mockMvc.perform(
				post("/api/users/help/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userFindPasswordChangeRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("uuid").description("사용자 식별 코드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자").ignored(),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then

	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크로 비밀번호 변경 실패 (비밀번호 입력 유효성 검사 실패)")
	@WithMockUser
	void failedPasswordChange2() throws Exception {
		// Given
		String test_uuid = "0123-4567-89AB-CDEF";

		UserAccount userAccount = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserFindPasswordChangeRequest userFindPasswordChangeRequest = new UserFindPasswordChangeRequest();

		userFindPasswordChangeRequest.setPassword1("pass");
		userFindPasswordChangeRequest.setPassword2("pass");

		given(userAccountService.checkUserIdByUuid(test_uuid)).willReturn(userAccount.getUserId());

		// When
		mockMvc.perform(
				post("/api/users/help/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userFindPasswordChangeRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").isEmpty())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("uuid").description("사용자 식별 코드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then

	}

	@Test
	@DisplayName("회원 비밀번호 찾기 링크로 비밀번호 변경 실패 (비밀번호와 비밀번호 확인이 서로 일치하지 않음)")
	@WithMockUser
	void failedPasswordChange3() throws Exception {
		// Given
		String test_uuid = "0123-4567-89AB-CDEF";

		UserAccount userAccount = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserFindPasswordChangeRequest userFindPasswordChangeRequest = new UserFindPasswordChangeRequest();

		userFindPasswordChangeRequest.setPassword1("changePassword");
		userFindPasswordChangeRequest.setPassword2("password");

		given(userAccountService.checkUserIdByUuid(test_uuid)).willReturn(userAccount.getUserId());

		// When
		mockMvc.perform(
				post("/api/users/help/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userFindPasswordChangeRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // PathVariable 방식
					parameterWithName("uuid").description("사용자 식별 코드")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then

	}

	@Test
	@DisplayName("회원 삭제 성공")
	@WithMockUser
	void successDeleteUser() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserDeleteRequest userDeleteRequest = new UserDeleteRequest();
		userDeleteRequest.setUserId("user_1");
		userDeleteRequest.setPassword("testPassword");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);
		given(userAccountService.isCurrentPassword(user, "testPassword")).willReturn(true);

		// When
		mockMvc.perform(
				post("/api/users/delete")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userDeleteRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("password").description("사용자 현재 비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상"))
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
		verify(userAccountService).deleteUser(user);
	}

	@Test
	@DisplayName("회원 삭제 실패 (id에 해당하는 유저가 없음)")
	@WithMockUser
	void failedDeleteUser1() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserDeleteRequest userDeleteRequest = new UserDeleteRequest();
		userDeleteRequest.setUserId("user");
		userDeleteRequest.setPassword("testPassword");

		given(userAccountService.findUserByUserId("user")).willReturn(null);

		// When
		mockMvc.perform(
				post("/api/users/delete")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userDeleteRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("password").description("사용자 현재 비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상"))
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then

	}

	@Test
	@DisplayName("회원 삭제 실패 (본인확인 비밀번호 유효성 검사 실패)")
	@WithMockUser
	void failedDeleteUser2() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserDeleteRequest userDeleteRequest = new UserDeleteRequest();
		userDeleteRequest.setUserId("id ");
		userDeleteRequest.setPassword("");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);

		// When
		mockMvc.perform(
				post("/api/users/delete")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userDeleteRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("password").description("사용자 현재 비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상"))
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 삭제 실패 (본인확인 비밀번호가 일치하지 않음)")
	@WithMockUser
	void failedDeleteUser3() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName1")
			.email("test1@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserDeleteRequest userDeleteRequest = new UserDeleteRequest();
		userDeleteRequest.setUserId("user_1");
		userDeleteRequest.setPassword("PasswordTest");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);

		// When
		mockMvc.perform(
				post("/api/users/delete")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userDeleteRequest))
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("password").description("사용자 현재 비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상"))
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 사용자"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원 정보 열람 성공")
	@WithMockUser
	void successShowUserDetail() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		Careers major = Careers.builder()
			.careerId(0L)
			.categoryName("대분류_1")
			.categoryType("대분류")
			.categoryImage("/major/image0")
			.build();
		Careers middle = Careers.builder()
			.careerId(1L)
			.categoryName("중분류_1")
			.categoryType("중분류")
			.categoryImage("/middle/image0")
			.parent(major)
			.build();
		Careers small = Careers.builder()
			.careerId(2L)
			.categoryName("소분류_1")
			.categoryType("소분류")
			.categoryImage("/small/image0")
			.parent(middle)
			.build();

		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
			.smallCategory(small)
			.userAccount(user)
			.build();

		UserTechnologyStack stack1 = UserTechnologyStack.builder()
			.stackId("JAVA")
			.userAccount(user)
			.build();

		UserTechnologyStack stack2 = UserTechnologyStack.builder()
			.stackId("PHP")
			.userAccount(user)
			.build();

		List<UserTechnologyStack> stacks = new ArrayList<>();
		stacks.add(stack1);
		stacks.add(stack2);

		user.setStacks(stacks);

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);
		given(userAccountService.findCareerDetailsByUser(user)).willReturn(
			userCareerDetails.getSmallCategory().getCategoryName());
		given(userAccountService.getTechnologyStack(user)).willReturn(Arrays.asList("JAVA", "PHP"));

		// When
		mockMvc.perform(
				get("/api/users/details/info")
					.queryParam("userId", "user_1")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.email").exists())
			.andExpect(jsonPath("$.smallCategory").exists())
			.andExpect(jsonPath("$.technologyStacks").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // path 방식
					parameterWithName("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("smallCategory").description("요청한 직무[소분류]"),
					fieldWithPath("technologyStacks").description("요청한 기술스택")
				)));
		// Then

	}

	@Test
	@DisplayName("회원 정보 열람 실패 (id에 해당하는 유저가 없음)")
	@WithMockUser
	void failedShowUserDetail() throws Exception {
		// Given

		given(userAccountService.findUserByUserId("user_1")).willReturn(null);

		// When
		mockMvc.perform(
				get("/api/users/details/info")
					.queryParam("userId", "user_1")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // path 방식
					parameterWithName("userId").description("사용자 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));
		// Then
	}

	@Test
	@DisplayName("회원 정보 수정 성공")
	@WithMockUser
	void successChangeUserDetail() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();
		Careers major = Careers.builder()
			.careerId(0L)
			.categoryName("대분류_1")
			.categoryType("대분류")
			.categoryImage("/major/image0")
			.build();
		Careers middle = Careers.builder()
			.careerId(1L)
			.categoryName("중분류_1")
			.categoryType("중분류")
			.categoryImage("/middle/image0")
			.parent(major)
			.build();
		Careers small = Careers.builder()
			.careerId(2L)
			.categoryName("소분류_1")
			.categoryType("소분류")
			.categoryImage("/small/image0")
			.parent(middle)
			.build();

		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
			.smallCategory(Careers.builder().build())
			.userAccount(user)
			.smallCategory(small)
			.build();

		UserTechnologyStack stack1 = UserTechnologyStack.builder().stackId("JAVA").userAccount(user).build();
		UserTechnologyStack stack2 = UserTechnologyStack.builder().stackId("PHP").userAccount(user).build();

		List<UserTechnologyStack> stacks = new ArrayList<>();
		stacks.add(stack1);
		stacks.add(stack2);

		user.setStacks(stacks);

		List<String> changeStack = new ArrayList<>();
		changeStack.add("C#");
		changeStack.add("C++");
		changeStack.add("C");
		Collections.sort(changeStack);

		ShowUserDetailsToChangeRequest showUserDetailsToChangeRequest = new ShowUserDetailsToChangeRequest();
		showUserDetailsToChangeRequest.setUserId("user_1");
		showUserDetailsToChangeRequest.setPhoneNum("010-9999-8888");
		showUserDetailsToChangeRequest.setSmallCategory("소분류_2");
		showUserDetailsToChangeRequest.setTechnologyStacks(changeStack);
		showUserDetailsToChangeRequest.setPassword("testPassword");

		UserAccount changeUser = user;
		changeUser.setPhoneNum(showUserDetailsToChangeRequest.getPhoneNum());
		Careers major2 = Careers.builder()
			.careerId(3L)
			.categoryName("대분류_2")
			.categoryType("대분류")
			.categoryImage("/major/image1")
			.build();
		Careers middle2 = Careers.builder()
			.careerId(4L)
			.categoryName("중분류_2")
			.categoryType("중분류")
			.categoryImage("/middle/image1")
			.parent(major2)
			.build();
		Careers small2 = Careers.builder()
			.careerId(5L)
			.categoryName("소분류_2")
			.categoryType("소분류")
			.categoryImage("/small/image1")
			.parent(middle2)
			.build();
		UserCareerDetails changeUserCareerDetails = UserCareerDetails.builder()
			.userAccount(changeUser)
			.smallCategory(small2)
			.build();

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);
		given(userAccountService.isCurrentPassword(any(UserAccount.class), eq("testPassword"))).willReturn(true);
		given(userAccountRepository.findByUserId("user_1")).willReturn(Optional.of(user));
		given(userAccountRepository.save(user)).willReturn(changeUser);
		given(userAccountService.findCareerDetailsByUser(changeUser)).willReturn(
			changeUserCareerDetails.getSmallCategory().getCategoryName());
		given(userAccountService.getTechnologyStack(changeUser)).willReturn(changeStack);

		// When
		mockMvc.perform(
				post("/api/users/details/info")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(showUserDetailsToChangeRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.phoneNum").exists())
			.andExpect(jsonPath("$.smallCategory").exists())
			.andExpect(jsonPath("$.technologyStacks").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("phoneNum").description("사용자 전화번호"),
					fieldWithPath("smallCategory").description("사용자 직무[소분류]"),
					fieldWithPath("technologyStacks").description("사용자 기술스택"),
					fieldWithPath("password").description("사용자 비밀번호")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("phoneNum").description("요청한 전화번호"),
					fieldWithPath("smallCategory").description("요청한 직무[소분류]"),
					fieldWithPath("technologyStacks").description("요청한 기술스택")
				)));

		// Then
		verify(userAccountService).updateDetails(refEq(user), refEq(showUserDetailsToChangeRequest));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 (id에 해당하는 유저가 없음)")
	@WithMockUser
	void failedChangeUserDetail() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		List<String> changeStack = new ArrayList<>();
		changeStack.add("C#");
		changeStack.add("C++");
		changeStack.add("C");
		Collections.sort(changeStack);

		ShowUserDetailsToChangeRequest showUserDetailsToChangeRequest = new ShowUserDetailsToChangeRequest();
		showUserDetailsToChangeRequest.setUserId("user_1");
		showUserDetailsToChangeRequest.setPhoneNum("010-9999-8888");
		showUserDetailsToChangeRequest.setSmallCategory("소분류_2");
		showUserDetailsToChangeRequest.setTechnologyStacks(changeStack);
		showUserDetailsToChangeRequest.setPassword("testPassword");

		given(userAccountService.findUserByUserId("user_1")).willReturn(null);

		// When
		mockMvc.perform(
				post("/api/users/details/info")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(showUserDetailsToChangeRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("phoneNum").description("사용자 전화번호"),
					fieldWithPath("smallCategory").description("사용자 직무[소분류]"),
					fieldWithPath("technologyStacks").description("사용자 기술스택"),
					fieldWithPath("password").description("사용자 비밀번호")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 정보 수정 실패 (유효성 검사 실패)")
	@WithMockUser
	void failedChangeUserDetail2() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		List<String> changeStack = new ArrayList<>();
		changeStack.add("C#");
		changeStack.add("C++");
		changeStack.add("C");
		Collections.sort(changeStack);

		ShowUserDetailsToChangeRequest showUserDetailsToChangeRequest = new ShowUserDetailsToChangeRequest();
		showUserDetailsToChangeRequest.setUserId("id ");
		showUserDetailsToChangeRequest.setPhoneNum("010-9-8");
		showUserDetailsToChangeRequest.setSmallCategory("소분류_2");
		showUserDetailsToChangeRequest.setTechnologyStacks(changeStack);
		showUserDetailsToChangeRequest.setPassword("");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);

		// When
		mockMvc.perform(
				post("/api/users/details/info")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(showUserDetailsToChangeRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("phoneNum").description("사용자 전화번호"),
					fieldWithPath("smallCategory").description("사용자 직무[소분류]"),
					fieldWithPath("technologyStacks").description("사용자 기술스택"),
					fieldWithPath("password").description("사용자 비밀번호")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 정보 수정 실패 (유저의 비밀번호가 일치하지 않음)")
	@WithMockUser
	void failedChangeUserDetail3() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		List<String> changeStack = new ArrayList<>();
		changeStack.add("C#");
		changeStack.add("C++");
		changeStack.add("C");
		Collections.sort(changeStack);

		ShowUserDetailsToChangeRequest showUserDetailsToChangeRequest = new ShowUserDetailsToChangeRequest();
		showUserDetailsToChangeRequest.setUserId("user_1");
		showUserDetailsToChangeRequest.setPhoneNum("010-9999-8888");
		showUserDetailsToChangeRequest.setSmallCategory("소분류_2");
		showUserDetailsToChangeRequest.setTechnologyStacks(changeStack);
		showUserDetailsToChangeRequest.setPassword("password");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);
		given(userAccountService.isCurrentPassword(user, "password")).willReturn(false);
		// When
		mockMvc.perform(
				post("/api/users/details/info")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(showUserDetailsToChangeRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("phoneNum").description("사용자 전화번호"),
					fieldWithPath("smallCategory").description("사용자 직무[소분류]"),
					fieldWithPath("technologyStacks").description("사용자 기술스택"),
					fieldWithPath("password").description("사용자 비밀번호")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 비밀번호 변경 성공")
	@WithMockUser
	void successChangeUserPassword() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest();
		userPasswordUpdateRequest.setUserId("user_1");
		userPasswordUpdateRequest.setCurrentPassword("testPassword");
		userPasswordUpdateRequest.setNewPassword1("newPassword");
		userPasswordUpdateRequest.setNewPassword2("newPassword");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);
		given(userAccountService.isCurrentPassword(user, userPasswordUpdateRequest.getCurrentPassword())).willReturn(
			true);

		// When
		mockMvc.perform(
				post("/api/users/details/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userPasswordUpdateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("currentPassword").description("사용자 현재 비밀번호"),
					fieldWithPath("newPassword1").description("사용자 새로운 비밀번호"),
					fieldWithPath("newPassword2").description("사용자 새로운 비밀번호 확인")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
		verify(userAccountService).updatePassword(user.getUserId(),
			bCryptPasswordEncoder.encode(userPasswordUpdateRequest.getNewPassword1()));
	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 (id에 해당하는 유저가 없음)")
	@WithMockUser
	void failedChangeUserPassword() throws Exception {
		// Given
		UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest();
		userPasswordUpdateRequest.setUserId("user_1");
		userPasswordUpdateRequest.setCurrentPassword("testPassword");
		userPasswordUpdateRequest.setNewPassword1("newPassword");
		userPasswordUpdateRequest.setNewPassword2("newPassword");

		given(userAccountService.findUserByUserId("user_1")).willReturn(null);

		// When
		mockMvc.perform(
				post("/api/users/details/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userPasswordUpdateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("currentPassword").description("사용자 현재 비밀번호"),
					fieldWithPath("newPassword1").description("사용자 새로운 비밀번호"),
					fieldWithPath("newPassword2").description("사용자 새로운 비밀번호 확인")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 (유효성 검사 실패)")
	@WithMockUser
	void failedChangeUserPassword2() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest();
		userPasswordUpdateRequest.setUserId("id ");
		userPasswordUpdateRequest.setCurrentPassword("");
		userPasswordUpdateRequest.setNewPassword1("word");
		userPasswordUpdateRequest.setNewPassword2("word");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);

		// When
		mockMvc.perform(
				post("/api/users/details/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userPasswordUpdateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("currentPassword").description("사용자 현재 비밀번호"),
					fieldWithPath("newPassword1").description("사용자 새로운 비밀번호"),
					fieldWithPath("newPassword2").description("사용자 새로운 비밀번호 확인")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then

	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 (본인확인 비밀번호 계정 비밀번호가 서로 일치하지 않음)")
	@WithMockUser
	void failedChangeUserPassword3() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest();
		userPasswordUpdateRequest.setUserId("user_1");
		userPasswordUpdateRequest.setCurrentPassword("password");
		userPasswordUpdateRequest.setNewPassword1("newPassword");
		userPasswordUpdateRequest.setNewPassword2("newPassword");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);
		given(userAccountService.isCurrentPassword(user, userPasswordUpdateRequest.getCurrentPassword())).willReturn(
			false);

		// When
		mockMvc.perform(
				post("/api/users/details/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userPasswordUpdateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("currentPassword").description("사용자 현재 비밀번호"),
					fieldWithPath("newPassword1").description("사용자 새로운 비밀번호"),
					fieldWithPath("newPassword2").description("사용자 새로운 비밀번호 확인")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 비밀번호 변경 실패 (새로운 비밀번호와 새로운 비밀번호 확인이 서로 일치하지 않음)")
	@WithMockUser
	void failedChangeUserPassword4() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserPasswordUpdateRequest userPasswordUpdateRequest = new UserPasswordUpdateRequest();
		userPasswordUpdateRequest.setUserId("user_1");
		userPasswordUpdateRequest.setCurrentPassword("testPassword");
		userPasswordUpdateRequest.setNewPassword1("Password");
		userPasswordUpdateRequest.setNewPassword2("newPassword");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);
		given(userAccountService.isCurrentPassword(user, userPasswordUpdateRequest.getCurrentPassword())).willReturn(
			true);

		// When
		mockMvc.perform(
				post("/api/users/details/password")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userPasswordUpdateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("currentPassword").description("사용자 현재 비밀번호"),
					fieldWithPath("newPassword1").description("사용자 새로운 비밀번호"),
					fieldWithPath("newPassword2").description("사용자 새로운 비밀번호 확인")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 이메일 변경 메일 전송 성공")
	@WithMockUser
	void successSendEmailToChange() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserChangeEmailRequest userChangeEmailRequest = new UserChangeEmailRequest();
		userChangeEmailRequest.setUserId("user_1");
		userChangeEmailRequest.setEmail("newTest@test.com");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);

		// When
		mockMvc.perform(
				post("/api/users/details/email")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userChangeEmailRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("email").description("사용자가 변경할 이메일 주소")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
		verify(userAccountService).sendUpdateEmailLink(userChangeEmailRequest.getUserId(),
			userChangeEmailRequest.getEmail());
	}

	@Test
	@DisplayName("회원 이메일 변경 메일 전송 실패 (id에 해당하는 유저가 없음)")
	@WithMockUser
	void failedSendEmailToChange() throws Exception {
		// Given
		UserChangeEmailRequest userChangeEmailRequest = new UserChangeEmailRequest();
		userChangeEmailRequest.setUserId("user_1");
		userChangeEmailRequest.setEmail("newTest@test.com");

		given(userAccountService.findUserByUserId("user_1")).willReturn(null);

		// When
		mockMvc.perform(
				post("/api/users/details/email")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userChangeEmailRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("email").description("사용자가 변경할 이메일 주소")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 이메일 변경 메일 전송 실패 (유효성 검사 실패)")
	@WithMockUser
	void failedSendEmailToChange2() throws Exception {
		// Given
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();

		UserChangeEmailRequest userChangeEmailRequest = new UserChangeEmailRequest();
		userChangeEmailRequest.setUserId("id ");
		userChangeEmailRequest.setEmail("newTest");

		given(userAccountService.findUserByUserId("user_1")).willReturn(user);

		// When
		mockMvc.perform(
				post("/api/users/details/email")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(userChangeEmailRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 방식
					fieldWithPath("userId").description("사용자 아이디"),
					fieldWithPath("email").description("사용자가 변경할 이메일 주소")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원 이메일 변경 성공")
	@WithMockUser
	void successChangeEmail() throws Exception {
		// Given
		String uuid = "0123-4567-89AB-CDEF";
		UserAccount user = UserAccount.builder()
			.userId("user_1")
			.userName("testName")
			.email("test@email.com")
			.phoneNum("010-1111-2222")
			.password("testPassword")
			.birth("01-01-01")
			.gender("M")
			.role(UserRole.ROLE_TEMPORARY_USER)
			.build();
		ChangeUserEmail changeUserEmail = new ChangeUserEmail(uuid, user.getUserId(), user.getEmail());

		given(userAccountService.checkUpdateEmailUserIdByUuid(uuid)).willReturn(changeUserEmail);

		// When
		mockMvc.perform(
				get("/api/users/details/email/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // Json 방식
					parameterWithName("uuid").description("이메일 변경 유저 식별자")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
		verify(userAccountService).updateEmail(changeUserEmail);
	}

	@Test
	@DisplayName("회원 이메일 변경 실패 (uuid에 일치하는 계정이 없음)")
	@WithMockUser
	void failedChangeEmail() throws Exception {
		// Given
		String uuid = "0123-4567-89AB-CDEF";
		given(userAccountService.checkUpdateEmailUserIdByUuid(uuid)).willReturn(null);

		// When
		mockMvc.perform(
				get("/api/users/details/email/{uuid}", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters( // pathVarialbles 방식
					parameterWithName("uuid").description("이메일 변경 유저 식별자")
				),
				responseFields( // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("회원가입 이메일 인증 재전송 성공")
	@WithMockUser
	void sendAgainMailSccuess() throws Exception {
		// Given
		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd123");
		request.setPassword("wjsansrk");
		request.setUserName("홍길동");
		request.setPhoneNum("010-1111-2222");
		request.setEmail("hgd123@naver.com");
		request.setBirth("00-01-01");
		request.setGender("M");
		request.setIsMarketed(true);

		// When
		mockMvc.perform(
				post("/api/users/email")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			// Spring REST Docs
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
		// 이메일 전송 메서드가 동작했는지 확인
		verify(mailService).sendAgainAuthenticationEmail(request.getUserId(), request.getUserName(),
			request.getPhoneNum(),
			request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()), request.getBirth(),
			request.getGender(), request.getIsMarketed());
	}

	@Test
	@DisplayName("회원가입 이메일 인증 재전송 실패 (유효성 검사 실패)")
	@WithMockUser
	void sendAgainMailFail() throws Exception {
		// Given
		UserAccountRegisterRequest request = new UserAccountRegisterRequest();

		request.setUserId("hgd");
		request.setPassword("wjsa");
		request.setUserName("홍 길 동");
		request.setPhoneNum("01011112222");
		request.setEmail("hgd123@naver");
		request.setBirth("00 01 01");
		request.setGender("M");
		request.setIsMarketed(null);

		Gson gson = new GsonBuilder().serializeNulls().create();

		// When
		mockMvc.perform(
				post("/api/users/email")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			// Spring REST Docs
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // Json 요청 방식
					fieldWithPath("userId").description("사용할 아이디")
						.attributes(field("constraints", "아이디는 5자 이상")),
					fieldWithPath("userName").description("사용자 이름"),
					fieldWithPath("email").description("사용자 이메일")
						.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
					fieldWithPath("phoneNum").description("사용자 연락처"),
					fieldWithPath("password").description("비밀번호")
						.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
					fieldWithPath("birth").description("사용자 생일"),
					fieldWithPath("gender").description("사용자 성별"),
					fieldWithPath("isMarketed").description("사용자 마케팅 수신 여부")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("userName").description("요청한 이름"),
					fieldWithPath("email").description("요청한 이메일"),
					fieldWithPath("phoneNum").description("요청한 연락처"),
					fieldWithPath("birth").description("요청한 생일"),
					fieldWithPath("gender").description("요청한 성별"),
					fieldWithPath("isMarketed").description("요청한 마케팅 수신 여부"),
					fieldWithPath("msg").description("요청에 대한 처리결과")
				)));

		// Then
	}

	@Test
	@DisplayName("유저 MBTI 가져오기 성공 (mbti가 있음)")
	@WithMockUser
	void getUserMBTISuccess() throws Exception {
		// Given
		given(userAccountService.getUserMBTI(any())).willReturn("ENTP");

		// When
		mockMvc.perform(
				get("/api/users/details/mbti")
					.queryParam("userId", "testId")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			// Spring REST Docs
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // path 요청 방식
					parameterWithName("userId").description("MBTI를 조회할 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("유저 MBTI 가져오기 성공 (mbti가 없음)")
	@WithMockUser
	void getUserMBTISuccess2() throws Exception {
		// Given
		given(userAccountService.getUserMBTI(any())).willReturn("");

		// When
		mockMvc.perform(
				get("/api/users/details/mbti")
					.queryParam("userId", "testId")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			// Spring REST Docs
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters( // path 요청 방식
					parameterWithName("userId").description("MBTI를 조회할 아이디")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("유저 MBTI 갱신 성공")
	@WithMockUser
	void updateUserMBTISuccess() throws Exception {
		// Given
		UserMBTIRequest request = UserMBTIRequest.builder().userId("testId").mbti(MBTI.ENTP).build();

		// When
		mockMvc.perform(
				post("/api/users/details/mbti")
					.content(gson.toJson(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			// Spring REST Docs
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields( // path 요청 방식
					fieldWithPath("userId").description("MBTI를 변경할 아이디"),
					fieldWithPath("mbti").description("변경할 MBTI")
				),
				responseFields(    // Json 응답 형식
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("유저 MBTI 갱신 실패 (유효성 검사 실패)")
	@WithMockUser
	void updateUserMBTIFailed() throws Exception {
		// Given
		UserMBTIRequest request = UserMBTIRequest.builder().userId("").mbti(null).build();

		// When
		mockMvc.perform(
				post("/api/users/details/mbti")
					.content(gson.toJson(request))
					.contentType(MediaType.APPLICATION_JSON)
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			// Spring REST Docs
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("MBTI를 변경할 아이디")
				),
				responseFields(
					fieldWithPath("userId").description("요청한 아이디"),
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}
}
