package pulleydoreurae.careerquestbackend.common.community.controller;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostFailResponse;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.service.PostService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 게시글 컨트롤러 추상화 클래스
 *
 * @author : parkjihyeok
 * @since : 2024/05/06
 */
public abstract class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	/**
	 * 게시글 전체 조회
	 *
	 * @param pageable 페이지 정보
	 * @return 게시글 리스트
	 */
	public ResponseEntity<List<PostResponse>> getPostList(Pageable pageable) {
		List<PostResponse> posts = postService.getPostResponseList(pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 카테고리별 게시글 조회
	 *
	 * @param category 카테고리 정보
	 * @param pageable 페이지 정보
	 * @return 게시글 리스트
	 */
	public ResponseEntity<List<PostResponse>> getPostListByCategory(Long category, Pageable pageable) {

		List<PostResponse> posts = postService.getPostResponseListByCategory(category, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 작성자별 게시글 조회
	 *
	 * @param userId 작성자
	 * @param pageable 페이지 정보
	 * @return 게시글 리스트
	 */
	public ResponseEntity<?> getPostListByUserId(String userId, Pageable pageable) {

		List<PostResponse> posts = postService.getPostListByUserAccount(userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 검색한 게시글 조회
	 *
	 * @param keyword 검색어
	 * @param category 카테고리 정보
	 * @param pageable 페이지 정보
	 * @return 게시글 리스트
	 */
	public ResponseEntity<List<PostResponse>> searchPosts(String keyword, Long category, Pageable pageable) {

		List<PostResponse> posts = postService.searchPosts(keyword, category, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 게시글 단건 조회
	 *
	 * @param request 요청자 정보
	 * @param response 응답
	 * @param postId 게시글 정보
	 * @return 게시글
	 */
	public ResponseEntity<?> getPost(HttpServletRequest request, HttpServletResponse response, Long postId) {

		PostResponse post = postService.findByPostId(request, response, postId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(post);
	}

	/**
	 * 사진 조회
	 *
	 * @param fileName 사진 정보
	 * @return 사진 경로
	 * @throws MalformedURLException 사진 정보를 찾지 못했을 경우
	 */
	public Resource getImage(String fileName) throws MalformedURLException {
		return postService.getImageResource(fileName);
	}

	/**
	 * 사진 저장
	 *
	 * @param images 이미지 정보
	 * @return 저장된 사진 정보 리스트
	 */
	public ResponseEntity<List<String>> saveImage(List<MultipartFile> images) {
		List<String> imagesName = postService.saveImage(images);
		return ResponseEntity.status(HttpStatus.OK)
				.body(imagesName);
	}

	/**
	 * 게시글 저장
	 *
	 * @param postRequest 게시글 정보
	 * @param bindingResult 에러 검증
	 * @return 처리에 대한 결과
	 */
	public ResponseEntity<?> savePost(PostRequest postRequest, BindingResult bindingResult) {

		// 검증
		ResponseEntity<PostFailResponse> BAD_REQUEST = validCheck(postRequest, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		postService.savePost(postRequest);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("게시글 등록에 성공했습니다.")
						.build());
	}

	/**
	 * 게시글 수정
	 *
	 * @param postId 게시글 정보
	 * @param postRequest 업데이트 정보
	 * @param bindingResult 에러 검증
	 * @return 처리 결과
	 */
	public ResponseEntity<?> updatePost(Long postId, PostRequest postRequest, BindingResult bindingResult) {

		// 검증
		ResponseEntity<PostFailResponse> BAD_REQUEST = validCheck(postRequest, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		if (!postService.updatePost(postId, postRequest)) {
			return makeBadRequestUsingSimpleResponse("해당 게시글을 수정할 수 없습니다.");
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("게시글 수정에 성공했습니다.")
						.build());
	}

	/**
	 * 게시글 삭제
	 *
	 * @param postId 게시글 정보
	 * @param userId 요청자 정보
	 * @return 처리 결과
	 */
	public ResponseEntity<SimpleResponse> deletePost(Long postId, String userId) {
		if (!postService.deletePost(postId, userId)) {
			return makeBadRequestUsingSimpleResponse("해당 게시글을 삭제할 수 없습니다.");
		}
		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("게시글 삭제에 성공하였습니다.")
						.build());
	}

	/**
	 * SimpleResponse 형태의 BAD_REQUEST 생성 메서드
	 *
	 * @param message 작성할 메시지
	 * @return 완성된 BAD_REQUEST
	 */
	private ResponseEntity<SimpleResponse> makeBadRequestUsingSimpleResponse(String message) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(SimpleResponse.builder()
						.msg(message)
						.build());
	}

	/**
	 * 검증 메서드
	 *
	 * @param postRequest   게시글 요청 (검증에 실패하더라도 입력한 값은 그대로 돌려준다.)
	 * @param bindingResult 검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 ResponseEntity 반환
	 */
	private ResponseEntity<PostFailResponse> validCheck(PostRequest postRequest, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String[] errors = new String[bindingResult.getAllErrors().size()];
			int index = 0;
			for (ObjectError error : bindingResult.getAllErrors()) {
				errors[index++] = error.getDefaultMessage();
			}

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(PostFailResponse.builder()
							.title(postRequest.getTitle())
							.content(postRequest.getContent())
							.category(postRequest.getCategory())
							.errors(errors)
							.build());
		}
		return null;
	}
}
