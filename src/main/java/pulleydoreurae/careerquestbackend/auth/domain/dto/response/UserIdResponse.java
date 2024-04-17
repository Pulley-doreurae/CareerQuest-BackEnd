package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * userId와 간단한 msg를 담는 Response
 *
 * @author : hanjaeseong
 * @since : 2024/04/03
 */

@Getter
@Setter
@Builder
public class UserIdResponse {

    private String userId;
    private String msg;

}
