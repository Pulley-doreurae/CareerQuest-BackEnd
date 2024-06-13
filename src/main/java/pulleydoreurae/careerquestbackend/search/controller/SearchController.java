package pulleydoreurae.careerquestbackend.search.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.search.domain.response.SearchRankResponse;
import pulleydoreurae.careerquestbackend.search.domain.response.SearchResultResponse;
import pulleydoreurae.careerquestbackend.search.service.SearchService;

@RequestMapping("/api")
@RestController
@AllArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@GetMapping("/search/ranking")
	public ResponseEntity<?> getRanking(){
		return ResponseEntity.status(HttpStatus.OK).body(searchService.getShowRanking());
	}

	@GetMapping("/search/keyword")
	public ResponseEntity<SearchResultResponse> searchAllByKeyword(@RequestParam("keyword") String keyword,
		@PageableDefault(size = 15, direction = Sort.Direction.DESC) Pageable pageable){

		return ResponseEntity.status(HttpStatus.OK).body(searchService.findAllByKeyword(keyword, pageable));
	}

	@GetMapping("/search/update")
	public ResponseEntity<SimpleResponse> updateKeyword(){
		searchService.updateRankings();

		return ResponseEntity.status(HttpStatus.OK).body(
			SimpleResponse.builder()
				.msg("검색어 랭킹이 업데이트 되었습니다.")
				.build()
		);
	}

}
