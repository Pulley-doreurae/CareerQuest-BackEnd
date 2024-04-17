package pulleydoreurae.careerquestbackend.auth.domain.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId; // 회원아이디
	private List<String> stacks; // 기술스택 id
}
