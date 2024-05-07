package pulleydoreurae.careerquestbackend.common.community.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostLikeRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.service.PostLikeService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 게시글 좋아요 컨트롤러 추상화 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/05/06
 */
public abstract class PostLikeController {

	private final PostLikeService postLikeService;

	public PostLikeController(PostLikeService postLikeService) {
		this.postLikeService = postLikeService;
	}

	/**
	 * 좋아요 상태를 변경하는 메서드
	 *
	 * @param postLikeRequest 좋아요 요청
	 * @param bindingResult 에러검증
	 * @return 처리결과
	 */
	public ResponseEntity<SimpleResponse> changeLikeStatus(PostLikeRequest postLikeRequest,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		if (!postLikeService.changePostLike(postLikeRequest)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg("좋아요 상태 변경에 실패했습니다.")
							.build());
		}

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("좋아요 상태 변경에 성공했습니다.")
						.build());
	}

	/**
	 * 한 사용자가 좋아요 누른 게시글 리스트 반환 메서드
	 *
	 * @param userId 사용자 id
	 * @param pageable 페이지
	 * @return 조회한 리스트
	 */
	public ResponseEntity<List<PostResponse>> findAllPostLikeByUserAccount(String userId, Pageable pageable) {

		List<PostResponse> result = postLikeService.findAllPostLikeByUserAccount(userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(result);
	}

	/**
	 * 검증 메서드
	 *
	 * @param bindingResult 검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 ResponseEntity 반환
	 */
	private ResponseEntity<SimpleResponse> validCheck(BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			StringBuilder sb = new StringBuilder();
			bindingResult.getAllErrors().forEach(error -> {
				sb.append(error.getDefaultMessage()).append("\n");
			});

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(SimpleResponse.builder()
							.msg(sb.toString())
							.build());
		}
		return null;
	}
}
