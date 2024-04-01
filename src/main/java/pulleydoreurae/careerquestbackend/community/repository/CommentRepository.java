package pulleydoreurae.careerquestbackend.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;

/**
 * 댓글 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByUserAccount(UserAccount userAccount);

	List<Comment> findAllByPost(Post post);
}
