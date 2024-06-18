package pulleydoreurae.careerquestbackend.portfolio.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAboutMeRequest {
	private String userId;
	private String aboutMe;
}


