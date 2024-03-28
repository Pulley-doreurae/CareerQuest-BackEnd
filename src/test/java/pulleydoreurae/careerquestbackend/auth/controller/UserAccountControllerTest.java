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

import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserAccountRegisterRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserTechnologyStackRequest;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserCareerDetailsRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserTechnologyStackRepository;
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

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UserAccountRepository userAccountRepository;
	@MockBean
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@MockBean
	private MailService mailService;
	@MockBean
	private UserInfoUserIdRepository userIdRepository;
	@MockBean
	private EmailAuthenticationRepository emailAuthenticationRepository;
	@MockBean
	private UserCareerDetailsRepository userCareerDetailsRepository;
	@MockBean
	private UserTechnologyStackRepository userTechnologyStackRepository;

	Gson gson = new Gson();

	@Test
	@DisplayName("1. 회원가입 테스트")
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
								fieldWithPath("gender").description("사용자 성별")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("birth").description("요청한 생일"),
								fieldWithPath("gender").description("요청한 성별"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
		// 이메일 전송 메서드가 동작했는지 확인
		verify(mailService).sendMail(request.getUserId(), request.getUserName(), request.getPhoneNum(),
				request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()), request.getBirth(), request.getGender());
	}

	@Test
	@DisplayName("2. 회원가입 중복 테스트 (id)")
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
				.andExpect(jsonPath("$.gender").exists()).andDo(print())
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
								fieldWithPath("gender").description("사용자 성별")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("birth").description("요청한 생일"),
								fieldWithPath("gender").description("요청한 성별"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
		// id 로 검색하는 메서드가 동작했는지 확인
		verify(userAccountRepository).existsByUserId(any());
	}

	@Test
	@DisplayName("3. 회원가입 중복 테스트 (email)")
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
				.andExpect(jsonPath("$.gender").exists()).andDo(print())
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
								fieldWithPath("gender").description("사용자 성별")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("birth").description("요청한 생일"),
								fieldWithPath("gender").description("요청한 성별"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
		// email 로 검색하는 메서드가 동작했는지 확인
		verify(userAccountRepository).existsByEmail(any());
	}

	@Test
	@DisplayName("4. 유효성 검사를 통과못하는 테스트케이스 (id)")
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
								fieldWithPath("gender").description("사용자 성별")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("birth").description("요청한 생일"),
								fieldWithPath("gender").description("요청한 성별"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("5. 유효성 검사를 통과못하는 테스트케이스 (password)")
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
								fieldWithPath("gender").description("사용자 성별")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("birth").description("요청한 생일"),
								fieldWithPath("gender").description("요청한 성별"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("6. 유효성 검사를 통과못하는 테스트케이스 (email)")
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
								fieldWithPath("gender").description("사용자 성별")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("birth").description("요청한 생일"),
								fieldWithPath("gender").description("요청한 성별"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("7. 유효성 검사를 통과못하는 테스트케이스 (phoneNum)")
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
								fieldWithPath("gender").description("사용자 성별")

						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("birth").description("요청한 생일"),
								fieldWithPath("gender").description("요청한 성별"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("8. 아이디 중복확인 (중복 X)")
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
	@DisplayName("9. 아이디 중복확인 (중복 O)")
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
	@DisplayName("10. 이메일 중복확인 (중복 X)")
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
	@DisplayName("11. 이메일 중복확인 (중복 O)")
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
	@DisplayName("12. 회원직무 추가 실패 (사용자 정보를 찾을 수 없음)")
	@WithMockUser
	void addCareerFailTest() throws Exception {
		// Given

		// When
		mockMvc.perform(
						post("/api/users/details/careers")
								.with(csrf())
								.param("userId", "testId")
								.param("majorCategory", "1")
								.param("middleCategory", "1")
								.param("smallCategory", "1")
				)
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용자 아이디")
										.attributes(field("constraints", "String")),
								parameterWithName("majorCategory").description("대분류 코드(id)")
										.attributes(field("constraints", "Long")),
								parameterWithName("middleCategory").description("중분류 코드(id)")
										.attributes(field("constraints", "Long")),
								parameterWithName("smallCategory").description("소분류 코드(id)")
										.attributes(field("constraints", "Long")),
								parameterWithName("_csrf").description("csrf 코드 (무시)")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 결과")
						)));
		// Then
		verify(userAccountRepository).findByUserId(any());
	}

	@Test
	@DisplayName("13. 회원직무 추가 성공")
	@WithMockUser
	void addCareerSuccessTest() throws Exception {
		// Given
		given(userAccountRepository.findByUserId(any())) // 사용자 id가 들어왔다면
				.willReturn(Optional.of(new UserAccount()));

		// When
		mockMvc.perform(
						post("/api/users/details/careers")
								.with(csrf())
								.param("userId", "testId")
								.param("majorCategory", "1")
								.param("middleCategory", "1")
								.param("smallCategory", "1")
				)
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용자 아이디")
										.attributes(field("constraints", "String")),
								parameterWithName("majorCategory").description("대분류 코드(id)")
										.attributes(field("constraints", "Long")),
								parameterWithName("middleCategory").description("중분류 코드(id)")
										.attributes(field("constraints", "Long")),
								parameterWithName("smallCategory").description("소분류 코드(id)")
										.attributes(field("constraints", "Long")),
								parameterWithName("_csrf").description("csrf 코드 (무시)")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 결과")
						)));

		// Then
		verify(userAccountRepository).findByUserId(any());
		verify(userAccountRepository).save(any());
		verify(userCareerDetailsRepository).save(any());
	}

	@Test
	@DisplayName("14. 기술스택 추가 실패 (사용자 정보를 찾을 수 없음)")
	@WithMockUser
	void addStacksFailTest() throws Exception {
		// Given
		UserTechnologyStackRequest stackRequest = new UserTechnologyStackRequest();
		stackRequest.setUserId("testId");
		List<Long> stacks = new ArrayList<>();
		stacks.add(1L);
		stacks.add(2L);
		stacks.add(3L);
		stacks.add(4L);
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
						requestFields(    // form-data 형식
								fieldWithPath("userId").description("사용자 아이디"),
								fieldWithPath("stacks").description("추가할 스택들의 id 값 (List 형식)")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 결과")
						)));
		// Then
		verify(userAccountRepository).findByUserId(any());
	}

	@Test
	@DisplayName("15. 기술스택 추가 성공")
	@WithMockUser
	void addStacksSuccessTest() throws Exception {
		// Given
		UserTechnologyStackRequest stackRequest = new UserTechnologyStackRequest();
		stackRequest.setUserId("testId");
		List<Long> stacks = new ArrayList<>();
		stacks.add(1L);
		stacks.add(2L);
		stacks.add(3L);
		stacks.add(4L);
		stackRequest.setStacks(stacks);
		given(userAccountRepository.findByUserId(any())).willReturn(Optional.of(new UserAccount()));

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
						requestFields(    // form-data 형식
								fieldWithPath("userId").description("사용자 아이디"),
								fieldWithPath("stacks").description("추가할 스택들의 id 값 (List 형식)")
						),
						responseFields(
								fieldWithPath("msg").description("요청에 대한 결과")
						)));
		// Then
		verify(userAccountRepository).findByUserId(any());
		verify(userAccountRepository).save(any());
		// 4개의 리스트 내용이 전부 저장되는지 검사
		verify(userTechnologyStackRepository, times(4)).save(any());
	}

	public static Attribute field(String key, String value) {
		return new Attribute(key, value);
	}
}
