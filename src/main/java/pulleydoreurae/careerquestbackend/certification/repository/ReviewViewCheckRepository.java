package pulleydoreurae.careerquestbackend.certification.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewViewCheck;

/**
 * 자격증 후기 조회수 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/24
 */
public interface ReviewViewCheckRepository extends CrudRepository<ReviewViewCheck, String> {
}
