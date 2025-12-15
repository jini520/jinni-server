package site.jejinni.server.service.project;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.jejinni.server.domain.entity.project.Project;
import site.jejinni.server.domain.entity.project.ProjectContent;
import site.jejinni.server.dto.common.ApiResponse;
import site.jejinni.server.dto.project.ProjectContentDto;
import site.jejinni.server.dto.project.ProjectContentRequestDto;
import site.jejinni.server.dto.project.ProjectDetailDto;
import site.jejinni.server.dto.project.ProjectListItemDto;
import site.jejinni.server.dto.project.ProjectListDto;
import site.jejinni.server.dto.project.ProjectRequestDto;
import site.jejinni.server.repository.project.ProjectContentRepository;
import site.jejinni.server.repository.project.ProjectRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectContentRepository projectContentRepository;

	public ApiResponse<ProjectListDto> getProjectList(Pageable pageable) {
		Page<Project> projects = projectRepository.findAllByOrderByOrderAsc(pageable);
		Page<ProjectListItemDto> dtoPage = projects.map(this::toListDto);

		ProjectListDto data = ProjectListDto.builder()
				.items(dtoPage.getContent())
				.totalPages(dtoPage.getTotalPages())
				.totalElements(dtoPage.getTotalElements())
				.size(dtoPage.getSize())
				.number(dtoPage.getNumber())
				.first(dtoPage.isFirst())
				.last(dtoPage.isLast())
				.build();

		return new ApiResponse<>(data);
	}

	@Transactional
	public ApiResponse<ProjectDetailDto> createProject(ProjectRequestDto request) {
		Project project = Project.builder()
				.title(request.getTitle())
				.description(request.getDescription())
				.skills(request.getSkills())
				.participants(request.getParticipants())
				.period(request.getPeriod())
				.order(request.getOrder())
				.build();

		project = projectRepository.save(project);

		ProjectDetailDto data = ProjectDetailDto.builder()
				.id(project.getId())
				.title(project.getTitle())
				.description(project.getDescription())
				.skills(project.getSkills())
				.participants(project.getParticipants())
				.period(project.getPeriod())
				.order(project.getOrder())
				.contents(List.of()) // 새로 생성된 프로젝트는 컨텐츠가 없음
				.build();

		return new ApiResponse<>(data);
	}

	public ApiResponse<ProjectDetailDto> getProjectDetail(UUID projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

		List<ProjectContent> contents = projectContentRepository.findByProjectIdOrderByOrderAsc(projectId);

		ProjectDetailDto data = ProjectDetailDto.builder()
				.id(project.getId())
				.title(project.getTitle())
				.description(project.getDescription())
				.skills(project.getSkills())
				.participants(project.getParticipants())
				.period(project.getPeriod())
				.order(project.getOrder())
				.contents(contents.stream()
						.map(this::toContentDto)
						.collect(Collectors.toList()))
				.build();

		return new ApiResponse<>(data);
	}

	@Transactional
	public ApiResponse<ProjectDetailDto> updateProject(UUID projectId, ProjectRequestDto request) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

		if (request.getTitle() != null) {
			project.updateTitle(request.getTitle());
		}
		if (request.getDescription() != null) {
			project.updateDescription(request.getDescription());
		}
		if (request.getSkills() != null) {
			project.updateSkills(request.getSkills());
		}
		if (request.getParticipants() != null) {
			project.updateParticipants(request.getParticipants());
		}
		if (request.getPeriod() != null) {
			project.updatePeriod(request.getPeriod());
		}
		if (request.getOrder() != null) {
			project.updateOrder(request.getOrder());
		}

		List<ProjectContent> contents = projectContentRepository.findByProjectIdOrderByOrderAsc(projectId);

		ProjectDetailDto data = ProjectDetailDto.builder()
				.id(project.getId())
				.title(project.getTitle())
				.description(project.getDescription())
				.skills(project.getSkills())
				.participants(project.getParticipants())
				.period(project.getPeriod())
				.order(project.getOrder())
				.contents(contents.stream()
						.map(this::toContentDto)
						.collect(Collectors.toList()))
				.build();

		return new ApiResponse<>(data);
	}

	private ProjectListItemDto toListDto(Project project) {
		return ProjectListItemDto.builder()
				.id(project.getId())
				.title(project.getTitle())
				.description(project.getDescription())
				.skills(project.getSkills())
				.period(project.getPeriod())
				.order(project.getOrder())
				.build();
	}

	@Transactional
	public ApiResponse<ProjectContentDto> createProjectContent(ProjectContentRequestDto request) {
		Project project = projectRepository.findById(request.getProjectId())
				.orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + request.getProjectId()));

		ProjectContent parent = null;
		if (request.getParentId() != null) {
			parent = projectContentRepository.findById(request.getParentId())
					.orElseThrow(
							() -> new IllegalArgumentException("Parent content not found with id: " + request.getParentId()));

			// 부모 컨텐츠가 같은 프로젝트에 속하는지 확인
			if (!parent.getProject().getId().equals(project.getId())) {
				throw new IllegalArgumentException("Parent content does not belong to the same project");
			}
		}

		ProjectContent content = ProjectContent.builder()
				.project(project)
				.parent(parent)
				.children(request.getChildren())
				.order(request.getOrder())
				.content(request.getContent())
				.build();

		content = projectContentRepository.save(content);

		return new ApiResponse<>(toContentDto(content));
	}

	@Transactional
	public ApiResponse<ProjectContentDto> updateProjectContent(UUID contentId, ProjectContentRequestDto request) {
		ProjectContent content = projectContentRepository.findById(contentId)
				.orElseThrow(() -> new IllegalArgumentException("Project content not found with id: " + contentId));

		if (request.getContent() != null) {
			content.updateContent(request.getContent());
		}
		if (request.getOrder() != null) {
			content.updateOrder(request.getOrder());
		}
		if (request.getChildren() != null) {
			content.updateChildren(request.getChildren());
		}
		if (request.getParentId() != null) {
			ProjectContent parent = projectContentRepository.findById(request.getParentId())
					.orElseThrow(
							() -> new IllegalArgumentException("Parent content not found with id: " + request.getParentId()));

			// 부모 컨텐츠가 같은 프로젝트에 속하는지 확인
			if (!parent.getProject().getId().equals(content.getProject().getId())) {
				throw new IllegalArgumentException("Parent content does not belong to the same project");
			}

			// 자기 자신을 부모로 설정하는 것 방지
			if (parent.getId().equals(contentId)) {
				throw new IllegalArgumentException("Cannot set itself as parent");
			}

			content.setParent(parent);
		} else if (request.getParentId() == null && content.getParent() != null) {
			// parentId가 null로 전달되면 부모 제거
			content.setParent(null);
		}

		return new ApiResponse<>(toContentDto(content));
	}

	@Transactional
	public void deleteProjectContent(UUID contentId) {
		ProjectContent content = projectContentRepository.findById(contentId)
				.orElseThrow(() -> new IllegalArgumentException("Project content not found with id: " + contentId));

		// 자식 컨텐츠가 있는지 확인
		if (content.getChildren() != null && content.getChildren().length > 0) {
			throw new IllegalStateException("Cannot delete content: Child contents exist");
		}

		projectContentRepository.delete(content);
	}

	private ProjectContentDto toContentDto(ProjectContent content) {
		return ProjectContentDto.builder()
				.id(content.getId())
				.parentId(content.getParent() != null ? content.getParent().getId() : null)
				.order(content.getOrder())
				.content(content.getContent())
				.children(content.getChildren() != null ? content.getChildren() : new UUID[0])
				.build();
	}
}
