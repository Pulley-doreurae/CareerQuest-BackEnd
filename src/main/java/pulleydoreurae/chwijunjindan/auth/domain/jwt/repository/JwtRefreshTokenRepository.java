// package pulleydoreurae.chwijunjindan.auth.domain.jwt.repository;
//
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.core.ValueOperations;
//
// import org.springframework.stereotype.Repository;
// import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtRefreshToken;
//
// import java.util.Objects;
// import java.util.Optional;
// import java.util.concurrent.TimeUnit;
//
// /**
//  * Redis 에 사용할 리프레시 토큰 Repository
//  *
//  * @author : parkjihyeok
//  * @since : 2024/01/21
//  */
// @Repository
// public class JwtRefreshTokenRepository {
//
//     private final RedisTemplate<String, String> redisTemplate;
//
//     public JwtRefreshTokenRepository(final @Qualifier("redisTemplate") RedisTemplate<String, String> redisJwtTemplate){
//         this.redisTemplate = redisJwtTemplate;
//     }
//
//     public void save(final JwtRefreshToken jwtRefreshToken){
//         ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//         valueOperations.set(jwtRefreshToken.getRefreshToken(), jwtRefreshToken.getUserId());
//         redisTemplate.expire(jwtRefreshToken.getRefreshToken(), 10L, TimeUnit.DAYS); // 리프레시 토큰의 유효기간은 10일
//     }
//
//     public Optional<JwtRefreshToken> findById(final String jwtRefreshToken){
//         ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
//         String userId = valueOperations.get(jwtRefreshToken);
//
//         if(Objects.isNull(userId)){
//             return Optional.empty();
//         }
//
//         return Optional.of(new JwtRefreshToken(jwtRefreshToken, userId));
//     }
//
//     public void deleteById(final String jwtRefreshToken){
//         redisTemplate.delete(jwtRefreshToken);
//     }
//
//     public void deleteAll() {
//         redisTemplate.delete("*");
//     }
// }

package pulleydoreurae.chwijunjindan.auth.domain.jwt.repository;

import org.springframework.data.repository.CrudRepository;

import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtRefreshToken;

/**
 * Redis 에 사용할 리프레시 토큰 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/01/21
 */
public interface JwtRefreshTokenRepository extends CrudRepository<JwtRefreshToken, String> {
}
