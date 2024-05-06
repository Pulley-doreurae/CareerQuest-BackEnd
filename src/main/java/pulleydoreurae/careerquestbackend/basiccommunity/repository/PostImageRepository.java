package pulleydoreurae.careerquestbackend.basiccommunity.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostImage;

/**
 * 게시글 사진 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/04/09
 */
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

	List<PostImage> findAllByPost(Post post);

	void deleteByFileName(String fileName);

	boolean existsByFileName(String fileName);
}
