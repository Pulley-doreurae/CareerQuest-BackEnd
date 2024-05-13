package pulleydoreurae.careerquestbackend.certification.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationReviewViewCheck;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostImage;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostViewCheck;
import pulleydoreurae.careerquestbackend.common.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostViewCheckRepository;
import pulleydoreurae.careerquestbackend.common.community.service.CommonCommunityService;
import pulleydoreurae.careerquestbackend.common.community.service.PostService;
import pulleydoreurae.careerquestbackend.common.service.FileManagementService;

/**
 * 자격증 후기 서비스 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@Service
public class CertificationReviewService extends PostService {

	public CertificationReviewService(PostRepository postRepository,
			@Qualifier("commonCertificationService") CommonCommunityService commonCommunityService,
			PostLikeRepository postLikeRepository, PostViewCheckRepository postViewCheckRepository,
			PostImageRepository postImageRepository, FileManagementService fileManagementService) {

		super(postRepository, commonCommunityService, postLikeRepository, postViewCheckRepository,
				postImageRepository, fileManagementService);
	}

	/**
	 * 조회수 구현체 생성하기
	 *
	 * @param postId 게시글 정보
	 * @param name UUID값
	 * @return 조회수 구현체
	 */
	@Override
	public PostViewCheck mackPostViewCheck(Long postId, String name) {
		return new CertificationReviewViewCheck(name, postId);
	}

	// 게시글 후기는 이미지 저장을 사용하지 않음
	@Deprecated
	@Override
	public PostImage mackPostImage(Post post, String fileName) {
		return null;
	}
}
