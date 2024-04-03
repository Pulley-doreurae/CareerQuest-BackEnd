package pulleydoreurae.careerquestbackend.community.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostLike;

/**
 * 게시글을 담당하는 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findAllByUserAccount(UserAccount userAccount);
	List<Post> findAllByCategory(Long category);
}
