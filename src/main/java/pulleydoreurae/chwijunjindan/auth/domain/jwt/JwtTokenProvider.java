package pulleydoreurae.chwijunjindan.auth.domain.jwt;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 토큰을 발급, 확인 하는 객체
 *
 * @author : parkjihyeok
 * @since : 2024/01/18
 */
// TODO: 2024/01/18 수정 필요 (JwtTokenProvider)
@Component
public class JwtTokenProvider {

	private final String secretKey;
	private final String issuer;

	private final long tokenValidTime = 10 * 60 * 1000L;    // 토큰의 유효기간은 10분

	public JwtTokenProvider(@Value("${JWT.SECRET_KEY}") String secretKey, @Value("${JWT.ISSUER}") String issuer) {
		this.secretKey = secretKey;
		this.issuer = issuer;
	}

	// AccessToken 생성
	public String createAccessToken(String userId) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(userId)
				.setIssuer(issuer)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + tokenValidTime))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	// 토큰에서 정보 가져오기
	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	// 토큰의 유효성 + 만료일자 확인
	public boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
}
