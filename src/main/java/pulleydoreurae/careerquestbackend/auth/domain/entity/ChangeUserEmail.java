package pulleydoreurae.careerquestbackend.auth.domain.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

/**
 * 이메일 변경 엔티티
 *
 * @author : hanjaeseong
 * @since : 2024/04/13
 */

@Getter
@RedisHash(value = "changeUserEmail", timeToLive = 60 * 3)
public class ChangeUserEmail {

    @Id
    private final String uuid;  // 식별하기 위한 uuid
    private final String userId;    // 이메일을 변경하는 userId
    private final String email;     // 변경할 이메일 주소

    public ChangeUserEmail(String uuid, String userId, String email){
        this.uuid = uuid;
        this.userId = userId;
        this.email = email;
    }
}
