package pulleydoreurae.careerquestbackend.portfolio.domain.entity;

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
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitRepoLanguage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name; // 기술 이름(언어)
	private Double count; // 지분 (퍼센트)

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserAccount userAccount;
}
