package pulleydoreurae.careerquestbackend.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.ListResponse;
import pulleydoreurae.careerquestbackend.chat.domain.dto.request.ChatRoomCreateRequest;
import pulleydoreurae.careerquestbackend.chat.domain.dto.request.JoinChatRoomRequest;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomGetResponse;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomResponse;
import pulleydoreurae.careerquestbackend.chat.service.ChatMongoService;
import pulleydoreurae.careerquestbackend.chat.service.ChatRoomService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 채팅방 내부 정보를
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/chat/room")
public class ChatRoomController {

	private final ChatRoomService chatRoomService;
	private final ChatMongoService chatMongoService;

	@GetMapping("/info")
	public ResponseEntity<?> getChatRoomInfoChat(@RequestParam(name = "roomId") String roomId) {
		return ResponseEntity.status(HttpStatus.OK).body(
			chatRoomService.getChatRoomInfo(roomId)
		);
	}

	@PostMapping("/create")
	public ResponseEntity<?> createRoom(
		@RequestBody ChatRoomCreateRequest chatRoomCreateRequest) {
		ChatRoomResponse response =  chatRoomService.createChatRoom(chatRoomCreateRequest);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PostMapping("/join")
	public ResponseEntity<?> joinRoom(
		@RequestBody JoinChatRoomRequest joinChatRoomRequest) {
		ChatRoomResponse response =  chatRoomService.joinChatRoom(joinChatRoomRequest);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/list")
	public ResponseEntity<ListResponse> findChatRoomListByMemberIdChat(
		@RequestParam(name = "userId") String userId
	) {
		List<ChatRoomGetResponse> responses = chatRoomService.getChatRoomList(userId);
		return ResponseEntity.status(HttpStatus.OK).body(
			ListResponse.builder()
				.lists(responses)
				.msg(userId + "의 채팅방 리스트 조회")
				.build()
		);
	}

	@GetMapping("/exit")
	public ResponseEntity<?> deleteChatRoom(
		@RequestParam(name = "userId") String userId,
		@RequestParam(name = "roomId") String roomId
	) {
		 chatRoomService.deleteChatRoom(userId, roomId);
		 return ResponseEntity.status(HttpStatus.OK).body(
			 SimpleResponse.builder()
				 .msg("퇴장한 채팅방 : " + roomId)
				 .build()
		 );
	}

	@GetMapping("/find")
	public ResponseEntity<?> roomFindInfo(
		@RequestParam(name = "roomId") String id,
		@RequestParam(name = "page") Integer pageNumber
	) {
		return ResponseEntity.status(HttpStatus.OK).body(
			ListResponse.builder()
				.lists(chatMongoService.findAll(id,pageNumber))
				.msg("채팅방 : " +id + " - page : " + pageNumber)
				.build()
		);
	}
}
