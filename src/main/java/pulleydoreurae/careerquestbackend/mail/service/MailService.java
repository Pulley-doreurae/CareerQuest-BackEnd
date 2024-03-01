package pulleydoreurae.careerquestbackend.mail.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.mail.repository.MailRepository;
import pulleydoreurae.careerquestbackend.mail.verifyException;

/**
 * 이메일 전송 및 인증을 담당하는 Service
 *
 * @author : hanjaeseong
 * @since : 2024/02/04
 */
@Service
@RequiredArgsConstructor
public class MailService {

	private final JavaMailSender javaMailSender;
	private final MailRepository mailRepository;

	private static final String senderEmail = "admin@chwijunjundan";

	@Value("${spring.mail.domain}")
	private String domain;

	private final SpringTemplateEngine templateEngine;

	/**
	 * 인증번호를 생성하는 메서드
	 *
	 * @return 인증번호를 돌려준다.
	 * @throws NoSuchAlgorithmException 해당 알고리즘을 찾을 수 없을 때의 예외처리
	 */
	public String createNumber() throws NoSuchAlgorithmException {
		String result;
		do {
			int num = SecureRandom.getInstanceStrong().nextInt(999999);
			result = String.valueOf(num);
		} while (result.length() != 6);

		return result;
	}

	/**
	 * 인증링크 이메일을 전송하는 메서드
	 *
	 * @param userId,userName,phoneNum,email,password 인증요청을 한 사용자의 회원정보
	 * @throws NoSuchAlgorithmException 인증번호를 생성할 때 해당 알고리즘을 찾을 수 없을 때의 예외처리
	 */
	public void sendMail(String userId, String userName, String phoneNum, String email, String password) throws
			NoSuchAlgorithmException {

		String number = createNumber();
		String verification_url = domain + "/api/verify?certificationNumber=" + number + "&email=" + email;

		MimeMessagePreparator preparatory = mimeMessage -> {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

			//template 명: src/main/resources/templates 하단에 작성된 html 파일 명
			Context context = new Context();

			String content = templateEngine.process("mailForm", context);
			content = content.replace("@{verification_url}", verification_url);

			helper.setTo(email);
			helper.setFrom(senderEmail);
			helper.setSubject("취준진담 이메일 인증");

			helper.setText(content, true);
		};
		mailRepository.saveCertificationNumber(email, number);
		mailRepository.setUserAccount(userId, userName, phoneNum, email, password);
		javaMailSender.send(preparatory);

	}

	/**
	 * 인증을 확인하는 메서드
	 *
	 * @param email               인증을 요청한 이메일
	 * @param certificationNumber 인증을 요청한 이메일의 인증번호
	 * @return 인증번호가 맞다면 true / 아닐경우 false
	 * @throws verifyException 인증 실패에 대한 예외처리
	 */
	public boolean verifyEmail(String email, String certificationNumber) throws verifyException {
		boolean isOk = isVerify(email, certificationNumber) ? true : false;
		return isOk;
	}

	/**
	 * Redis에 인증을 요청한 이메일을 검증하는 메서드
	 *
	 * @param email               인증을 요청한 이메일
	 * @param certificationNumber 인증을 요청한 이메일의 인증번호
	 * @return 인증 정보가 일치하는지 확인
	 * @throws verifyException 인증 실패에 대한 예외처리
	 */
	private boolean isVerify(String email, String certificationNumber) throws verifyException {
		boolean validatedEmail = isEmailExists(email);
		if (!isEmailExists(email)) {
			// 이메일이 없을 떄
			throw new verifyException("failed : 해당 이메일이 존재하지 않습니다.");
		}
		return (validatedEmail &&
				mailRepository.getCertificationNumber(email).equals(certificationNumber));
	}

	/**
	 * 이메일 존재 여부 확인 메서드
	 *
	 * @param email 요청한 이메일
	 * @return 존재 여부 확인
	 */
	private boolean isEmailExists(String email) {
		return mailRepository.hasKey(email);
	}

	/**
	 * 요청한 이메일의 유저 정보 반환 메서드
	 *
	 * @param email 요청한 이메일
	 * @return 이메일의 유저 정보
	 */
	public UserAccount getVerifiedUser(String email) {
		return mailRepository.getUserAccount(email);
	}
}

