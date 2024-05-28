package pulleydoreurae.careerquestbackend.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.MiddleCareers;
import pulleydoreurae.careerquestbackend.auth.domain.entity.SmallCareers;

@Deprecated
public interface SmallCareersRepository extends JpaRepository<SmallCareers, Long> {
	List<SmallCareers> findAllByMiddleCategory(MiddleCareers middleCareers);
}
