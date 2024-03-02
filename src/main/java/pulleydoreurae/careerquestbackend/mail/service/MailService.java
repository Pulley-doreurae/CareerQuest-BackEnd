package pulleydoreurae.careerquestbackend.mail.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.mail.entity.EmailAuthentication;
import pulleydoreurae.careerquestbackend.mail.repository.EmailAuthenticationRepository;

/**
 * 이메일 전송 및 인증을 담당하는 Service
 *
 * @author : hanjaeseong
 * @since : 2024/02/04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

	private final JavaMailSender javaMailSender;
	private final EmailAuthenticationRepository emailAuthenticationRepository;

	private static final String senderEmail = "admin@chwijunjundan";

	@Value("${spring.mail.domain}")
	private String domain;

	private final SpringTemplateEngine templateEngine;

	/**
	 * 인증번호를 생성하는 메서드
	 *
	 * @return 인증번호를 돌려준다.
	 */
	public String createNumber() {
		String result;
		do {
			int num;
			try {
				num = SecureRandom.getInstanceStrong().nextInt(999999);
			} catch (NoSuchAlgorithmException e) {
				// 알고리즘에서 예외가 발생하면 Random 클래스를 사용해 인증번호를 뽑는다.
				do {
					Random random = new Random();
					num = random.nextInt(999999);
					result = String.valueOf(num);
				} while (result.length() != 6);
			}
			result = String.valueOf(num);
		} while (result.length() != 6);

		return result;
	}

	/**
	 * 인증링크 이메일을 전송하는 메서드
	 *
	 * @param userId,userName,phoneNum,email,password 인증요청을 한 사용자의 회원정보
	 */
	public void sendMail(String userId, String userName, String phoneNum, String email, String password) {

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
		emailAuthenticationRepository.save(
				new EmailAuthentication(email, userId, userName, phoneNum, password, number));
		javaMailSender.send(preparatory);
		log.info("[회원가입 - 인증] : {} 의 회원가입을 위한 객체저장 및 메일전송", email);
	}

	/**
	 * 인증을 확인하는 메서드
	 *
	 * @param email               인증을 요청한 이메일
	 * @param certificationNumber 인증을 요청한 이메일의 인증번호
	 * @return 인증번호가 맞다면 true / 아닐경우 false
	 */
	public boolean verifyEmail(String email, String certificationNumber) {
		return isVerify(email, certificationNumber);
	}

	/**
	 * Redis 에 인증을 요청한 이메일을 검증하는 메서드
	 *
	 * @param email               인증을 요청한 이메일
	 * @param certificationNumber 인증을 요청한 이메일의 인증번호
	 * @return 인증 정보가 일치하는지 확인
	 */
	private boolean isVerify(String email, String certificationNumber) {
		Optional<EmailAuthentication> authentication = emailAuthenticationRepository.findById(email);
		if (!isEmailExists(email) || authentication.isEmpty()) {
			// 이메일이 redis 에 없거나 인증정보를 불러올 수 없다면
			log.warn("[회원가입 - 인증] : {} 의 회원가입을 위한 정보를 불러올 수 없음 (1차 확인)", email);
			return false;
		}
		EmailAuthentication getAuthentication = authentication.get();
		return getAuthentication.getCode().equals(certificationNumber);
	}

	/**
	 * 이메일 존재 여부 확인 메서드
	 *
	 * @param email 요청한 이메일
	 * @return 존재 여부 확인
	 */
	private boolean isEmailExists(String email) {
		return emailAuthenticationRepository.existsById(email);
	}

	/**
	 * 요청한 이메일의 유저 정보 반환 메서드
	 * 유저 정보를 확인할 수 있다면 회원가입에 성공하므로 redis 에 제거해주기
	 *
	 * @param email 요청한 이메일
	 * @return 이메일의 유저 정보
	 */
	public UserAccount getVerifiedUser(String email) {
		Optional<EmailAuthentication> authentication = emailAuthenticationRepository.findById(email);

		if (authentication.isEmpty()) {
			log.warn("[회원가입 - 인증] : {} 의 회원가입을 위한 정보를 불러올 수 없음 (2차 확인)", email);
			return null;
		}
		EmailAuthentication getAuthentication = authentication.get();
		return UserAccount.builder()
				.userId(getAuthentication.getUserId())
				.email(getAuthentication.getEmail())
				.userName(getAuthentication.getUserName())
				.phoneNum(getAuthentication.getPhoneNum())
				.password(getAuthentication.getPassword())
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();
	}

	/**
	 * 인증에 완료한 사용자 정보를 redis 에서 삭제하는 메서드
	 *
	 * @param email redis 에 저장된 key 정보
	 */
	public void removeVerifiedUser(String email) {
		emailAuthenticationRepository.deleteById(email);
	}
}

