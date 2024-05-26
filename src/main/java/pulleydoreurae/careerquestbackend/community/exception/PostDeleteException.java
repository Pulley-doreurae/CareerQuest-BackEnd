package pulleydoreurae.careerquestbackend.community.exception;

/**
 * 게시글 삭제에 실패했을때 발생하는 예외
 *
 * @author : parkjihyeok
 * @since : 2024/05/26
 */
public class PostDeleteException extends CommunityException {
	public PostDeleteException(String message) {
		super(message);
	}
}
