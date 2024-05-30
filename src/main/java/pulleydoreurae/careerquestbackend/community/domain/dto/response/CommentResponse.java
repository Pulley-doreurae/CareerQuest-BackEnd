package pulleydoreurae.careerquestbackend.community.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 response
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class CommentResponse {

	private Long commentId;
	private String userId;
	private Long postId;
	private String content;
	private String createdAt;
	private String modifiedAt;
}
