package pulleydoreurae.careerquestbackend.certification.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관심자격증 등록/삭제 요청에 사용될 Request
 *
 * @author : parkjihyeok
 * @since : 2024/05/22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InterestedCertificationRequest {

	@NotBlank(message = "사용자 아이디는 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	@Size(min = 5, message = "아이디는 5자 이상으로 입력해주세요.")    // 사용자의 id 길이는 최소 5자
	private String userId;

	@NotBlank(message = "자격증이름은 필수입니다.")    // null, "", " " 을 허용하지 않는다.
	private String certificationName;

	private Boolean isInterested; // 관심자격증 등록여부(등록되어 있었다면 true, 아니라면 false)
}
