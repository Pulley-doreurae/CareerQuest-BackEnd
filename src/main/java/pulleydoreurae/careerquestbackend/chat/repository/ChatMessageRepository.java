package pulleydoreurae.careerquestbackend.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatMessage;

/**
 * 메세지 내용을 저장하는 repository
 *
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, Long> {

}
