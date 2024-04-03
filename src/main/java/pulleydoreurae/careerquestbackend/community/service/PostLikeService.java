package pulleydoreurae.careerquestbackend.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostLikeRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostLike;
import pulleydoreurae.careerquestbackend.community.repository.CommentRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostLikeRepository;
import pulleydoreurae.careerquestbackend.community.repository.PostRepository;

/**
 * 좋아요 Service
 *
 * @author : parkjihyeok
 * @since : 2024/04/03
 */
@Slf4j
@Service
public class PostLikeService {

	private final UserAccountRepository userAccountRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final PostLikeRepository postLikeRepository;

	@Autowired
	public PostLikeService(UserAccountRepository userAccountRepository, PostRepository postRepository,
			CommentRepository commentRepository,
			PostLikeRepository postLikeRepository) {
		this.userAccountRepository = userAccountRepository;
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
		this.postLikeRepository = postLikeRepository;
	}

	/**
	 * 좋아요 상태를 변경하는 메서드
	 *
	 * @param postLikeRequest 좋아요 요청 (isLiked 가 0일땐 증가, 1일땐 감소)
	 * @return 상태변경에 성공하면 true, 실패하면 false
	 */
	public boolean changePostLike(PostLikeRequest postLikeRequest) {
		UserAccount user = findUserAccount(postLikeRequest.getUserId());
		Post post = findPost(postLikeRequest.getPostId());

		if (user == null || post == null) {
			return false;
		}
		if (postLikeRequest.getIsLiked() == 0) { // 증가
			PostLike postLike = PostLike.builder()
					.userAccount(user)
					.post(post)
					.build();
			postLikeRepository.save(postLike);
		} else if (postLikeRequest.getIsLiked() == 1) { // 감소
			PostLike postLike = findPostLike(post, user);

			if (postLike == null) {
				return false;
			}
			postLikeRepository.delete(postLike);
		}
		return true;
	}

	/**
	 * 한 사용자가 좋아요 누른 게시글 리스트를 반환하는 메서드
	 *
	 * @param userId 사용자 아이디
	 * @return 게시글 리스트
	 */
	public List<PostResponse> findAllPostLikeByUserAccount(String userId) {
		UserAccount user = findUserAccount(userId);
		List<PostResponse> list = new ArrayList<>();

		postLikeRepository.findAllByUserAccount(user)
				.forEach(postLike -> {
					Post post = postLike.getPost();
					PostResponse postResponse = PostResponse.builder()
							.userId(post.getUserAccount().getUserId())
							.title(post.getTitle())
							.content(post.getContent())
							.hit(post.getHit())
							.commentCount(countComment(post.getId()))
							.postLikeCount(countPostLike(post.getId()))
							.category(post.getCategory())
							.createdAt(post.getCreatedAt())
							.modifiedAt(post.getModifiedAt())
							.build();
					list.add(postResponse);
				});

		return list;
	}

	/**
	 * 회원아이디로 회원정보를 찾아오는 메서드
	 *
	 * @param userId 회원아이디
	 * @return 해당하는 회원정보가 있으면 회원정보를, 없다면 null 리턴
	 */
	private UserAccount findUserAccount(String userId) {
		Optional<UserAccount> findUser = userAccountRepository.findByUserId(userId);

		// 회원정보를 찾을 수 없다면
		if (findUser.isEmpty()) {
			log.error("{} 의 회원 정보를 찾을 수 없습니다.", userId);
			return null;
		}
		return findUser.get();
	}

	/**
	 * 게시글 id 로 게시글을 찾아오는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 해당하는 게시글이 있다면 게시글을, 없다면 null 리턴
	 */
	private Post findPost(Long postId) {
		Optional<Post> optionalPost = postRepository.findById(postId);

		// 게시글 정보를 찾을 수 없다면
		if (optionalPost.isEmpty()) {
			log.error("postId = {} 의 게시글 정보를 찾을 수 없습니다.", postId);
			return null;
		}
		return optionalPost.get();
	}

	/**
	 * 게시글과 사용자 정보로 좋아요 정보 가져오기
	 *
	 * @param post 게시글 정보
	 * @param user 사용자 정보
	 * @return 게시글 좋아요
	 */
	private PostLike findPostLike(Post post, UserAccount user) {
		Optional<PostLike> optionalPostLike = postLikeRepository.findByPostAndUserAccount(post, user);

		if (optionalPostLike.isEmpty()) {
			log.error("좋아요 정보를 찾을 수 없습니다. post = {},  user = {}", post, user);
			return null;
		}
		return optionalPostLike.get();
	}

	/**
	 * 한 게시글에 달린 댓글을 세어주는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 해당 게시글의 댓글 수
	 */
	private Long countComment(Long postId) {
		Post post = findPost(postId);
		return (long)commentRepository.findAllByPost(post).size();
	}

	/**
	 * 한 게시글에 달린 좋아요를 세어주는 메서드
	 *
	 * @param postId 게시글 id
	 * @return 해당 게시글의 좋아요 수
	 */
	private Long countPostLike(Long postId) {
		Post post = findPost(postId);
		return (long)postLikeRepository.findAllByPost(post).size();
	}
}
