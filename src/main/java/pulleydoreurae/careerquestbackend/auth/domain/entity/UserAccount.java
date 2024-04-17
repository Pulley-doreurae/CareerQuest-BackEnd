package pulleydoreurae.careerquestbackend.auth.domain.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
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
	private String email;
	private String password;
	private String phoneNum;
	private String birth;
	private String gender;

	@Enumerated(EnumType.STRING)    // enum 을 데이터베이스에 문자열로 저장한다.
	private UserRole role;

	@Setter
	@OneToOne // 단방향 매핑, 회원객체가 직무정보에 대한 정보를 가진다.
	@JoinColumn(name = "user_career_id")
	private UserCareerDetails userCareerDetails;

	@Setter
	@OneToMany(mappedBy = "userAccount",cascade = CascadeType.ALL)
	// 양방향 매핑, 기술스택이 연관관계의 주인이 된다. 회원이 삭제된다면 그 회원의 기술스택도 삭제한다.
	private List<UserTechnologyStack> stacks;

	@OneToMany(mappedBy = "userAccount")
	private List<Post> posts;

	@OneToMany(mappedBy = "userAccount")
	private List<Comment> comments;

	@OneToMany(mappedBy = "userAccount")
	private List<PostLike> postLikes;

	public void updatePassword(String password) {
		this.password = password;
	}
	public void updateEmail(String email) {
		this.email = email;
	}

	public void updatePhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
}
