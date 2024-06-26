package pulleydoreurae.careerquestbackend.auth.domain.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pulleydoreurae.careerquestbackend.auth.domain.MBTI;
import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.common.entity.BaseEntity;
import pulleydoreurae.careerquestbackend.community.domain.entity.Comment;
import pulleydoreurae.careerquestbackend.community.domain.entity.Post;
import pulleydoreurae.careerquestbackend.community.domain.entity.PostLike;

/**
 * 회원가입이나 로그인 시 사용자 정보를 담을 엔티티
 * 사용자 id, 사용자 이름, 이메일, 휴대폰 번호, 비밀번호, 권한을 가진다.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccount extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String userId;
	private String userName;
	@Setter
	private String email;
	@Setter
	private String password;
	@Setter
	private String phoneNum;
	private String birth;
	private String gender;
	@Setter
	@Column(columnDefinition = "boolean default false")
	private Boolean isMarketed;

	@Enumerated(EnumType.STRING)
	private MBTI mbti;

	@Enumerated(EnumType.STRING)    // enum 을 데이터베이스에 문자열로 저장한다.
	private UserRole role;

	@Setter
	@OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
	// 양방향 매핑, 기술스택이 연관관계의 주인이 된다. 회원이 삭제된다면 그 회원의 기술스택도 삭제한다.
	private List<UserTechnologyStack> stacks;

	@OneToMany(mappedBy = "userAccount")
	private List<Post> posts;

	@OneToMany(mappedBy = "userAccount")
	private List<Comment> comments;

	@OneToMany(mappedBy = "userAccount")
	private List<PostLike> postLikes;

	// MBTI 설정하는 부분
	public void updateMBTI(MBTI mbti){
		this.mbti = mbti;
	}
}
