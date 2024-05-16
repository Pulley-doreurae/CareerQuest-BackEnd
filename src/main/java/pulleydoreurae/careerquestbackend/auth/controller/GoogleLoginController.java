package pulleydoreurae.careerquestbackend.auth.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.LoginFailResponse;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.dto.JwtTokenResponse;
import pulleydoreurae.careerquestbackend.auth.service.GoogleLoginService;

/**
 * 구글 로그인을 담당하는 컨트롤러
 *
 */
@RequestMapping("/api")
@Slf4j
@Controller
public class GoogleLoginController {

    private final GoogleLoginService googleLoginService;
    private final String authUrl;
    private final String tokenUrl;
    private final String getUserDetailUrl;

    public GoogleLoginController(GoogleLoginService googleLoginService,
                                 @Value("${LOGIN.GOOGLE_AUTH_URL}") String authUrl,
                                 @Value("${LOGIN.GOOGLE_TOKEN_URL}") String tokenUrl,
                                 @Value("${LOGIN.GOOGLE_GET_USERDETAIL_URL}") String getUserDetailUrl) {
        this.googleLoginService = googleLoginService;
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
        this.getUserDetailUrl = getUserDetailUrl;
    }

    @GetMapping("/login-google")
    public String googleLogin() {

        String url = googleLoginService.getRedirectUrl(authUrl);
        log.info("[로그인-구글] : 로그인창으로 이동");

        return "redirect:" + url;
    }


    @GetMapping("/login-google/code")
    public ResponseEntity<?> googleLoginCode(@RequestParam(value = "code") String code){

        log.info("[로그인-구글] : 구글 로그인 성공");
        String token = googleLoginService.getToken(code, tokenUrl);
        if (token != null) { // 정상적인 구글 액세스 토큰을 받아왔다면
            // 액세스 토큰을 다시 보내서 정보를 가져오기
            String userDetails = googleLoginService.getUserDetails(token, getUserDetailUrl); // 액세스 토큰으로 서버로부터 받아온 사용자 정보
            if (userDetails != null) {
                // 해당 정보에 맞는 유저를 찾아 응답에 담음
                ResponseEntity<JwtTokenResponse> response = googleLoginService.login(userDetails);

                if (response == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(LoginFailResponse.builder()
                                    .code(HttpStatus.BAD_REQUEST.toString())
                                    .error("유효하지 않은 구글 액세스토큰")
                                    .build());
                }
                // TODO: 2024/01/24 받아온 사용자 정보가 email 보다 늘어난다면 dto 로 만들기
                return response;
            }

            // 유효하지 않은 액세스토큰이라서 사용자 정보를 받지 못한경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(LoginFailResponse.builder()
                            .code(HttpStatus.BAD_REQUEST.toString())
                            .error("유효하지 않은 구글 액세스토큰")
                            .build());
        }
        // 유효하지 않은 코드라 액세스 토큰을 받지 못한경우
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(LoginFailResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.toString())
                        .error("유효하지 않은 구글 인증코드")
                        .build());
    }

}
