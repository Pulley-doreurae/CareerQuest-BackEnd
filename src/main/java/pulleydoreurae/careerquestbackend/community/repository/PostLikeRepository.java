package pulleydoreurae.careerquestbackend.community.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostLike;

/**
 * 게시글 좋아요 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/04/02
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	List<PostLike> findAllByPost(Post post);

	List<PostLike> findAllByUserAccount(UserAccount userAccount);

	Optional<PostLike> findByPostAndUserAccount(Post post, UserAccount userAccount);
}
