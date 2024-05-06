package pulleydoreurae.careerquestbackend.basiccommunity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;

/**
 * 게시글 좋아요 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/04/02
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	Page<PostLike> findAllByPostOrderByIdDesc(Post post, Pageable pageable);

	List<PostLike> findAllByPost(Post post);

	Page<PostLike> findAllByUserAccountOrderByIdDesc(UserAccount userAccount, Pageable pageable);

	Optional<PostLike> findByPostAndUserAccount(Post post, UserAccount userAccount);

	boolean existsByPostAndUserAccount(Post post, UserAccount userAccount);
}
