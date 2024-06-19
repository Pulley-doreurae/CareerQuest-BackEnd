package pulleydoreurae.careerquestbackend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * AI 질의결과
 *
 * @author : parkjihyeok
 * @since : 2024/06/19
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {

	private Long id; // 엔티티의 id값
	private String image; // 이미지가 있는 경우 이미지 경로
	private String name; // 이름
	private String value; // AI 질의 결과값
}
