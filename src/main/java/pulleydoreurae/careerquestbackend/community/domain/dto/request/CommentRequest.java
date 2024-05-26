package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId; // 작성자

	// 게시글 id 는 PathVariable 로 들어오므로 검증할 필요가 없다.
	private Long postId; // 게시글 id

	@NotBlank(message = "내용은 필수입니다.")
	private String content; // 댓글 내용
}
