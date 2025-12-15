package site.jejinni.server.dto.project;

import lombok.Getter;

@Getter
public class ProjectRequestDto {

  private String title;
  private String description;
  private String[] skills;
  private Integer participants;
  private String period;
  private Integer order;
}
