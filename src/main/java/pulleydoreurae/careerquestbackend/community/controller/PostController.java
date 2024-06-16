package pulleydoreurae.careerquestbackend.community.controller;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.ContestSearchRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.JoinContestRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostAndContestRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.ContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.JoinContestResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostFailResponse;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.community.service.ContestService;
import pulleydoreurae.careerquestbackend.community.service.PostService;

/**
 * 게시글을 담당하는 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;
	private final ContestService contestService;

	/**
	 * 게시글 전체 조회
	 *
	 * @param pageable 페이지 정보
	 * @return 게시글 리스트
	 */
	@GetMapping("/posts")
	public ResponseEntity<List<PostResponse>> getPostList(@PageableDefault(size = 15) Pageable pageable) {
		List<PostResponse> posts = postService.getPostResponseList(pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 카테고리별 게시글 조회
	 *
	 * @param postCategory 카테고리 정보
	 * @param pageable     페이지 정보
	 * @return 게시글 리스트
	 */
	@GetMapping("/posts/category/{postCategory}")
	public ResponseEntity<List<PostResponse>> getPostListByCategory(@PathVariable PostCategory postCategory,
			@PageableDefault(size = 15) Pageable pageable) {

		List<PostResponse> posts = postService.getPostResponseListByCategory(postCategory, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 작성자별 게시글 조회
	 *
	 * @param userId   작성자
	 * @param pageable 페이지 정보
	 * @return 게시글 리스트
	 */
	@GetMapping("/posts/user/{userId}")
	public ResponseEntity<?> getPostListByUserId(@PathVariable String userId,
			@PageableDefault(size = 15) Pageable pageable) {

		List<PostResponse> posts = postService.getPostListByUserAccount(userId, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 검색한 게시글 조회
	 *
	 * @param keyword      검색어
	 * @param postCategory 카테고리 정보
	 * @param pageable     페이지 정보
	 * @return 게시글 리스트
	 */
	@GetMapping("/posts/search")
	public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam(name = "keyword") String keyword,
			@RequestParam(name = "postCategory", required = false) PostCategory postCategory,
			@PageableDefault(size = 15, direction = Sort.Direction.DESC) Pageable pageable) {

		List<PostResponse> posts = postService.searchPosts(keyword, postCategory, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(posts);
	}

	/**
	 * 공모전 검색조회
	 *
	 * @param contestSearchRequest 공모전 검색조회
	 * @param pageable             페이지 정보
	 * @return 게시글 리스트
	 */
	@PostMapping("/contests/search")
	public ResponseEntity<List<ContestResponse>> searchPosts(@RequestBody ContestSearchRequest contestSearchRequest,
			@PageableDefault(size = 15, direction = Sort.Direction.DESC) Pageable pageable) {

		List<ContestResponse> contests = contestService.findBySearchRequest(contestSearchRequest, pageable);

		return ResponseEntity.status(HttpStatus.OK)
				.body(contests);
	}

	/**
	 * 게시글 단건 조회
	 *
	 * @param request  요청자 정보
	 * @param response 응답
	 * @param postId   게시글 정보
	 * @return 게시글
	 */
	@GetMapping("/posts/{postId}")
	public ResponseEntity<?> getPost(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long postId) {

		PostResponse post = postService.findByPostId(request, response, postId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(post);
	}

	/**
	 * 공모전 정보 조회
	 *
	 * @param postId 게시글 정보
	 * @return 게시글
	 */
	@GetMapping("/contests/{postId}")
	public ResponseEntity<?> getContest(@PathVariable Long postId) {

		ContestResponse contest = contestService.findByPostId(postId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(contest);
	}

	/**
	 * 사진 조회
	 *
	 * @param fileName 사진 정보
	 * @return 사진 경로
	 * @throws MalformedURLException 사진 정보를 찾지 못했을 경우
	 */
	@GetMapping("/posts/images/{fileName}")
	public Resource getImage(@PathVariable String fileName) throws MalformedURLException {
		return postService.getImageResource(fileName);
	}

	/**
	 * 사진 저장
	 *
	 * @param images 이미지 정보
	 * @return 저장된 사진 정보 리스트
	 */
	@PostMapping("/posts/images")
	public ResponseEntity<List<String>> saveImage(List<MultipartFile> images) {
		List<String> imagesName = postService.saveImage(images);
		return ResponseEntity.status(HttpStatus.OK)
				.body(imagesName);
	}

	/**
	 * 게시글 저장
	 *
	 * @param postRequest   게시글 정보
	 * @param bindingResult 에러 검증
	 * @return 처리에 대한 결과
	 */
	@PostMapping("/posts")
	public ResponseEntity<?> savePost(@Valid @RequestBody PostRequest postRequest,
			BindingResult bindingResult) {

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
	 * @param postId        게시글 정보
	 * @param postRequest   업데이트 정보
	 * @param bindingResult 에러 검증
	 * @return 처리 결과
	 */
	@PatchMapping("/posts/{postId}")
	public ResponseEntity<?> updatePost(@PathVariable Long postId,
			@Valid @RequestBody PostRequest postRequest, BindingResult bindingResult) {

		// 검증
		ResponseEntity<PostFailResponse> BAD_REQUEST = validCheck(postRequest, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		postService.updatePost(postId, postRequest);

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
	@DeleteMapping("/posts/{postId}")
	public ResponseEntity<SimpleResponse> deletePost(@PathVariable Long postId, String userId) {
		postService.deletePost(postId, userId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("게시글 삭제에 성공하였습니다.")
						.build());
	}

	/**
	 * 게시글 + 공모전 저장
	 *
	 * @param request       게시글 + 공모전 정보
	 * @param bindingResult 에러 검증
	 * @return 처리에 대한 결과
	 */
	@PostMapping("/contests")
	public ResponseEntity<?> saveContest(@Valid @RequestBody PostAndContestRequest request,
			BindingResult bindingResult) {

		// 검증
		ResponseEntity<PostFailResponse> BAD_REQUEST = validCheckContest(request, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		contestService.save(request.getPostRequest(), request.getContestRequest());

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("공모전 등록에 성공했습니다.")
						.build());
	}

	/**
	 * 게시글 + 공모전 수정
	 *
	 * @param postId        게시글 정보
	 * @param request       게시글 + 공모전 업데이트 정보
	 * @param bindingResult 에러 검증
	 * @return 처리 결과
	 */
	@PatchMapping("/contests/{postId}")
	public ResponseEntity<?> updateContest(@PathVariable Long postId,
			@Valid @RequestBody PostAndContestRequest request, BindingResult bindingResult) {

		// 검증
		ResponseEntity<PostFailResponse> BAD_REQUEST = validCheckContest(request, bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		contestService.update(postId, request.getPostRequest(), request.getContestRequest());

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("공모전 수정에 성공했습니다.")
						.build());
	}

	/**
	 * 게시글 + 공모전 삭제
	 *
	 * @param postId 게시글 정보
	 * @param userId 요청자 정보
	 * @return 처리 결과
	 */
	@DeleteMapping("/contests/{postId}")
	public ResponseEntity<SimpleResponse> deleteContest(@PathVariable Long postId, String userId) {
		contestService.delete(postId, userId);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("공모전 삭제에 성공하였습니다.")
						.build());
	}

	/**
	 * 참여한 공모전 리스트를 출력하는 메서드
	 *
	 * @param userId 회원ID
	 * @return 리스트
	 */
	@GetMapping("/contests/join/{userId}")
	public ResponseEntity<List<JoinContestResponse>> findByUserId(@PathVariable String userId) {
		return ResponseEntity.status(HttpStatus.OK)
				.body(contestService.findJoinContest(userId));
	}

	/**
	 * 공모전 참여 메서드
	 *
	 * @param request 공모전 참여 요청
	 * @param bindingResult 에러 검출
	 * @return 처리결과
	 */
	@PostMapping("/contests/join")
	public ResponseEntity<SimpleResponse> joinContest(@RequestBody @Valid JoinContestRequest request,
			BindingResult bindingResult) {

		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		contestService.joinContest(request);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("공모전 참여에 성공하였습니다.")
						.build());
	}

	/**
	 * 공모전 참여 제거 메서드
	 *
	 * @param request 공모전 제거 요청
	 * @param bindingResult 에러 검출
	 * @return 처리결과
	 */
	@DeleteMapping("/contests/join")
	public ResponseEntity<SimpleResponse> removeFromJoinContest(@RequestBody @Valid JoinContestRequest request,
			BindingResult bindingResult) {

		ResponseEntity<SimpleResponse> BAD_REQUEST = validCheck(bindingResult);
		if (BAD_REQUEST != null) {
			return BAD_REQUEST;
		}

		contestService.removeFromJoinContest(request);

		return ResponseEntity.status(HttpStatus.OK)
				.body(SimpleResponse.builder()
						.msg("공모전 참여 제거에 성공하였습니다.")
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
							.postCategory(postRequest.getPostCategory())
							.errors(errors)
							.build());
		}
		return null;
	}

	/**
	 * 검증 메서드
	 *
	 * @param request       게시글 + 공모전 요청 (검증에 실패하더라도 입력한 값은 그대로 돌려준다.)
	 * @param bindingResult 검증 결과
	 * @return 검증결과 에러가 없다면 null 에러가 있다면 해당 에러를 담은 ResponseEntity 반환
	 */
	private ResponseEntity<PostFailResponse> validCheckContest(PostAndContestRequest request,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String[] errors = new String[bindingResult.getAllErrors().size()];
			int index = 0;
			for (ObjectError error : bindingResult.getAllErrors()) {
				errors[index++] = error.getDefaultMessage();
			}

			PostRequest postRequest = request.getPostRequest();

			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(PostFailResponse.builder()
							.title(postRequest.getTitle())
							.content(postRequest.getContent())
							.postCategory(postRequest.getPostCategory())
							.errors(errors)
							.build());
		}
		return null;
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
