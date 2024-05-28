package pulleydoreurae.careerquestbackend.common.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;

/**
 * 자주 사용되는 메서드를 묶은 Service
 *
 * @author : parkjihyeok
 * @since : 2024/05/28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommonService {

	private final UserAccountRepository userAccountRepository;

	/**
	 * 회원아이디로 회원정보를 찾아오는 메서드
	 *
	 * @param userId    회원아이디
	 * @param checkAuth 요청자의 권한확인 요청 (true -> 권한확인, false -> 권한확인 X)
	 * @return 해당하는 회원정보가 있으면 회원정보를, 없다면 null 리턴
	 */
	public UserAccount findUserAccount(String userId, boolean checkAuth) {

		if (checkAuth) { // 권한확인 요청이 들어오면
			checkAuth(userId);
		}
		Optional<UserAccount> findUser = userAccountRepository.findByUserId(userId);

		// 회원정보를 찾을 수 없다면
		if (findUser.isEmpty()) {
			log.error("{} 의 회원 정보를 찾을 수 없습니다.", userId);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return findUser.get();
	}

	/**
	 * 요청자가 접근 권한이 있는지 확인하는 메서드
	 *
	 * @param userId 요청자가 가지고 있어야하는 userId
	 */
	public void checkAuth(String userId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof UserDetails) {
				if (!userId.equals(((UserDetails)principal).getUsername())) { // 요청자가 권한이 없다면
					throw new IllegalAccessError("요청자의 권한을 확인할 수 없습니다.");
				}
			} else { // 시큐리티에서 권한을 꺼낼 수 없다면
				throw new IllegalAccessError("요청자의 권한을 확인할 수 없습니다.");
			}
		}
	}
}
