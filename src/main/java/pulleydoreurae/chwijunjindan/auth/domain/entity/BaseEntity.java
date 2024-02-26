package pulleydoreurae.chwijunjindan.auth.domain.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

/**
 * 생성시간, 수정시간을 담고있을 BaseEntity
 *
 * @author : parkjihyeok
 * @since : 1/26/24
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Column(name = "created_at", updatable = false, nullable = false)
	@CreatedDate // 생성일자 자동설정
	private String createdAt; // 생성일자

	@Column(name = "modified_at", nullable = false)
	@LastModifiedDate // 마지막 수정일자 자동설정
	private String modifiedAt; // 수정일자

	// 날짜 포멧팅
	@PrePersist // 해당 엔티티를 저장하기 전에 실행
	public void onPrePersist() {
		this.createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
		this.modifiedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
	}

	// 업데이트 날짜 포멧팅
	@PreUpdate  // 해당 엔티티를 업데이트하기 전에 실행
	public void onPreUpdate() {
		this.modifiedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
	}
}
