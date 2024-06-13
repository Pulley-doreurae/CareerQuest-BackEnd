package pulleydoreurae.careerquestbackend.community.domain;

import lombok.Getter;

/**
 * 공모전 대상자
 *
 * @author : parkjihyeok
 * @since : 2024/06/13
 */
@Getter
public enum Target {
	EVERYONE("누구나"),
	KINDERGARTEN("유치원"),
	ELEMENTARY("초등학생"),
	MIDDLE_SCHOOL("중학생"),
	HIGH_SCHOOL("고등학생"),
	UNIVERSITY("대학생"),
	GRADUATE("대학원생"),
	GENERAL("일반인"),
	FOREIGNER("외국인"),
	OTHER("기타");

	private final String target;

	Target(String target) {
		this.target = target;
	}
}
