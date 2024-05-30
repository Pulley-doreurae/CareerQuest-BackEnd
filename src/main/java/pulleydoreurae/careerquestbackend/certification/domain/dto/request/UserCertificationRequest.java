package pulleydoreurae.careerquestbackend.certification.domain.dto.request;

import java.time.LocalDate;

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

	private String userId;
	private String certificationName;
	private LocalDate acqDate;
}
