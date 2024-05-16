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
import pulleydoreurae.careerquestbackend.auth.service.NaverLoginService;

/**
 * 네이버 로그인을 담당하는 컨트롤러
 *
 */
@RequestMapping("/api")
@Slf4j
@Controller
public class NaverLoginController {

    private final NaverLoginService naverLoginService;
    private final String authUrl;
    private final String tokenUrl;
    private final String getUserDetailUrl;

    private NaverLoginController(NaverLoginService naverLoginService,
                                 @Value("${LOGIN.NAVER_AUTH_URL}") String authUrl,
                                 @Value("${LOGIN.NAVER_TOKEN_URL}") String tokenUrl,
                                 @Value("${LOGIN.NAVER_GET_USERDETAIL_URL}") String getUserDetailUrl) {
        this.naverLoginService = naverLoginService;
        this.authUrl = authUrl;
        this.tokenUrl = tokenUrl;
        this.getUserDetailUrl = getUserDetailUrl;
    }

    @GetMapping("/login-naver")
    public String googleLogin() {

        String url = naverLoginService.getRedirectUrl(authUrl);
        log.info("[로그인-네이버] : 로그인창으로 이동");

        return "redirect:" + url;
    }


    @GetMapping("/login-naver/code")
    public ResponseEntity<?> naverLoginCode(@RequestParam(value = "code") String code, @RequestParam(value = "state") String state){

        log.info("[로그인-네이버] : 네이버 로그인 성공");
        log.info("[로그인-네이버] : 코드 : {} | 상태 : {}", code, state);
        String token = naverLoginService.getToken(code, state, tokenUrl);
        if (token != null) { // 정상적인 네이버 액세스 토큰을 받아왔다면
            // 액세스 토큰을 다시 보내서 정보를 가져오기
            String userDetails = naverLoginService.getUserDetails(token, getUserDetailUrl); // 액세스 토큰으로 서버로부터 받아온 사용자 정보
            if (userDetails != null) {
                // 해당 정보에 맞는 유저를 찾아 응답에 담음
                ResponseEntity<JwtTokenResponse> response = naverLoginService.login(userDetails);

                if (response == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(LoginFailResponse.builder()
                                    .code(HttpStatus.BAD_REQUEST.toString())
                                    .error("유효하지 않은 네이버 액세스토큰")
                                    .build());
                }
                return response;
            }

            // 유효하지 않은 액세스토큰이라서 사용자 정보를 받지 못한경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(LoginFailResponse.builder()
                            .code(HttpStatus.BAD_REQUEST.toString())
                            .error("유효하지 않은 네이버 액세스토큰")
                            .build());
        }
        // 유효하지 않은 코드라 액세스 토큰을 받지 못한경우
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(LoginFailResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.toString())
                        .error("유효하지 않은 네이버 인증코드")
                        .build());
    }


}
