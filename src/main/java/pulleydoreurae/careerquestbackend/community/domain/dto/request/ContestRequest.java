package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pulleydoreurae.careerquestbackend.community.domain.ContestCategory;
import pulleydoreurae.careerquestbackend.community.domain.Organizer;
import pulleydoreurae.careerquestbackend.community.domain.Region;
import pulleydoreurae.careerquestbackend.community.domain.Target;

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

	@NotNull(message = "공모전 분야는 필수입니다.")
	private ContestCategory contestCategory; // 공모전 분야
	@NotNull(message = "공모전 대상은 필수입니다.")
	private Target target; // 대상
	@NotNull(message = "공모전 개최지역은 필수입니다.")
	private Region region; // 개최지역
	@NotNull(message = "공모전 주관처는 필수입니다.")
	private Organizer organizer; // 주관처
	@NotNull(message = "공모전 상금은 필수입니다.")
	private Long totalPrize; // 총상금
}
