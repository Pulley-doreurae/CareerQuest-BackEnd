package pulleydoreurae.careerquestbackend.community.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 참여한 공모전 정보
 *
 * @author : parkjihyeok
 * @since : 2024/06/16
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class JoinContestResponse {

	private Long postId; // 게시글 ID
	private Long contestId; // 공모전 ID
	private Long joinContestId; // 참여 공모전 ID
	private String userId; // 참여자 ID
	private String title; // 공모전 제목
}
