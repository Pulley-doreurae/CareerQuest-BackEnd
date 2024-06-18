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

	@Value("${AI_METADATA_PATH}")
	private String AI_PATH;

	@Value("${AI_CREATE_SELF_INTRODUCE_PATH}")
	private String AI_CREATE_SELF_INTRODUCE_PATH;

	@Autowired
	private AiService aiService;

	@Test
	@DisplayName("AI 스크립트 실행 테스트")
	public void testFindResult() {
		// Given
		AiRequest request = new AiRequest("user1", "mbti_vector");

		// When

		// Then
		assertDoesNotThrow(() -> aiService.findResult(aiService.returnCmd(request)));
	}

	@Test
	@DisplayName("CMD 변환 테스트 ")
	public void testChangeCmd() {
		// Given
		AiRequest request1 = new AiRequest("user1", "mbti_vector");
		String request2 = "testId";

		String[] returnCmd1 = {"/bin/sh", "-c", AI_PATH + " " + "mbti_vector" + " " + "user1"};
		String[] returnCmd2 = {"/bin/sh", "-c", AI_CREATE_SELF_INTRODUCE_PATH + " " + "testId" + " " + "300 글자 충족하는 자기소개서 만들어줘"};


		// When

		// Then
		assertArrayEquals(aiService.returnCmd(request1), returnCmd1);
		assertArrayEquals(aiService.returnCmd(request2), returnCmd2);

	}
}
