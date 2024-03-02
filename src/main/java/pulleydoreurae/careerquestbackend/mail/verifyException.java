package pulleydoreurae.careerquestbackend.mail;

/**
 * 이메일 인증에 대한 예외처리를 담당하는 Exception
 *
 * @author : hanjaeseong
 * @since : 2024/02/04
 */
@Deprecated
public class verifyException extends Exception {

	public verifyException(String errMeg) {
		super(errMeg);
	}

}
