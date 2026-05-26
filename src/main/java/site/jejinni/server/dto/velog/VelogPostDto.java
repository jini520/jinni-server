package site.jejinni.server.dto.velog;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VelogPostDto {

    private String title;
    private String link;
    private String pubDate;
}
