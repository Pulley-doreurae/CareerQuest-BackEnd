package pulleydoreurae.careerquestbackend.auth.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pulleydoreurae.careerquestbackend.auth.domain.UserRole;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccessLog;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;

/**
 * UserAccountLogRepository 클래스를 테스트 하는 클래스
 */
@DataJpaTest
@DisplayName("접속기록 Repository 테스트")
public class UserAccountLogRepositoryTest {

    @Autowired
    UserAccountRepository userAccountRepository;
    @Autowired
    private UserAccessLogRepository userAccessLogRepository;

    @BeforeEach     // 테스트 하기 전 유저 사전 정보 저장
    public void before(){
        UserAccount user1 = UserAccount.builder()
                .userId("user_1")
                .userName("testName1")
                .email("test1@email.com")
                .phoneNum("010-1111-2222")
                .password("testPassword")
                .role(UserRole.ROLE_TEMPORARY_USER)
                .build();

        UserAccount user2 = UserAccount.builder()
                .userId("user_2")
                .userName("testName2")
                .email("test2@email.com")
                .phoneNum("010-2222-2222")
                .password("testPassword")
                .role(UserRole.ROLE_TEMPORARY_USER)
                .build();

        userAccountRepository.save(user1);
        userAccountRepository.save(user2);
    }

    @AfterEach      // 테스트 종료 후 추가한 정보들 삭제
    public void after() {
        userAccessLogRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    @DisplayName("1. 로그인 한 기록 저장 테스트")
    void addUserAccessLog(){
        // Given
        UserAccount userAccount = userAccountRepository.findByUserId("user_1").get();

        UserAccessLog userAccessLog = UserAccessLog.builder()
                .userAccount(userAccount)
                .ipv4("127.0.0.1")
                .ipv6(":1")
                .location("Localhost")
                .AccessedAt("2024.03.28 00:00")
                .build();

        // When
        userAccessLogRepository.save(userAccessLog);

        // Then
        assertEquals(1, userAccessLogRepository.findAll().size());
    }

    @Test
    @DisplayName("2. 특정 유저의 접속 기록을 조회하는 테스트")
    void findByUserAccessLogByUserId(){
        // Given
        UserAccount userAccount_1 = userAccountRepository.findByUserId("user_1").get();
        UserAccount userAccount_2 = userAccountRepository.findByUserId("user_2").get();

        UserAccessLog userAccessLog_1 = UserAccessLog.builder()
                .userAccount(userAccount_1)
                .ipv4("127.0.0.1")
                .ipv6(":1")
                .location("Localhost")
                .AccessedAt("2024.03.28 01:00")
                .build();

        UserAccessLog userAccessLog_2 = UserAccessLog.builder()
                .userAccount(userAccount_2)
                .ipv4("127.0.0.1")
                .ipv6(":1")
                .location("Localhost")
                .AccessedAt("2024.03.28 13:00")
                .build();

        UserAccessLog userAccessLog_3 = UserAccessLog.builder()
                .userAccount(userAccount_1)
                .ipv4("127.0.0.1")
                .ipv6(":1")
                .location("Localhost")
                .AccessedAt("2024.03.28 15:00")
                .build();

        userAccessLogRepository.save(userAccessLog_1);
        userAccessLogRepository.save(userAccessLog_2);
        userAccessLogRepository.save(userAccessLog_3);

        // When

        // Then
        assertEquals(2, userAccessLogRepository.findAllByUserAccount(userAccount_1).size());
    }

}
