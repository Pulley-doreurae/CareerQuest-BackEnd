package pulleydoreurae.careerquestbackend.chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.service.ChatMongoService;
import pulleydoreurae.careerquestbackend.chat.service.ChatService;

/**
 * websocket에서 들어오는 메시징을 처리하는 컨트롤러
 *
 */
@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {

	private final ChatMongoService chatMongoService;
	private final ChatService chatService;

	/**
	 * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
	 */
	@MessageMapping("/chat/message")
	public void message(ChatMessageDto message) {

		ChatMessageDto chatMessageDto = chatMongoService.save(message);
		chatService.sendChatMessage(chatMessageDto);

	}

}
