package pulleydoreurae.careerquestbackend.community.exception;

/**
 * 게시글 저장에 실패했을때 발생하는 예외
 *
 * @author : parkjihyeok
 * @since : 2024/04/15
 */
public class PostSaveException extends CommunityException {
	public PostSaveException(String message) {
		super(message);
	}
}
