package pulleydoreurae.careerquestbackend.basiccommunity.exception;

/**
 * postId 로 게시글을 찾지 못했을때 발생하는 예외
 *
 * @author : parkjihyeok
 * @since : 2024/04/15
 */
public class PostNotFoundException extends CommunityException {
	public PostNotFoundException(String message) {
		super(message);
	}
}
