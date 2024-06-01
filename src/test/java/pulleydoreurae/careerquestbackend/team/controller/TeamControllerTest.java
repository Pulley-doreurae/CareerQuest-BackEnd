package pulleydoreurae.careerquestbackend.team.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.KickRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamDeleteRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamMemberRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.request.TeamRequest;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.EmptyTeamMemberResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamDetailResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamMemberResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponseWithPageInfo;
import pulleydoreurae.careerquestbackend.team.domain.entity.EmptyTeamMember;
import pulleydoreurae.careerquestbackend.team.domain.entity.Team;
import pulleydoreurae.careerquestbackend.team.domain.entity.TeamMember;
import pulleydoreurae.careerquestbackend.team.service.TeamService;

/**
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@WebMvcTest(TeamController.class)
@AutoConfigureRestDocs
class TeamControllerTest {

	@Autowired MockMvc mockMvc;

	@MockBean TeamService teamService;

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
		gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateTimeSerializer());
		gson = gsonBuilder.setPrettyPrinting().create();
	}

	@Test
	@DisplayName("전체 팀 목록 불러오기")
	@WithMockUser
	void findAllTest() throws Exception {
		// Given
		TeamResponseWithPageInfo response = new TeamResponseWithPageInfo(3);
		TeamResponse teamResponse1 = TeamResponse.builder().teamId(100L).teamName("정보처리기사 1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamResponse teamResponse2 = TeamResponse.builder().teamId(101L).teamName("정보처리기사 2팀").teamType(TeamType.STUDY).maxMember(4).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamResponse teamResponse3 = TeamResponse.builder().teamId(102L).teamName("정보처리기사 3팀").teamType(TeamType.STUDY).maxMember(6).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamResponse teamResponse4 = TeamResponse.builder().teamId(103L).teamName("공모전에 도전해보자").teamType(TeamType.CONTEST).maxMember(3).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		response.getTeamResponse().add(teamResponse1);
		response.getTeamResponse().add(teamResponse2);
		response.getTeamResponse().add(teamResponse3);
		response.getTeamResponse().add(teamResponse4);
		given(teamService.findAll(any())).willReturn(response);

		// When
		mockMvc.perform(get("/api/teams")
						.queryParam("page", "0"))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						responseFields(
								fieldWithPath("totalPage").description("전체 페이지 수"),
								fieldWithPath("teamResponse.[].teamId").description("팀 ID"),
								fieldWithPath("teamResponse.[].teamName").description("팀 이름"),
								fieldWithPath("teamResponse.[].teamType").description("팀 타입"),
								fieldWithPath("teamResponse.[].maxMember").description("팀 최대 인원"),
								fieldWithPath("teamResponse.[].startDate").description("모집 시작일"),
								fieldWithPath("teamResponse.[].endDate").description("모집 종료일")
						)));

		// Then
	}

	@Test
	@DisplayName("전체 팀에서 팀 타입으로 분류해서 목록 불러오기")
	@WithMockUser
	void findAllByTeamTypeTest() throws Exception {
		// Given
		TeamResponseWithPageInfo response = new TeamResponseWithPageInfo(3);
		TeamResponse teamResponse1 = TeamResponse.builder().teamId(100L).teamName("정보처리기사 1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamResponse teamResponse2 = TeamResponse.builder().teamId(101L).teamName("정보처리기사 2팀").teamType(TeamType.STUDY).maxMember(4).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamResponse teamResponse3 = TeamResponse.builder().teamId(102L).teamName("정보처리기사 3팀").teamType(TeamType.STUDY).maxMember(6).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamResponse teamResponse4 = TeamResponse.builder().teamId(103L).teamName("공모전에 도전해보자").teamType(TeamType.CONTEST).maxMember(3).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamResponse teamResponse5 = TeamResponse.builder().teamId(104L).teamName("새로운 공모전에 도전해보자").teamType(TeamType.CONTEST).maxMember(3).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		response.getTeamResponse().add(teamResponse4);
		response.getTeamResponse().add(teamResponse5);
		given(teamService.findAllByTeamType(any(), any())).willReturn(response);

		// When
		mockMvc.perform(get("/api/teams/{teamType}", "contest")
						.queryParam("page", "0"))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						pathParameters(
							parameterWithName("teamType").description("팀 타입")
						),
						responseFields(
								fieldWithPath("totalPage").description("전체 페이지 수"),
								fieldWithPath("teamResponse.[].teamId").description("팀 ID"),
								fieldWithPath("teamResponse.[].teamName").description("팀 이름"),
								fieldWithPath("teamResponse.[].teamType").description("팀 타입"),
								fieldWithPath("teamResponse.[].maxMember").description("팀 최대 인원"),
								fieldWithPath("teamResponse.[].startDate").description("모집 시작일"),
								fieldWithPath("teamResponse.[].endDate").description("모집 종료일")
						)));

		// Then
	}

	@Test
	@DisplayName("한 팀에 대한 세부정보 불러오기")
	@WithMockUser
	void findByTeamIdTest() throws Exception {
		// Given
		TeamResponse teamResponse = TeamResponse.builder().teamId(100L).teamName("정보처리기사 1팀").teamType(TeamType.STUDY).maxMember(5).startDate(LocalDate.of(2024, 1, 10)).endDate(LocalDate.of(2024, 2, 10)).build();
		TeamDetailResponse response = new TeamDetailResponse(teamResponse);
		TeamMemberResponse teamMember1 = TeamMemberResponse.builder().userId("user1").isTeamLeader(true).position("백엔드 개발자").build();
		TeamMemberResponse teamMember2 = TeamMemberResponse.builder().userId("user2").isTeamLeader(false).position("프론트엔드 개발자").build();
		TeamMemberResponse teamMember3 = TeamMemberResponse.builder().userId("user3").isTeamLeader(false).position("디자이너").build();

		EmptyTeamMemberResponse emptyTeamMember1 = EmptyTeamMemberResponse.builder().position("AI 개발자").build();
		EmptyTeamMemberResponse emptyTeamMember2 = EmptyTeamMemberResponse.builder().position("백엔드 개발자").build();
		response.getTeamMemberResponses().add(teamMember1);
		response.getTeamMemberResponses().add(teamMember2);
		response.getTeamMemberResponses().add(teamMember3);
		response.getEmptyTeamMemberResponses().add(emptyTeamMember1);
		response.getEmptyTeamMemberResponses().add(emptyTeamMember2);
		given(teamService.findByTeamId(any())).willReturn(response);

		// When
		mockMvc.perform(get("/api/teams-details/{teamId}", "100")
						.queryParam("page", "0"))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						queryParameters(
								parameterWithName("page").description("요청하는 페이지 (0부터 시작, 15개씩 자름)")
						),
						pathParameters(
								parameterWithName("teamId").description("팀 ID")
						),
						responseFields(
								fieldWithPath("teamResponse.teamId").description("팀 ID"),
								fieldWithPath("teamResponse.teamName").description("팀 이름"),
								fieldWithPath("teamResponse.teamType").description("팀 타입"),
								fieldWithPath("teamResponse.maxMember").description("팀 최대 인원"),
								fieldWithPath("teamResponse.startDate").description("모집 시작일"),
								fieldWithPath("teamResponse.endDate").description("모집 종료일"),
								fieldWithPath("teamMemberResponses.[].userId").description("팀원 ID"),
								fieldWithPath("teamMemberResponses.[].position").description("팀원 포지션"),
								fieldWithPath("teamMemberResponses.[].teamLeader").description("팀장 여부"),
								fieldWithPath("emptyTeamMemberResponses.[].position").description("팀장이 선호하는 포지션")
						)));

		// Then
	}

	@Test
	@DisplayName("팀 생성 테스트")
	@WithMockUser
	void makeTeamTest() throws Exception {
		// Given
		List<String> positions = List.of("백엔드 개발자", "프론트엔드 개발자", "디자이너", "AI 개발자");
		TeamRequest request = new TeamRequest("leaderId", "백엔드 개발자", "새로운 프로젝트를 만들어 봅시다.", TeamType.STUDY, 5, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1), positions);

		// When
		mockMvc.perform(post("/api/teams")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("teamLeaderId").description("팀장 ID"),
								fieldWithPath("position").description("팀장의 포지션"),
								fieldWithPath("teamName").description("팀 이름"),
								fieldWithPath("teamType").description("팀 타입"),
								fieldWithPath("maxMember").description("팀 최대 인원"),
								fieldWithPath("startDate").description("모집 시작일"),
								fieldWithPath("endDate").description("모집 종료일"),
								fieldWithPath("positions").description("선호하는 팀원 포지션들")
						)));

		// Then
	}

	@Test
	@DisplayName("팀 수정 테스트")
	@WithMockUser
	void updateTeamTest() throws Exception {
		// Given
		List<String> positions = List.of("백엔드 개발자", "프론트엔드 개발자", "디자이너", "AI 개발자");
		TeamRequest request = new TeamRequest(100L, "leaderId", "백엔드 개발자", "새로운 프로젝트를 만들어 봅시다.", TeamType.STUDY, 5, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 2, 1), positions);

		// When
		mockMvc.perform(patch("/api/teams")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("teamId").description("팀 ID"),
								fieldWithPath("teamLeaderId").description("팀장 ID"),
								fieldWithPath("position").description("팀장의 포지션"),
								fieldWithPath("teamName").description("팀 이름"),
								fieldWithPath("teamType").description("팀 타입"),
								fieldWithPath("maxMember").description("팀 최대 인원"),
								fieldWithPath("startDate").description("모집 시작일"),
								fieldWithPath("endDate").description("모집 종료일"),
								fieldWithPath("positions").description("선호하는 팀원 포지션들")
						),
						responseFields(
								fieldWithPath("msg").description("처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("팀 삭제 테스트")
	@WithMockUser
	void deleteTeamTest() throws Exception {
		// Given
		TeamDeleteRequest request = new TeamDeleteRequest(100L, "leaderId");

		// When
		mockMvc.perform(delete("/api/teams")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("teamId").description("팀 ID"),
								fieldWithPath("teamLeaderId").description("팀장 ID")
						),
						responseFields(
								fieldWithPath("msg").description("처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("팀 참가 테스트")
	@WithMockUser
	void joinTeamTest() throws Exception {
		// Given
		TeamMemberRequest request = new TeamMemberRequest(100L, "userA", "AI 개발자");

		// When
		mockMvc.perform(post("/api/teams/members")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("teamId").description("팀 ID"),
								fieldWithPath("userId").description("요청자(팀원) ID"),
								fieldWithPath("position").description("요청자(팀원)의 포지션")
						),
						responseFields(
								fieldWithPath("msg").description("처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("팀 떠나기 테스트")
	@WithMockUser
	void leaveTeamTest() throws Exception {
		// Given
		TeamMemberRequest request = new TeamMemberRequest(100L, "userA", "AI 개발자");

		// When
		mockMvc.perform(patch("/api/teams/members")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("teamId").description("팀 ID"),
								fieldWithPath("userId").description("요청자(팀원) ID"),
								fieldWithPath("position").description("요청자(팀원)의 포지션")
						),
						responseFields(
								fieldWithPath("msg").description("처리결과")
						)));

		// Then
	}

	@Test
	@DisplayName("팀원 추방 테스트")
	@WithMockUser
	void kickMemberTest() throws Exception {
		// Given
		KickRequest request = new KickRequest(100L, "leaderId", "userA", "AI 개발자");

		// When
		mockMvc.perform(delete("/api/teams/members")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(gson.toJson(request)))
				.andExpect(status().isOk())
				.andDo(print())
				.andDo(document("{class-name}/{method-name}/",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("teamId").description("팀 ID"),
								fieldWithPath("teamLeaderId").description("요청자(팀장) ID"),
								fieldWithPath("targetId").description("삭제할 대상(팀원) ID"),
								fieldWithPath("position").description("빈자리에 추가할(선호하는 팀원) 포지션")
						),
						responseFields(
								fieldWithPath("msg").description("처리결과")
						)));

		// Then
	}
}
