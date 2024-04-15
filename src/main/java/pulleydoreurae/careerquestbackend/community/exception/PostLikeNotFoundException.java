package pulleydoreurae.careerquestbackend.community.exception;

/**
 * 게시글 좋아요를 찾지 못했을때 발생하는 예외
 *
 * @author : parkjihyeok
 * @since : 2024/04/15
 */
public class PostLikeNotFoundException extends CommunityException {
	public PostLikeNotFoundException(String message) {
		super(message);
	}
}
