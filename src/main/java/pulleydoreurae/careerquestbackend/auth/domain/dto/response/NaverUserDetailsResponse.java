package pulleydoreurae.careerquestbackend.auth.domain.dto.response;

import lombok.*;

/**
 * 네이버 로그인에 성공한 토큰으로 사용자 정보를 받아올 dto
 *
 * @see <a href="https://developers.naver.com/docs/login/profile/profile.md">네이버 개발자센터</a>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverUserDetailsResponse {

    private String resultcode;
    private String message;
    private ResponseData response;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseData {
        private String email;
        private String nickname;
        private String profile_image;
        private String age;
        private String gender;
        private String id;
        private String name;
        private String birthday;
        private String birthyear;
        private String mobile;
    }

}
