package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewLike;

/**
 * 자격증 좋아요 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/24
 */
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

	Page<ReviewLike> findAllByReviewOrderByIdDesc(Review review, Pageable pageable);

	List<ReviewLike> findAllByReview(Review review);

	Page<ReviewLike> findAllByUserAccountOrderByIdDesc(UserAccount userAccount, Pageable pageable);

	Optional<ReviewLike> findByReviewAndUserAccount(Review review, UserAccount userAccount);

	boolean existsByReviewAndUserAccount(Review review, UserAccount userAccount);
}
