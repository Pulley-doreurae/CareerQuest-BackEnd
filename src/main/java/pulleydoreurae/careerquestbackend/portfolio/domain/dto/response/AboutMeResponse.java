package pulleydoreurae.careerquestbackend.portfolio.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.portfolio.domain.entity.AboutMe;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutMeResponse {
	private String content;
	private String userId;

	public AboutMeResponse aboutMeFrom(AboutMe aboutMe){
		return AboutMeResponse.builder()
			.content(aboutMe.getContent())
			.userId(aboutMe.getUserAccount().getUserId())
			.build();
	}

}
