package pulleydoreurae.careerquestbackend.certification.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 사용자가 취득한 자격증 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/10
 */
@Getter
@Setter
@AllArgsConstructor
public class UserCertificationResponse {
	private String userId;
	private List<UserCertificationInfo> certificationInfos;
}
