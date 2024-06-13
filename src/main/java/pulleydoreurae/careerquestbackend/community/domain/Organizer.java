package pulleydoreurae.careerquestbackend.community.domain;

import lombok.Getter;

/**
 * 주관처
 *
 * @author : parkjihyeok
 * @since : 2024/06/13
 */
@Getter
public enum Organizer {
	ALL("전체"),
	GOVERNMENT("정부"),
	LOCAL_GOVERNMENT("지자체"),
	PUBLIC_INSTITUTION("공공기관");

	private final String region;

	Organizer(String region) {
		this.region = region;
	}
}
