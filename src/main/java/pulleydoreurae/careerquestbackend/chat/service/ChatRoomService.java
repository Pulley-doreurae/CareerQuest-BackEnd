package pulleydoreurae.careerquestbackend.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.service.UserAccountService;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.domain.dto.request.ChatRoomCreateRequest;
import pulleydoreurae.careerquestbackend.chat.domain.dto.request.JoinChatRoomRequest;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomGetResponse;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomResponse;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatMessage;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatRoom;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatRoomMember;
import pulleydoreurae.careerquestbackend.chat.repository.ChatRoomMemberRepository;
import pulleydoreurae.careerquestbackend.chat.repository.ChatRoomRedisRepository;
import pulleydoreurae.careerquestbackend.chat.repository.ChatRoomRepository;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatRoomService {

	private final UserAccountService userAccountService;
	private final ChatRoomRedisRepository chatRoomRedisRepository;
	private final ChatRoomMemberRepository chatRoomMemberRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChatMongoService chatMongoService;

	/**
	 * 채팅방 정보 가져오기
	 *
	 * @param roomId
	 * @return
	 */
	public ChatRoomGetResponse getChatRoomInfo(String roomId) {
		ChatRoom chatRoom = chatRoomRepository.findByChatRoomNumber(roomId).orElseThrow();

		return ChatRoomGetResponse.builder()
			.chatRoomNumber(chatRoom.getChatRoomNumber())
			.chatRoomName(chatRoom.getChatRoomName())
			.participants(chatRoomMemberRepository.findUserByChatRoom(chatRoom))
			.build();
	}

	/**
	 * 초기
	 *  요청한 유저가 속해있는 채팅 리스트를 가져오는 메서드
	 *
	 * @param userId 요청한 유저
	 * @return
	 */
	public List<ChatRoomGetResponse> findChatRoomListByUserId(String userId){
		// 처음 HTTP 요청에서는 무조건 레디스 초기화 진행하도록 로직 수정
		// RDB에서 유저의 해당하는 채팅방 리스트를 가져옴
		List<ChatRoomMember> chatRoomList = chatRoomMemberRepository.findAllByUser(userId);

		// RDB에서 가져온 리스트를 ChatRoomGetResponse 형식으로 변경
		List<ChatRoomGetResponse> chatRoomListGetResponseList = new ArrayList<>();

		for(ChatRoomMember crm : chatRoomList){
			ChatRoom cr = crm.getChatRoom();
			chatRoomListGetResponseList.add(
				ChatRoomGetResponse.builder()
					.chatRoomNumber(cr.getChatRoomNumber())
					.chatRoomName(cr.getChatRoomName())
					.participants(chatRoomMemberRepository.findUserByChatRoom(cr))
					.build()
			);
		}

		chatRoomListGetResponseList.forEach(this::setListChatLastMessage);
		chatRoomRedisRepository.initChatRoomList(userId, chatRoomListGetResponseList);
		return sortChatRoomListLatest(chatRoomListGetResponseList);
	}

	/**
	 * 초기 이후 부터
	 * 채팅리스트 가져오기
	 *
	 * @param userId
	 * @return
	 */
	public List<ChatRoomGetResponse> getChatRoomList(String userId) {

		List<ChatRoomGetResponse> chatRoomListGetResponseList = null;
		if (chatRoomRedisRepository.existChatRoomList(userId)) {
			chatRoomListGetResponseList = chatRoomRedisRepository.getChatRoomList(userId);
			log.info(chatRoomListGetResponseList.toString());
		} else {
			// 채팅방이 레디스에 없으면 페인 사용해서 불러온다!
			chatRoomListGetResponseList = findChatRoomListByUserId(userId);
			chatRoomRedisRepository.initChatRoomList(userId, chatRoomListGetResponseList);
		}

		chatRoomListGetResponseList.forEach(this::setListChatLastMessage);

		return chatRoomListGetResponseList;
	}


	/**
	 * 몽고 디비에서 마지막 메시지 가져와서 저장하는 로직
	 *
	 * @param chatRoomListGetResponse
	 */
	public void setListChatLastMessage(ChatRoomGetResponse chatRoomListGetResponse) {

		// 몽고 디비에서 마지막 메시지 가져와서 저장.
		String chatRoomNumber = chatRoomListGetResponse.getChatRoomNumber();
		if (chatRoomRedisRepository.getLastMessage(chatRoomNumber) != null) {
			chatRoomListGetResponse.updateChatMessageDto(
				chatRoomRedisRepository.getLastMessage(chatRoomNumber)
			);
		} else {
			ChatMessage chatMessage = chatMongoService.findLatestMessageByRoomId(chatRoomNumber);
			if (chatMessage != null) {
				chatRoomListGetResponse.updateChatMessageDto(
					ChatMessageDto.fromEntity(chatMessage)
				);
			}
		}
	}

	/**
	 * 채팅방 마지막 메시지의 시간들을 비교하여 정렬하는 메소드
	 *
	 * @param chatRoomListGetResponseList
	 */
	public List<ChatRoomGetResponse> sortChatRoomListLatest (
		List<ChatRoomGetResponse> chatRoomListGetResponseList
	) {
		List<ChatRoomGetResponse> newChatRoomList = new ArrayList<>();
		for (ChatRoomGetResponse response : chatRoomListGetResponseList) {
			if (response.getLastChatMessageDto() != null) newChatRoomList.add(response);
		}

		if(newChatRoomList.isEmpty()) return chatRoomListGetResponseList;

		newChatRoomList.sort((o1, o2) ->
			o2.getLastChatMessageDto().getTime().compareTo(o1.getLastChatMessageDto().getTime()));

		return newChatRoomList;
	}

	/**
	 * 채팅방 생성
	 *
	 * @param chatRoomCreateRequest
	 * @return
	 */
	@Transactional
	public ChatRoomResponse createChatRoom(ChatRoomCreateRequest chatRoomCreateRequest) {

		ChatRoom chatRoom = ChatRoom.create(chatRoomCreateRequest.getChatRoomName());
		chatRoomRepository.save(chatRoom);
		UserAccount user = userAccountService.findUserByUserId(chatRoomCreateRequest.getUserId());

		ChatRoomMember chatRoomMember = ChatRoomMember.builder()
			.user(user)
			.chatRoom(chatRoom)
			.build();
		chatRoomMemberRepository.save(chatRoomMember);

		if(chatRoomRedisRepository.existChatRoomList(user.getUserId())){
			chatRoomRedisRepository.setChatRoom(user.getUserId(), chatRoom.getChatRoomNumber(), getChatRoomInfo(chatRoom.getChatRoomNumber()));
		}

		return ChatRoomResponse.fromEntity(user, chatRoom, "채팅방 생성을 성공했습니다.");
	}

	/**
	 * 채팅방 참가
	 *
	 * @param request
	 * @return
	 */
	public ChatRoomResponse joinChatRoom(JoinChatRoomRequest request){

		ChatRoom chatRoom = chatRoomRepository.findByChatRoomNumber(request.getChatRoomNumber()).orElseThrow(
			()-> new UsernameNotFoundException("채팅방이 없습니다."));
		UserAccount user = userAccountService.findUserByUserId(request.getUserId());

		ChatRoomMember chatRoomMember = ChatRoomMember.builder()
			.user(user)
			.chatRoom(chatRoom)
			.build();
		chatRoomMemberRepository.save(chatRoomMember);
		if(chatRoomRedisRepository.existChatRoomList(user.getUserId())){
			chatRoomRedisRepository.setChatRoom(user.getUserId(), chatRoom.getChatRoomNumber(), getChatRoomInfo(chatRoom.getChatRoomNumber()));
		}

		return ChatRoomResponse.fromEntity(user, chatRoom, "채팅방 참가를 성공했습니다.");
	}
	// redis에 채팅방

	/**
	 * 채팅방 삭제 로직
	 *
	 * @param userId
	 * @param roomId
	 */
	@Transactional
	public void deleteChatRoom(String userId, String roomId) {
		// 해당하는 유저 및 채팅방 찾기
		UserAccount user = userAccountService.findUserByUserId(userId);
		ChatRoom chatRoom = chatRoomRepository.findByChatRoomNumber(roomId).orElseThrow();

		// 해당하는 유저를 채팅방에서 탈퇴
		chatRoomMemberRepository.deleteByUserAndChatRoom(user, chatRoom);
		chatRoomRedisRepository.deleteChatRoom(userId, roomId);


		// 탈퇴 후 채팅방에 아무도 없으면 그 채팅방 까지 삭제
		if(!chatRoomMemberRepository.existsByChatRoom(chatRoom)){
			chatRoomRepository.deleteByChatRoomNumber(roomId);
		}

	}
}
