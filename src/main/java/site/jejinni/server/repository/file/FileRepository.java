package site.jejinni.server.repository.file;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.jejinni.server.domain.entity.file.File;
import site.jejinni.server.service.file.FileType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

  Page<File> findByFileTypeOrderByCreatedAtDesc(FileType fileType, Pageable pageable);

  long countByFileType(FileType fileType);

  Optional<File> findByIdAndFileType(UUID id, FileType fileType);
}
