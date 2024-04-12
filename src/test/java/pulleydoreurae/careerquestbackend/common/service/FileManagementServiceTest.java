package pulleydoreurae.careerquestbackend.common.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

/**
 * @author : parkjihyeok
 * @since : 2024/04/10
 */
@DisplayName("파일 입출력 테스트")
class FileManagementServiceTest {

	String IMAGE_PATH = "";

	FileManagementService fileManagementService = new FileManagementService();

	@Test
	@DisplayName("파일 저장 테스트")
	void saveFileTest() throws IOException {
		// Given
		MockMultipartFile file = new MockMultipartFile("test", "Test.png", "image/png", "사진내용".getBytes());

		// When
		String fileName = fileManagementService.saveFile(file, IMAGE_PATH);

		// Then
		File saved = new File(IMAGE_PATH + fileName);
		Path result = Paths.get(IMAGE_PATH + fileName);

		assertAll(
				() -> assertArrayEquals(file.getBytes(), Files.readAllBytes(result)),
				() -> assertEquals(fileName, saved.getName()),
				() -> assertTrue(saved.exists()),
				() -> assertTrue(saved.canRead())
		);

		saved.delete(); // 파일 삭제
	}

	@Test
	@DisplayName("파일 삭제 테스트")
	void deleteFailTest() throws IOException {
		// Given
		MockMultipartFile file = new MockMultipartFile("test", "Test.png", "image/png", "사진내용".getBytes());
		String fileName = fileManagementService.saveFile(file, IMAGE_PATH);

		// When
		fileManagementService.deleteFile(List.of(fileName), IMAGE_PATH);

		// Then
		Path result = Paths.get(IMAGE_PATH + fileName);
		// 파일이 존재하지 않으므로 NoSuchFileException 예외를 던져야한다.
		assertThrows(NoSuchFileException.class, () -> Files.readAllBytes(result));
	}
}