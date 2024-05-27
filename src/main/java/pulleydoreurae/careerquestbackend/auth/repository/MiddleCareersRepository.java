package pulleydoreurae.careerquestbackend.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.MajorCareers;
import pulleydoreurae.careerquestbackend.auth.domain.entity.MiddleCareers;

public interface MiddleCareersRepository extends JpaRepository<MiddleCareers, Long> {
	Optional<MiddleCareers> findMiddleCareersByCategoryName(String name);
	List<MiddleCareers> findAllByMajorCategory(MajorCareers majorCareers);
}
