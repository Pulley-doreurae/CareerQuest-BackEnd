package pulleydoreurae.careerquestbackend.chat.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.type.MessageType;

/**
 * MongoDB에 저장되는 메시지 엔티티
 *
 */
@Getter
@Builder
@AllArgsConstructor
@Document(collection = "chat")
public class ChatMessage {

	private MessageType type;	// 메시지 타입
	private String roomId;		// 방 번호
	private String userId; // 전송한 유저
	private String message; // 메시지 내용
	private String time; // 메세지 전송 시간

	public static ChatMessage of(ChatMessageDto dto){
		return ChatMessage.builder()
			.type(dto.getType())
			.roomId(dto.getRoomId())
			.userId(dto.getUserId())
			.message(dto.getMessage())
			.time(LocalDateTime.now().toString())
			.build();
	}

}
