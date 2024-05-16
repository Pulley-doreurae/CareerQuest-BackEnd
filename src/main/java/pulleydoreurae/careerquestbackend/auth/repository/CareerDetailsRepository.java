package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.Careers;

import java.util.List;

public interface CareerDetailsRepository extends JpaRepository<Careers, Long> {

    List<Careers> findAllByCategoryType(String categoryType);

}
