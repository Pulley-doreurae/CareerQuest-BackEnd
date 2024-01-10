package pulleydoreurae.chwijunjindan.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UserService {

	public final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void createUser(String userId, String userPw, String userName, String userEmail, String userRole){
		UserDTO userDTO = new UserDTO();
		userDTO.setUserid(userId);
		userDTO.setPassword(passwordEncoder.encode(userPw));
		userDTO.setUsername(userName);
		userDTO.setEmail(userEmail);
		userDTO.setRole(userRole);

		userRepository.save(userDTO);
	}

}
