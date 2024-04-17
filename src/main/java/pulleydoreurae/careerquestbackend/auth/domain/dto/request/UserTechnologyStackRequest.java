package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 기술스택 요청
 *
 * @author : parkjihyeok
 * @since : 2024/03/26
 */
@Getter
@Setter
public class UserTechnologyStackRequest {

	private String userId; // 회원아이디
	private List<String> stacks; // 기술스택 id
}
