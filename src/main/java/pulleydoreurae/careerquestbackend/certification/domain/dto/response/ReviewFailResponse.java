package pulleydoreurae.careerquestbackend.certification.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 후기 저장, 수정에 실패하면 반환할 dto
 *
 * @author : parkjihyeok
 * @since : 2024/05/24
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class ReviewFailResponse {

	private String title;
	private String content;
	private String certificationName;
	private String[] errors;
}
