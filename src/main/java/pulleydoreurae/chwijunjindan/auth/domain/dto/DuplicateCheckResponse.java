package pulleydoreurae.chwijunjindan.auth.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 중복확인에 대한 응답을 담은 dto 클래스
 *
 * @author : parkjihyeok
 * @since : 2/26/24
 */
@Getter
@Setter
@Builder
public class DuplicateCheckResponse {

	private String field;
	private String msg;
}
