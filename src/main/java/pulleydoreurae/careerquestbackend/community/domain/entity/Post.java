package pulleydoreurae.careerquestbackend.community.domain.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;

/**
 * 게시글 엔티티
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
	private Long view; // 조회수

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private PostCategory postCategory; // 카테고리

	// TODO: 2024/05/24 좋아요와 댓글의 경우 굳이 양방향 매핑할필요가 있는지 확인해보기
	@OneToMany(mappedBy = "post")
	private List<PostLike> postLikes; // 좋아요

	@OneToMany(mappedBy = "post")
	private List<Comment> comments; // 댓글 리스트
}
