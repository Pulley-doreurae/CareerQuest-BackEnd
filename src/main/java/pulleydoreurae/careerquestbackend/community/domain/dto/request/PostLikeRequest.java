package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId; // 사용자 아이디

	@NotNull(message = "게시글 아이디는 null 일 수 없습니다.")
	private Long postId; // 게시글 번호

	@NotNull(message = "좋아요 상태는 null 일 수 없습니다.")
	private Boolean isLiked; // 좋아요 상태 ture -> O, false -> X
}
