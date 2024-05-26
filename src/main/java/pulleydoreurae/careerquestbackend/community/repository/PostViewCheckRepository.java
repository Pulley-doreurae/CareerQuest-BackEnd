package pulleydoreurae.careerquestbackend.community.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.careerquestbackend.community.domain.entity.PostViewCheck;

/**
 * 조회수 중복을 막기 위해 사용되는 객체의 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/04/05
 */
public interface PostViewCheckRepository extends CrudRepository<PostViewCheck, String> {
}
