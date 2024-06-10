package pulleydoreurae.careerquestbackend.chat.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatRoom;
import pulleydoreurae.careerquestbackend.chat.domain.entity.ChatRoomMember;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

	@Query("select crm from ChatRoomMember crm where crm.user.userId = :userId")
	List<ChatRoomMember> findAllByUser(String userId);

	@Query("select crm.user.userId from ChatRoomMember crm where crm.chatRoom = :chatRoom")
	List<String> findUserByChatRoom(ChatRoom chatRoom);

	void deleteByUserAndChatRoom(UserAccount user, ChatRoom chatRoom);

	boolean existsByChatRoom(ChatRoom chatRoom);
}
