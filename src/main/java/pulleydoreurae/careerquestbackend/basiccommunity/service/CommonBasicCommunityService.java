package pulleydoreurae.careerquestbackend.basiccommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.basiccommunity.domain.entity.BasicPost;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.common.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostImageRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostRepository;
import pulleydoreurae.careerquestbackend.common.community.repository.PostViewCheckRepository;
import pulleydoreurae.careerquestbackend.common.community.service.CommonCommunityService;

/**
 * 커뮤니티에서 쟈주 사용되는 메서드 모음
 *
 * @author : parkjihyeok
 * @since : 2024/04/11
 */
@Service
public class CommonBasicCommunityService extends CommonCommunityService {

	@Autowired
	public CommonBasicCommunityService(PostRepository postRepository, UserAccountRepository userAccountRepository,
			PostViewCheckRepository postViewCheckRepository, CommentRepository commentRepository,
			PostLikeRepository postLikeRepository, PostImageRepository postImageRepository) {

		super(postRepository, userAccountRepository, postViewCheckRepository, commentRepository, postLikeRepository,
				postImageRepository);
	}

	/**
	 * 게시글 요청 -> 게시글 엔티티 변환 메서드 (작성시 사용)
	 *
	 * @param postRequest 게시글 요청
	 * @param user        회원정보
	 * @return 게시글 엔티티
	 */
	public Post postRequestToPost(PostRequest postRequest, UserAccount user) {
		return BasicPost.builder()
				.userAccount(user)
				.title(postRequest.getTitle())
				.content(postRequest.getContent())
				.postCategory(postRequest.getPostCategory())
				.view(0L)
				.build();
	}

	/**
	 * 게시글 수정 요청 -> 게시글 엔티티 변환 메서드 (수정시 사용)
	 *
	 * @param post        수정할 게시글 엔티티 전달
	 * @param postRequest 수정할 게시글
	 * @param user        작성자(수정자)
	 * @return 게시글 엔티티
	 */
	public Post postRequestToPostForUpdate(Post post, PostRequest postRequest, UserAccount user) {
		// 엔티티의 Setter 사용을 막기위해 새로운 post 생성하며 덮어쓰기

		return BasicPost.builder()
				.id(post.getId()) // id 를 덮어씌어 수정함
				.userAccount(user)
				.title(postRequest.getTitle())
				.content(postRequest.getContent())
				.postCategory(postRequest.getPostCategory())
				.view(post.getView()) // 조회수도 유지
				.comments(post.getComments()) // 댓글 리스트도 유지
				.postLikes(post.getPostLikes()) // 좋아요 리스트도 유지
				.build();
	}
}
