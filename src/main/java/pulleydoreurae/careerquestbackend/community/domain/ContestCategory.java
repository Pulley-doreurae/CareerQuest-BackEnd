package pulleydoreurae.careerquestbackend.community.domain;

import lombok.Getter;

/**
 * 공모전 분야
 *
 * @author : parkjihyeok
 * @since : 2024/06/13
 */
@Getter
public enum ContestCategory {
	ARCHITECTURE("건축"),
	GAME_SOFTWARE("게임/소프트웨어"),
	SCIENCE("과학"),
	MARKETING("광고/마케팅"),
	PLANNING_IDEA("기획/아이디어"),
	THESIS("논문"),
	NAMING_SLOGAN("네이밍/슬로건"),
	CONTEST("대회"),
	DESIGN("디자인"),
	COMIC_CHARACTER("만화/캐릭터"),
	LITERATURE_ESSAY("문학/수기"),
	ART("미술"),
	PHOTOGRAPHY("사진"),
	MUSIC("음악"),
	EVENT("이벤트"),
	EMPLOYMENT_STARTUP("취업/창업"),
	VIDEO_UCC("영상/ucc"),
	OVERSEAS("해외");

	private final String category;

	ContestCategory(String category) {
		this.category = category;
	}
}
