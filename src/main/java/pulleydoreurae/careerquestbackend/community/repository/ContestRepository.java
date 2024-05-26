package pulleydoreurae.careerquestbackend.community.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;

/**
 * 공모전 엔티티 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
public interface ContestRepository extends JpaRepository<Contest, Long>, ContestRepositoryCustom {

	Optional<Contest> findByPostId(Long postId);
}
