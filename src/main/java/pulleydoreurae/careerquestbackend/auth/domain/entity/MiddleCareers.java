package pulleydoreurae.careerquestbackend.auth.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Deprecated
public class MiddleCareers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false)
	private String categoryName; // 직무 이름

	@Column(nullable = false)
	private String categoryType; // 직무 종류

	private String categoryImage; // 직무아이콘

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "major_category", referencedColumnName = "id", nullable = false)
	private MajorCareers majorCategory; // 대분류 카테고리
}
