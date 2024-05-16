package pulleydoreurae.careerquestbackend.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pulleydoreurae.careerquestbackend.auth.domain.dto.request.ShowUserDetailsToChangeRequest;
import pulleydoreurae.careerquestbackend.auth.domain.entity.*;
import pulleydoreurae.careerquestbackend.auth.repository.*;
import pulleydoreurae.careerquestbackend.mail.service.MailService;

import java.io.Serializable;
import java.util.*;

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
	private final CareerDetailsRepository careerDetailsRepository;
	private final TechnologyStackRepository technologyStackRepository;
	private final UserFirstAddInfoRepository userFirstAddInfoRepository;

	@Value("${spring.mail.domain}")
	private String domain;

	@Value("${FIND_PASSWORD_PATH}")
	private String findPasswordPath;

	@Value("${UPDATE_EMAIL_PATH}")
	private String updateEmailPath;

	@Value("${serialVersionUID}")
	private static long serialVersionUID;

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
	public void deleteHelpUser(String uuid) { helpUserPasswordRepository.deleteById(uuid); }

	/**
	 * user를 삭제하는 메서드
	 *
	 * @param user 삭제할 user
	 */
	public void deleteUser(UserAccount user) { userAccountRepository.delete(user); }

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
	 * @param userId	이메일 변경을 요청한 userId
	 * @param email		변경할 이메일 주소
	 */
	public void sendUpdateEmailLink(String userId, String email){
		String uuid = UUID.randomUUID().toString();
		String verification_url = domain + updateEmailPath + uuid;

		changeUserEmailRepository.save(new ChangeUserEmail(uuid, userId, email));
		log.info("[회원 - 이메일 변경] : {} 의 이메일 변경을 위한 객체저장 및 메일전송", email);
		log.info("[회원 - 이메일 변경] : {} 의 이메일 변경을 위한 UUID", uuid);

		mailService.sendMail(email, verification_url, "updateEmailForm", "취준진담 이메일 변경");
	}

	public ChangeUserEmail checkUpdateEmailUserIdByUuid(String uuid){
		Optional<ChangeUserEmail> helpUser = changeUserEmailRepository.findById(uuid);

		// uuid가 없다면
		if (helpUser.isEmpty()) {
			log.error("{} 의 해당하는 정보를 찾을 수 없습니다.", uuid);
			throw new UsernameNotFoundException("요청한 회원 정보를 찾을 수 없습니다.");
		}
		return helpUser.get();
	}

	public void updateEmail(ChangeUserEmail changeUser){
		Optional<UserAccount> userAccount = userAccountRepository.findByUserId(changeUser.getUserId());
		if (userAccount.isPresent()) {
			UserAccount user = userAccount.get();
			user.setEmail(changeUser.getEmail());
			userAccountRepository.save(user);
		}
		changeUserEmailRepository.delete(changeUser);
	}

	public void updateDetails(UserAccount user, ShowUserDetailsToChangeRequest showUserDetailsToChangeRequest) {

		user.setPhoneNum(showUserDetailsToChangeRequest.getPhoneNum());

		UserCareerDetails updateUserCareerDetails = UserCareerDetails.builder()
				.id(user.getUserCareerDetails().getId()) // 동일한 id 로 덮어쓰기
				.majorCategory(showUserDetailsToChangeRequest.getMajorCategory())
				.middleCategory(showUserDetailsToChangeRequest.getMiddleCategory())
				.smallCategory(showUserDetailsToChangeRequest.getSmallCategory())
				.build();
		userCareerDetailsRepository.save(updateUserCareerDetails);

		Collections.sort( showUserDetailsToChangeRequest.getTechnologyStacks() ); // 오름차순 정렬 후

		if(showUserDetailsToChangeRequest.getTechnologyStacks().size() > user.getStacks().size()){	// 크기가 더 크다 -> 기술을 더 추가함
			for(int i =  user.getStacks().size() ; i < showUserDetailsToChangeRequest.getTechnologyStacks().size() ;i++){
				UserTechnologyStack userTechnologyStack = UserTechnologyStack.builder()
						.stackId(showUserDetailsToChangeRequest.getTechnologyStacks().get(i))
						.userAccount(user)								 	// 크기가 넘은 나머진 부분은 새로 정의하여 추가
						.build();
				userTechnologyStackRepository.save(userTechnologyStack);
				user.getStacks().add(userTechnologyStack);
			}
		}

		else if (showUserDetailsToChangeRequest.getTechnologyStacks().size() < user.getStacks().size()){ // 크기가 더 작다 -> 기술을 더 제거
			int size = user.getStacks().size() - 1; // 삭제할 인덱스 지정(맨 뒤에서부터 삭제)
			int sizeDifference = user.getStacks().size() - showUserDetailsToChangeRequest.getTechnologyStacks().size();
			for(int i = 0; i < sizeDifference; i++){
				UserTechnologyStack removeTech = user.getStacks().get(size-i);
				user.getStacks().remove(removeTech);
				userTechnologyStackRepository.delete(removeTech);
			}
		}

		for(int i = 0 ; i <  user.getStacks().size() ;i++) { // 기존에 있던 기술스택을 재사용
			UserTechnologyStack userTechnologyStack = UserTechnologyStack.builder()
					.id(user.getStacks().get(i).getId())                // 크기가 동일한 부분은 가지고 있던 기술스택에셔 변경
					.userAccount(user)
					.stackId(showUserDetailsToChangeRequest.getTechnologyStacks().get(i))
					.build();
			userTechnologyStackRepository.save(userTechnologyStack);
		}

		userAccountRepository.save(user);
	}

	public List<String> getTechnologyStack(UserAccount user) {
		List<String> stackIds = new ArrayList<>();

		for(UserTechnologyStack userTechnologyStack : user.getStacks()){
			stackIds.add(userTechnologyStack.getStackId());
		}
		return stackIds;
	}

	public List<String> getCareerList(String categoryType){
		List<Careers> careerList = careerDetailsRepository.findAllByCategoryType(categoryType);

		if (careerList.isEmpty()) {
			log.error(" 해당하는 정보를 찾을 수 없습니다.");
			throw new UsernameNotFoundException("요청한 정보를 찾을 수 없습니다.");
		}

		List<String> careerNameList = new ArrayList<>();
        for (Careers careers : careerList) {
            careerNameList.add(careers.getCategoryName());
        }

		return careerNameList;
	}

	public List<TechnologyStack> getTechnologyStackByKeyword(String keyword){
        return technologyStackRepository.findByStackNameContaining(keyword);
	}

	public boolean isAddInfoShow(String user) {
		boolean isAddInfo = userFirstAddInfoRepository.existsByUserId(user);
		if (isAddInfo){ userFirstAddInfoRepository.deleteByUserId(user); }

		return !isAddInfo;
	}

	public void saveUser(UserAccount user) {

		userAccountRepository.save(user);
		mailService.removeVerifiedUser(user.getUserId(), user.getEmail());
		userFirstAddInfoRepository.save(UserFirstAddInfo.builder()
				.userId(user.getUserId())
				.build()
		);

	}
}
