package pulleydoreurae.careerquestbackend.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 컨트롤러
 *
 * @author : parkjihyeok
 * @since : 1/26/24
 */
// TODO: 2024/03/27 정상적인 로직들이 구현되면 해당 클래스는 삭제한다.
@RestController
public class TestController {

	@GetMapping("/test")
	public String get() {
		return "hi";
	}

	@GetMapping("/api-test")
	public ResponseEntity<String> apiTest() {
		return ResponseEntity.status(HttpStatus.OK).body("서버는 정상작동 중");
	}
}
