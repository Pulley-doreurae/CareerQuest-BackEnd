package pulleydoreurae.careerquestbackend.certification.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationRegistrationPeriod;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 *
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
@DataJpaTest
@DisplayName("자격증 접수기간 정보를 정상적으로 불러오는지 테스트")
@Transactional
@Import(QueryDSLConfig.class)
class CertificationRegistrationPeriodRepositoryTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	CertificationRepository certificationRepository;
	@Autowired
	CertificationRegistrationPeriodRepository certificationRegistrationPeriodRepository;

	@BeforeEach
	void setUp() { // 기본적인 자격증 정보 저장
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String sql1 =
				"insert into certification(certification_id, certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (100, 1, '정보처리기사', '4년제 졸업', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		jdbcTemplate.execute(sql1);

		String sql2 =
				"insert into certification_registration_period(certification_id, exam_type, exam_round, start_date, end_date, created_at, modified_at) "
						+ "values (100, 'FIRST_STAGE', 1234, '2024-03-10','2024-04-20', '2024-01-01','2024-01-01')";

		jdbcTemplate.execute(sql2);
	}

	@Test
	@DisplayName("자격증 이름과 회차로 검색")
	void findByNameAndExamRound() {
		// Given

		// When
		CertificationRegistrationPeriod result = certificationRegistrationPeriodRepository.findByNameAndExamRound(
				"정보처리기사", 1234L).get();

		// Then
		assertEquals(certificationRepository.findById(100L).get(), result.getCertification());
		assertEquals(100, result.getCertification().getId());
		assertEquals(ExamType.FIRST_STAGE, result.getExamType());
		assertEquals("한국산업인력공단", result.getCertification().getOrganizer());
		assertEquals(LocalDate.of(2024, 3, 10), result.getStartDate());
		assertEquals(LocalDate.of(2024, 4, 20), result.getEndDate());
	}

	@Test
	@DisplayName("날짜로 자격증 기간 리스트 불러오기")
	void findByDate() {
	    // Given

	    // When
		List<CertificationRegistrationPeriod> result = certificationRegistrationPeriodRepository.findByDate(
				LocalDate.of(2024, 3, 11));

		// Then
		assertEquals(1, result.size());
		assertEquals("정보처리기사", result.get(0).getCertification().getCertificationName());
		assertEquals(ExamType.FIRST_STAGE, result.get(0).getExamType());
	}
}
