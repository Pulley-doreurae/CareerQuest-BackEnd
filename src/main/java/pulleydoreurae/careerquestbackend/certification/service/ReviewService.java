package pulleydoreurae.careerquestbackend.certification.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.ReviewRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewViewCheck;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewLikeRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewViewCheckRepository;
import pulleydoreurae.careerquestbackend.common.service.CommonService;

/**
 * 자격증 후기 서비스 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

	private final CommonReviewService commonReviewService;
	private final ReviewRepository reviewRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewViewCheckRepository reviewViewCheckRepository;
	private final CommonService commonService;

	/**
	 * 후기 리스트를 불러오는 메서드
	 *
	 * @param pageable 페이지
	 * @return Repository 에서 가져온 리스트 반환
	 */
	public List<ReviewResponse> getPostResponseList(Pageable pageable) {
		return commonReviewService.reviewListToReviewResponseList(reviewRepository.findAllByOrderByIdDesc(pageable));
	}

	/**
	 * 후기 자격증명으로 리스트를 불러오는 메서드
	 *
	 * @param certificationName 자격증 이름
	 * @param pageable          페이지
	 * @return 자격증에 맞는 리스트 반환
	 */
	public List<ReviewResponse> getReviewResponseListByCertificationName(String certificationName, Pageable pageable) {

		return commonReviewService.reviewListToReviewResponseList(
				reviewRepository.findAllByCertificationNameOrderByIdDesc(certificationName, pageable));
	}

	/**
	 * 한 사용자가 작성한 리스트를 불러오는 메서드 (15 개 씩 페이지로 나눠서 호출함)
	 *
	 * @param userId   회원아이디
	 * @param pageable 페이지
	 * @return 회원정보에 맞는 리스트 반환
	 */
	public List<ReviewResponse> getReviewListByUserAccount(String userId, Pageable pageable) {
		UserAccount user = commonService.findUserAccount(userId, false);

		return commonReviewService.reviewListToReviewResponseList(
				reviewRepository.findAllByUserAccountOrderByIdDesc(user, pageable));
	}

	/**
	 * 후기 검색 메서드
	 *
	 * @param keyword           키워드
	 * @param certificationName 자격증명 (필수값 X)
	 * @param pageable          페이지
	 * @return 검색결과
	 */
	public List<ReviewResponse> searchPosts(String keyword, String certificationName, Pageable pageable) {

		// 자격증명 없이 전체 검색
		if (certificationName == null) {
			return commonReviewService.reviewListToReviewResponseList(
					reviewRepository.searchByKeyword(keyword, pageable));
		}

		// 자격증명 포함 검색
		return commonReviewService.reviewListToReviewResponseList(
				reviewRepository.searchByKeywordAndCertificationName(keyword, certificationName, pageable));
	}

	/**
	 * 하나의 후기를 불러오는 메서드
	 *
	 * @param reviewId 후기 id
	 * @return 후기 dto 를 반환
	 */
	public ReviewResponse findByReviewId(HttpServletRequest request, HttpServletResponse response, Long reviewId) {
		Review review = commonReviewService.findReview(reviewId);

		String userId = checkView(request, response, reviewId, review);
		// 게시글 단건 요청은 게시글 좋아요 정보가 필요하므로 좋아요 정보를 넘기기
		return commonReviewService.reviewToReviewResponse(review, getIsLiked(userId, review));
	}

	/**
	 * 조회수에 대한 처리를 담당하는 메서드
	 *
	 * @param request  요청
	 * @param response 응답
	 * @param reviewId 게시글 id
	 * @param review   게시글 정보
	 * @return 현재 로그인한 사용자 정보
	 */
	public String checkView(HttpServletRequest request, HttpServletResponse response, Long reviewId, Review review) {
		// 회원정보(userId) 가져오기
		String name;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// 인증 정보가 있고 로그인한 사용자 일때
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			name = authentication.getName();
		} else { // 회원정보가 없다면 쿠키와 UUID 를 사용해 식별한다.
			name = commonReviewService.getUUID(request, response);
		}

		ReviewViewCheck reviewViewCheck = commonReviewService.findReviewViewCheck(name);
		if (reviewViewCheck == null || !reviewViewCheck.getReviewId()
				.equals(reviewId)) { // Redis 에 저장되어 있지 않다면 저장하고 조회수 증가
			review.setView(review.getView() + 1); // 조회수 증가
			reviewViewCheckRepository.save(mackReviewViewCheck(reviewId, name)); // 지정한 시간동안 저장
		}
		return name;
	}

	/**
	 * 좋아요 상태를 반환하는 메서드
	 *
	 * @param userId 현재 요청한 사용자 정보
	 * @param review 게시글 정보
	 * @return 0이면 좋아요 X, 1이라면 좋아요 상태
	 */
	public Boolean getIsLiked(String userId, Review review) {
		// userId 로 회원을 가져온다.
		UserAccount user = commonService.findUserAccount(userId, false);
		// user 가 null 이거나 좋아요 누른 정보를 가져올 수 없다면 false, 눌렀다면 true
		return reviewLikeRepository.existsByReviewAndUserAccount(review, user);
	}

	/**
	 * 후기 저장 메서드
	 *
	 * @param reviewRequest 게시글 요청
	 */
	public void saveReview(ReviewRequest reviewRequest) {
		UserAccount user = commonService.findUserAccount(reviewRequest.getUserId(), true);
		Review review = commonReviewService.reviewRequestToReview(reviewRequest, user);
		reviewRepository.save(review);
	}

	/**
	 * 후기 수정 메서드
	 *
	 * @param reviewId      후기 id
	 * @param reviewRequest 후기 수정요청
	 * @return 수정에 성공하면 true 실패하면 false
	 */
	public boolean updatePost(Long reviewId, ReviewRequest reviewRequest) {
		Review review = commonReviewService.findReview(reviewId);
		UserAccount user = commonService.findUserAccount(reviewRequest.getUserId(), true);
		// 작성자와 수정자가 다르다면 실패
		if (!review.getUserAccount().getUserId().equals(user.getUserId())) {
			return false;
		}

		Review updatedReview = commonReviewService.reviewRequestToReviewForUpdate(review, reviewRequest, user);
		reviewRepository.save(updatedReview);

		return true;
	}

	/**
	 * 후기 삭제 메서드
	 *
	 * @param reviewId 게시글 id
	 * @param userId   삭제 요청자
	 * @return 삭제 요청이 성공이면 true 실패하면 false
	 */
	public boolean deleteReview(Long reviewId, String userId) {
		UserAccount user = commonService.findUserAccount(userId, true);
		Review review = commonReviewService.findReview(reviewId);
		// 작성자와 요청자가 다르다면 실패 (권한 없음)
		if (!review.getUserAccount().getUserId().equals(user.getUserId())) {
			return false;
		}
		reviewRepository.deleteById(reviewId);

		return true;
	}

	/**
	 * 조회수 생성하기
	 *
	 * @param reviewId 게시글 정보
	 * @param name     UUID값
	 * @return 조회수
	 */
	public ReviewViewCheck mackReviewViewCheck(Long reviewId, String name) {
		return new ReviewViewCheck(name, reviewId);
	}
}
