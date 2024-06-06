package pulleydoreurae.careerquestbackend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AI 질의 결과를 담을 Response
 *
 * @author : parkjihyeok
 * @since : 2024/06/06
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiResponse {
	String[] name;
}
