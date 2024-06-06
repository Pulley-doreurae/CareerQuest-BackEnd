package pulleydoreurae.careerquestbackend.ai.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AI에 질의할 Request
 *
 * @author : parkjihyeok
 * @since : 2024/06/02
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {

	@NotEmpty(message = "검색할 대상 ID는 필수입니다.")
	private String userId; // 검색할 대상ID
	@NotEmpty(message = "검색할 데이터베이스 정보는 필수입니다.")
	private String database; // 질의할 데이터베이스
}
