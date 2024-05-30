package pulleydoreurae.careerquestbackend.certification.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.UserCertificationRequest;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.UserCertification;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;
import pulleydoreurae.careerquestbackend.certification.repository.UserCertificationRepository;
import pulleydoreurae.careerquestbackend.common.service.CommonService;

/**
 * 사용자가 취득한 정보를 담당하는 Service
 *
 * @author : parkjihyeok
 * @since : 2024/05/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCertificationService {

	private final UserAccountRepository userAccountRepository;
	private final CertificationRepository certificationRepository;
	private final UserCertificationRepository userCertificationRepository;
	private final CommonService commonService;

	/**
	 * 취득 자격증을 추가하는 메서드
	 *
	 * @param request 취득 자격증 요청
	 */
	public void saveUserCertification(UserCertificationRequest request) {
		commonService.checkAuth(request.getUserId());

		UserAccount user = findUser(request.getUserId());
		Certification certification = findCertification(request.getCertificationName());

		UserCertification userCertification = UserCertification.builder()
				.userAccount(user)
				.certification(certification)
				.acqDate(request.getAcqDate())
				.build();

		log.info("취득 자격증 저장 - 회원 ID: {}, 자격증 이름: {}", request.getUserId(), request.getCertificationName());
		userCertificationRepository.save(userCertification);
	}

	/**
	 * 취득 자격증을 제거하는 메서드
	 *
	 * @param request 취즉 자격증 정보
	 */
	public void deleteUserCertification(UserCertificationRequest request) {
		commonService.checkAuth(request.getUserId());

		UserCertification userCertification = findUserCertification(request);

		log.info("취득 자격증 제거 - 회원 ID: {}, 자격증 이름: {}", request.getUserId(), request.getCertificationName());
		userCertificationRepository.delete(userCertification);
	}

	private UserAccount findUser(String userId) {
		Optional<UserAccount> byUserId = userAccountRepository.findByUserId(userId);

		if (byUserId.isEmpty()) {
			throw new UsernameNotFoundException("회원 정보를 찾을 수 없습니다.");
		}

		return byUserId.get();
	}

	private Certification findCertification(String certificationName) {
		Optional<Certification> byCertificationName = certificationRepository.findByCertificationName(
				certificationName);

		if (byCertificationName.isEmpty()) {
			throw new IllegalArgumentException("자격증 정보를 확인할 수 없습니다.");
		}

		return byCertificationName.get();
	}

	private UserCertification findUserCertification(UserCertificationRequest request) {
		Optional<UserCertification> byCertificationNameAndUserId = userCertificationRepository.findByCertificationNameAndUserId(
				request.getCertificationName(),
				request.getUserId());

		if (byCertificationNameAndUserId.isEmpty()) {
			throw new IllegalArgumentException("취득자격증 정보를 찾을 수 없습니다.");
		}

		return byCertificationNameAndUserId.get();
	}
}
