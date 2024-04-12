package pulleydoreurae.careerquestbackend.community.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 저장, 수정에 실패하면 반환할 dto
 *
 * @author : parkjihyeok
 * @since : 2024/04/07
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class PostFailResponse {

	private String title;
	private String content;
	private Long category;
	private String[] errors;
}
