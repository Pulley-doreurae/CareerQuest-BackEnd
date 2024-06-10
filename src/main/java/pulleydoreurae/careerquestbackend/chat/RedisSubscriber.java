package pulleydoreurae.careerquestbackend.chat;

import java.util.List;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.domain.MessageSubDto;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomGetResponse;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.PartnerChatRoomGetResponse;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisSubscriber {

	private final ObjectMapper objectMapper;
	private final SimpMessageSendingOperations messagingTemplate;

	/**
	 * Redis에서 메시지가 발생(publish) 되면
	 * 대기하고 있던 Redis Subscriber가 해당
	 * @param publishMessage
	 */
	public void sendMessage(String publishMessage){
		try {
			ChatMessageDto chatMessage = objectMapper.readValue(publishMessage, MessageSubDto.class).getChatMessageDto();
			log.info("Redis Subscriber chatMSG : {}", chatMessage.getMessage());
			// 채팅방을 구독한 클라이언트에게 메시지 발송
			messagingTemplate.convertAndSend("/sub/chat/room" + chatMessage.getRoomId(), chatMessage);
		} catch (Exception e){
			log.error("Exception {}", e.getMessage());
		}
	}

	/**
	 * 메세지를 보냈을때 채팅방을 최상단으로 옮기기
	 *
	 * @param publishMessage
	 */
	public void sendRoomList(String publishMessage) {
		try {
			log.info("Redis Subcriber roomList ing.. ");

			MessageSubDto dto = objectMapper.readValue(publishMessage, MessageSubDto.class);

			ChatMessageDto chatMessage = dto.getChatMessageDto();

			// 내 user
			String userId = dto.getUserId();

			// 내 채팅방 가져오기
			List<ChatRoomGetResponse> chatRoomListGetResponseList = dto.getList();


			// 로그인 유저 채팅방 리스트 최신화 -> 내 계정에 보냄
			messagingTemplate.convertAndSend(
				"/sub/chat/roomlist/" + userId, chatRoomListGetResponseList
			);


			for(PartnerChatRoomGetResponse p : dto.getPartnerList()){
				// 상대 user
				String partnerId = p.getPartnerId();
				// 상대방 채팅방 가져오기
				List<ChatRoomGetResponse> chatRoomListGetResponseListPartner = p.getList();

				// 파트너 계정에도 리스트 최신화 보냄.
				messagingTemplate.convertAndSend(
					"/sub/chat/roomlist/" + partnerId, chatRoomListGetResponseListPartner
				);

			}

		} catch (Exception e) {
			log.error("Exception {}", e);
		}
	}

}
