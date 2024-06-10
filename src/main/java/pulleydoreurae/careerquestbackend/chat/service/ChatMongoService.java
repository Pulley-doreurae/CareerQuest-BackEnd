package pulleydoreurae.careerquestbackend.chat.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.chat.domain.ChatMessageDto;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatMessage;
import pulleydoreurae.careerquestbackend.chat.repository.ChatMessageRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMongoService {

	private final ChatMessageRepository chatMessageRepository;
	private final MongoTemplate mongoTemplate;

	public ChatMessageDto save(ChatMessageDto chatMessageDto) {
		ChatMessage chatMessage = chatMessageRepository.save(ChatMessage.of(chatMessageDto));

		log.info("save success : {}", chatMessage.getMessage());
		return ChatMessageDto.fromEntity(chatMessage);
	}

	// 채팅 불러오기
	public List<ChatMessageDto> findAll(String roomId, Integer pageNumber) {
		return findByRoomIdWithPaging(roomId,pageNumber,20)
			.stream().map(ChatMessageDto::fromEntity)
			.collect(Collectors.toList());
	}


	private Page<ChatMessage> findByRoomIdWithPaging(String roomId, int page, int size) {
		Pageable pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC,"time"));

		Query query = new Query()
			.with(pageable)
			.skip((long) pageable.getPageSize() * pageable.getPageNumber())
			.limit(pageable.getPageSize());

		query.addCriteria(Criteria.where("roomId").is(roomId));

		List<ChatMessage> filteredChatMessage = mongoTemplate.find(query, ChatMessage.class, "chat");
		Collections.sort(filteredChatMessage, Comparator.comparing(ChatMessage::getTime));
		return PageableExecutionUtils.getPage(
			filteredChatMessage,
			pageable,
			() -> mongoTemplate.count(query.skip(-1).limit(-1), ChatMessage.class)
		);
	}

	public ChatMessage findLatestMessageByRoomId(String roomId) {
		try {
			Query query = new Query(Criteria.where("roomId").is(roomId))
				.with(Sort.by(Sort.Order.desc("_id")))
				.limit(1);

			return mongoTemplate.findOne(query, ChatMessage.class);
		} catch (Exception e) {
			return null;
		}
	}


}
