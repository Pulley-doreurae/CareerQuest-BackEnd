package pulleydoreurae.chwijunjindan;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pulleydoreurae.chwijunjindan.User.UserService;

@SpringBootTest
class ChwijunjindanApplicationTests {

	@Autowired
	private UserService userService;
	@Test
	public void signUpTest(){
		userService.createUser("test1", "1234", "준회원", "tester1@naver.com", "ROLE_ASSOCIATE");
		userService.createUser("test2", "1234", "정회원", "tester2@naver.com", "ROLE_REGULAR");
		userService.createUser("admin", "1234", "일반관리자", "admin@naver.com", "ROLE_ADMIN");
	}
	@Test
	void contextLoads() {
	}

}
