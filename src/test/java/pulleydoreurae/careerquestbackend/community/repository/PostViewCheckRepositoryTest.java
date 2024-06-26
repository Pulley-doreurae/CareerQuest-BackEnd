package pulleydoreurae.careerquestbackend.community.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import pulleydoreurae.careerquestbackend.community.domain.entity.PostViewCheck;

/**
 * @author : parkjihyeok
 * @since : 2024/04/05
 */
@SpringBootTest
@DisplayName("조회수 중복을 방지하기 위한 Repository 테스트")
class PostViewCheckRepositoryTest {

	@Autowired
	PostViewCheckRepository postViewCheckRepository;

	@Test
	@DisplayName("1. 방문한 게시글이 정상적으로 저장되는지 테스트")
	void saveTest() {
		// Given
		String name = "testId";
		// When
		postViewCheckRepository.save(new PostViewCheck(name, 1L));

		// Then
		PostViewCheck result = postViewCheckRepository.findById(name).get();
		assertAll(
				() -> assertEquals(name, result.getUserId()),
				() -> assertEquals(1, result.getPostId())
		);
	}
}