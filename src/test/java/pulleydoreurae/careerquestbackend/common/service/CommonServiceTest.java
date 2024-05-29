package pulleydoreurae.careerquestbackend.common.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;

/**
 * @author : parkjihyeok
 * @since : 2024/05/28
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("공통으로 자주 사용되는 메서드 테스트")
class CommonServiceTest {

	@InjectMocks CommonService commonService;
	@Mock UserAccountRepository userAccountRepository;
	@Mock SecurityContext securityContext;
	@Mock Authentication authentication;
	@Mock UserDetails userDetails;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.setContext(securityContext);
	}

	@Test
	@DisplayName("사용자 찾기 실패")
	void findUserAccountFailTest() {
		// Given
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.empty());

		// When

		// Then
		assertThrows(UsernameNotFoundException.class, () -> commonService.findUserAccount("testId", false));
	}

	@Test
	@DisplayName("사용자 찾기 성공")
	void findUserAccountSuccessTest() {
		// Given
		UserAccount user = UserAccount.builder().userId("testId").build();
		given(userAccountRepository.findByUserId("testId")).willReturn(Optional.of(user));

		// When
		UserAccount result = commonService.findUserAccount("testId", false);

		// Then
		assertEquals(user, result);
	}

	@Test
	@DisplayName("요청자가 권한이 있는지 테스트 - 실패")
	void checkAuthTest1() {
		// Given
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.isAuthenticated()).willReturn(true);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn("testId");

		// When

		// Then
		assertThrows(IllegalAccessError.class, () -> commonService.checkAuth("testI"));
	}

	@Test
	@DisplayName("요청자가 권한이 있는지 테스트 - 성공")
	void checkAuthTest2() {
		// Given
		given(securityContext.getAuthentication()).willReturn(authentication);
		given(authentication.isAuthenticated()).willReturn(true);
		given(authentication.getPrincipal()).willReturn(userDetails);
		given(userDetails.getUsername()).willReturn("testId");

		// When

		// Then
		assertDoesNotThrow(() -> commonService.checkAuth("testId"));
	}
}
