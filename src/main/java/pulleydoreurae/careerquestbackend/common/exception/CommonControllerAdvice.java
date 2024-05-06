package pulleydoreurae.careerquestbackend.common.exception;

import java.net.MalformedURLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.basiccommunity.exception.CommunityException;

/**
 * 컨트롤러 예외처리
 *
 * @author : parkjihyeok
 * @since : 2024/04/15
 */
@RestControllerAdvice
public class CommonControllerAdvice {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<SimpleResponse> common(Exception e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(SimpleResponse.builder().msg("예상치 못한 오류가 발생했습니다. 관리자에게 문의해주세요.").build());
	}

	@ExceptionHandler({UsernameNotFoundException.class, CommunityException.class, MalformedURLException.class})
	public ResponseEntity<SimpleResponse> community(Exception e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(SimpleResponse.builder().msg(e.getMessage()).build());
	}
}
