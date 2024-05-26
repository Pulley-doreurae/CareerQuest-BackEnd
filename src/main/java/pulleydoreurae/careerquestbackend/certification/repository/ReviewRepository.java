package pulleydoreurae.careerquestbackend.certification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;

/**
 * 자격증 후기 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/24
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

	Page<Review> findAllByOrderByIdDesc(Pageable pageable);

	Page<Review> findAllByCertificationNameOrderByIdDesc(String certificationName, Pageable pageable);

	Page<Review> findAllByUserAccountOrderByIdDesc(UserAccount userAccount, Pageable pageable);

	@Query("SELECT r FROM Review r WHERE r.title LIKE concat('%', :keyword, '%') OR r.content LIKE concat('%', :keyword, '%')")
	Page<Review> searchByKeyword(String keyword, Pageable pageable);

	@Query("SELECT r FROM Review r WHERE r.certificationName = :certificationName AND (r.title LIKE concat('%', :keyword, '%') OR r.content LIKE concat('%', :keyword, '%'))")
	Page<Review> searchByKeywordAndCertificationName(@Param("keyword") String keyword,
			@Param("certificationName") String certificationName, Pageable pageable);
}
