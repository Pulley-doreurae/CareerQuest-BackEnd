package pulleydoreurae.careerquestbackend.community.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;

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
	private List<String> images; // 사진 리스트
	private Long view; // 조회수
	private Long commentCount; // 댓글 수
	private Long postLikeCount; // 좋아요 수
	private PostCategory postCategory; // 카테고리
	private Boolean isLiked; // 좋아요 상태 ture -> O, false -> X
	private String createdAt; // 작성일자
	private String modifiedAt; // 수정일자
}
