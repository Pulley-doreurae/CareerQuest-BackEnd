package pulleydoreurae.careerquestbackend.chat.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatRoom;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponse {

	private String userId;
	private String chatRoomNumber;
	private String msg;

	public static ChatRoomResponse fromEntity(UserAccount user, ChatRoom chatRoom, String msg){
		return ChatRoomResponse.builder()
			.userId(user.getUserId())
			.chatRoomNumber(chatRoom.getChatRoomNumber())
			.msg(msg)
			.build();
	}


}
