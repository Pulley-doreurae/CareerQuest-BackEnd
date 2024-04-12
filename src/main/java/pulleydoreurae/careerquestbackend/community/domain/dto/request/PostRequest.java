package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 게시글 request
 *
 * @author : parkjihyeok
 * @since : 2024/03/31
 */
@Getter
@AllArgsConstructor
@Builder
public class PostRequest {

	@NotBlank(message = "사용자 아이디는 필수입니다.")
	@Size(min = 5, message = "유효하지 않은 아이디입니다.") // 작성자의 아이디는 5자보다 작을 수 없다.
	private String userId; // 작성자

	@NotBlank(message = "제목은 필수입니다.")
	private String title; // 제목

	@NotBlank(message = "내용은 필수입니다.")
	private String content; // 내용

	@NotNull(message = "카테고리는 필수입니다.")
	private Long category; // 카테고리

	private List<String> images; // 저장한 사진 파일명을 담은 리스트 (null 가능)
}
