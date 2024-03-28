package pulleydoreurae.careerquestbackend.auth.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

/**
 * 접속기록 엔티티
 *
 * @author : hanjaeseong
 * @since : 2024/03/28
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;  // 사용자 식별자

    private String ipv4;         // 접속한 Client의 ipv4

    private String ipv6;         // 접속한 Client의 ipv6

    private String location;     // 접속한 Client의 위치

    private String AccessedAt;   // 접속한 Client의 접속일자
}
