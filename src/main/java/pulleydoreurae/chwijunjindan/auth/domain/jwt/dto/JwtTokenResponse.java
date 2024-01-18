package pulleydoreurae.chwijunjindan.auth.domain.jwt.dto;

import lombok.Builder;
import lombok.Getter;
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
public class JwtTokenResponse {

	private String token_type;
	private String access_token;
	private Integer expires_in;
	private String refresh_token;
	private Integer refresh_token_expires_in;
}
