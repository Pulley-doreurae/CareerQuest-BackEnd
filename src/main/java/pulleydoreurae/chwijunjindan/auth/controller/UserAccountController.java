package pulleydoreurae.chwijunjindan.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pulleydoreurae.chwijunjindan.auth.domain.UserAccount;
import pulleydoreurae.chwijunjindan.auth.domain.UserAccountRegisterRequest;
import pulleydoreurae.chwijunjindan.auth.domain.UserAccountRegisterResponse;
import pulleydoreurae.chwijunjindan.auth.domain.UserRole;
import pulleydoreurae.chwijunjindan.auth.repository.UserAccountRepository;

/**
 * 회원가입을 처리하는 컨트롤러
 * 중복을 확인하기 위해 UserAccountRepository 를 주입받아 사용한다.
 */
@RestController
public class UserAccountController {

	private final UserAccountRepository userAccountRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UserAccountController(UserAccountRepository userAccountRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userAccountRepository = userAccountRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			bindingResult.getAllErrors().forEach(objectError -> {
				String message = objectError.getDefaultMessage();

				sb.append(message).append("\n");
			});

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					UserAccountRegisterResponse.builder()
							.userId(user.getUserId())
							.userName(user.getUserName())
							.email(user.getEmail())
							.userName(user.getUserName())
							.msg(sb.toString())
							.build()
			);
		}
		if (userAccountRepository.existsByUserId(user.getUserId()) ||
				userAccountRepository.existsByEmail(user.getEmail())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					UserAccountRegisterResponse.builder()
							.userId(user.getUserId())
							.userName(user.getUserName())
							.email(user.getEmail())
							.userName(user.getUserName())
							.msg("아이디 혹은 이메일이 이미 존재합니다.")
							.build()
			);
		}
		UserAccount userAccount = UserAccount.builder()
				.userId(user.getUserId())
				.userName(user.getUserName())
				.email(user.getEmail())
				.password(bCryptPasswordEncoder.encode(user.getPassword()))
				.role(UserRole.ROLE_TEMPORARY_USER)
				.build();
		userAccountRepository.save(userAccount);

		return ResponseEntity.status(HttpStatus.OK).body(
				UserAccountRegisterResponse.builder()
						.userId(user.getUserId())
						.userName(user.getUserName())
						.email(user.getEmail())
						.userName(user.getUserName())
						.msg("회원가입에 성공하였습니다.")
						.build()
		);
	}
}
