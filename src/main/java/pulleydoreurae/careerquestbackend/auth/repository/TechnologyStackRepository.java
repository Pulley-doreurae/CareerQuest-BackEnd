package pulleydoreurae.careerquestbackend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pulleydoreurae.careerquestbackend.auth.domain.entity.TechnologyStack;

import java.util.List;

/**
 * 기술 스택 변경 및 조회에 관한 Repository
 *
 * @author : hanjaeseong
 */
public interface TechnologyStackRepository extends JpaRepository<TechnologyStack, Long> {

   List<TechnologyStack> findByStackNameContaining(String keyword);

}
