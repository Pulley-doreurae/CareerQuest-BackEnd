package pulleydoreurae.careerquestbackend.certification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;
import pulleydoreurae.careerquestbackend.certification.domain.entity.InterestedCertification;
import pulleydoreurae.careerquestbackend.certification.repository.CertificationRepository;
import pulleydoreurae.careerquestbackend.certification.repository.InterestedCertificationRepository;

/**
 * 관심자격증을 담당하는 서비스
 *
 * @author : parkjihyeok
 * @since : 2024/05/21
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterestedCertificationService {

	private final InterestedCertificationRepository interestedCertificationRepository;
	private final UserAccountRepository userAccountRepository;
	private final CertificationRepository certificationRepository;

	/**
	 * 회원id로 관심자격증에 추가한 자격증 정보를 가져오는 메서드
	 *
	 * @param userId 회원id
	 * @return 관심자격증 정보
	 */
	public List<Certification> findAllByUserId(String userId) {
		List<Certification> result = new ArrayList<>();
		interestedCertificationRepository.findAllByUserId(userId).forEach(ic -> result.add(ic.getCertification()));
		return result;
	}

	/**
	 * 회원정보와 자격증 정보로 관심자격증에 추가하는 메서드
	 *
	 * @param userId 회원id
	 * @param certificationName 자격증이름
	 */
	public void saveInterestedCertification(String userId, String certificationName) {
		Certification certification = findCertification(certificationName);
		UserAccount userAccount = findUserAccount(userId);

		InterestedCertification interestedCertification = InterestedCertification.builder()
				.userAccount(userAccount)
				.certification(certification)
				.build();

		interestedCertificationRepository.save(interestedCertification);
	}

	/**
	 * 회원정보와 자격증 정보로 관심자격증에서 제거하는 메서드
	 *
	 * @param userId 회원id
	 * @param certificationName 자격증이름
	 */
	public void deleteInterestedCertification(String userId, String certificationName) {
		InterestedCertification interestedCertification = findInterestedCertification(userId, certificationName);
		interestedCertificationRepository.delete(interestedCertification);
	}

	/**
	 * 회원아이디로 회원정보를 찾아오는 메서드
	 *
	 * @param userId 회원아이디
	 * @return 해당하는 회원정보가 있으면 회원정보를, 없다면 null 리턴
	 */
	private UserAccount findUserAccount(String userId) {
		Optional<UserAccount> findUser = userAccountRepository.findByUserId(userId);

		// 회원정보를 찾을 수 없다면
		if (findUser.isEmpty()) {
			log.error("{} 의 회원 정보를 찾을 수 없습니다.", userId);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return findUser.get();
	}

	/**
	 * 자격증 이름으로 자격증을 찾아오는 메서드
	 *
	 * @param certificationName 자격증 이름
	 * @return 해당하는 자격증이 있으면 자격증을, 없다면 null 리턴
	 */
	private Certification findCertification(String certificationName) {
		Optional<Certification> findCertification = certificationRepository.findByCertificationName(
				certificationName);

		// 자격증 정보를 찾을 수 없다면
		if (findCertification.isEmpty()) {
			log.error("{} 의 자격증 정보를 찾을 수 없습니다.", certificationName);
			throw new IllegalArgumentException("요청한 자격증 정보를 찾을 수 없습니다.");
		}
		return findCertification.get();
	}

	/**
	 * 회원id와 자격증 이름으로 관심자격증을 찾아오는 메서드
	 *
	 * @param userId 회원id
	 * @param certificationName 자격증 이름
	 * @return 해당하는 관심자격증을 리턴
	 */
	private InterestedCertification findInterestedCertification(String userId, String certificationName) {
		Optional<InterestedCertification> findInterestedCertification = interestedCertificationRepository
				.findByUserIdAndCertificationName(userId, certificationName);

		// 자격증 정보를 찾을 수 없다면
		if (findInterestedCertification.isEmpty()) {
			log.error("[{}]는 [{}](을/를) 관심 자격증에 추가하지 않았습니다.", userId, certificationName);
			throw new IllegalArgumentException("요청한 정보에 맞는 관심 자격증 정보를 찾을 수 없습니다.");
		}
		return findInterestedCertification.get();
	}
}
