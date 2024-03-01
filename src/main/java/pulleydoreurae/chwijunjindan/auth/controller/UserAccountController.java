package pulleydoreurae.chwijunjindan.auth.controller;

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.chwijunjindan.auth.domain.dto.DuplicateCheckResponse;
import pulleydoreurae.chwijunjindan.auth.domain.dto.UserAccountRegisterRequest;
import pulleydoreurae.chwijunjindan.auth.domain.dto.UserAccountRegisterResponse;
import pulleydoreurae.chwijunjindan.auth.domain.entity.UserAccount;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;
import pulleydoreurae.chwijunjindan.mail.repository.MailRepository;
import pulleydoreurae.chwijunjindan.mail.service.MailService;
import pulleydoreurae.chwijunjindan.mail.verifyException;

/**
 * 회원가입을 처리하는 컨트롤러
 * 중복을 확인하기 위해 UserAccountRepository 를 주입받아 사용한다.
 */
@Slf4j
@RequestMapping("/api")
@RestController
public class UserAccountController {

	private final UserAccountRepository userAccountRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final MailService mailService;
	private final MailRepository mailRepository;

	@Autowired
	public UserAccountController(UserAccountRepository userAccountRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder, MailService mailService, MailRepository mailRepository) {
		this.userAccountRepository = userAccountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.mailService = mailService;
		this.mailRepository = mailRepository;
	}

	/**
	 * 유효성 검증 메서드
	 *
	 * @param request       사용자의 회원가입, 중복확인 요청
	 * @param bindingResult 유효성 검사에 관련된 파라미터
	 * @return 에러가 존재하면 BAD_REQUEST 를 만들어 리턴하고 정상적인 요청이라면 null 을 리턴해 유효성을 검사함
	 */
	private static ResponseEntity<UserAccountRegisterResponse> validCheck(
			UserAccountRegisterRequest request, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			bindingResult.getAllErrors().forEach(objectError -> {
				String message = objectError.getDefaultMessage();

				sb.append(message).append("\n");
			});

			log.warn("[회원가입] 유효성 검사 실패 : {}", sb);

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					UserAccountRegisterResponse.builder()
							.userId(request.getUserId())
							.userName(request.getUserName())
							.email(request.getEmail())
							.userName(request.getUserName())
							.phoneNum(request.getPhoneNum())
							.msg(sb.toString())
							.build()
			);
		}
		return null;
	}

	/**
	 * 아이디 중복확인 메서드 (1차)
	 *
	 * @param userId 사용자의 아이디 중복확인을 위한 요청
	 * @return 중복이라면 400, 중복이 아니라면 200 리턴
	 */
	@PostMapping("duplicate-check-id")
	public ResponseEntity<DuplicateCheckResponse> duplicateCheckId(String userId) {

		// TODO: 2024/02/26 중복확인시 redis 도 확인해서 동시에 같은 아이디로 회원가입 할 수 없도록 추가하기
		//  중복이 안된다면 해당 아이디를 선점하기 위해 redis 에 저장
		if (userAccountRepository.existsByUserId(userId)) {
			log.warn("[회원가입] 중복된 아이디 : {}", userId);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					DuplicateCheckResponse.builder()
							.field(userId)
							.msg("이미 존재하는 아이디입니다.")
							.build()
			);
		}

		log.info("[회원가입] 가입 가능한 아이디 : {}", userId);
		return ResponseEntity.status(HttpStatus.OK).body(
				DuplicateCheckResponse.builder()
						.field(userId)
						.msg("가입 가능한 아이디입니다.")
						.build()
		);
	}

	/**
	 * 이메일 중복확인 메서드 (1차)
	 *
	 * @param email 사용자의 이메일 중복확인을 위한 요청
	 * @return 중복이라면 400, 중복이 아니라면 200 리턴
	 */
	@PostMapping("duplicate-check-email")
	public ResponseEntity<DuplicateCheckResponse> duplicateCheckEmail(String email) {

		// TODO: 2024/02/26 중복확인시 redis 도 확인해서 동시에 같은 아이디로 회원가입 할 수 없도록 추가하기
		//  중복이 안된다면 해당 이메일을 선점하기위해 redis 에 저장
		if (userAccountRepository.existsByEmail(email)) {
			log.warn("[회원가입] 중복된 이메일 : {}", email);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					DuplicateCheckResponse.builder()
							.field(email)
							.msg("이미 존재하는 이메일입니다.")
							.build()
			);
		}

		log.info("[회원가입] 가입 가능한 이메일 : {}", email);
		return ResponseEntity.status(HttpStatus.OK).body(
				DuplicateCheckResponse.builder()
						.field(email)
						.msg("가입 가능한 이메일입니다.")
						.build()
		);
	}

	/**
	 * 회원가입 메서드
	 *
	 * @param user          dto 에 회원가입에 필요한 정보가 담겨서 전달된다.
	 * @param bindingResult @Valid 어노테이션으로 유효성 검증에서 에러가 발생하면 해당 에러를 가져올 수 있다.
	 * @return 회원가입에 요청했었던 정보를 요청 결과와 함께 돌려준다.
	 */
	@PostMapping("/register")
	public ResponseEntity<UserAccountRegisterResponse> register(@Valid UserAccountRegisterRequest user,
			BindingResult bindingResult) {

		// 유효성 검사
		ResponseEntity<UserAccountRegisterResponse> BAD_REQUEST = validCheck(
				user, bindingResult);
		if (BAD_REQUEST != null)
			return BAD_REQUEST;

		// 아이디 중복확인 (2차)
		ResponseEntity<DuplicateCheckResponse> DUPLICATE_CHECK = duplicateCheckId(user.getUserId());
		if (DUPLICATE_CHECK.getStatusCode() == HttpStatus.BAD_REQUEST) {

			log.error("[회원가입] 중복된 아이디 : {}", user.getUserId());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					UserAccountRegisterResponse.builder()
							.userId(user.getUserId())
							.userName(user.getUserName())
							.email(user.getEmail())
							.userName(user.getUserName())
							.phoneNum(user.getPhoneNum())
							.msg("이미 존재하는 아이디입니다.")
							.build());
		}

		// 이메일 중복확인 (2차)
		DUPLICATE_CHECK = duplicateCheckEmail(user.getEmail());
		if (DUPLICATE_CHECK.getStatusCode() == HttpStatus.BAD_REQUEST) {

			log.error("[회원가입] 중복된 이메일 : {}", user.getEmail());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					UserAccountRegisterResponse.builder()
							.userId(user.getUserId())
							.userName(user.getUserName())
							.email(user.getEmail())
							.userName(user.getUserName())
							.phoneNum(user.getPhoneNum())
							.msg("이미 존재하는 이메일입니다.")
							.build());
		}

		// TODO: 2024/02/26 예외처리 추가하기
		// 이메일 인증 전송
		try {
			mailService.sendMail(user.getUserId(), user.getUserName(),
					user.getPhoneNum(), user.getEmail(),
					bCryptPasswordEncoder.encode(user.getPassword()));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		log.info("[회원가입 - 인증] 인증을 요청한 회원 : {}", user.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(
				UserAccountRegisterResponse.builder()
						.userId(user.getUserId())
						.userName(user.getUserName())
						.email(user.getEmail())
						.userName(user.getUserName())
						.phoneNum(user.getPhoneNum())
						.msg("이메일 인증을 요청했습니다.")
						.build()
		);
	}

	/**
	 * 이메일 인증 확인 메서드
	 *
	 * @param email 인증 링크에 포함되어 있는 이메일
	 * @param num   인증 링크에 포함되어 있는 인증번호
	 * @return 인증을 요청한 회원 정보를 요청 결과와 함께 돌려준다.
	 */
	@GetMapping("/verify")
	public ResponseEntity<UserAccountRegisterResponse> verifyMailCheck(
			@RequestParam(name = "email") String email,
			@RequestParam(name = "certificationNumber") String num
	) {
		// TODO: 2024/02/26 예외처리 추가하기
		boolean isOk;
		try {
			isOk = mailService.verifyEmail(email, num);
		} catch (verifyException e) {
			throw new RuntimeException(e);
		}

		UserAccount user = mailService.getVerifiedUser(email);
		mailRepository.removeCertification(email);

		if (!isOk) {
			log.warn("[회원가입 - 인증] 인증실패 : {}", user.getUserId());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					UserAccountRegisterResponse.builder()
							.userId(user.getUserId())
							.userName(user.getUserName())
							.email(user.getEmail())
							.phoneNum(user.getPhoneNum())
							.msg("인증을 실패했습니다.")
							.build()
			);
		}

		userAccountRepository.save(user);

		log.info("[회원가입 - 인증] 새로 추가된 회원 : {}", user.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(
				UserAccountRegisterResponse.builder()
						.userId(user.getUserId())
						.userName(user.getUserName())
						.email(user.getEmail())
						.phoneNum(user.getPhoneNum())
						.msg("회원가입에 성공하였습니다.")
						.build()
		);
	}
}

