package pulleydoreurae.careerquestbackend.team.domain.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 검색한 팀의 정보와 페이지 정보를 담은 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/01
 */
@Getter
@Setter
@EqualsAndHashCode
public class TeamResponseWithPageInfo {

	private int totalPage; // 검색 내용의 전체 페이지 수
	private List<TeamResponse> teamResponse = new ArrayList<>(); // 각 팀의 정보

	public TeamResponseWithPageInfo(int totalPage) {
		this.totalPage = totalPage;
	}
}
