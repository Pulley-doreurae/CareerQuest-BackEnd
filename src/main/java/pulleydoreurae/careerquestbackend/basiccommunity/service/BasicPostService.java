package pulleydoreurae.careerquestbackend.basiccommunity.service;

import org.springframework.stereotype.Service;

import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostImage;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPostViewCheck;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.PostImage;
import pulleydoreurae.careerquestbackend.common.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostViewCheckRepository;
import pulleydoreurae.careerquestbackend.common.community.service.CommonCommunityService;
import pulleydoreurae.careerquestbackend.common.community.service.PostService;
import pulleydoreurae.careerquestbackend.common.service.FileManagementService;

/**
 * 게시판을 담당하는 Service
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
@Service
public class BasicPostService extends PostService {

	public BasicPostService(PostRepository postRepository, CommonCommunityService commonCommunityService,
			PostLikeRepository postLikeRepository, PostViewCheckRepository postViewCheckRepository,
			PostImageRepository postImageRepository, FileManagementService fileManagementService) {

		super(postRepository, commonCommunityService, postLikeRepository, postViewCheckRepository,
				postImageRepository, fileManagementService);
	}

	/**
	 * 이미지 저장 구현체 만들기
	 *
	 * @param post 게시글
	 * @param fileName 파일명
	 * @return 구현체
	 */
	@Override
	public PostImage mackPostImage(Post post, String fileName) {
		return BasicPostImage.builder()
				.post(post)
				.fileName(fileName)
				.build();
	}

	/**
	 * 조회수 구현체 생성하기
	 *
	 * @param postId 게시글 정보
	 * @param name UUID값
	 * @return 조회수 구현체
	 */
	@Override
	public BasicPostViewCheck mackPostViewCheck(Long postId, String name) {
		return new BasicPostViewCheck(name, postId);
	}
}
