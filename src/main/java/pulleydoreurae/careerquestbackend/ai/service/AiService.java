package pulleydoreurae.careerquestbackend.ai.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import pulleydoreurae.careerquestbackend.ai.dto.AiRequest;
import pulleydoreurae.careerquestbackend.ai.dto.AiResponse;

/**
 * AI를 담당하는 Service
 *
 * @author : parkjihyeok
 * @since : 2024/06/02
 */
@Slf4j
@Service
public class AiService {

	@Value("${AI_METADATA_PATH}")
	private String AI_PATH;

	private final Gson gson = new Gson();

	/**
	 * AI에 질의하고 그 결과를 반환하는 메서드
	 * @param request AI 질의 요청
	 * @return AI 질의 결과를 담은 응답
	 */
	public AiResponse findResult(AiRequest request) {

		String[] cmd = {"/bin/sh", "-c", AI_PATH + " " + request.getDatabase() + " " + request.getUserId()};

		ProcessBuilder processBuilder = new ProcessBuilder(cmd);
		processBuilder.redirectErrorStream(true); // 표준 오류를 표준 출력과 합침

		try {
			Process process = processBuilder.start(); // 프로세스 시작

			// 프로세스의 출력 결과를 읽기 위한 BufferedReader 생성
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line).append("\n");
			}

			// 프로세스 종료 상태 확인
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new IllegalStateException("AI 질의 실행에 실패했습니다. 종료 코드: " + exitCode);
			}

			// 결과 반환
			AiResponse aiResponse = gson.fromJson(String.valueOf(result), AiResponse.class);
			if (aiResponse.getName() != null) {
				return aiResponse;
			} else {
				throw new IllegalStateException("AI 질의에 실패했습니다.");
			}
		} catch (IOException | InterruptedException e) {
			log.error("AI 질의에 실패했습니다. : {}", e.getMessage());
			throw new IllegalStateException("AI 질의에 실패했습니다." + e);
		}
	}
}
