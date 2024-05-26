package pulleydoreurae.careerquestbackend.community.exception;

/**
 * 게시글 수정에 실패했을때 발생하는 예외
 *
 * @author : parkjihyeok
 * @since : 2024/05/26
 */
public class PostUpdateException extends CommunityException {
	public PostUpdateException(String message) {
		super(message);
	}
}
