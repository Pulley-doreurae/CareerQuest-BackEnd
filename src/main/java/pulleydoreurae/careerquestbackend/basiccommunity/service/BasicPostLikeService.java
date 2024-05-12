package pulleydoreurae.careerquestbackend.basiccommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostLike;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.service.CommonCommunityService;
import pulleydoreurae.careerquestbackend.common.community.service.PostLikeService;

/**
 * 좋아요 Service
 *
 * @author : parkjihyeok
 * @since : 2024/04/03
 */
@Service
public class BasicPostLikeService extends PostLikeService {

	@Autowired
	public BasicPostLikeService(PostLikeRepository postLikeRepository, CommonCommunityService commonCommunityService) {
		super(postLikeRepository, commonCommunityService);
	}

	/**
	 * 좋아요 구현체 생성
	 *
	 * @param user 사용자 정보
	 * @param post 게시글 정보
	 * @return 구현체
	 */
	@Override
	public PostLike mackPostLike(UserAccount user, Post post) {
		return BasicPostLike.builder()
				.userAccount(user)
				.post(post)
				.build();
	}
}
