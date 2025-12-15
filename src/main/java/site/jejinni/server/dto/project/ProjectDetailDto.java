package site.jejinni.server.dto.project;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ProjectDetailDto {

	private UUID id;
	private String title;
	private String description;
	private String[] skills;
	private Integer participants;
	private String period;
	private Integer order;
	private List<ProjectContentDto> contents;
}
