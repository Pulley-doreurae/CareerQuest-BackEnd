package pulleydoreurae.careerquestbackend.certification.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pulleydoreurae.careerquestbackend.certification.domain.entity.ReviewViewCheck;

/**
 * @author : parkjihyeok
 * @since : 2024/05/12
 */
@SpringBootTest
@DisplayName("자격증 후기 조회수 DB 테스트")
class ReviewViewCheckRepositoryTest {

	@Autowired
	ReviewViewCheckRepository reviewViewCheckRepository;

	@Test
	@DisplayName("방문한 후기가 정상적으로 저장되는지 테스트")
	void saveTest() {
		// Given
		String name = "testId";
		// When
		reviewViewCheckRepository.save(new ReviewViewCheck(name, 1L));

		// Then
		ReviewViewCheck result = reviewViewCheckRepository.findById(name).get();
		assertAll(
				() -> assertEquals(name, result.getUserId()),
				() -> assertEquals(1, result.getReviewId())
		);
	}
}
