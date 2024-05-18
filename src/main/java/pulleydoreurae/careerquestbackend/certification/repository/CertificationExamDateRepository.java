package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pulleydoreurae.careerquestbackend.certification.domain.ExamType;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationExamDate;

/**
 * 자격증 시험 일정 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/18
 */
public interface CertificationExamDateRepository extends JpaRepository<CertificationExamDate, Long> {

	@Query("select ced from CertificationExamDate ced join ced.certification c where c.certificationName = :name and ced.examRound = :examRound and c.examType = :examType")
	List<CertificationExamDate> findByNameAndExamRoundAndExamType(String name, Long examRound, ExamType examType);
}
