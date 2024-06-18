package pulleydoreurae.careerquestbackend.auth.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.ShowUserDetailsToChangeRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.UserMBTIRequest;
import pulleydoreurae.careerquestbackend.auth.domain.dto.response.ShowCareersResponse;
import pulleydoreurae.careerquestbackend.auth.domain.entity.Careers;
import pulleydoreurae.careerquestbackend.auth.domain.entity.ChangeUserEmail;
import pulleydoreurae.careerquestbackend.auth.domain.entity.HelpUserPassword;
import pulleydoreurae.careerquestbackend.auth.domain.entity.TechnologyStack;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserCareerDetails;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserTechnologyStack;
import pulleydoreurae.careerquestbackend.auth.repository.CareerDetailsRepository;
import pulleydoreurae.careerquestbackend.auth.repository.ChangeUserEmailRepository;
import pulleydoreurae.careerquestbackend.auth.repository.HelpUserPasswordRepository;
import pulleydoreurae.careerquestbackend.auth.repository.TechnologyStackRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserCareerDetailsRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserTechnologyStackRepository;
import pulleydoreurae.careerquestbackend.mail.service.MailService;

/**
 * 사용자 계정을 관리하는 서비스
 *
 * @author : Hanjaeseong
 * @since : 2024/04/03
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserAccountService implements Serializable {

	private final UserAccountRepository userAccountRepository;
	private final HelpUserPasswordRepository helpUserPasswordRepository;
	private final ChangeUserEmailRepository changeUserEmailRepository;
	private final UserTechnologyStackRepository userTechnologyStackRepository;
	private final UserCareerDetailsRepository userCareerDetailsRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final MailService mailService;
	private final TechnologyStackRepository technologyStackRepository;
	private final CareerDetailsRepository careerDetailsRepository;
	@Value("${spring.mail.domain}")
	private String domain;
	@Value("${FIND_PASSWORD_PATH}")
	private String findPasswordPath;
	@Value("${UPDATE_EMAIL_PATH}")
	private String updateEmailPath;

	/**
	 * userId에 해당하는 UserAccount를 찾는 메서드
	 *
	 * @param userId 찾고싶은 userId
	 */
	public UserAccount findUserByUserId(String userId) {
		Optional<UserAccount> findUser = userAccountRepository.findByUserId(userId);

		// 회원정보를 찾을 수 없다면
		if (findUser.isEmpty()) {
			log.error("{} 의 회원 정보를 찾을 수 없습니다.", userId);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return findUser.get();
	}

	/**
	 * 이메일로 비밀번홀 찾기 링크를 발송해주는 메서드
	 *
	 * @param userId 찾기를 요청한 userId
	 * @param email 찾기 링크를 송신받는 이메일
	 */
	public void findPassword(String userId, String email) {
		String uuid = UUID.randomUUID().toString();
		String verification_url = domain + findPasswordPath + uuid;

		helpUserPasswordRepository.save(new HelpUserPassword(uuid, userId));

		log.info("[비밀번호 찾기 - 인증] : {} 의 비밀번호 찾기를 위한 객체저장 및 메일전송", email);
		log.info("[비밀번호 찾기 - 인증] : {} 의 비밀번호 찾기를 위한 UUID", uuid);

		mailService.sendMail(email, verification_url, "findPasswordForm", "취준진담 비밀번호 찾기");
	}

	/**
	 * 비밀번호 찾기를 요청한 uuid가 있는지 확인하는 메서드
	 *
	 * @param uuid 요청한 uuid
	 * @return 있으면 - userId 리턴 | 없으면 - null 리턴
	 */
	public String checkUserIdByUuid(String uuid) {
		Optional<HelpUserPassword> helpUser = helpUserPasswordRepository.findById(uuid);

		// uuid가 없다면
		if (helpUser.isEmpty()) {
			log.error("{} 의 해당하는 정보를 찾을 수 없습니다.", uuid);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return helpUser.get().getHelpUserId();
	}

	/**
	 * 비밀번호 변경을 처리하는 메서드
	 *
	 * @param userId    비밀번호 변경을 해야하는 userId
	 * @param password  변경할 비밀번호
	 */
	public void updatePassword(String userId, String password) {
		Optional<UserAccount> userAccount = userAccountRepository.findByUserId(userId);
		if (userAccount.isPresent()) {
			UserAccount user = userAccount.get();
			user.setPassword(password);
			userAccountRepository.save(user);
		}
	}

	/**
	 * 비밀번호 찾기를 요청한 uuid를 제거하는 메서드
	 *
	 * @param uuid  비밀번호 찾기를 요청한 uuid
	 */
	public void deleteHelpUser(String uuid) {
		helpUserPasswordRepository.deleteById(uuid);
	}

	/**
	 * user를 삭제하는 메서드
	 *
	 * @param user 삭제할 user
	 */
	public void deleteUser(UserAccount user) {
		userAccountRepository.delete(user);
	}

	/**
	 * 유저를 삭제할 때 본인확인을 위해 입력한 비밀번호와 삭제할 유저의 현재 비밀번호가 서로 일치하는지 확인하는 메서드
	 *
	 * @param user  삭제할 user
	 * @param password   본인확인을 위해 입력한 비밀번호
	 * @return 일치 - true | 불일치 - false
	 */
	public boolean isCurrentPassword(UserAccount user, String password) {
		return bCryptPasswordEncoder.matches(password, user.getPassword());
	}

	/**
	 * 이메일 변경 주소로 인증 링크를 보내주는 메서드
	 *
	 * @param userId    이메일 변경을 요청한 userId
	 * @param email        변경할 이메일 주소
	 */
	public void sendUpdateEmailLink(String userId, String email) {
		String uuid = UUID.randomUUID().toString();
		String verification_url = domain + updateEmailPath + uuid;

		changeUserEmailRepository.save(new ChangeUserEmail(uuid, userId, email));
		log.info("[회원 - 이메일 변경] : {} 의 이메일 변경을 위한 객체저장 및 메일전송", email);
		log.info("[회원 - 이메일 변경] : {} 의 이메일 변경을 위한 UUID", uuid);

		mailService.sendMail(email, verification_url, "updateEmailForm", "취준진담 이메일 변경");
	}

	/**
	 * 이메일 변경을 요청한 유저를 반환하는 메서드
	 *
	 * @param uuid 이메일 변경을 요청한 uuid
	 * @return uuid에 해당하는 요청한 유저
	 */
	public ChangeUserEmail checkUpdateEmailUserIdByUuid(String uuid) {
		Optional<ChangeUserEmail> helpUser = changeUserEmailRepository.findById(uuid);

		// uuid가 없다면
		if (helpUser.isEmpty()) {
			log.error("{} 의 해당하는 정보를 찾을 수 없습니다.", uuid);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return helpUser.get();
	}

	/**
	 * 이메일을 변경하는 메서드
	 *
	 * @param changeUser 변경할 유저
	 */
	public void updateEmail(ChangeUserEmail changeUser) {
		Optional<UserAccount> userAccount = userAccountRepository.findByUserId(changeUser.getUserId());
		if (userAccount.isPresent()) {
			UserAccount user = userAccount.get();
			user.setEmail(changeUser.getEmail());
			userAccountRepository.save(user);
		}
		changeUserEmailRepository.delete(changeUser);
	}

	/**
	 * 회원 정보를 변경하는 메서드
	 *
	 * @param user	변경할 유저
	 * @param showUserDetailsToChangeRequest 변경할 정보가 담아이는 변수
	 */
	public void updateDetails(UserAccount user, ShowUserDetailsToChangeRequest showUserDetailsToChangeRequest) {

		// 전화번호 변경
		user.setPhoneNum(showUserDetailsToChangeRequest.getPhoneNum());

		// 직무 변경
		Optional<UserCareerDetails> careerDetails = userCareerDetailsRepository.findByUserAccount(user);
		Optional<Careers> career = careerDetailsRepository.findCareersByCategoryNameAndCategoryType(
			showUserDetailsToChangeRequest.getSmallCategory(), "소분류");

		if (careerDetails.isEmpty() || career.isEmpty()) {
			log.error("{} 의 해당하는 직무를 찾을 수 없습니다.", user.getUserId());
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}

		UserCareerDetails updateUserCareerDetails = UserCareerDetails.builder()
			.id(careerDetails.get().getId()) // 동일한 id 로 덮어쓰기
			.smallCategory(career.get())    // 직무 이름에 해당하는 직무로 덮어쓰기
			.userAccount(user)
			.build();
		userCareerDetailsRepository.save(updateUserCareerDetails);

		// 기술스택 변경
		Collections.sort(showUserDetailsToChangeRequest.getTechnologyStacks()); // 오름차순 정렬 후

		if (showUserDetailsToChangeRequest.getTechnologyStacks().size() > user.getStacks()
			.size()) {    // 크기가 더 크다 -> 기술을 더 추가함
			for (int i = user.getStacks().size();
				 i < showUserDetailsToChangeRequest.getTechnologyStacks().size(); i++) {
				UserTechnologyStack userTechnologyStack = UserTechnologyStack.builder()
					.stackId(showUserDetailsToChangeRequest.getTechnologyStacks().get(i))
					.userAccount(user)                                    // 크기가 넘은 나머진 부분은 새로 정의하여 추가
					.build();
				userTechnologyStackRepository.save(userTechnologyStack);
				user.getStacks().add(userTechnologyStack);
			}
		} else if (showUserDetailsToChangeRequest.getTechnologyStacks().size() < user.getStacks()
			.size()) { // 크기가 더 작다 -> 기술을 더 제거
			int size = user.getStacks().size() - 1; // 삭제할 인덱스 지정(맨 뒤에서부터 삭제)
			int sizeDifference = user.getStacks().size() - showUserDetailsToChangeRequest.getTechnologyStacks().size();
			for (int i = 0; i < sizeDifference; i++) {
				UserTechnologyStack removeTech = user.getStacks().get(size - i);
				user.getStacks().remove(removeTech);
				userTechnologyStackRepository.delete(removeTech);
			}
		}

		for (int i = 0; i < user.getStacks().size(); i++) { // 기존에 있던 기술스택을 재사용
			UserTechnologyStack userTechnologyStack = UserTechnologyStack.builder()
				.id(user.getStacks().get(i).getId())                // 크기가 동일한 부분은 가지고 있던 기술스택에셔 변경
				.userAccount(user)
				.stackId(showUserDetailsToChangeRequest.getTechnologyStacks().get(i))
				.build();
			userTechnologyStackRepository.save(userTechnologyStack);
		}

		userAccountRepository.save(user);
	}

	/**
	 * 회원의 기술스택을 보여주는 메서드
	 *
	 * @param user 보여줄 회원
	 * @return	기술스택의 이름이 담아있는 List를 반환
	 */
	public List<String> getTechnologyStack(UserAccount user) {
		List<String> stackIds = new ArrayList<>();

		for (UserTechnologyStack userTechnologyStack : user.getStacks()) {
			stackIds.add(userTechnologyStack.getStackId());
		}
		return stackIds;
	}

	/**
	 * 직무 리스트를 반환하는 메서드
	 *
	 * @param major 	대분류의 이름
	 * @param middle	중분류의 이름
	 * @return	해당하는 직무를 리스트에 담아서 반환
	 */
	public List<ShowCareersResponse> getCareerList(String major, String middle) {
		List<ShowCareersResponse> careerList = new ArrayList<>();    // 직무 리스트를 담을 변수
		if (major.isEmpty() && middle.isEmpty()) {            // 대분류, 중분류가 없으면 -> 대분류 가져오기
			List<Careers> majorCareersList = careerDetailsRepository.findAllByCategoryType("대분류");
			for (Careers c : majorCareersList)
				careerList.add(ShowCareersResponse.builder()
					.categoryName(c.getCategoryName())
					.categoryImage(c.getCategoryImage())
					.build());
		} else if (!major.isEmpty() && middle.isEmpty()) {        // 대분류가 있고 중분류가 없으면 -> 중분류 가져오기
			Optional<Careers> majorCareer = careerDetailsRepository.findCareersByCategoryNameAndCategoryType(major,
				"대분류");    // 대분류 직무 를 변수로 가져옴
			if (majorCareer.isPresent()) {                                                                                                    // 해당 대분류가 존재하면
				List<Careers> middleCareersList = careerDetailsRepository.findAllByCategoryType(
					"중분류");                                    // 중분류 전체 리스트를 가져와서
				for (Careers m : middleCareersList) {
					if (m.getParent()
						.getCareerId()
						.equals(majorCareer.get()
							.getCareerId())) {                                                // 대분류를 부모로 가지는 중분류를 담음
						careerList.add(ShowCareersResponse.builder()
							.categoryName(m.getCategoryName())
							.categoryImage(m.getCategoryImage())
							.build());
					}
				}
			} else {
				log.error("해당하는 중분류를 찾을 수 없습니다.");
				throw new UsernameNotFoundException("요청한 정보를 찾을 수 없습니다.");
			}
		} else if (!major.isEmpty()) {        // 대분류와 중분류 모두 있으면 -> 소분류 가져오기
			Optional<Careers> middleCareer = careerDetailsRepository.findCareersByCategoryNameAndCategoryType(middle,
				"중분류");    // 중분류를 직무로 가져오고
			Optional<Careers> majorCareer = careerDetailsRepository.findCareersByCategoryNameAndCategoryType(major,
				"대분류");    // 대분류를 직무로 가져오고
			if (middleCareer.isPresent()
				&& majorCareer.isPresent()) {                                                                        // 해당하는 대/중분류 직무가 있으면
				List<Careers> middleCareersList = careerDetailsRepository.findAllByCategoryType(
					"소분류");                                    // 소분류 리스트를 전부 가져와
				for (Careers m : middleCareersList) {
					if (m.getParent().equals(middleCareer.get()) && m.getParent()
						.getParent()
						.equals(majorCareer.get())) {                    // 소분류의 중분류, 대분류가 일치하는지 확인하고 담음
						careerList.add(ShowCareersResponse.builder()
							.categoryName(m.getCategoryName())
							.categoryImage(m.getCategoryImage())
							.build());
					}
				}
			} else {
				log.error("해당하는 소분류를 찾을 수 없습니다.");
				throw new UsernameNotFoundException("요청한 정보를 찾을 수 없습니다.");
			}
		}

		return careerList;
	}

	/**
	 * 기술스택을 검색하는 메서드
	 *
	 * @param keyword 검색 키워드
	 * @return 키워드에 해당하는 기술스택을 반환
	 */
	public List<TechnologyStack> getTechnologyStackByKeyword(String keyword) {
		return technologyStackRepository.findByStackNameContaining(keyword);
	}

	/**
	 * 추가 정보를 입력했는지 확인하는 메서드
	 *
	 * @param user 확인할 유저
	 * @return true: 추가정보 입력함 / false: 입력안함
	 */
	public boolean isAddInfoShow(UserAccount user) {
		return !userCareerDetailsRepository.existsByUserAccount(user);
	}

	public void saveUser(UserAccount user) {
		userAccountRepository.save(user);
		mailService.removeVerifiedUser(user.getUserId(), user.getEmail());
	}

	public UserCareerDetails findCareerDetailsByUser(UserAccount user) {

		Optional<UserCareerDetails> userCareerDetails = userCareerDetailsRepository.findByUserAccount(user);

		if (userCareerDetails.isEmpty()) {
			log.error("해당하는 정보를 찾을 수 없습니다.");
			throw new UsernameNotFoundException("요청한 정보를 찾을 수 없습니다.");
		}

		return userCareerDetails.get();
	}

	public void saveUserCareerDetail(UserAccount user, String smallCategory) {

		Optional<Careers> careers = careerDetailsRepository.findCareersByCategoryNameAndCategoryType(smallCategory,
			"소분류");

		if (careers.isEmpty()) {
			log.error("해당하는 직무를 찾을 수 없습니다.");
			throw new UsernameNotFoundException("요청한 정보를 찾을 수 없습니다.");
		}

		UserCareerDetails userCareerDetails = UserCareerDetails.builder()
			.smallCategory(careers.get())
			.userAccount(user)
			.build();
		userCareerDetailsRepository.save(userCareerDetails);

	}

	public String getUserMBTI(String userId) {
		return userAccountRepository.findMBTIByUserId(userId).toString();
	}

	public void updateUserMBTI(UserMBTIRequest userMBTIRequest) {
		UserAccount user = findUserByUserId(userMBTIRequest.getUserId());
		user.updateMBTI(userMBTIRequest.getMbti());
	}
}
