package pulleydoreurae.careerquestbackend.auth.domain.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 토큰을 반환하는 dto
 *
 * @author : parkjihyeok
 * @since : 2024/01/18
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponse {

	private String userId;
	private String token_type;
	private String access_token;
	private Long expires_in;
	private String refresh_token;
	private Long refresh_token_expires_in;
}
