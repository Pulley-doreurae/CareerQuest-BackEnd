package pulleydoreurae.careerquestbackend.common.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 입출력을 담당하는 Service
 *
 * @author : parkjihyeok
 * @since : 2024/04/10
 */
@Slf4j
@Service
public class FileManagementService {

	/**
	 * 파일 저장 메서드
	 *
	 * @param file 전달받은 이미지
	 * @return 저장에 성공하면 true, 실패하면 false 리턴
	 */
	public String saveFile(MultipartFile file, String PATH) {
		String uuid = UUID.randomUUID().toString();
		String fileName = uuid + ":" + file.getOriginalFilename();
		String filePath = PATH + fileName;
		File imageDestination = new File(filePath);

		try {
			file.transferTo(imageDestination);
		} catch (IOException e) {
			log.error("이미지 업로드 실패 : {}", e.getMessage());
			return null;
		}
		return fileName;
	}

	/**
	 * 파일 삭제 메서드
	 *
	 * @param fileNames 저장된 파일명 리스트
	 */
	public void deleteFile(List<String> fileNames, String PATH) {
		fileNames.forEach(fileName -> {
			if (!new File(PATH + fileName).delete()) {
				// 파일 삭제에 실패하면 로그로 남기고 나중에 스크립트로 로그를 확인하고 삭제하기
				log.error("파일 삭제 실패 fileName = {}", fileName);
			}
		});
	}
}
