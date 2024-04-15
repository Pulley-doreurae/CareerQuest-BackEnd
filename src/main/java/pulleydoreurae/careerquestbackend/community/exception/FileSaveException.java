package pulleydoreurae.careerquestbackend.community.exception;

/**
 * 파일저장에 실패했을때 발생하는 예외
 *
 * @author : parkjihyeok
 * @since : 2024/04/15
 */
public class FileSaveException extends CommunityException {
	public FileSaveException(String message) {
		super(message);
	}
}
