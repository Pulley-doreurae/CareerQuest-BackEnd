package pulleydoreurae.careerquestbackend.community.domain.dto.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 게시글 + 공모전에 대한 정보를 담은 Request
 *
 * @author : parkjihyeok
 * @since : 2024/05/26
 */
@Getter
@AllArgsConstructor
@Builder
public class PostAndContestRequest {

	@Valid
	private PostRequest postRequest;

	@Valid
	private ContestRequest contestRequest;
}
