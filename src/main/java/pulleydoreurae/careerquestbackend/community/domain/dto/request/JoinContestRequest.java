package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 공모전 참여 Request
 *
 * @author : parkjihyeok
 * @since : 2024/06/16
 */
@Getter
@AllArgsConstructor
@Builder
public class JoinContestRequest {

	@NotNull(message = "공모전ID는 필수입니다.")
	private Long contestId;
	@NotBlank(message = "회원ID는 필수입니다.")
	private String userId;
}
