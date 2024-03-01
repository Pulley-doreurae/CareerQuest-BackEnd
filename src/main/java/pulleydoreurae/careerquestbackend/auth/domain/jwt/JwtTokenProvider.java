package pulleydoreurae.careerquestbackend.auth.domain.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import pulleydoreurae.careerquestbackend.auth.domain.jwt.dto.JwtTokenResponse;

/**
 * 토큰을 발급, 확인 하는 객체
 *
 * @author : parkjihyeok
 * @since : 2024/01/18
 */
@Component
public class JwtTokenProvider {

	private final String accessSecretKey;
	private final String refreshSecretKey;
	private final String issuer;

	private final long accessTokenValidTime = 10 * 60 * 1000L;    // 액세스 토큰의 유효기간은 10분
	private final long refreshTokenValidTime = 10 * 3600 * 60 * 1000L;    // 리프레시 토큰의 유효기간은 10일

	public JwtTokenProvider(@Value("${JWT.ACCESS_SECRET_KEY}") String accessSecretKey,
			@Value("${JWT.REFRESH_SECRET_KEY}") String refreshSecretKey, @Value("${JWT.ISSUER}") String issuer) {
		this.accessSecretKey = accessSecretKey;
		this.refreshSecretKey = refreshSecretKey;
		this.issuer = issuer;
	}

	/**
	 * Access 토큰을 생성하는 메서드
	 *
	 * @param userId 사용자의 id 를 전달받아 jwt 토큰을 생성함
	 * @return 생성된 jwt 토큰을 리턴함
	 */
	private String createAccessToken(String userId) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(userId)
				.setIssuer(issuer)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + accessTokenValidTime))
				.signWith(SignatureAlgorithm.HS256, accessSecretKey)
				.compact();
	}

	/**
	 * Refresh 토큰을 생성하는 메서드, AccessToken 과 다른 비밀번호로 암호화함
	 *
	 * @param userId 사용자의 id 를 전달받아 jwt 토큰을 생성함
	 * @return 생성된 jwt 토큰을 리턴함
	 */
	private String createRefreshToken(String userId) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(userId)
				.setIssuer(issuer)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + refreshTokenValidTime))
				.signWith(SignatureAlgorithm.HS256, refreshSecretKey)
				.compact();
	}

	/**
	 * 로그인에 성공하면 전달할 JWT dto 객체를 만들어 리턴함
	 *
	 * @param userId 사용자 id 를 전달받음
	 * @return dto 를 생성하여 리턴
	 */
	public JwtTokenResponse createJwtResponse(String userId) {

		return JwtTokenResponse.builder()
				.token_type("bearer")
				.access_token(createAccessToken(userId))
				.expires_in(accessTokenValidTime)
				.refresh_token(createRefreshToken(userId))
				.refresh_token_expires_in(refreshTokenValidTime)
				.build();
	}

	/**
	 * 리프레시 토큰을 이용해 액세스 토큰을 다시 생성하는 메서드
	 *
	 * @param refreshToken 리프레시 토큰을 전달받으면
	 * @return 새로운 액세스 토큰을 만들어 dto 형식으로 리턴
	 */
	public JwtTokenResponse refreshAccessToken(String refreshToken) {

		return JwtTokenResponse.builder()
				.token_type("bearer")
				.access_token(createAccessToken(getUserPkFromRefreshToken(refreshToken)))
				.expires_in(accessTokenValidTime)
				.build();
	}

	/**
	 * JWT 토큰에서 사용자 정보를 가져오는 메서드
	 *
	 * @param token 토큰을 입력받으면
	 * @return 정보를 리턴한다.
	 * @deprecated Redis 를 사용하므로 더 이상 사용되지 않는다.
	 */
	@Deprecated
	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token).getBody().getSubject();
	}

	// 리프레시 토큰에서 정보 가져오기
	private String getUserPkFromRefreshToken(String refreshToken) {
		return Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(refreshToken).getBody().getSubject();
	}

	/**
	 * 토큰의 유효성을 검증하는 메서드
	 *
	 * @param token 토큰을 입력으로 받는다.
	 * @return 토큰에 대한 유효성을 검사해 반환한다.
	 * @deprecated Redis 를 사용하므로 더 이상 사용되지 않는다.
	 */
	@Deprecated
	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(accessSecretKey).parseClaimsJws(token);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
}
