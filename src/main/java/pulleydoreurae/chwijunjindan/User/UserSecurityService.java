package pulleydoreurae.chwijunjindan.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		Optional<UserDTO> userDTO = userRepository.findByUserid(userId);
		if(userDTO.isPresent()){
			UserDTO user = userDTO.get();
			List<GrantedAuthority> authorities = new ArrayList<>();
			if ("ROLE_ADMIN".equals(user.getRole())) {
				authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getRole()));
			} else if ("ROLE_REGULAR".equals(user.getRole())) {
				authorities.add(new SimpleGrantedAuthority(UserRole.REGULAR.getRole()));
			} else {
				authorities.add(new SimpleGrantedAuthority(UserRole.ASSOCIATE.getRole()));
			}
			return new User(user.getUserid(), user.getPassword(), authorities);
		}
		return null;
	}
}
