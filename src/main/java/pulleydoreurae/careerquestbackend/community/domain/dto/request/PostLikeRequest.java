package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 좋아요 request
 *
 * @author : parkjihyeok
 * @since : 2024/04/02
 */
@Getter
@AllArgsConstructor
@Builder
public class PostLikeRequest {

	private String userId; // 사용자 아이디
	private Long postId; // 게시글 번호
	private int isLiked; // 좋아요 상태 1이면 눌림, 0이면 안눌림
}
