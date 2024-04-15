package pulleydoreurae.careerquestbackend.community.exception;

/**
 * 커뮤니티에서 발생하는 예외들의 부모예외
 *
 * @author : parkjihyeok
 * @since : 2024/04/15
 */
public class CommunityException extends RuntimeException {
	public CommunityException(String message) {
		super(message);
	}
}
