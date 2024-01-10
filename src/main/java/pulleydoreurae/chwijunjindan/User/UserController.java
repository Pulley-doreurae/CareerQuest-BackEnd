package pulleydoreurae.chwijunjindan.User;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserController {

	@GetMapping("/login")
	public String main(){
		return "login";
	}


}
