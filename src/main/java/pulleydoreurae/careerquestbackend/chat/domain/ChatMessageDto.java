package pulleydoreurae.careerquestbackend.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatMessage;
import pulleydoreurae.careerquestbackend.chat.type.MessageType;

/**
 * 메시지 DTO
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

	private MessageType type;	// 메시지 타입
	private String roomId;		// 방 번호
	private String userId; // 전송한 유저
	private String message; // 메시지 내용
	private String time; // 전송 시간


	public static ChatMessageDto fromEntity(ChatMessage chatMessage){
		return ChatMessageDto.builder()
			.type(chatMessage.getType())
			.userId(chatMessage.getUserId())
			.roomId(chatMessage.getRoomId())
			.message(chatMessage.getMessage())
			.time(chatMessage.getTime())
			.build();

	}

}
