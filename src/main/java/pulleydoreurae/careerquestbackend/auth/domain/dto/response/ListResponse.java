package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 특정 목록들과 간단한 msg를 담는 Response
 *
 */
@Getter
@Setter
@Builder
public class ListResponse {

    private List<?> lists;
    private String msg;
}
