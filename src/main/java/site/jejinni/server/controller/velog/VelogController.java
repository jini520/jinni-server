package site.jejinni.server.controller.velog;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.jejinni.server.dto.common.ApiResponse;
import site.jejinni.server.dto.velog.VelogPostDto;
import site.jejinni.server.service.velog.VelogService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class VelogController {

    private final VelogService velogService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VelogPostDto>>> getPosts() {
        return ResponseEntity.ok(velogService.getPosts());
    }
}
