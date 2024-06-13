package pulleydoreurae.careerquestbackend.search.domain.response;

import org.springframework.data.redis.core.ZSetOperations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRankResponse {

	private String keyword;
	private String rankChange;
	private int rank;

}
