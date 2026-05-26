package site.jejinni.server.service.velog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import site.jejinni.server.dto.common.ApiResponse;
import site.jejinni.server.dto.velog.VelogPostDto;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class VelogService {

    @Value("${velog.rss-url}")
    private String rssUrl;

    @Value("${velog.username}")
    private String username;

    private static final int LIMIT = 4;
    private static final DateTimeFormatter RSS_DATE_FMT =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public ApiResponse<List<VelogPostDto>> getPosts() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(rssUrl + "/" + username))
                    .GET()
                    .build();

            HttpResponse<InputStream> response =
                    client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(response.body());

            NodeList items = doc.getElementsByTagName("item");
            List<VelogPostDto> posts = new ArrayList<>();

            for (int i = 0; i < Math.min(items.getLength(), LIMIT); i++) {
                Element item = (Element) items.item(i);
                posts.add(VelogPostDto.builder()
                        .title(getText(item, "title"))
                        .link(getText(item, "link"))
                        .pubDate(formatDate(getText(item, "pubDate")))
                        .build());
            }

            return new ApiResponse<>(posts);
        } catch (Exception e) {
            log.error("Failed to fetch Velog posts", e);
            return new ApiResponse<>(List.of());
        }
    }

    private String getText(Element item, String tag) {
        NodeList nodes = item.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "";
        return nodes.item(0).getTextContent().trim();
    }

    private String formatDate(String raw) {
        try {
            ZonedDateTime date = ZonedDateTime.parse(raw, RSS_DATE_FMT);
            return String.format("%d.%02d", date.getYear(), date.getMonthValue());
        } catch (Exception e) {
            return raw;
        }
    }
}
