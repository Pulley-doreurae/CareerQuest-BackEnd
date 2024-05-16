package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.TechnologyStack;

import java.util.List;

public interface TechnologyStackRepository extends JpaRepository<TechnologyStack, Long> {

   List<TechnologyStack> findByStackNameContaining(String keyword);

}
