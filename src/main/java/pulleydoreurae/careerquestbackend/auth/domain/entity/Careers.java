package pulleydoreurae.careerquestbackend.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Careers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String categoryType; // 직무종류
    @Column(nullable = false)
    private String categoryName; // 직무이름

    private String categoryImage; // 직무
}
