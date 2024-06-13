package pulleydoreurae.careerquestbackend.community.domain;

import lombok.Getter;

/**
 * 개최지역
 *
 * @author : parkjihyeok
 * @since : 2024/06/13
 */
@Getter
public enum Region {
	ALL("전체"),
	ONLINE("온라인"),
	NATIONAL("전국"),
	SEOUL("서울"),
	INCHEON("인천"),
	DAEJEON("대전"),
	DAEGU("대구"),
	BUSAN("부산"),
	ULSAN("울산"),
	GWANGJU("광주");

	private final String region;

	Region(String region) {
		this.region = region;
	}
}











