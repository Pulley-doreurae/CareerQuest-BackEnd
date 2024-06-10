package pulleydoreurae.careerquestbackend.chat.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomGetResponse;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.PartnerChatRoomGetResponse;

/**
 * 메세지를 받는 사람들의 처리를 담은 DTO
 *
 */
@Getter
@Setter
@Builder
public class MessageSubDto {
	// 본인 userId
	private String userId;
	// 채팅 메시지
	private ChatMessageDto chatMessageDto;
	// 본인 채팅방 리스트
	private List<ChatRoomGetResponse> list;
	// 상대방 채팅방 리스트
	private List<PartnerChatRoomGetResponse> partnerList;
}
