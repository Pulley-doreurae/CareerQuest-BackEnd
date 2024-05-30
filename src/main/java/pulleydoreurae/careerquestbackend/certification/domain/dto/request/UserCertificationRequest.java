package pulleydoreurae.careerquestbackend.certification.domain.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 취득 자격증 Request
 *
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCertificationRequest {

	@NotBlank(message = "회원ID는 공백일 수 없습니다.")
	private String userId;
	@NotBlank(message = "자격증 이름은 공백일 수 없습니다.")
	private String certificationName;
	@NotNull(message = "취득일자는 null일 수 없습니다.")
	private LocalDate acqDate;
}
