package pulleydoreurae.careerquestbackend.chat.domain.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 상대방 계정의 채팅방 리스트를 가져오는 response
 *
 */
@Getter
@Setter
@Builder
public class PartnerChatRoomGetResponse {

	private String partnerId;	// 상대방의 계정
	private List<ChatRoomGetResponse> list;	// 상대방의 채팅 리스트
}
