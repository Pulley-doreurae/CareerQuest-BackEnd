package pulleydoreurae.careerquestbackend.certification.domain.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 취득 자격증 정보
 *
 * @author : parkjihyeok
 * @since : 2024/06/10
 */
@Getter
@Setter
@AllArgsConstructor
public class UserCertificationInfo {

	private String certificationName;
	private LocalDate acqDate;
}
