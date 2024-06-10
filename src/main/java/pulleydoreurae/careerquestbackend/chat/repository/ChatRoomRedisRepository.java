package pulleydoreurae.careerquestbackend.chat.repository;

import java.util.List;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomGetResponse;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRoomRedisRepository {

	private static final String CHAT_ROOM_KEY = "_CHAT_ROOM_RESPONSE_LIST";
	private static final String CHAT_ROOM = "CHAT_ROOM_LAST_MSG"; //채팅방 마지막 메시지 저장

	@Resource(name = "redisChatTemplate")
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;
	@Resource(name = "redisChatTemplate")
	private HashOperations<String, String, ChatRoomGetResponse> opsHashChatRoom;
	@Resource(name = "redisChatTemplate")
	private HashOperations<String, String, ChatMessageDto> opsHashLastChatMessage;

	/**
	 * 채팅방 키를 반환하는 메서드
	 *
	 * @param userId 요청한 유저
	 * @return	해당 유저의 채팅방 키
	 */
	private String getChatRoomKey(String userId){
		return userId + CHAT_ROOM_KEY;
	}

	/**
	 * 요청한 유저의 채팅방 목록이 존재하는지 확인하는 메서드
	 *
	 * @param userId 요청한 유저
	 * @return true: 있음 | false: 없음
	 */
	public boolean existChatRoomList(String userId){
		return redisTemplate.hasKey(getChatRoomKey(userId));
	}

	/**
	 * 처음 채팅방에 연결했을 때 채팅방 리스트를 redis 에 가져오는 메서드
	 *
	 * @param userId 요청한 유저
	 * @param list  요청한 유저의 채팅방 리스트
	 */
	public void initChatRoomList(String userId, List<ChatRoomGetResponse> list){
		if(redisTemplate.hasKey(getChatRoomKey(userId))){
			redisTemplate.delete(getChatRoomKey(userId));
		}

		opsHashChatRoom = redisTemplate.opsForHash();
		for(ChatRoomGetResponse chatRoomGetResponse : list){
			setChatRoom(userId, chatRoomGetResponse.getChatRoomNumber(), chatRoomGetResponse);
		}
	}

	/**
	 * 레디스에 채팅방을 저장하는 메서드
	 *
	 * @param userId  요청한 유저
	 * @param roomId  유저의 방 id
	 * @param response 해당 방의 정보
	 */
	public void setChatRoom(String userId, String roomId, ChatRoomGetResponse response){
		opsHashChatRoom.put(getChatRoomKey(userId), roomId, response);
	}

	/**
	 * 레디스에 해당 채팅방이 있는지 확인하는 메서드
	 *
	 * @param userId	요청한 유저
	 * @param roomId	요청한 채팅방
	 * @return			true: 있다 | false: 없다
	 */
	public boolean existChatRoom(String userId, String roomId){
		return opsHashChatRoom.hasKey(getChatRoomKey(userId), roomId);
	}

	/**
	 * 레디스에 해당 채팅방을 삭제하는 메서드
	 *
	 * @param userId	요청한 유저
	 * @param roomId	요청한 채팅방
	 */
	public void deleteChatRoom(String userId, String roomId) {
		opsHashChatRoom.delete(getChatRoomKey(userId), roomId);
	}

	/**
	 * 레디스에 있는 채팅방의 정보를 가져오는 메서드
	 *
	 * @param userId	요청한 유저
	 * @param roomId	요청한 방 id
	 * @return			채팅방 정보
	 */
	public ChatRoomGetResponse getChatRoom(String userId, String roomId) {
		return objectMapper.convertValue(opsHashChatRoom.get(getChatRoomKey(userId), roomId), ChatRoomGetResponse.class);
	}

	/**
	 * 요청한 유저가 속해있는 채팅방 리스트를 조회하는 메서드
	 *
	 * @param userId	요청한 유저
	 * @return			채팅방 리스트
	 */
	public List<ChatRoomGetResponse> getChatRoomList(String userId) {
		return objectMapper.convertValue(opsHashChatRoom.values(getChatRoomKey(userId)), new TypeReference<>() {});
	}

	/**
	 * 채팅방 리스트를 볼 때 마지막 메시지를 설정하기 위한 메서드
	 *
	 * @param roomId			채팅방 ID
	 * @param chatMessageDto	마지막 메시지
	 */
	public void setLastChatMessage(String roomId, ChatMessageDto chatMessageDto) {
		opsHashLastChatMessage.put(CHAT_ROOM, roomId, chatMessageDto);
	}

	/**
	 * 채팅방 리스트를 볼 때 마지막 메시지를 가져오기 위한 메서드
	 *
	 * @param roomId	채팅방 ID
	 * @return			마지막 메시지
	 */
	public ChatMessageDto getLastMessage(String roomId) {
		return objectMapper.convertValue(opsHashLastChatMessage.get(CHAT_ROOM, roomId), ChatMessageDto.class);
	}

}
