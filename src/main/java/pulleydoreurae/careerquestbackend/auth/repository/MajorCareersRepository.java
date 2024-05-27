package pulleydoreurae.careerquestbackend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.MajorCareers;

public interface MajorCareersRepository extends JpaRepository<MajorCareers, Long> {
	Optional<MajorCareers> findMajorCareersByCategoryName(String name);
}
