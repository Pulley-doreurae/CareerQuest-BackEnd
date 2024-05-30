package pulleydoreurae.careerquestbackend.auth.domain.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 직무 엔티티
 *
 * @author : hanjaeseong
 */
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
    private String categoryName;    // 직무 이름

    @Column(nullable = false)
    private String categoryType;    // 직무 타입 : [대분류, 중분류, 소분류]

    private String categoryImage;   // 직무 이미지

    private String description;     // 직무 설명

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", referencedColumnName = "career_id")
    private Careers parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Careers> children;
}
