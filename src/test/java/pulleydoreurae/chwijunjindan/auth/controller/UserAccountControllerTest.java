package pulleydoreurae.chwijunjindan.auth.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;
import pulleydoreurae.chwijunjindan.mail.repository.MailRepository;
import pulleydoreurae.chwijunjindan.mail.service.MailService;

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
	private MailRepository mailRepository;

	@Test
	@DisplayName("1. 회원가입 테스트")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest1() throws Exception {
		// Given

		// When
		mockMvc.perform(
						post("/api/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("phoneNum", "010-1111-2222")
								.param("password", "testPassword"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.phoneNum").exists())
				.andDo(print())
				// Spring REST Docs
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.optional()
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("userName").description("사용자 이름"),
								parameterWithName("email").description("사용자 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("phoneNum").description("사용자 연락처"),
								parameterWithName("password").description("비밀번호")
										.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
		// 저장 메서드가 동작했는지 확인
		verify(mailService).sendMail("testId", "testName", "010-1111-2222",
				"test@email.com", bCryptPasswordEncoder.encode("testPassword"));
	}

	@Test
	@DisplayName("2. 회원가입 중복 테스트 (id)")
	@WithMockUser
		// 시큐리티 설정파일 전체를 불러오지 않기 때문에 권한이 있다고 가정하고 테스트
	void RegisterTest2() throws Exception {
		// Given
		given(userAccountRepository.existsByUserId(any())).willReturn(true);
		// When
		mockMvc.perform(
						post("/api/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("phoneNum", "010-1111-2222")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.phoneNum").exists()).andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.optional()
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("userName").description("사용자 이름"),
								parameterWithName("email").description("사용자 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("phoneNum").description("사용자 연락처"),
								parameterWithName("password").description("비밀번호")
										.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
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
		// When
		mockMvc.perform(
						post("/api/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("phoneNum", "010-1111-2222")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.phoneNum").exists()).andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.optional()
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("userName").description("사용자 이름"),
								parameterWithName("email").description("사용자 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("phoneNum").description("사용자 연락처"),
								parameterWithName("password").description("비밀번호")
										.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
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

		// When
		mockMvc.perform(
						post("/api/register")
								.with(csrf())
								.param("userId", "test")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("phoneNum", "010-1111-2222")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.phoneNum").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.optional()
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("userName").description("사용자 이름"),
								parameterWithName("email").description("사용자 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("phoneNum").description("사용자 연락처"),
								parameterWithName("password").description("비밀번호")
										.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
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

		// When
		mockMvc.perform(
						post("/api/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("phoneNum", "010-1111-2222")
								.param("password", "testPw"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.phoneNum").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.optional()
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("userName").description("사용자 이름"),
								parameterWithName("email").description("사용자 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("phoneNum").description("사용자 연락처"),
								parameterWithName("password").description("비밀번호")
										.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
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

		// When
		mockMvc.perform(
						post("/api/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test")
								.param("phoneNum", "010-1111-2222")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.phoneNum").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.optional()
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("userName").description("사용자 이름"),
								parameterWithName("email").description("사용자 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("phoneNum").description("사용자 연락처"),
								parameterWithName("password").description("비밀번호")
										.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
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

		// When
		mockMvc.perform(
						post("/api/register")
								.with(csrf())
								.param("userId", "testId")
								.param("userName", "testName")
								.param("email", "test@email.com")
								.param("phoneNum", "01-11-22")
								.param("password", "testPassword"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.userId").exists())
				.andExpect(jsonPath("$.userName").exists())
				.andExpect(jsonPath("$.email").exists())
				.andExpect(jsonPath("$.phoneNum").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("userName").description("사용자 이름"),
								parameterWithName("email").description("사용자 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("phoneNum").description("사용자 연락처"),
								parameterWithName("password").description("비밀번호")
										.attributes(new Attributes.Attribute("constraints", "비밀번호는 8자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("userId").description("요청한 아이디"),
								fieldWithPath("userName").description("요청한 이름"),
								fieldWithPath("email").description("요청한 이메일"),
								fieldWithPath("phoneNum").description("요청한 연락처"),
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
						post("/api/duplicate-check-id")
								.with(csrf())
								.param("userId", "testId"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.field").exists())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
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
						post("/api/duplicate-check-id")
								.with(csrf())
								.param("userId", "testId"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.field").exists())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("userId").description("사용할 아이디")
										.attributes(field("constraints", "아이디는 5자 이상")),
								parameterWithName("_csrf").description("csrf 토큰값")
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
						post("/api/duplicate-check-email")
								.with(csrf())
								.param("email", "test@email.com"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.field").exists())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("email").description("사용할 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("_csrf").description("csrf 토큰값")
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
						post("/api/duplicate-check-email")
								.with(csrf())
								.param("email", "test@email.com"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.field").exists())
				.andExpect(jsonPath("$.msg").exists())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						formParameters(    // form-data 형식
								parameterWithName("email").description("사용할 이메일")
										.attributes(new Attributes.Attribute("constraints", "이메일 형식만 가능")),
								parameterWithName("_csrf").description("csrf 토큰값")
						),
						responseFields(    // Json 응답 형식
								fieldWithPath("field").description("요청한 이메일"),
								fieldWithPath("msg").description("요청에 대한 처리결과")
						)));

		// Then
	}

	public static Attribute field(String key, String value) {
		return new Attribute(key, value);
	}
}
