package pulleydoreurae.careerquestbackend.chat.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.google.gson.Gson;

import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.domain.dto.request.ChatRoomCreateRequest;
import pulleydoreurae.careerquestbackend.chat.domain.dto.request.JoinChatRoomRequest;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomGetResponse;
import pulleydoreurae.careerquestbackend.chat.domain.dto.response.ChatRoomResponse;
import pulleydoreurae.careerquestbackend.chat.service.ChatMongoService;
import pulleydoreurae.careerquestbackend.chat.service.ChatRoomService;
import pulleydoreurae.careerquestbackend.chat.type.MessageType;

@WebMvcTest(ChatRoomController.class)
@AutoConfigureRestDocs
public class ChatRoomControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ChatRoomService chatRoomService;

	@MockBean
	private ChatMongoService chatMongoService;

	private Gson gson = new Gson();

	@Test
	@DisplayName("특정 채팅방 정보 조회 성공")
	@WithMockUser
	void showChatRoomInfoSuccess() throws Exception {
		// Given
		ChatRoomGetResponse response = ChatRoomGetResponse.builder()
			.chatRoomNumber("0123-4567-89AB-CDEF")
			.chatRoomName("테스트 채팅방")
			.participants(List.of("참가자1", "참가자2"))
			.lastChatMessageDto(null)
			.build();

		given(chatRoomService.getChatRoomInfo(any())).willReturn(response);

		// When
		mockMvc.perform(
				get("/api/chat/room/info")
					.queryParam("roomId", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("roomId").description("채팅방 식별 번호")
				),
				responseFields(
					fieldWithPath("chatRoomNumber").description("요청한 채팅방 식별 번호"),
					fieldWithPath("chatRoomName").description("요청한 채팅방 이름"),
					fieldWithPath("participants").description("요청한 채팅방 참여자 목록"),
					fieldWithPath("lastChatMessageDto").description("요청한 채팅방의 가장 최근 메시지").optional()
				)));

		// Then
	}

	@Test
	@DisplayName("특정 채팅방 정보 조회 실패")
	@WithMockUser
	void showChatRoomInfoFailed() throws Exception {
		// Given
		doThrow(new UsernameNotFoundException("채팅방 찾기 실패")).when(chatRoomService).getChatRoomInfo(any());

		// When
		mockMvc.perform(
				get("/api/chat/room/info")
					.queryParam("roomId", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("roomId").description("채팅방 식별 번호")
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then

	}

	@Test
	@DisplayName("채팅방 생성 성공")
	@WithMockUser
	void createChatRoomSuccess() throws Exception {
		// Given
		ChatRoomCreateRequest request = ChatRoomCreateRequest.builder()
			.chatRoomName("새로운 채팅방")
			.userId("testId")
			.build();
		ChatRoomResponse response = ChatRoomResponse.builder()
			.userId("testId")
			.chatRoomNumber("0123-4567-89AB-CDEF")
			.msg("채팅방 생성을 성공했습니다.")
			.build();

		given(chatRoomService.createChatRoom(any())).willReturn(response);

		// When
		mockMvc.perform(
				post("/api/chat/room/create")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.chatRoomNumber").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("채팅방 생성 유저"),
					fieldWithPath("chatRoomName").description("채팅방 이름")
				),
				responseFields(
					fieldWithPath("userId").description("요청한 유저"),
					fieldWithPath("chatRoomNumber").description("채팅방 식별 번호"),
					fieldWithPath("msg").description("요청에 대한 처리 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("채팅방 생성 실패 (유저가 없음)")
	@WithMockUser
	void createChatRoomFailed() throws Exception {
		// Given
		ChatRoomCreateRequest request = ChatRoomCreateRequest.builder().chatRoomName("새로운 채팅방").userId("test").build();

		doThrow(new UsernameNotFoundException("유저가 없음")).when(chatRoomService).createChatRoom(any());

		// When
		mockMvc.perform(
				post("/api/chat/room/create")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("채팅방 생성 유저"),
					fieldWithPath("chatRoomName").description("채팅방 이름")
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 처리 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("채팅방 참가 성공")
	@WithMockUser
	void joinChatRoomSuccess() throws Exception {
		// Given
		JoinChatRoomRequest request = JoinChatRoomRequest.builder()
			.chatRoomNumber("0123-4567-89AB-CDEF")
			.userId("testId")
			.build();

		ChatRoomResponse response = ChatRoomResponse.builder()
			.userId("testId")
			.chatRoomNumber("0123-4567-89AB-CDEF")
			.msg("채팅방 참가를 성공했습니다.")
			.build();

		given(chatRoomService.joinChatRoom(any())).willReturn(response);

		// When
		mockMvc.perform(
				post("/api/chat/room/join")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.userId").exists())
			.andExpect(jsonPath("$.chatRoomNumber").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("채팅방 생성 유저"),
					fieldWithPath("chatRoomNumber").description("채팅방 이름")
				),
				responseFields(
					fieldWithPath("userId").description("요청한 유저"),
					fieldWithPath("chatRoomNumber").description("채팅방 식별 번호"),
					fieldWithPath("msg").description("요청에 대한 처리 결과")
				)));

		// Then
	}

	@Test
	@DisplayName("채팅방 참가 실패 (유저 없음)")
	@WithMockUser
	void joinChatRoomFailed1() throws Exception {
		// Given
		JoinChatRoomRequest request = JoinChatRoomRequest.builder()
			.chatRoomNumber("0123-4567-89AB-CDEF")
			.userId("test")
			.build();

		doThrow(new UsernameNotFoundException("유저가 없음")).when(chatRoomService).joinChatRoom(any());

		// When
		mockMvc.perform(
				post("/api/chat/room/join")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("채팅방 생성 유저"),
					fieldWithPath("chatRoomNumber").description("채팅방 이름")
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 처리 결과")
				)));
	}

	@Test
	@DisplayName("채팅방 참가 실패 (채팅방 식별 번호가 없음)")
	@WithMockUser
	void joinChatRoomFailed2() throws Exception {
		// Given
		JoinChatRoomRequest request = JoinChatRoomRequest.builder().chatRoomNumber("1234").userId("testId").build();

		doThrow(new UsernameNotFoundException("해당하는 채팅방이 없음")).when(chatRoomService).joinChatRoom(any());

		// When
		mockMvc.perform(
				post("/api/chat/room/join")
					.with(csrf())
					.contentType(MediaType.APPLICATION_JSON)
					.content(gson.toJson(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("userId").description("채팅방 생성 유저"),
					fieldWithPath("chatRoomNumber").description("채팅방 이름")
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 처리 결과")
				)));
		// Then
	}

	@Test
	@DisplayName("채팅방 리스트 조회 성공")
	@WithMockUser
	void showChatRoomListSuccess() throws Exception {

		// Given
		List<ChatRoomGetResponse> response = new ArrayList<>();

		response.add(ChatRoomGetResponse.builder()
			.chatRoomNumber("0123-4567-89AB-CDEF")
			.chatRoomName("채팅방 1")
			.participants(List.of("user_1", "user_2", "testId"))
			.lastChatMessageDto(null)
			.build());

		response.add(ChatRoomGetResponse.builder()
			.chatRoomNumber("0123-4567-89AB-CDE0")
			.chatRoomName("채팅방 2")
			.participants(List.of("user_0", "testId"))
			.lastChatMessageDto(ChatMessageDto.builder()
				.type(MessageType.TALK)
				.roomId("0123-4567-89AB-CDE0")
				.userId("user_0")
				.message("안녕하세요?")
				.time("2024-06-06T20:02:46.230023500")
				.build())
			.build());


		given(chatRoomService.getChatRoomList(any())).willReturn(response);

		// When
		mockMvc.perform(
				get("/api/chat/room/list")
					.queryParam("userId", "testId")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("userId").description("채팅방 조회할 유저")
				),
				responseFields(
					fieldWithPath("lists").description("요청한 채팅방 리스트"),
					fieldWithPath("lists[].chatRoomNumber").description("채팅방 식별 번호"),
					fieldWithPath("lists[].chatRoomName").description("채팅방 이름"),
					fieldWithPath("lists[].participants").description("채팅방 참가자 목록"),
					fieldWithPath("lists[].lastChatMessageDto").description("마지막 채팅 메시지").optional(),
					fieldWithPath("lists[].lastChatMessageDto.type").description("메시지 타입").optional(),
					fieldWithPath("lists[].lastChatMessageDto.roomId").description("채팅방 ID").optional(),
					fieldWithPath("lists[].lastChatMessageDto.userId").description("유저 ID").optional(),
					fieldWithPath("lists[].lastChatMessageDto.message").description("메시지 내용").optional(),
					fieldWithPath("lists[].lastChatMessageDto.time").description("메시지 시간").optional(),
					fieldWithPath("msg").description("요청에 대한 처리 결과")
				)));

		// Then
	}


	@Test
	@DisplayName("채팅방 리스트 조회 실패 (유저가 없음)")
	@WithMockUser
	void showChatRoomListFailed() throws Exception {

		// Given
		given(chatRoomService.getChatRoomList(any())).willReturn(List.of());

		// When
		mockMvc.perform(
				get("/api/chat/room/list")
					.queryParam("userId", "test")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("userId").description("채팅방 조회할 유저")
				),
				responseFields(
					fieldWithPath("lists").description("요청에 대한 리스트"),
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("채팅방 나가기 성공")
	@WithMockUser
	void deleteChatRoomSuccess() throws Exception {

		// Given

		// When
		mockMvc.perform(
				get("/api/chat/room/exit")
					.queryParam("userId", "testId")
					.queryParam("roomId", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("userId").description("채팅방 조회할 유저"),
					parameterWithName("roomId").description("조회할 채팅방 식별 번호")
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("채팅방 나가기 실패 (유저가 없음)")
	@WithMockUser
	void deleteChatRoomFailed() throws Exception {

		// Given
		doThrow(new UsernameNotFoundException("해당하는 유저가 없음")).when(chatRoomService).deleteChatRoom(any(), any());

		// When
		mockMvc.perform(
				get("/api/chat/room/exit")
					.queryParam("userId", "test")
					.queryParam("roomId", "0123-4567-89AB-CDEF")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("userId").description("채팅방 조회할 유저"),
					parameterWithName("roomId").description("조회할 채팅방 식별 번호")
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("채팅방 나가기 실패 (해당하는 방이 없음)")
	@WithMockUser
	void deleteChatRoomFailed2() throws Exception {

		// Given
		doThrow(new UsernameNotFoundException("해당하는 채팅방이 없음")).when(chatRoomService).deleteChatRoom(any(), any());

		// When
		mockMvc.perform(
				get("/api/chat/room/exit")
					.queryParam("userId", "testId")
					.queryParam("roomId", "0123-4567-89AB-0000")
					.with(csrf()))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("userId").description("채팅방 조회할 유저"),
					parameterWithName("roomId").description("조회할 채팅방 식별 번호")
				),
				responseFields(
					fieldWithPath("msg").description("요청에 대한 응답")
				)));

		// Then
	}

	@Test
	@DisplayName("특정 채팅방 메시지 불러오기 성공")
	@WithMockUser
	void showChatMessagesSuccess() throws Exception {

		List<ChatMessageDto> response = new ArrayList<>();

		response.add(ChatMessageDto.builder().type(MessageType.TALK)
			.roomId("0123-4567-89AB-CDEF")
			.userId("user_0")
			.message("안녕하세요?")
			.time("2024-06-06T20:02:46.230023500")
			.build());

		response.add(ChatMessageDto.builder().type(MessageType.TALK)
			.roomId("0123-4567-89AB-CDEF")
			.userId("user_1")
			.message("안녕하소")
			.time("2024-06-06T20:02:48.230023500")
			.build());

		response.add(ChatMessageDto.builder().type(MessageType.TALK)
			.roomId("0123-4567-89AB-CDEF")
			.userId("user_0")
			.message("넵 환영해요!")
			.time("2024-06-06T20:02:49.230023500")
			.build());

		// Given
		given(chatMongoService.findAll(any(), any())).willReturn(response);

		// When
		mockMvc.perform(
				get("/api/chat/room/find")
					.queryParam("roomId", "0123-4567-89AB-CDEF")
					.queryParam("page", String.valueOf(0))
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("roomId").description("조회할 채팅방 식별 번호"),
					parameterWithName("page").description("조회할 페이지(한 페이지에 20개)")
				),
				responseFields(
					fieldWithPath("lists").description("요청한 채팅방 리스트"),
					fieldWithPath("lists[].type").description("메시지 타입"),
					fieldWithPath("lists[].roomId").description("채팅방 ID"),
					fieldWithPath("lists[].userId").description("유저 ID"),
					fieldWithPath("lists[].message").description("메시지 내용"),
					fieldWithPath("lists[].time").description("메시지 시간"),
					fieldWithPath("msg").description("요청에 대한 응답 메시지")
				)));

		// Then

	}

	@Test
	@DisplayName("특정 채팅방 메시지 불러오기 실패 (해당하는 채팅방이 없음)")
	@WithMockUser
	void showChatMessagesFailed() throws Exception {

		// Given
		given(chatMongoService.findAll(any(), any())).willReturn(List.of());

		// When
		mockMvc.perform(
				get("/api/chat/room/find")
					.queryParam("roomId", "0123-4567-89AB-CDEF")
					.queryParam("page", String.valueOf(0))
					.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.lists").exists())
			.andExpect(jsonPath("$.msg").exists())
			.andDo(print())
			.andDo(document("{class-name}/{method-name}/",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				queryParameters(
					parameterWithName("roomId").description("조회할 채팅방 식별 번호"),
					parameterWithName("page").description("조회할 페이지(한 페이지에 20개)")
				),
				responseFields(
					fieldWithPath("lists").description("요청한 채팅방 리스트"),
					fieldWithPath("msg").description("요청에 대한 응답")
				)));
		// Then

	}

}
