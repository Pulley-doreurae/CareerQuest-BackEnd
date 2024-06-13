package pulleydoreurae.careerquestbackend.community.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;

/**
 * 공모전 엔티티 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
public interface ContestRepository extends JpaRepository<Contest, Long>, ContestRepositoryCustom {

	@Query("select c from Contest c join fetch c.post p where p.id = :postId")
	Optional<Contest> findByPostId(Long postId);

	void deleteByPostId(Long postId);
}
