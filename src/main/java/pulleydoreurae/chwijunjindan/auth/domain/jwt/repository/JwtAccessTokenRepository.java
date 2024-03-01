package pulleydoreurae.chwijunjindan.auth.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtAccessToken;

/**
 * Redis 에 사용할 액세스 토큰 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/01/22
 */
public interface JwtAccessTokenRepository extends CrudRepository<JwtAccessToken, String> {
}
