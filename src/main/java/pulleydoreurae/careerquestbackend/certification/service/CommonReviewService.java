package pulleydoreurae.careerquestbackend.certification.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.ReviewRequest;
import pulleydoreurae.careerquestbackend.certification.domain.dto.response.ReviewResponse;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Review;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewLike;
import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewViewCheck;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewLikeRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewRepository;
import pulleydoreurae.careerquestbackend.certification.repository.ReviewViewCheckRepository;
import pulleydoreurae.careerquestbackend.community.exception.PostLikeNotFoundException;
import pulleydoreurae.careerquestbackend.community.exception.PostNotFoundException;

/**
 * 자격증 후기에서 쟈주 사용되는 메서드 모음
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonReviewService {

	private final UserAccountRepository userAccountRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewViewCheckRepository reviewViewCheckRepository;

	/**
	 * 후기 id로 게시글을 찾아오는 메서드
	 *
	 * @param reviewId 후기 id
	 * @return 후기가 있다면 후기를, 없다면 null 리턴
	 */
	public Review findReview(Long reviewId) {
		Optional<Review> findPost = reviewRepository.findById(reviewId);

		if (findPost.isEmpty()) {
			log.error("후기를 찾을 수 없습니다. reviewId = {}", reviewId);
			throw new PostNotFoundException("요청한 후기 정보를 찾을 수 없습니다.");
		}
		return findPost.get();
	}

	/**
	 * 회원아이디로 회원정보를 찾아오는 메서드
	 *
	 * @param userId 회원아이디
	 * @return 해당하는 회원정보가 있으면 회원정보를, 없다면 null 리턴
	 */
	public UserAccount findUserAccount(String userId) {
		Optional<UserAccount> findUser = userAccountRepository.findByUserId(userId);

		// 회원정보를 찾을 수 없다면
		if (findUser.isEmpty()) {
			log.error("{} 의 회원 정보를 찾을 수 없습니다.", userId);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return findUser.get();
	}

	/**
	 * 조회수 중복을 확인하기 위해 Optional 을 제거하는 메서드
	 *
	 * @param userId 키값
	 * @return 제거한 결과 없으면 null, 있다면 해당 객체를 리턴
	 */
	public ReviewViewCheck findReviewViewCheck(String userId) {
		Optional<ReviewViewCheck> reviewViewCheck = reviewViewCheckRepository.findById(userId);
		return reviewViewCheck.orElse(null);
	}

	/**
	 * 한 후기에 달린 좋아요를 세어주는 메서드
	 *
	 * @param reviewId 후기 id
	 * @return 해당 게시글의 좋아요 수
	 */
	public Long countReviewLike(Long reviewId) {
		Review review = findReview(reviewId);
		return (long)reviewLikeRepository.findAllByReview(review).size();
	}

	/**
	 * 회원정보가 없는 사용자를 식별하기 위해 쿠키에 UUID 를 추가하고 해당 UUID를 리턴하는 메서드
	 *
	 * @param request  요청
	 * @param response 응답
	 * @return UUID 값 리턴
	 */
	public String getUUID(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		String name = null;

		if (cookies != null) { // 쿠키가 있다면 UUID 가 있는지 확인한다.
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("UUID")) {
					name = cookie.getValue();
					break;
				}
			}
		}
		if (name == null) { // 쿠키가 없거나 있더라도 UUID 값이 없다면 UUID 값을 새로 만들어 쿠키에 저장한다.
			name = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("UUID", name);
			cookie.setMaxAge(60 * 60 * 24); // UUID 쿠키는 10일 동안 사용한다.
			response.addCookie(cookie); // UUID 값 쿠키에 저장
		}

		return name;
	}

	/**
	 * 후기와 사용자 정보로 좋아요 정보 가져오기
	 *
	 * @param review 후기 정보
	 * @param user   사용자 정보
	 * @return 게시글 좋아요
	 */
	public ReviewLike findReviewLike(Review review, UserAccount user) {
		Optional<ReviewLike> reviewLikeOptional = reviewLikeRepository.findByReviewAndUserAccount(review, user);

		if (reviewLikeOptional.isEmpty()) {
			log.error("좋아요 정보를 찾을 수 없습니다. review = {},  user = {}", review, user);
			throw new PostLikeNotFoundException("요청한 좋아요 정보를 찾을 수 없습니다.");
		}
		return reviewLikeOptional.get();
	}

	/**
	 * 후기 Entity -> 후기 Response 변환 메서드
	 *
	 * @param review  게시글 정보
	 * @param isLiked 좋아요 정보 (리스트를 출력할땐 뭘로?)
	 * @return 변환된 객체
	 */
	public ReviewResponse reviewToReviewResponse(Review review, Boolean isLiked) {
		return ReviewResponse.builder()
				.userId(review.getUserAccount().getUserId())
				.title(review.getTitle())
				.content(review.getContent())
				.view(review.getView())
				.postLikeCount(countReviewLike(review.getId()))
				.certificationName(review.getCertificationName())
				.isLiked(isLiked)
				.createdAt(review.getCreatedAt())
				.modifiedAt(review.getModifiedAt())
				.build();
	}

	/**
	 * 후기 엔티티 리스트 -> 후기 response 리스트
	 *
	 * @param reviewList 후기 엔티티 리스트
	 * @return 후기 response 리스트
	 */
	public List<ReviewResponse> reviewListToReviewResponseList(Page<Review> reviewList) {
		return reviewList.stream()
				// 게시글 리스트를 반환할땐 좋아요 상태를 사용하지 않는다. (false 으로 지정)
				.map(review -> reviewToReviewResponse(review, false))
				.toList();
	}

	/**
	 * 후기 요청 ->후기 엔티티 변환 메서드 (작성시 사용)
	 *
	 * @param reviewRequest 후기 요청
	 * @param user          회원정보
	 * @return 후기 엔티티
	 */
	public Review reviewRequestToReview(ReviewRequest reviewRequest, UserAccount user) {
		return Review.builder()
				.userAccount(user)
				.title(reviewRequest.getTitle())
				.content(reviewRequest.getContent())
				.certificationName(reviewRequest.getCertificationName())
				.view(0L)
				.build();
	}

	/**
	 * 후기 수정 요청 -> 후기 엔티티 변환 메서드 (수정시 사용)
	 *
	 * @param review        수정할 후기 엔티티 전달
	 * @param reviewRequest 수정할 후기
	 * @param user          작성자(수정자)
	 * @return 후기 엔티티
	 */
	public Review reviewRequestToReviewForUpdate(Review review, ReviewRequest reviewRequest, UserAccount user) {
		// 엔티티의 Setter 사용을 막기위해 새로운 review 생성하며 덮어쓰기

		return Review.builder()
				.id(review.getId()) // id 를 덮어씌어 수정함
				.userAccount(user)
				.title(reviewRequest.getTitle())
				.content(reviewRequest.getContent())
				.certificationName(reviewRequest.getCertificationName())
				.view(review.getView()) // 조회수도 유지
				.postLikes(review.getPostLikes()) // 좋아요 리스트도 유지
				.build();
	}
}
