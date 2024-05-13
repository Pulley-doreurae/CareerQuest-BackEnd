package pulleydoreurae.careerquestbackend.certification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReviewLike;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.service.CommonCommunityService;
import pulleydoreurae.careerquestbackend.common.community.service.PostLikeService;

/**
 * 자격증 좋아요 서비스 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Service
public class CertificationReviewLikeService extends PostLikeService {

	@Autowired
	public CertificationReviewLikeService(PostLikeRepository postLikeRepository,
			@Qualifier("commonCertificationService") CommonCommunityService commonCommunityService) {
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
		return CertificationReviewLike.builder()
				.userAccount(user)
				.post(post)
				.build();
	}
}
