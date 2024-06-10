package pulleydoreurae.careerquestbackend.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.chat.RedisPublisher;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.domain.MessageSubDto;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomGetResponse;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.PartnerChatRoomGetResponse;
import pulleydoreurae.careerquestbackend.chat.repository.ChatRoomRedisRepository;
import pulleydoreurae.careerquestbackend.chat.type.MessageType;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatService {

	private final RedisPublisher redisPublisher;
	private final ChatRoomRedisRepository chatRoomRedisRepository;
	private final ChatRoomService chatRoomService;

	/**
	 * 채팅방에 메시지 발송
	 */
	public void sendChatMessage(ChatMessageDto chatMessage) {

		String userId = chatMessage.getUserId();

		// 1. 채팅방 리스트에 새로운 채팅방 정보가 없다면, 넣어준다. 마지막 메시지도 같이 담는다. 상대방 레디스에도 업데이트 해준다.
		ChatRoomGetResponse newChatRoom = null;
		if (chatRoomRedisRepository.existChatRoom(userId, chatMessage.getRoomId())) {
			newChatRoom = chatRoomRedisRepository.getChatRoom(userId, chatMessage.getRoomId());
		} else {
			newChatRoom = chatRoomService.getChatRoomInfo(chatMessage.getRoomId());
		}

		setNewChatRoomInfo(chatMessage, newChatRoom);

		// 2. 마지막 메시지들이 담긴 채팅방 리스트들을 가져온다.
		List<ChatRoomGetResponse> chatRoomList = chatRoomService.getChatRoomList(userId);

		// 3. 파트너 채팅방 리스트도 가져온다.
		List<PartnerChatRoomGetResponse> partnerChatRoomList = getChatRoomListByPartners(chatMessage, newChatRoom);

		// 4. 마지막 메세지 기준으로 정렬 채팅방 리스트 정렬
		chatRoomList = chatRoomService.sortChatRoomListLatest(chatRoomList);

		// 5. 메시지 제작 및 발행
		MessageSubDto messageSubDto = MessageSubDto.builder()
			.userId(userId)
			.chatMessageDto(chatMessage)
			.list(chatRoomList)
			.partnerList(partnerChatRoomList)
			.build();

		redisPublisher.publish(messageSubDto);
	}


	/**
	 * redis 에 채팅방 정보가 없는 경우 새로 저장.
	 * @param chatMessage
	 */
	private void setNewChatRoomInfo(ChatMessageDto chatMessage, ChatRoomGetResponse newChatRoom) {

		newChatRoom.updateChatMessageDto(chatMessage);

		/** 상대방 채팅 리스트와 내 리스트 둘다 채팅방을 저장한다. */
		// 내가 전송한 메시지를 다른 사람들한테도 저장
		for(String userId : newChatRoom.getParticipants()){
			if (chatMessage.getType().equals(MessageType.QUIT) && userId.equals(chatMessage.getUserId())) { continue;}
			chatRoomRedisRepository.setChatRoom(userId,
				chatMessage.getRoomId(), newChatRoom);
		}

	}

	// redis에서 채팅방 리스트 불러오는 로직
	private List<PartnerChatRoomGetResponse> getChatRoomListByPartners(ChatMessageDto chatMessage, ChatRoomGetResponse response) {

		List<PartnerChatRoomGetResponse> partnerChatRoomGetResponses = new ArrayList<>();

		for(String partnerId : response.getParticipants()) {
			if (chatRoomRedisRepository.existChatRoomList(partnerId) && !partnerId.equals(chatMessage.getUserId())) {
				List<ChatRoomGetResponse> chatRoomListGetResponseList = chatRoomRedisRepository.getChatRoomList(partnerId);
				for (ChatRoomGetResponse chatRoomListGetResponse : chatRoomListGetResponseList) {
					if(chatMessage.getType().equals(MessageType.QUIT)) { chatRoomListGetResponse.quitParticipant(chatMessage.getUserId()); }
					chatRoomService.setListChatLastMessage(chatRoomListGetResponse);
				}
				chatRoomService.sortChatRoomListLatest(chatRoomListGetResponseList);
				partnerChatRoomGetResponses.add(
					PartnerChatRoomGetResponse.builder()
						.partnerId(partnerId)
						.list(chatRoomListGetResponseList)
						.build()
				);
			}
		}

		return partnerChatRoomGetResponses;
	}

}
