package pulleydoreurae.careerquestbackend.certification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pulleydoreurae.careerquestbackend.certification.domain.dto.ReviewRequest;
import pulleydoreurae.careerquestbackend.common.community.controller.PostController;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.service.PostService;

/**
 * 자격증 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@RestController
@RequestMapping("/api")
public class CertificationController extends PostController {

	public CertificationController(@Qualifier("certificationReviewService") PostService postService) {
		super(postService);
	}

	@GetMapping("/certifications/{certificationId}/reviews")
	@Override
	public ResponseEntity<List<PostResponse>> getPostListByCategory(
			@PathVariable(name = "certificationId") Long category, Pageable pageable) {

		return super.getPostListByCategory(category, pageable);
	}

	@PostMapping("/certifications/{certificationId}/reviews")
	public ResponseEntity<?> savePost(@PathVariable(name = "certificationId") Long category,
			@Valid ReviewRequest request, BindingResult bindingResult) {

		PostRequest postRequest = mackPostRequest(category, request);
		return super.savePost(postRequest, bindingResult);
	}

	/**
	 * 후기를 PostRequest 형태로 저장하기 위해 형변환
	 * @param category 자격증 구분
	 * @param request 후기 요청
	 * @return PostRequest 형태의 후기
	 */
	private PostRequest mackPostRequest(Long category, ReviewRequest request) {
		return PostRequest.builder()
				.title(request.getTitle())
				.content(request.getContent())
				.userId(request.getUserId())
				.category(category)
				.build();
	}
}
