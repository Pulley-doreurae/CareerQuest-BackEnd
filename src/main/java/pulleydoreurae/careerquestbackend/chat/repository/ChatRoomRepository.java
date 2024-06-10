package pulleydoreurae.careerquestbackend.chat.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

	Optional<ChatRoom> findByChatRoomNumber(String roomId);

	void deleteByChatRoomNumber(String roomId);
}
