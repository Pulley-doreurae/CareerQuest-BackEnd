package pulleydoreurae.careerquestbackend.auth.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.careerquestbackend.auth.domain.jwt.entity.JwtRefreshToken;

/**
 * Redis 에 사용할 리프레시 토큰 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/01/21
 */
public interface JwtRefreshTokenRepository extends CrudRepository<JwtRefreshToken, String> {
}
