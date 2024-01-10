package pulleydoreurae.chwijunjindan.auth.domain;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * UserDetails 를 구현하여 사용자의 정보를 담는 클래스
 */
public class CustomUserDetails implements UserDetails {

	private final UserAccount userAccount;

	/**
	 * 사용자의 정보를 담기 위해 UserAccount 를 주입받아 사용한다.
	 *
	 * @param userAccount 사용자의 정보를 주입받는다.
	 */
	public CustomUserDetails(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	/**
	 * 사용자의 권한을 리턴한다
	 *
	 * @return 사용자의 권한을 리턴한다.
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collection = new ArrayList<>();
		collection.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return userAccount.getRole().toString();
			}
		});
		return collection;
	}

	@Override
	public String getPassword() {    // 사용자의 비밀번호를 리턴한다.
		return userAccount.getPassword();
	}

	@Override
	public String getUsername() {    // 사용자의 아이디를 리턴한다.
		return userAccount.getUserId();
	}

	@Override
	public boolean isAccountNonExpired() {    // 계정이 만료되지 않은경우 true 를 리턴
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {    // 계정이 잠기지 않은경우 true 를 리턴
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {    // 비밀번호가 만료되지 않은경우 true 를 리턴
		return true;
	}

	@Override
	public boolean isEnabled() {    // 계정이 활성화된 경우 true 리턴
		return true;
	}
}
