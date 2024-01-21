package pulleydoreurae.chwijunjindan.auth.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.chwijunjindan.auth.domain.jwt.JwtRefreshToken;

/**
 * Redis 에 사용할 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/01/21
 */
public interface JwtRefreshTokenRepository extends CrudRepository<JwtRefreshToken, String> {
}
