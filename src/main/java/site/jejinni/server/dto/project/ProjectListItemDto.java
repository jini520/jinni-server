package site.jejinni.server.dto.project;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ProjectListItemDto {

	private UUID id;
	private String title;
	private String description;
	private String[] skills;
	private String period;
	private Integer order;
}
