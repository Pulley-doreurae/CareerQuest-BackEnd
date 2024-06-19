package pulleydoreurae.careerquestbackend.ai.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import pulleydoreurae.careerquestbackend.ai.dto.AiRequest;

/**
 * @author : parkjihyeok
 * @since : 2024/06/02
 */
@SpringBootTest
@DisplayName("AI 서비스 테스트")
class AiServiceTest {

	@Autowired
	private AiService aiService;

	@Test
	@DisplayName("AI 스크립트 실행 테스트")
	public void testFindResult() {
		// Given
		AiRequest request = new AiRequest("user1", "mbti_vector");

		// When

		// Then
		assertDoesNotThrow(() -> aiService.findResult(request));
	}
}
