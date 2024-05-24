package pulleydoreurae.careerquestbackend.community.controller;

import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
import pulleydoreurae.careerquestbackend.common.community.controller.PostController;
import pulleydoreurae.careerquestbackend.community.domain.PostCategory;
import pulleydoreurae.careerquestbackend.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.service.PostService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 게시글을 담당하는 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/03/28
 */
@RestController
@RequestMapping("/api")
public class BasicPostController extends PostController {

	public BasicPostController(@Qualifier("basicPostService") PostService postService) {
		super(postService);
	}

	@GetMapping("/posts")
	public ResponseEntity<List<PostResponse>> getPostList(@PageableDefault(size = 15) Pageable pageable) {
		return super.getPostList(pageable);
	}

	@GetMapping("/posts/category/{postCategory}")
	public ResponseEntity<List<PostResponse>> getPostListByCategory(@PathVariable PostCategory postCategory,
			@PageableDefault(size = 15) Pageable pageable) {

		return super.getPostListByCategory(postCategory, pageable);
	}

	@GetMapping("/posts/user/{userId}")
	public ResponseEntity<?> getPostListByUserId(@PathVariable String userId,
			@PageableDefault(size = 15) Pageable pageable) {

		return super.getPostListByUserId(userId, pageable);
	}

	@GetMapping("/posts/search")
	public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam(name = "keyword") String keyword,
			@RequestParam(name = "postCategory", required = false) PostCategory postCategory,
			@PageableDefault(size = 15, direction = Sort.Direction.DESC) Pageable pageable) {

		return super.searchPosts(keyword, postCategory, pageable);
	}

	@GetMapping("/posts/{postId}")
	public ResponseEntity<?> getPost(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long postId) {

		return super.getPost(request, response, postId);
	}

	@GetMapping("/posts/images/{fileName}")
	public Resource getImage(@PathVariable String fileName) throws MalformedURLException {
		return super.getImage(fileName);
	}

	@PostMapping("/posts/images")
	public ResponseEntity<List<String>> saveImage(List<MultipartFile> images) {
		return super.saveImage(images);
	}

	@PostMapping("/posts")
	public ResponseEntity<?> savePost(@Valid @RequestBody PostRequest postRequest,
			BindingResult bindingResult) {

		return super.savePost(postRequest, bindingResult);
	}

	@PatchMapping("/posts/{postId}")
	public ResponseEntity<?> updatePost(@PathVariable Long postId,
			@Valid @RequestBody PostRequest postRequest, BindingResult bindingResult) {

		return super.updatePost(postId, postRequest, bindingResult);
	}

	@DeleteMapping("/posts/{postId}")
	public ResponseEntity<SimpleResponse> deletePost(@PathVariable Long postId, String userId) {
		return super.deletePost(postId, userId);
	}
}
