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
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationExamDate;
import pulleydoreurae.careerquestbackend.config.QueryDSLConfig;

/**
 *
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
@DataJpaTest
@DisplayName("자격증 시험일정을 정상적으로 불러오는지 테스트")
@Transactional
@Import(QueryDSLConfig.class)
class CertificationExamDateRepositoryTest {

	@Autowired
	DataSource dataSource;

	@Autowired
	CertificationRepository certificationRepository;

	@Autowired
	CertificationExamDateRepository certificationExamDateRepository;

	@BeforeEach
	void setUp() { // 기본적인 자격증 정보 저장
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		String c_sql1 =
				"insert into certification(certification_id, certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (200, 1, '정보처리기사', '4년제 졸업', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		String c_sql2 =
				"insert into certification(certification_id, certification_code, certification_name, qualification, organizer, registration_link, ai_summary, created_at, modified_at) "
						+ "values (201, 1, '정보보안기사', '4년제 졸업', '한국산업인력공단', 'https://www.hrdkorea.or.kr/', 'AI요약내용입니다. ~~~', '2024-01-01','2024-01-01')";

		jdbcTemplate.execute(c_sql1);
		jdbcTemplate.execute(c_sql2);

		String sql1 =
				"insert into certification_exam_date(certification_id, exam_type, exam_round, exam_date, created_at, modified_at) "
						+ "values (200, 'FIRST_STAGE', 1234, '2024-01-10','2024-01-01','2024-01-01')";

		String sql2 =
				"insert into certification_exam_date(certification_id, exam_type, exam_round, exam_date, created_at, modified_at) "
						+ "values (200, 'FIRST_STAGE', 1234, '2024-01-11','2024-01-01','2024-01-01')";

		String sql3 =
				"insert into certification_exam_date(certification_id, exam_type, exam_round, exam_date, created_at, modified_at) "
						+ "values (200, 'FIRST_STAGE', 1234, '2024-01-12','2024-01-01','2024-01-01')";

		String sql4 =
				"insert into certification_exam_date(certification_id, exam_type, exam_round, exam_date, created_at, modified_at) "
						+ "values (201, 'FIRST_STAGE', 1234, '2024-1-11','2024-01-01','2024-01-01')";

		String sql5 =
				"insert into certification_exam_date(certification_id, exam_type, exam_round, exam_date, created_at, modified_at) "
						+ "values (201, 'LAST_STAGE', 1234, '2024-05-10','2024-01-01','2024-01-01')";

		jdbcTemplate.execute(sql1);
		jdbcTemplate.execute(sql2);
		jdbcTemplate.execute(sql3);
		jdbcTemplate.execute(sql4);
		jdbcTemplate.execute(sql5);
	}

	@Test
	@DisplayName("자격증 이름과 회차, 시험구분으로 검색")
	void findByNameAndExamRoundAndExamType() {
		// Given

		// When
		List<CertificationExamDate> result1 = certificationExamDateRepository.findByNameAndExamRoundAndExamType(
				"정보처리기사", 1234L, ExamType.FIRST_STAGE);
		List<CertificationExamDate> result2 = certificationExamDateRepository.findByNameAndExamRoundAndExamType(
				"정보보안기사", 1234L, ExamType.LAST_STAGE);

		// Then
		assertEquals(certificationRepository.findById(200L).get(), result1.get(0).getCertification());
		assertEquals(3, result1.size());
		assertEquals(LocalDate.of(2024, 1, 10), result1.get(0).getExamDate());
		assertEquals(LocalDate.of(2024, 1, 11), result1.get(1).getExamDate());
		assertEquals(LocalDate.of(2024, 1, 12), result1.get(2).getExamDate());
		assertEquals(certificationRepository.findById(201L).get(), result2.get(0).getCertification());
		assertEquals(1, result2.size());
		assertEquals(LocalDate.of(2024, 5, 10), result2.get(0).getExamDate());

	}

	@Test
	@DisplayName("날짜로 자격증 시험일정 리스트 불러오기")
	void findByDate() {
	    // Given

	    // When
		List<CertificationExamDate> result = certificationExamDateRepository.findByDate(LocalDate.of(2024, 1, 11));

		// Then
		assertEquals(2, result.size());
		assertEquals(ExamType.FIRST_STAGE, result.get(0).getExamType());
		assertEquals(ExamType.FIRST_STAGE, result.get(1).getExamType());
	}
}
