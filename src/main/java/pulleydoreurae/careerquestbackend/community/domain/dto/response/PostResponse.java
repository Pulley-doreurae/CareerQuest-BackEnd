package pulleydoreurae.careerquestbackend.community.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 response
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class PostResponse {

	private String userId; // 작성자
	private String title; // 제목
	private String content; // 내용
	private Long hit; // 조회수
	private Long likeCount; // 좋아요
	private Long category; // 카테고리
	private String createdAt; // 작성일자
	private String modifiedAt; // 수정일자
}
