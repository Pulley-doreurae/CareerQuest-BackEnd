package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 공모전 Request
 *
 * @author : parkjihyeok
 * @since : 2024/05/26
 */
@Getter
@AllArgsConstructor
@Builder
public class ContestRequest {

	@NotBlank(message = "공모전 분야는 필수입니다.")
	private String contestCategory; // 공모전 분야 -> enum으로 변경 가능할듯?
	@NotBlank(message = "공모전 대상은 필수입니다.")
	private String target; // 대상 -> enum으로 변경 가능할듯?
	@NotBlank(message = "공모전 개최지역은 필수입니다.")
	private String region; // 개최지역
	@NotBlank(message = "공모전 주관처는 필수입니다.")
	private String organizer; // 주관처
	@NotNull(message = "공모전 상금은 필수입니다.")
	private Long totalPrize; // 총상금
}
