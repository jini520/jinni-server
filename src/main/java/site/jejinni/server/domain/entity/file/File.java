package site.jejinni.server.domain.entity.file;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import site.jejinni.server.domain.entity.BaseEntity;
import site.jejinni.server.service.file.FileType;

import java.util.UUID;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseEntity {

  @Id
  @Column(name = "file_id", columnDefinition = "UUID")
  private UUID id;

  @Column(name = "original_file_name", nullable = false, length = 500)
  private String originalFileName;

  @Column(name = "file_size", nullable = false)
  private Long fileSize;

  @Column(name = "content_type", length = 200)
  private String contentType;

  @JdbcTypeCode(SqlTypes.VARCHAR)
  @Column(name = "file_type", nullable = false, length = 50)
  @Enumerated(EnumType.STRING)
  private FileType fileType;

  @Builder
  public File(String originalFileName, Long fileSize, String contentType, FileType fileType) {
    this.originalFileName = originalFileName;
    this.fileSize = fileSize;
    this.contentType = contentType;
    this.fileType = fileType;
  }

  // 파일 시스템에서 생성된 UUID를 설정하기 위한 메서드
  public void setId(UUID id) {
    this.id = id;
  }

  public void updateFile(String originalFileName, Long fileSize, String contentType) {
    this.originalFileName = originalFileName;
    this.fileSize = fileSize;
    this.contentType = contentType;
  }
}
