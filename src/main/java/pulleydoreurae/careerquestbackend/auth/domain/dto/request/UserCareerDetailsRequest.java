package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 직무정보 요청
 *
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
@Getter
@Setter
public class UserCareerDetailsRequest {

	private String userId; // 사용자 아이디
	private Long majorCategory; // 대분류
	private Long middleCategory; // 중분류
	private Long smallCategory; // 소분류
}
