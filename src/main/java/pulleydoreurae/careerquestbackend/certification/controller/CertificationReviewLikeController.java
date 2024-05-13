package pulleydoreurae.careerquestbackend.certification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pulleydoreurae.careerquestbackend.common.community.controller.PostLikeController;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.request.PostLikeRequest;
import pulleydoreurae.careerquestbackend.common.community.domain.dto.response.PostResponse;
import pulleydoreurae.careerquestbackend.common.community.service.PostLikeService;
import pulleydoreurae.careerquestbackend.common.dto.response.SimpleResponse;

/**
 * 자격증 좋아요 Controller
 *
 * @author : parkjihyeok
 * @since : 2024/05/13
 */
@RestController
@RequestMapping("/api")
public class CertificationReviewLikeController extends PostLikeController {

	@Autowired
	public CertificationReviewLikeController(
			@Qualifier("certificationReviewLikeService") PostLikeService postLikeService) {

		super(postLikeService);
	}

	@PostMapping("/certifications/reviews/likes")
	@Override
	public ResponseEntity<SimpleResponse> changeLikeStatus(@Valid @RequestBody PostLikeRequest postLikeRequest,
			BindingResult bindingResult) {

		return super.changeLikeStatus(postLikeRequest, bindingResult);
	}

	@GetMapping("/certifications/likes/{userId}")
	@Override
	public ResponseEntity<List<PostResponse>> findAllPostLikeByUserAccount(@PathVariable String userId,
			@PageableDefault(size = 15) Pageable pageable) {

		return super.findAllPostLikeByUserAccount(userId, pageable);
	}
}
