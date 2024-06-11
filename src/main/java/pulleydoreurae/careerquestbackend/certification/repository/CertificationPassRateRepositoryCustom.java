package pulleydoreurae.careerquestbackend.certification.repository;

import java.util.List;

import pulleydoreurae.careerquestbackend.certification.domain.dto.request.PassRateSearchRequest;
import pulleydoreurae.careerquestbackend.certification.domain.entity.CertificationPassRate;

/**
 * 자격증 합격률 검색을 위한 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/06/11
 */
public interface CertificationPassRateRepositoryCustom {

	List<CertificationPassRate> findBySearchRequest(PassRateSearchRequest request);
}
