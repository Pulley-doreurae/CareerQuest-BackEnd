package pulleydoreurae.careerquestbackend.chat.domain.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomGetResponse {

	private String chatRoomNumber;

	private String chatRoomName;

	private List<String> participants;

	@JsonProperty("lastChatMessageDto")
	private ChatMessageDto lastChatMessageDto;

	public void updateChatMessageDto(ChatMessageDto chatMessageDto){
		this.lastChatMessageDto = chatMessageDto;
	}

	public void quitParticipant(String quitUserId) { this.participants.remove(quitUserId); }
}
