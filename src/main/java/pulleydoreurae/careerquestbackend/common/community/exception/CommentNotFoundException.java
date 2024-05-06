package pulleydoreurae.careerquestbackend.common.community.exception;

/**
 * 댓글을 찾지 못했을때 발생하는 예외
 *
 * @author : parkjihyeok
 * @since : 2024/04/15
 */
public class CommentNotFoundException extends CommunityException {
	public CommentNotFoundException(String message) {
		super(message);
	}
}
