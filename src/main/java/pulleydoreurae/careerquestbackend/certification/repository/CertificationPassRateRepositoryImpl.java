package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.certification.domain.dto.request.PassRateSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationPassRate;
import pulleydoreurae.careerquestbackend.certification.domain.entity.QCertificationPassRate;

/**
 * 자격증 합격률 검색 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
@RequiredArgsConstructor
public class CertificationPassRateRepositoryImpl implements CertificationPassRateRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	/**
	 * 자격증 합격률 검색조건으로 검색
	 *
	 * @param request 검색조건
	 * @return 검색결과
	 */
	@Override
	public List<CertificationPassRate> findBySearchRequest(PassRateSearchRequest request) {
		QCertificationPassRate certificationPassRate = QCertificationPassRate.certificationPassRate;
		BooleanBuilder builder = new BooleanBuilder();

		if (request.getCertificationName() != null && !request.getCertificationName().isEmpty()) {
			builder.and(certificationPassRate.certification.certificationName.like(request.getCertificationName()));
		}
		if (request.getStartYear() != null && request.getStartYear() > 0) {
			builder.and(certificationPassRate.examYear.goe(request.getStartYear()));
		}
		if (request.getEndYear() != null && request.getEndYear() > 0) {
			builder.and(certificationPassRate.examYear.loe(request.getEndYear()));
		}
		if (request.getExamRound() != null) {
			builder.and(certificationPassRate.examRound.eq(request.getExamRound()));
		}
		if (request.getExamType() != null) {
			builder.and(certificationPassRate.examType.eq(request.getExamType()));
		}
		if (request.getMinPassRate() != null && request.getMinPassRate() > 0) {
			builder.and(certificationPassRate.passRate.goe(request.getMinPassRate()));
		}
		if (request.getMaxPassRate() != null && request.getMaxPassRate() > 0) {
			builder.and(certificationPassRate.passRate.loe(request.getMaxPassRate()));
		}

		return jpaQueryFactory
				.selectFrom(certificationPassRate)
				.join(certificationPassRate.certification)
				.where(builder)
				.fetch();
	}
}
