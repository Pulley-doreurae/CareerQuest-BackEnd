package pulleydoreurae.careerquestbackend.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pulleydoreurae.careerquestbackend.auth.domain.CustomUserDetails;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;

/**
 * UserDetailsService 를 구현하는 클래스
 * 사용자의 로그인 과정을 처리한다.
 * 사용자 정보를 확인하기 위해 UserAccountRepository 를 주입받아 확인한다.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserAccountRepository userAccountRepository;

	@Autowired
	public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
		this.userAccountRepository = userAccountRepository;
	}

	/**
	 * 사용자 로그인 처리 메서드
	 *
	 * @param username 사용자의 id 를 입력받는다. (email 이 전달되는 경우도 추가하여 id, email 두 경우 모두 확인한다.)
	 * @throws UsernameNotFoundException 사용자 정보를 찾지 못하는 경우 해당 에러를 던진다.
	 * @return 사용자 정보를 찾을 수 있는 경우 CustomUserDetails 에 사용자 정보를 넘겨 새로운 사용자 정보를 만들어 리턴한다.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Optional<UserAccount> user = userAccountRepository.findByUserId(username);
		if (user.isEmpty()) { // userId 로 검색하지 못했다면 email 로 검색
			user = userAccountRepository.findByEmail(username);
		}
		// email 로 검색해도 정보를 찾지 못했다면
		if (user.isEmpty()) {
			throw new UsernameNotFoundException("해당 사용자 정보를 찾을 수 없습니다.");
		}
		return new CustomUserDetails(user.get());
	}
}
