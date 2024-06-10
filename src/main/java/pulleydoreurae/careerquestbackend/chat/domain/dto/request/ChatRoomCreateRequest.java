package pulleydoreurae.careerquestbackend.chat.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomCreateRequest {

	private String userId;
	private String chatRoomName;
}
