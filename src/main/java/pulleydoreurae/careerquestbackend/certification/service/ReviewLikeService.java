package pulleydoreurae.careerquestbackend.certification.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.ReviewLikeRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewLike;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewLikeRepository;

/**
 * 자격증 좋아요 서비스 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Service
@RequiredArgsConstructor
public class ReviewLikeService {

	private final CommonReviewService commonReviewService;
	private final ReviewLikeRepository reviewLikeRepository;

	/**
	 * 좋아요 상태를 변경하는 메서드
	 *
	 * @param reviewLikeRequest 좋아요 요청 (isLiked 가 0일땐 증가, 1일땐 감소)
	 */
	public void changeReviewLike(ReviewLikeRequest reviewLikeRequest) {
		UserAccount user = commonReviewService.findUserAccount(reviewLikeRequest.getUserId());
		Review review = commonReviewService.findReview(reviewLikeRequest.getReviewId());

		if (reviewLikeRequest.getIsLiked()) { // 감소
			ReviewLike reviewLike = commonReviewService.findReviewLike(review, user);
			reviewLikeRepository.delete(reviewLike);
		} else { // 증가
			ReviewLike reviewLike = makeReviewLike(user, review);
			reviewLikeRepository.save(reviewLike);
		}
	}

	/**
	 * 한 사용자가 좋아요 누른 게시글 리스트를 반환하는 메서드
	 *
	 * @param userId   사용자 아이디
	 * @param pageable 페이지
	 * @return 게시글 리스트
	 */
	public List<ReviewResponse> findAllReviewLikeByUserAccount(String userId, Pageable pageable) {
		UserAccount user = commonReviewService.findUserAccount(userId);
		List<ReviewResponse> list = new ArrayList<>();

		reviewLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable)
				.forEach(reviewLike -> {
					Review review = reviewLike.getReview();
					// 게시글 리스트를 반환할땐 좋아요 상태를 사용하지 않는다. (false 으로 지정)
					ReviewResponse reviewResponse = commonReviewService.reviewToReviewResponse(review, false);
					list.add(reviewResponse);
				});

		return list;
	}

	/**
	 * 좋아요 생성
	 *
	 * @param user   사용자 정보
	 * @param review 후기 정보
	 * @return 좋아요
	 */
	public ReviewLike makeReviewLike(UserAccount user, Review review) {
		return ReviewLike.builder()
				.userAccount(user)
				.review(review)
				.build();
	}
}
