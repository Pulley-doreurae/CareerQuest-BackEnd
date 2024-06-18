package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.MBTI;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMBTIRequest {

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	private String userId;
	@NotNull(message = "변경할 MBTI는 필수입니다.")
	private MBTI mbti;
}
