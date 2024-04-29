package pulleydoreurae.careerquestbackend.auth.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.*;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.*;
import pulleydoreurae.careerquestbackend.auth.domain.entity.ChangeUserEmail;
import pulleydoreurae.careerquestbackend.auth.domain.entity.HelpUserPassword;
import pulleydoreurae.careerquestbackend.auth.service.UserAccountService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
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
	private final UserAccountService userAccountService;

	@Autowired
	public UserAccountController(UserAccountRepository userAccountRepository,
			UserCareerDetailsRepository userCareerDetailsRepository,
			UserTechnologyStackRepository userTechnologyStackRepository,
			EmailAuthenticationRepository emailAuthenticationRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
			MailService mailService, UserInfoUserIdRepository userIdRepository,
								 UserAccountService userAccountService) {
		this.userAccountRepository = userAccountRepository;
		this.userCareerDetailsRepository = userCareerDetailsRepository;
		this.userTechnologyStackRepository = userTechnologyStackRepository;
		this.emailAuthenticationRepository = emailAuthenticationRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.mailService = mailService;
		this.userIdRepository = userIdRepository;
		this.userAccountService = userAccountService;
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
		mailService.emailAuthentication(user.getUserId(), user.getUserName(), user.getPhoneNum(), user.getEmail(),
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
	public ResponseEntity<?> addCareer(@Valid UserCareerDetailsRequest userCareerDetailsRequest,
		BindingResult bindingResult) {

		if(bindingResult.hasErrors()) {
			return makeBadRequestByBindingResult("[회원 - 직무 추가] 유효성 검사 실패 : {}", userCareerDetailsRequest.getUserId(),
				bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(userCareerDetailsRequest.getUserId());

		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
				.majorCategory(userCareerDetailsRequest.getMajorCategory())
				.middleCategory(userCareerDetailsRequest.getMiddleCategory())
				.smallCategory(userCareerDetailsRequest.getSmallCategory())
				.build();
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
	public ResponseEntity<?> addStacks(
			@Valid @RequestBody UserTechnologyStackRequest userTechnologyStackRequest, BindingResult bindingResult) {


		if(bindingResult.hasErrors()){
			return makeBadRequestByBindingResult("[회원 - 기술스택 추가] 유효성 검사 실패 : {}", userTechnologyStackRequest.getUserId(), bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(userTechnologyStackRequest.getUserId());

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

	/**
	 * 비밀번호 변경링크를 보내주는 메서드
	 *
	 * @param userIdRequest 찾고싶은 계정의 ID가 담겨있는 Request
	 * @return 요청에 대한 응답
	 */
	@PostMapping("/users/help/sendPassword")
	public ResponseEntity<UserIdResponse> findPassword(@Valid @RequestBody UserIdRequest userIdRequest, BindingResult bindingResult) {

		if(bindingResult.hasErrors()){
			return makeBadRequestByBindingResult("[비밀번호 - 찾기] 유효성 검사 실패 : {}", userIdRequest.getUserId(), bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(userIdRequest.getUserId());

		userAccountService.findPassword(user.getUserId(), user.getEmail());

		log.info("[비밀번호 - 찾기] 찾기를 요청한 회원 : {}", user.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(
				UserIdResponse.builder()
						.userId(user.getUserId())
						.msg("이메일 인증을 요청했습니다.")
						.build()
		);
	}

	/**
	 * 비밀번호를 바꾸는 폼을 보여주는 곳
	 * uuid에 해당하는 유저가 비밀번호 변경 요청을 한 리스트에 있는지 확인하는 메서드
	 *
	 * @param uuid 변경할 유저의 식별번호
	 * @return 요청에 대한 응답을 반환
	 */
	@GetMapping("/users/help/{uuid}")
	public ResponseEntity<UserIdResponse> checkFindPassword(@PathVariable String uuid) {
		String userId = userAccountService.checkUserIdByUuid(uuid);

		if (userId == null) return makeBadRequestUsingUserIdResponse("[비밀번호 - 찾기] 일치하는 계저이 없습니다.", null);

		log.info("[비밀번호 - 찾기] 찾기를 요청한 회원 : {}", userId);
		return ResponseEntity.status(HttpStatus.OK).body(
				UserIdResponse.builder()
						.userId(userId)
						.msg("일치하는 계정이 있습니다.")
						.build()
		);
	}

	/**
	 * 비밀번호를 변경하는 메서드
	 *
	 * @param uuid                          변경할 유저의 식별번호
	 * @param userFindPasswordChangeRequest 변경할 비빌번호가 들어있는 Request
	 * @param bindingResult                 @Valid 어노테이션에서 생기는 오류는 담는 변수
	 * @return 요청에 대해 userId와 응답을 반환
	 */
	@PostMapping("/users/help/{uuid}")
	public ResponseEntity<UserIdResponse> changePassword(@PathVariable String uuid,
														 @Valid @RequestBody UserFindPasswordChangeRequest userFindPasswordChangeRequest, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return makeBadRequestByBindingResult("[비밀번호 - 찾기] 유효성 검사 실패 : {}", null, bindingResult);
		}

		String userId = userAccountService.checkUserIdByUuid(uuid);

		if (userId == null) return makeBadRequestUsingUserIdResponse("[비밀번호 - 찾기] 일치하는 계저이 없습니다.", null);

		if (!userFindPasswordChangeRequest.getPassword1().equals(userFindPasswordChangeRequest.getPassword2())) {
			log.warn("[비밀번호 - 찾기] 유효성 검사 실패 : {}", userId);
			return makeBadRequestUsingUserIdResponse("비밀번호와 비밀번호 확인이 서로 일치하지 않습니다.", userId);
		}

		userAccountService.updatePassword(userId,
				bCryptPasswordEncoder.encode(userFindPasswordChangeRequest.getPassword1()));
		userAccountService.deleteHelpUser(uuid);

		log.info("[비밀번호 - 찾기] 비밀번호 변경을 한 회원 : {}", userId);
		return ResponseEntity.status(HttpStatus.OK).body(
				UserIdResponse.builder()
						.userId(userId)
						.msg("비밀번호를 변경하였습니다.")
						.build()
		);
	}

	/**
	 * 유저를 삭제하는 메서드
	 *
	 * @param userDeleteRequest 삭제할 유저와 해당 유저의 확인 비밀번호를 받는 Request
	 * @param bindingResult     @Valid 어노테이션에서 나오는 오류를 담는 변수
	 * @return 요청에 대해 userId와 응답을 반환
	 */
	@PostMapping("/users/delete")
	public ResponseEntity<UserIdResponse> deleteUser(@Valid @RequestBody UserDeleteRequest userDeleteRequest,
													 BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return makeBadRequestByBindingResult("[회원 - 탈퇴] 유효성 검사 실패 : {}", userDeleteRequest.getUserId(), bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(userDeleteRequest.getUserId());

		if (!userAccountService.isCurrentPassword(user,
				userDeleteRequest.getPassword())) {
			log.warn("[회원 - 탈퇴] 유효성 검사 실패 : {}", user.getUserId());
			return makeBadRequestUsingUserIdResponse("본인확인 비밀번호 계정 비밀번호가 서로 일치하지 않습니다.", user.getUserId());
		}

		userAccountService.deleteUser(user);
		log.info("[회원 - 탈퇴] 탈퇴를 요청한 회원 : {}", userDeleteRequest.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(
				UserIdResponse.builder()
						.userId(userDeleteRequest.getUserId())
						.msg("일치하는 계정이 있습니다.")
						.build()
		);
	}

	/**
	 * 유저의 정보를 열람하는 메서드
	 *
	 * @param userIdRequest 정보를 열람하고 싶은 userId
	 * @return 요청에 대한 유저정보를 반환
	 */
	@GetMapping("/users/details/info")
	public ResponseEntity<?> showUserDetails(@Valid @RequestBody UserIdRequest userIdRequest, BindingResult bindingResult) {

		if (bindingResult.hasErrors()){
			return makeBadRequestByBindingResult("[회원 - 정보 보기] 유효성 검사 실패 : {}", userIdRequest.getUserId(), bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(userIdRequest.getUserId());

		List<String> stacks = userAccountService.getTechnologyStack(user);

		log.info("[회원 - 정보 보기] 정보를 열람할 회원 : {}", user.getUserId());
		return ResponseEntity.status(HttpStatus.OK).body(
				UserDetailsResponse.builder()
						.userId(user.getUserId())
						.email(user.getEmail())
						.majorCategory(user.getUserCareerDetails().getMajorCategory())
						.middleCategory(user.getUserCareerDetails().getMiddleCategory())
						.smallCategory(user.getUserCareerDetails().getSmallCategory())
						.technologyStacks(stacks)
						.build()
		);
	}

	/**
	 * 유저 정보를 수정하는 메서드
	 *
	 * @param showUserDetailsToChangeRequest 수정할 정보가 들어있는 Request
	 * @param bindingResult                  @Valid 어노테이션에서 나오는 오류를 담는 변수
	 * @return 요청에 대한 userId와 응답을 반환
	 */
	@PostMapping("/users/details/changeInfo")
	@ResponseBody
	public ResponseEntity<?> showUserDetailsToChange(@Valid @RequestBody ShowUserDetailsToChangeRequest showUserDetailsToChangeRequest,
													 BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return makeBadRequestByBindingResult("[회원 - 정보 변경] 유효성 검사 실패 : {}", showUserDetailsToChangeRequest.getUserId(), bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(showUserDetailsToChangeRequest.getUserId());

		if (userAccountService.isCurrentPassword(user,
				showUserDetailsToChangeRequest.getPassword())) {

			userAccountService.updateDetails(user, showUserDetailsToChangeRequest);
			List<String> stacks = userAccountService.getTechnologyStack(user);

			return ResponseEntity.status(HttpStatus.OK).body(
					ShowUserDetailsToChangeResponse.builder()
							.userId(user.getUserId())
							.phoneNum(user.getPhoneNum())
							.majorCategory(user.getUserCareerDetails().getMajorCategory())
							.middleCategory(user.getUserCareerDetails().getMiddleCategory())
							.smallCategory(user.getUserCareerDetails().getSmallCategory())
							.technologyStacks(stacks)
							.build()
			);
		} else {

			return makeBadRequestUsingUserIdResponse("비밀번호가 다릅니다", user.getUserId());
		}
	}

	/**
	 * 유저의 비빌번호를 변경하는 메서드
	 *
	 * @param userPasswordUpdateRequest 변경할 유저의 현재 비밀번호와 변경할 비빌번호, 변경할 비밀번호 확인이 들어있는 Reqyest
	 * @param bindingResult             @Valid 어노테이션에서 나오는 오류를 담는 변수
	 * @return 요청에 대한 userId와 응답을 반환
	 */
	@PostMapping("/users/details/update/password")
	public ResponseEntity<UserIdResponse> updateUserPassword(@Valid @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest,
															 BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return makeBadRequestByBindingResult("[회원 - 비밀번호 변경] 유효성 검사 실패 : {}", userPasswordUpdateRequest.getUserId(), bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(userPasswordUpdateRequest.getUserId());

		if (!userAccountService.isCurrentPassword(user,
				userPasswordUpdateRequest.getCurrentPassword())) {
			log.warn("[회원 - 비밀번호 변경] 유효성 검사 실패 : {}", user.getUserId());
			return makeBadRequestUsingUserIdResponse("본인확인 비밀번호 계정 비밀번호가 서로 일치하지 않습니다.", user.getUserId());
		} else {
			if (!userPasswordUpdateRequest.getNewPassword1().equals(userPasswordUpdateRequest.getNewPassword2())) {
				log.warn("[회원 - 비밀번호 변경] 유효성 검사 실패 : {}", user.getUserId());
				return makeBadRequestUsingUserIdResponse("새로운 비밀번호와 새로운 비밀번호 확인이 서로 일치하지 않습니다.", user.getUserId());
			} else {
				userAccountService.updatePassword(user.getUserId(),
						bCryptPasswordEncoder.encode(userPasswordUpdateRequest.getNewPassword1()));

				log.info("[회원 - 비밀번호 변경] 비밀번호 변경을 한 회원 : {}", user.getUserId());
				return ResponseEntity.status(HttpStatus.OK).body(
						UserIdResponse.builder()
								.userId(user.getUserId())
								.msg("비밀번호를 변경하였습니다.")
								.build()
				);
			}
		}
	}

	/**
	 * 유저의 이메일 변경을 위한 메일 전송을 하는 메서드
	 *
	 * @param userChangeEmailRequest 변경할 이메일과 userId가 들어있는 Request
	 * @param bindingResult          @Valid 어노테이션에서 나오는 오류를 담는 변수
	 * @return 요청에대한 userId와 응답을 리턴
	 */
	@PostMapping("/users/details/update/email")
	public ResponseEntity<UserIdResponse> sendUserEmailToUpdate(@Valid @RequestBody UserChangeEmailRequest userChangeEmailRequest
			, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return makeBadRequestByBindingResult("[회원 - 이메일 변경] 유효성 검사 실패 : {}", userChangeEmailRequest.getUserId(), bindingResult);
		}

		UserAccount user = userAccountService.findUserByUserId(userChangeEmailRequest.getUserId());

		userAccountService.sendUpdateEmailLink(userChangeEmailRequest.getUserId(), userChangeEmailRequest.getEmail());

		return ResponseEntity.status(HttpStatus.OK).body(
				UserIdResponse.builder()
						.userId(user.getUserId())
						.msg("변경할 이메일 주소로 인증 메일을 보냈습니다.")
						.build()
		);
	}

	/**
	 * 유저의 이메일 변경 요청을 확인하여 변경을 해주는 메서드
	 *
	 * @param uuid 이메일 변경을 요청한 유저의 식별자
	 * @return 요청에 대한 userId와 응답을 반환
	 */
	@GetMapping("/users/details/update/email/{uuid}")
	public ResponseEntity<?> checkUserEmailToUpdate(@PathVariable String uuid) {

		ChangeUserEmail changeUserEmail = userAccountService.checkUpdateEmailUserIdByUuid(uuid);

		log.info("[회원 - 이메일 변경] 찾기를 요청한 회원 : {}", changeUserEmail.getUserId());
		userAccountService.updateEmail(changeUserEmail);
		return ResponseEntity.status(HttpStatus.OK).body(
				UserIdResponse.builder()
						.userId(changeUserEmail.getUserId())
						.msg("이메일을 변경했습니다.")
						.build()
		);
	}

	/**
	 * UserIdResponse 형태의 BAD_REQUEST 생성 메서드
	 *
	 * @param message 작성할 메세지
	 * @param userId  요청한 userId
	 * @return 완성된 BAD_REQUEST
	 */
	private ResponseEntity<UserIdResponse> makeBadRequestUsingUserIdResponse(String message, String userId) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				UserIdResponse.builder()
						.userId(userId)
						.msg(message)
						.build());
	}

	/**
	 * 검증 실패 메서드
	 *
	 * @param logMessage    검증 실패시 나오는 메세지
	 * @param userId        요청한 userId
	 * @param bindingResult 검증 결과
	 * @return              에러를 담은 ResponseEntity 반환
	 */
	private ResponseEntity<UserIdResponse> makeBadRequestByBindingResult(String logMessage, String userId, BindingResult bindingResult) {

		StringBuilder sb = new StringBuilder();
		bindingResult.getAllErrors().forEach(objectError -> {
			String message = objectError.getDefaultMessage();
			sb.append(message).append("\\n");
		});

		log.warn(logMessage, sb);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				UserIdResponse.builder()
						.userId(userId)
						.msg(sb.toString())
						.build()
		);

	}
}
