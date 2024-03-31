package pulleydoreurae.careerquestbackend.community.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 게시판 엔티티
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	// referencedColumnName 을 생략하면 자동으로 UserAccount 의 id(기본키) 와 매핑하므로 (referencedColumnName = "id") 생략
	@JoinColumn(name = "user_id", nullable = false)
	private UserAccount userAccount; // 작성자

	@Column(nullable = false)
	private String title; // 제목
	@Column(nullable = false)
	private String content; // 내용

	@Setter
	@Column(nullable = false)
	private Long hit; // 조회수

	@Setter
	@Column(nullable = false)
	private Long likeCount; // 좋아요

	@Column(nullable = false)
	private Long category; // 카테고리
}
