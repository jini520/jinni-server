package site.jejinni.server.dto.project;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ProjectContentRequestDto {

	private UUID projectId;
	private UUID parentId;
	private Integer order;
	private String content;
	private UUID[] children;
}

