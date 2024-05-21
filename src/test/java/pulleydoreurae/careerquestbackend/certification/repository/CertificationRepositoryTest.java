package pulleydoreurae.careerquestbackend.certification.repository;

import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import pulleydoreurae.careerquestbackend.certification.domain.entity.Certification;

/**
 *
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
@DataJpaTest
@DisplayName("자격증의 기본 정보를 정상적으로 불러오는지 테스트")
@Transactional
class CertificationRepositoryTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	CertificationRepository certificationRepository;

	@BeforeEach
	void setUp() { // 기본적인 자격증 정보 저장
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql1 =
				"insert into certification(certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (1, '정보처리기사', '4년제 졸업', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		String sql2 =
				"insert into certification(certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (2, '정보처리산업기사', '2년제 졸업', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		String sql3 =
				"insert into certification(certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (3, '정보보안기사', '4년제 졸업', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		String sql4 =
				"insert into certification(certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (4, '정보보안산업기사', '2년제 졸업', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		String sql5 =
				"insert into certification(certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (5, '정보처리기능사', '무관', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		jdbcTemplate.execute(sql1);
		jdbcTemplate.execute(sql2);
		jdbcTemplate.execute(sql3);
		jdbcTemplate.execute(sql4);
		jdbcTemplate.execute(sql5);
	}

	@Test
	@DisplayName("자격증 코드로 자격증 정보 리스트 불러오기")
	void findAllByCode() {
		// Given

		// When
		Certification result = certificationRepository.findAllByCertificationCode(1L).get();

		// Then
		assertEquals("정보처리기사", result.getCertificationName());
	}

	@Test
	@DisplayName("자격증이름으로 자격증 정보 리스트 불러오기")
	void findAllByName() {
		// Given

		// When
		Certification result = certificationRepository.findAllByCertificationName("정보처리기사").get();

		// Then
		assertEquals(1L, result.getCertificationCode());
	}
}
