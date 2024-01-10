package pulleydoreurae.chwijunjindan.User;

import lombok.Getter;

@Getter
public enum UserRole {

	ADMIN("ROLE_ADMIN"),
	ASSOCIATE("ROLE_ASSOCIATE"),
	REGULAR("ROLE_REGULAR");

	UserRole(String role) {
		this.role = role;
	}

	private String role;
}
