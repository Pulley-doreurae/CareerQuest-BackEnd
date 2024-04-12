package pulleydoreurae.careerquestbackend.auth.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserAccountRegisterRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserCareerDetailsRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserTechnologyStackRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.DuplicateCheckResponse;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.UserAccountRegisterResponse;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserCareerDetails;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserTechnologyStack;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserCareerDetailsRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserTechnologyStackRepository;
import pulleydoreurae.careerquestbackend.mail.repository.EmailAuthenticationRepository;
import pulleydoreurae.careerquestbackend.mail.repository.UserInfoUserIdRepository;
import pulleydoreurae.careerquestbackend.mail.service.MailService;

/**
 * 회원가입을 처리하는 컨트롤러
 * 중복을 확인하기 위해 UserAccountRepository 를 주입받아 사용한다.
 */
@Slf4j
@RequestMapping("/api")
@RestController
public class UserAccountController {

	private final UserAccountRepository userAccountRepository;
	private final UserCareerDetailsRepository userCareerDetailsRepository;
	private final UserTechnologyStackRepository userTechnologyStackRepository;
	private final EmailAuthenticationRepository emailAuthenticationRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final MailService mailService;
	private final UserInfoUserIdRepository userIdRepository;

	@Autowired
	public UserAccountController(UserAccountRepository userAccountRepository,
			UserCareerDetailsRepository userCareerDetailsRepository,
			UserTechnologyStackRepository userTechnologyStackRepository,
			EmailAuthenticationRepository emailAuthenticationRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
			MailService mailService, UserInfoUserIdRepository userIdRepository) {
		this.userAccountRepository = userAccountRepository;
		this.userCareerDetailsRepository = userCareerDetailsRepository;
		this.userTechnologyStackRepository = userTechnologyStackRepository;
		this.emailAuthenticationRepository = emailAuthenticationRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.mailService = mailService;
		this.userIdRepository = userIdRepository;
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
							.birth(request.getBirth())
							.gender(request.getGender())
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
	@GetMapping("/users/username/{userId}")
	public ResponseEntity<DuplicateCheckResponse> duplicateCheckId(@PathVariable String userId) {

		// 회원가입 완료된 경우와 이메일 인증 대기중인 경우 모두 확인해서 중복을 피하기
		if (userAccountRepository.existsByUserId(userId) || userIdRepository.existsById(userId)) {
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
	@GetMapping("/users/email/{email}")
	public ResponseEntity<DuplicateCheckResponse> duplicateCheckEmail(@PathVariable String email) {

		// 회원가입 완료된 경우와 이메일 인증 대기중인 경우 모두 확인해서 중복을 피하기
		if (userAccountRepository.existsByEmail(email) || emailAuthenticationRepository.existsById(email)) {
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
	@PostMapping("/users")
	public ResponseEntity<UserAccountRegisterResponse> register(@Valid @RequestBody UserAccountRegisterRequest user,
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
							.birth(user.getBirth())
							.gender(user.getGender())
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
							.birth(user.getBirth())
							.gender(user.getGender())
							.msg("이미 존재하는 이메일입니다.")
							.build());
		}

		// 이메일 인증 전송
		mailService.sendMail(user.getUserId(), user.getUserName(), user.getPhoneNum(), user.getEmail(),
				bCryptPasswordEncoder.encode(user.getPassword()), user.getBirth(), user.getGender());

		log.info("[회원가입 - 인증] 인증을 요청한 회원 : {}", user.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(
				UserAccountRegisterResponse.builder()
						.userId(user.getUserId())
						.userName(user.getUserName())
						.email(user.getEmail())
						.userName(user.getUserName())
						.phoneNum(user.getPhoneNum())
						.birth(user.getBirth())
						.gender(user.getGender())
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
		boolean isOk = mailService.verifyEmail(email, num);
		UserAccount user = mailService.getVerifiedUser(email);
		if (!isOk || user == null) { // 이메일 인증에 실패한 경우
			log.warn("[회원가입 - 인증] 인증실패 : {}", email);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					UserAccountRegisterResponse.builder()
							.msg("인증을 실패했습니다.")
							.build()
			);
		}

		userAccountRepository.save(user);
		mailService.removeVerifiedUser(user.getUserId(), email);
		log.info("[회원가입 - 인증] 새로 추가된 회원 : {}", user.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(
				UserAccountRegisterResponse.builder()
						.userId(user.getUserId())
						.userName(user.getUserName())
						.email(user.getEmail())
						.phoneNum(user.getPhoneNum())
						.birth(user.getBirth())
						.gender(user.getGender())
						.msg("회원가입에 성공하였습니다.")
						.build()
		);
	}

	/**
	 * 사용자 직무 추가 메서드
	 *
	 * @param userCareerDetailsRequest 사용자 직무에 대한 request
	 * @return 요청에 대한 응답
	 */
	@PostMapping("/users/details/careers")
	public ResponseEntity<SimpleResponse> addCareer(UserCareerDetailsRequest userCareerDetailsRequest) {
		String userId = userCareerDetailsRequest.getUserId();
		Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUserId(userId);

		if (optionalUserAccount.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg("사용자 정보를 찾을 수 없습니다.")
							.build());
		}

		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
				.majorCategory(userCareerDetailsRequest.getMajorCategory())
				.middleCategory(userCareerDetailsRequest.getMiddleCategory())
				.smallCategory(userCareerDetailsRequest.getSmallCategory())
				.build();
		UserAccount user = optionalUserAccount.get();
		user.setUserCareerDetails(userCareerDetails);
		userAccountRepository.save(user); // 직무에 대한 정보 저장
		userCareerDetailsRepository.save(userCareerDetails);
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("직무 등록에 성공하였습니다.")
						.build());
	}

	/**
	 * 사용자의 기술스택을 전달받아 저장하는 메서드
	 *
	 * @param userTechnologyStackRequest 기술스택에 대한 정보
	 * @return 요청에 대한 처리
	 */
	@PostMapping("/users/details/stacks")
	public ResponseEntity<SimpleResponse> addStacks(
			@RequestBody UserTechnologyStackRequest userTechnologyStackRequest) {
		String userId = userTechnologyStackRequest.getUserId();
		Optional<UserAccount> optionalUserAccount = userAccountRepository.findByUserId(userId);

		if (optionalUserAccount.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg("사용자 정보를 찾을 수 없습니다.")
							.build());
		}

		UserAccount user = optionalUserAccount.get();
		List<UserTechnologyStack> stacks = new ArrayList<>(); // 기술스택을 담을 리스트
		userTechnologyStackRequest.getStacks().forEach(stackId -> { // 리스트로 넘어온 스택들 데이터베이스에 저장
			UserTechnologyStack stack = UserTechnologyStack.builder()
					.userAccount(user)
					.stackId(stackId)
					.build();
			stacks.add(stack);
			userTechnologyStackRepository.save(stack);
		});

		user.setStacks(stacks);
		userAccountRepository.save(user); // 사용자 정보 갱신

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("직무 등록에 성공하였습니다.")
						.build());
	}
}
