package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 요청한 유저 정보를 담아서 반환하는 Response
 *
 * @author : hanjaeseong
 */
@Getter
@Setter
@Builder
public class UserDetailsResponse {

	private String userId;
	private String email;
	private String smallCategory; // 소분류
	private List<String> technologyStacks;
}
