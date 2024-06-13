package pulleydoreurae.careerquestbackend.community.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestSearchRequest;
import pulleydoreurae.careerquestbackend.community.domain.entity.Contest;
import pulleydoreurae.careerquestbackend.community.domain.entity.QContest;

/**
 * 공모전 엔티티 QueryDSL검색 구현체
 *
 * @author : parkjihyeok
 * @since : 2024/05/25
 */
@RequiredArgsConstructor
public class ContestRepositoryImpl implements ContestRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	/**
	 * 검색조건에 따른 동적쿼리 (and 검색)
	 *
	 * @param request  요청
	 * @param pageable 페이지정보
	 * @return 처리결과
	 */
	@Override
	public Page<Contest> findAllBySearchRequest(ContestSearchRequest request, Pageable pageable) {
		QContest contest = QContest.contest;
		BooleanBuilder builder = new BooleanBuilder();

		// 공모전분야필드가 검색조건에 있는경우
		if (request.getContestCategory() != null) {
			builder.and(contest.contestCategory.eq(request.getContestCategory()));
		}
		// 대상필드가 검색조건에 있는경우
		if (request.getTarget() != null) {
			builder.and(contest.target.eq(request.getTarget()));
		}
		// 개최지역필드가 검색조건에 있는경우
		if (request.getRegion() != null) {
			builder.and(contest.region.eq(request.getRegion()));
		}
		// 주관처필드가 검색조건에 있는경우
		if (request.getOrganizer() != null) {
			builder.and(contest.organizer.eq(request.getOrganizer()));
		}
		// 총상금필드가 검색조건에 있는경우 (검색조건의 총 상금이 최소금액)
		if (request.getTotalPrize() != null && request.getTotalPrize() > 0) {
			builder.and(contest.totalPrize.goe(request.getTotalPrize())); // 크거나 같은 경우에만
		}
		// 날짜가 검색조건에 있는경우
		if (request.getStartDate() != null && request.getEndDate() != null) {
			builder.and(contest.startDate.between(request.getStartDate(), request.getEndDate())
					.or(contest.endDate.between(request.getStartDate(), request.getEndDate())));
		}
		List<Contest> contests = jpaQueryFactory
				.selectFrom(contest)
				.where(builder)
				.orderBy(contest.id.desc()) // 여기도 조건을 받아서 정방향, 역순을 입력할 수 있을듯 우선은 역순으로 고정
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		// 전체 개수
		long total = jpaQueryFactory
				.selectFrom(contest)
				.where(builder)
				.fetch()
				.size();

		if (contests.isEmpty()) { // 검색결과가 없는 경우
			return new PageImpl<>(List.of(), pageable, 0);
		}
		return new PageImpl<>(contests, pageable, total);
	}
}
