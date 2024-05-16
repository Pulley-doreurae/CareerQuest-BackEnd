package pulleydoreurae.careerquestbackend.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserFirstAddInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

}
