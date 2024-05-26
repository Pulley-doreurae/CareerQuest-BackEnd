package pulleydoreurae.careerquestbackend.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestSearchRequest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;

/**
 * 공모전 엔티티를 QueryDSL로 검색하기 위한 Repository
 *
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
public interface ContestRepositoryCustom {

	Page<Contest> findAllBySearchRequest(ContestSearchRequest request, Pageable pageable);
}
