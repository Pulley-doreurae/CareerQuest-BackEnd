package pulleydoreurae.careerquestbackend.certification.domain.dto.response;

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
public class ReviewResponse {

	private Long reviewId;
	private String userId; // 작성자
	private String title; // 제목
	private String content; // 내용
	private String certificationName; // 자격증명
	private Long view; // 조회수
	private Long postLikeCount; // 좋아요 수
	private Boolean isLiked; // 좋아요 상태 ture -> O, false -> X
	private String createdAt; // 작성일자
	private String modifiedAt; // 수정일자
}
