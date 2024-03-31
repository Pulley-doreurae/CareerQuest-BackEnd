package pulleydoreurae.careerquestbackend.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.community.domain.entity.Post;

/**
 * 게시글을 담당하는 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
