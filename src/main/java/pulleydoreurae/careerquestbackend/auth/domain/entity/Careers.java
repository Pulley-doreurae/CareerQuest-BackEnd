package pulleydoreurae.careerquestbackend.auth.domain.entity;

import java.util.List;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 생성
    @Column(name = "career_id")
    private Long careerId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private String categoryType;

    private String categoryImage;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "career_id")
    private Careers parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Careers> children;
}
