package pulleydoreurae.careerquestbackend.basiccommunity.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;

/**
 * 댓글 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/04/01
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

	Page<Comment> findAllByUserAccountOrderByIdDesc(UserAccount userAccount, Pageable pageable);

	Page<Comment> findAllByPostOrderByIdDesc(Post post, Pageable pageable);

	List<Comment> findAllByPost(Post post);
}
