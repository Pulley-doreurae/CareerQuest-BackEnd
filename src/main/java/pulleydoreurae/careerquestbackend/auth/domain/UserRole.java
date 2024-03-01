package pulleydoreurae.careerquestbackend.auth.domain;

import lombok.Getter;

/**
 * 회원들의 권한을 지정하는 enum
 * 관리자, 준회원, 정회원을 가진다.
 */
@Getter
public enum UserRole {
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_TEMPORARY_USER("ROLE_TEMPORARY_USER"),
	ROLE_USER("ROLE_USER");

	private final String role;

	UserRole(String role) {
		this.role = role;
	}
}
