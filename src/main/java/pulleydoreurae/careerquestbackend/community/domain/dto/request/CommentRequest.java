package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 댓글 request
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class CommentRequest {

	private String userId; // 작성자
	private Long postId; // 게시글 id
	private String content; // 댓글 내용
}
