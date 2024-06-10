package pulleydoreurae.careerquestbackend.chat.domain.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String chatRoomName; // 채팅방 이름
	private String chatRoomNumber; // 채팅방 번호

	public static ChatRoom create(String chatRoomName){
		String chatRoomNumber = UUID.randomUUID().toString();
		return ChatRoom.builder()
			.chatRoomName(chatRoomName)
			.chatRoomNumber(chatRoomNumber)
			.build();
	}

}
