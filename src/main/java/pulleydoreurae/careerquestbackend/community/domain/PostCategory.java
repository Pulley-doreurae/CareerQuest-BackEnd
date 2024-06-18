package pulleydoreurae.careerquestbackend.community.domain;

import lombok.Getter;

/**
 * 게시글 카테고리
 *
 * @author : parkjihyeok
 * @since : 2024/05/23
 */
@Getter
public enum PostCategory {
	NOTICE("공지사항"),
	CERTIFICATION_BOARD("자격증게시판"),
	CONTEST("공모전"),
	CONTEST_BOARD("공모전게시판"),
	FREE_BOARD("자유게시판"),
	QNA_BOARD("질문게시판"),
	STUDY_BOARD("스터디게시판");

	private final String category;

	PostCategory(String category) {
		this.category = category;
	}
}
