package pulleydoreurae.careerquestbackend.auth.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Subdivision;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccessLog;
import pulleydoreurae.careerquestbackend.auth.domain.entity.UserAccount;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccessLogRepository;
import pulleydoreurae.careerquestbackend.auth.repository.UserAccountRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



/**
 * 접속기록 Service
 *
 * @author : hanjaeseong
 * @since : 2024/03/28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccessLogService {

    private final UserAccountRepository userAccountRepository;
    private final UserAccessLogRepository userAccessLogRepository;

    @Value("${DATABASE_CITY_PATH}")
    private String DATABASE_CITY_PATH;
    private static DatabaseReader reader;

    public void saveLog(HttpServletRequest request,
                               Authentication authentication) throws IOException, GeoIp2Exception {

        UserAccount accessedUserAccount = userAccountRepository.findByUserId(authentication.getName()).get();
        String ipv4_addr = getClientIPv4(request);
        String ipv6_addr = getClientIPv4(request);
        String accessedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

        File dbFile = new File(DATABASE_CITY_PATH);
        reader = new DatabaseReader.Builder(dbFile).build();

        CityResponse cityResponse = reader.city(InetAddress.getByName(ipv4_addr));

        Subdivision subdivision = cityResponse.getMostSpecificSubdivision();
        City city = cityResponse.getCity();
        String accessedLocation = subdivision.getName() + ", " + city.getName();

        UserAccessLog userAccessLog = UserAccessLog.builder()
                .userAccount(accessedUserAccount)
                .ipv4(ipv4_addr)
                .ipv6(ipv6_addr)
                .location(accessedLocation)
                .AccessedAt(accessedAt)
                .build();

        userAccessLogRepository.save(userAccessLog);

    }

    // Client의 ipv6를 가져오는 함수
    public static void getClientIPv6() throws IOException {
        // TODO IPv6를 받는 방법
    }

    // Client의 ipv4를 가져오는 함수
    public static String getClientIPv4(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For"); // HTTP 요정 헤더 중의 하나 | ip 주소 식별을 담당

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
