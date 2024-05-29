package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.Careers;

import java.util.List;
import java.util.Optional;


/**
 * 직무 조회 및 변경에 관한 Repository
 *
 * @author : hanjaeseong
 */
public interface CareerDetailsRepository extends JpaRepository<Careers, Long> {

    List<Careers> findAllByCategoryType(String categoryType);

    Optional<Careers> findCareersByCategoryNameAndCategoryType(String categoryName, String categoryType);

    Optional<Careers> findByCategoryName(String categoryName);

}
