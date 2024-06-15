package pulleydoreurae.careerquestbackend.search.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.CertificationResponse;
import pulleydoreurae.careerquestbackend.community.domain.ContestCategory;
import pulleydoreurae.careerquestbackend.community.domain.Organizer;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.community.domain.Region;
import pulleydoreurae.careerquestbackend.community.domain.Target;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.ContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.search.domain.response.SearchRankResponse;
import pulleydoreurae.careerquestbackend.search.domain.response.SearchResultResponse;
import pulleydoreurae.careerquestbackend.search.service.SearchService;
import pulleydoreurae.careerquestbackend.team.domain.TeamType;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponse;
import pulleydoreurae.careerquestbackend.team.domain.dto.response.TeamResponseWithPageInfo;

@WebMvcTest(SearchController.class)
@AutoConfigureRestDocs
public class SearchControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SearchService searchService;

	@Test
	@DisplayName("실시간 검색어 불러오기")
	@WithMockUser
	void realTimeSearchTermSuccess() throws Exception {
		// Given
		List<SearchRankResponse> responses = List.of(
			SearchRankResponse.builder().rank(1).keyword("자격증").rankChange("0").build(),
			SearchRankResponse.builder().rank(2).keyword("공모전").rankChange("0").build(),
			SearchRankResponse.builder().rank(3).keyword("스터디").rankChange("0").build(),
			SearchRankResponse.builder().rank(4).keyword("컴퓨터").rankChange("N").build(),
			SearchRankResponse.builder().rank(5).keyword("4월").rankChange("1").build(),
			SearchRankResponse.builder().rank(6).keyword("전주").rankChange("-2").build(),
			SearchRankResponse.builder().rank(7).keyword("전주대학교").rankChange("N").build(),
			SearchRankResponse.builder().rank(8).keyword("정보처리기사").rankChange("2").build(),
			SearchRankResponse.builder().rank(9).keyword("자격증 일정").rankChange("1").build(),
			SearchRankResponse.builder().rank(10).keyword("공부").rankChange("-2").build()
		);

		given(searchService.getShowRanking()).willReturn(responses);

		// When
		mockMvc.perform(
				get("/api/search/ranking")
					.with(csrf()))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					fieldWithPath(".[]rank").description("순위"),
					fieldWithPath(".[]keyword").description("검색어"),
					fieldWithPath(".[]rankChange").description("순위 변동")
				)));

		// Then
	}

	@Test
	@DisplayName("전체 검색 성공")
	@WithMockUser
	void searchByKeywordSuccess() throws Exception {
		// Given
		String keyword = "정보처리기사";

		CertificationResponse certificationResponse = CertificationResponse.builder()
			.certificationCode(10L)
			.certificationName("정보처리기사")
			.qualification("4년제")
			.organizer("한국산업인력공단")
			.registrationLink("https://www.hrdkorea.or.kr/")
			.aiSummary("정보처리기사에 대한 AI 요약입니다.")
			.build();

		UserAccount userAccount = UserAccount.builder().userId("testId").build();
		Post post1 = new Post(100L, userAccount, "천하제일 정보처리기사!", "대충 정보가지고 처리하는 내용", 0L, PostCategory.CONTEST_BOARD, null, null);
		Contest contest1 = new Contest(100L, post1, ContestCategory.CONTEST, Target.UNIVERSITY, Region.SEOUL, Organizer.GOVERNMENT, 100000L, LocalDate.of(2024, 1, 10), LocalDate.of(2024, 3, 10));

		ContestResponse contestResponse = ContestResponse.builder()
			.contestId(contest1.getId())
			.title(contest1.getPost().getTitle())
			.content(contest1.getPost().getContent())
			.view(contest1.getPost().getView())
			.contestCategory(contest1.getContestCategory())
			.target(contest1.getTarget())
			.region(contest1.getRegion())
			.organizer(contest1.getOrganizer())
			.totalPrize(contest1.getTotalPrize())
			.startDate(contest1.getStartDate())
			.endDate(contest1.getEndDate())
			.build();

		PostResponse postResponse = PostResponse.builder()
			.postId(100L)
			.userId("testId")
			.title("자격증 딴 후기")
			.content("정보처리기사 내용")
			.postCategory(PostCategory.FREE_BOARD)
			.view(0L)
			.createdAt("2024.04.01 15:37")
			.modifiedAt("2024.04.01 15:37")
			.build();

		TeamResponse teamResponse = TeamResponse.builder()
			.teamId(100L)
			.teamName("정보처리기사 끝내기!")
			.teamType(TeamType.STUDY)
			.maxMember(5)
			.startDate(LocalDate.of(2024, 1, 10))
			.endDate(LocalDate.of(2024, 2, 10))
			.build();

		TeamResponseWithPageInfo teamResponseWithPageInfo = new TeamResponseWithPageInfo(1);
		teamResponseWithPageInfo.getTeamResponse().add(teamResponse);

		SearchResultResponse responses = SearchResultResponse.builder()
			.certificationList(List.of(certificationResponse))
			.contestList(List.of(contestResponse))
			.postList(List.of(postResponse))
			.teamList(teamResponseWithPageInfo)
			.msg("검색한 키워드 : " + keyword)
			.build();

		given(searchService.findAllByKeyword(any(), any())).willReturn(responses);

		// When
		mockMvc.perform(
				get("/api/search/keyword")
					.queryParam("keyword", keyword)
					.with(csrf()))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("keyword").description("입력할 검색어")
				),
				responseFields(
					fieldWithPath("certificationList").description("검색어를 포함하는 자격증 리스트"),
					fieldWithPath("certificationList[].certificationCode").description("자격증 코드"),
					fieldWithPath("certificationList[].certificationName").description("자격증 이름"),
					fieldWithPath("certificationList[].qualification").description("자격 요건"),
					fieldWithPath("certificationList[].organizer").description("주관 기관"),
					fieldWithPath("certificationList[].registrationLink").description("등록 링크"),
					fieldWithPath("certificationList[].aiSummary").description("AI 요약"),
					fieldWithPath("certificationList[].periodResponse").description("기간 응답"),
					fieldWithPath("certificationList[].examDateResponses").description("시험 날짜 응답"),
					fieldWithPath("contestList").description("검색어를 포함하는 공모전 리스트"),
					fieldWithPath("contestList[].contestId").description("공모전 ID"),
					fieldWithPath("contestList[].title").description("공모전 제목"),
					fieldWithPath("contestList[].content").description("공모전 내용"),
					fieldWithPath("contestList[].view").description("조회수"),
					fieldWithPath("contestList[].contestCategory").description("공모전 카테고리"),
					fieldWithPath("contestList[].target").description("목표 대상"),
					fieldWithPath("contestList[].region").description("지역"),
					fieldWithPath("contestList[].organizer").description("주최자"),
					fieldWithPath("contestList[].totalPrize").description("총 상금"),
					fieldWithPath("contestList[].startDate").description("시작 날짜"),
					fieldWithPath("contestList[].endDate").description("종료 날짜"),
					fieldWithPath("contestList[].images").description("공모전 이미지").optional(),
					fieldWithPath("postList").description("검색어를 포함하는 게시글 리스트"),
					fieldWithPath("postList[].postId").description("게시글 ID"),
					fieldWithPath("postList[].userId").description("사용자 ID"),
					fieldWithPath("postList[].title").description("게시글 제목"),
					fieldWithPath("postList[].content").description("게시글 내용"),
					fieldWithPath("postList[].postCategory").description("게시글 카테고리"),
					fieldWithPath("postList[].view").description("조회수"),
					fieldWithPath("postList[].createdAt").description("생성 날짜"),
					fieldWithPath("postList[].modifiedAt").description("수정 날짜"),
					fieldWithPath("postList[].images").description("게시글 이미지").optional(),
					fieldWithPath("postList[].commentCount").description("댓글 수").optional(),
					fieldWithPath("postList[].postLikeCount").description("좋아요 수").optional(),
					fieldWithPath("postList[].isLiked").description("좋아요 여부").optional(),
					fieldWithPath("teamList").description("검색어를 포함하는 팀 리스트"),
					fieldWithPath("teamList.totalPage").description("총 페이지 수"),
					fieldWithPath("teamList.teamResponse").description("팀 응답"),
					fieldWithPath("teamList.teamResponse[].teamId").description("팀 ID"),
					fieldWithPath("teamList.teamResponse[].teamName").description("팀 이름"),
					fieldWithPath("teamList.teamResponse[].teamType").description("팀 유형"),
					fieldWithPath("teamList.teamResponse[].maxMember").description("최대 멤버 수"),
					fieldWithPath("teamList.teamResponse[].startDate").description("시작 날짜"),
					fieldWithPath("teamList.teamResponse[].endDate").description("종료 날짜"),
					fieldWithPath("teamList.teamResponse[].opened").description("팀 활성화 여부"),
					fieldWithPath("msg").description("요청에 대한 응답").optional()
				)));

		// Then
	}

	
}
