package pulleydoreurae.careerquestbackend.common.community.domain.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.common.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 게시판 추상화 엔티티
 *
 * @author : parkjihyeok
 * @since : 2024/05/06
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Post extends BaseEntity {

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
	private Long view; // 조회수

	@OneToMany(mappedBy = "post")
	private List<PostLike> postLikes; // 좋아요

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private PostCategory postCategory; // 카테고리

	private String certificationName; // 자격증 이름

	@OneToMany(mappedBy = "post")
	private List<Comment> comments; // 댓글 리스트
}
