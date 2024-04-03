package pulleydoreurae.careerquestbackend.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;

/**
 * 게시글을 담당하는 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
public interface PostRepository extends JpaRepository<Post, Long> {

	Page<Post> findAllByOrderByIdDesc(Pageable pageable);

	Page<Post> findAllByUserAccountOrderByIdDesc(UserAccount userAccount, Pageable pageable);

	Page<Post> findAllByCategoryOrderByIdDesc(Long category, Pageable pageable);
}
