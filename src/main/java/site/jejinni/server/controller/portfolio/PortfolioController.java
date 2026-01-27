package site.jejinni.server.controller.portfolio;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.jejinni.server.dto.common.ApiResponse;
import site.jejinni.server.dto.file.FileDto;
import site.jejinni.server.dto.file.FileListDto;
import site.jejinni.server.service.file.FileService;
import site.jejinni.server.service.file.FileStorageService;
import site.jejinni.server.service.file.FileType;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

  private final FileService fileService;
  private final FileStorageService fileStorageService;
  private static final FileType FILE_TYPE = FileType.PORTFOLIO;

  /**
   * 포트폴리오 리스트 조회 (Read)
   * GET /api/portfolios?page=0&size=10
   */
  @GetMapping(produces = "application/json")
  public ResponseEntity<ApiResponse<FileListDto>> getPortfolioList(
      @RequestParam(value = "page", defaultValue = "0") int page,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    FileListDto fileListDto = fileService.getFileList(FILE_TYPE, page, size);
    return ResponseEntity.ok(new ApiResponse<>(fileListDto));
  }

  /**
   * 포트폴리오 업로드 (Create)
   * POST /api/portfolios/upload
   */
  @PostMapping("/upload")
  public ResponseEntity<ApiResponse<FileDto>> uploadPortfolio(
      @RequestParam("file") MultipartFile file) {

    FileDto fileDto = fileService.uploadFile(file, FILE_TYPE);
    return ResponseEntity.ok(new ApiResponse<>(fileDto));
  }

  /**
   * 포트폴리오 다운로드 (Read)
   * GET /api/portfolios/download/{id}
   */
  @GetMapping("/download/{id}")
  public ResponseEntity<Resource> downloadPortfolio(@PathVariable UUID id) {
    Resource resource = fileStorageService.loadFileAsResource(id, FILE_TYPE);

    // 원본 파일명 가져오기 (항상 존재함)
    String originalFileName = fileStorageService.getOriginalFileName(id, FILE_TYPE);
    String resourceFilename = resource.getFilename();

    // Content-Disposition 헤더 생성 (RFC 5987 형식으로 인코딩)
    String contentDispositionValue;
    if (originalFileName != null && !originalFileName.isEmpty()) {
      // 한글 및 특수문자 처리를 위해 UTF-8 인코딩 사용
      String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8)
          .replace("+", "%20"); // 공백 문자 처리
      // ASCII 문자만 포함된 경우 filename 파라미터도 추가, 그렇지 않으면 filename*만 사용
      boolean isAsciiOnly = originalFileName.matches("^[\\x00-\\x7F]*$");
      if (isAsciiOnly) {
        contentDispositionValue = String.format(
            "attachment; filename=\"%s\"; filename*=UTF-8''%s",
            originalFileName.replace("\"", "\\\""), // 따옴표 이스케이프
            encodedFileName);
      } else {
        // 한글이 포함된 경우 filename*만 사용
        contentDispositionValue = String.format("attachment; filename*=UTF-8''%s", encodedFileName);
      }
    } else {
      // originalFileName이 null이거나 비어있을 경우 resource filename 사용
      contentDispositionValue = "attachment; filename=\"" + resourceFilename + "\"";
    }

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionValue)
        .body(resource);
  }

  /**
   * 포트폴리오 정보 조회 (Read)
   * GET /api/portfolios/{id}
   */
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<FileDto>> getPortfolioInfo(@PathVariable UUID id) {
    FileDto fileDto = fileService.getFileInfo(id, FILE_TYPE);
    return ResponseEntity.ok(new ApiResponse<>(fileDto));
  }

  /**
   * 포트폴리오 업데이트 (Update)
   * PUT /api/portfolios/{id}
   */
  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<FileDto>> updatePortfolio(
      @PathVariable UUID id,
      @RequestParam("file") MultipartFile file) {

    FileDto fileDto = fileService.updateFile(id, file, FILE_TYPE);
    return ResponseEntity.ok(new ApiResponse<>(fileDto));
  }

  /**
   * 포트폴리오 삭제 (Delete)
   * DELETE /api/portfolios/{id}
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<String>> deletePortfolio(@PathVariable UUID id) {
    fileService.deleteFile(id, FILE_TYPE);
    return ResponseEntity.ok(new ApiResponse<>("포트폴리오가 삭제되었습니다: " + id));
  }
}
