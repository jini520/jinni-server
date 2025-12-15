package site.jejinni.server.repository.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.jejinni.server.domain.entity.project.Project;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

	Page<Project> findAllByOrderByOrderAsc(Pageable pageable);
}

