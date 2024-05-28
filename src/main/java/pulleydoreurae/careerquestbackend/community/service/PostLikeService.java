package pulleydoreurae.careerquestbackend.community.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.service.CommonService;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostLikeRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.community.repository.PostLikeRepository;

/**
 * 좋아요 Service
 *
 * @author : parkjihyeok
 * @since : 2024/04/03
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final PostLikeRepository postLikeRepository;
	private final CommonCommunityService commonCommunityService;
	private final CommonService commonService;

	/**
	 * 좋아요 상태를 변경하는 메서드
	 *
	 * @param postLikeRequest 좋아요 요청 (isLiked 가 false 일땐 추가, true 일땐 제거)
	 */
	public void changePostLike(PostLikeRequest postLikeRequest) {
		UserAccount user = commonService.findUserAccount(postLikeRequest.getUserId(), true);
		Post post = commonCommunityService.findPost(postLikeRequest.getPostId());

		if (postLikeRequest.getIsLiked()) { // 좋아요 제거
			PostLike postLike = commonCommunityService.findPostLike(post, user);
			postLikeRepository.delete(postLike);
		} else { // 좋아요 추가
			PostLike postLike = mackPostLike(user, post);
			postLikeRepository.save(postLike);
		}
	}

	/**
	 * 한 사용자가 좋아요 누른 게시글 리스트를 반환하는 메서드
	 *
	 * @param userId   사용자 아이디
	 * @param pageable 페이지
	 * @return 게시글 리스트
	 */
	public List<PostResponse> findAllPostLikeByUserAccount(String userId, Pageable pageable) {
		UserAccount user = commonService.findUserAccount(userId, false);
		List<PostResponse> list = new ArrayList<>();

		postLikeRepository.findAllByUserAccountOrderByIdDesc(user, pageable)
				.forEach(postLike -> {
					Post post = postLike.getPost();
					// 게시글 리스트를 반환할땐 좋아요 상태를 사용하지 않는다. (false 로 지정)
					PostResponse postResponse = commonCommunityService.postToPostResponse(post, false);
					list.add(postResponse);
				});

		return list;
	}

	/**
	 * 좋아요 구현체 생성
	 *
	 * @param user 사용자 정보
	 * @param post 게시글 정보
	 * @return 구현체
	 */
	public PostLike mackPostLike(UserAccount user, Post post) {
		return PostLike.builder()
				.userAccount(user)
				.post(post)
				.build();
	}
}
