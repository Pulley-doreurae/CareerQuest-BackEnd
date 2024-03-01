package pulleydoreurae.careerquestbackend.auth.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pulleydoreurae.careerquestbackend.auth.domain.UserRole;

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

	@Enumerated(EnumType.STRING)    // enum 을 데이터베이스에 문자열로 저장한다.
	private UserRole role;

}
