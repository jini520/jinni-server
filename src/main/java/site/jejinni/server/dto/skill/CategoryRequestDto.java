package site.jejinni.server.dto.skill;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryRequestDto {

  @NotBlank
  private String name;

  @NotBlank
  private String nameEn;

  private Integer order;
}
