package pulleydoreurae.careerquestbackend.community.domain.dto.request;

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

	private String userId; // 작성자
	private String title; // 제목
	private String content; // 내용
	private Long category; // 카테고리
}
