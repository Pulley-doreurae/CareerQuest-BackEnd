package pulleydoreurae.chwijunjindan.auth.domain.jwt.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import org.springframework.stereotype.Repository;
import pulleydoreurae.chwijunjindan.auth.domain.jwt.entity.JwtAccessToken;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis 에 사용할 액세스 토큰 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/01/22
 */
@Repository
public class JwtAccessTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public JwtAccessTokenRepository(final @Qualifier("redisTemplate") RedisTemplate<String, String> redisJwtTemplate){
        this.redisTemplate = redisJwtTemplate;
    }

    public void save(final JwtAccessToken jwtAccessToken){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(jwtAccessToken.getAccessToken(), jwtAccessToken.getUserId());
        redisTemplate.expire(jwtAccessToken.getAccessToken(), 10L, TimeUnit.MINUTES); // 액세스 토큰의 유효기간은 10분
    }

    public Optional<JwtAccessToken> findById(final String jwtAccessToken){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String userId = valueOperations.get(jwtAccessToken);

        if(Objects.isNull(userId)){
            return Optional.empty();
        }

        return Optional.of(new JwtAccessToken(jwtAccessToken, userId));
    }

    public void deleteById(final String jwtAccessToken){
        redisTemplate.delete(jwtAccessToken);
    }

    public void deleteAll() {
        redisTemplate.delete("*");
    }
}