package pulleydoreurae.careerquestbackend.certification.domain.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * 자격증 정보를 검색하는 Request
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
@Getter
@Setter
public class CertificationSearchRequest {

	private LocalDate date;
}
