package cl.duoc.cordillera.bffgateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReportesProxyController {

    private final RestTemplate restTemplate;

    @Value("${services.report.url}")
    private String reportServiceUrl;

    public ReportesProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return restTemplate.exchange(
                reportesUrl(),
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    @PostMapping("/generar")
    public ResponseEntity<Object> generar(@RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(reportesUrl() + "/generar", payload, Object.class);
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(reportesUrl(), payload, Object.class);
    }

    @GetMapping("/{id}/exportar")
    public ResponseEntity<byte[]> exportar(
            @PathVariable Long id,
            @RequestParam(defaultValue = "pdf") String formato
    ) {
        String url = UriComponentsBuilder
                .fromUriString(reportesUrl() + "/" + id + "/exportar")
                .queryParam("formato", formato)
                .toUriString();

        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                byte[].class
        );

        HttpHeaders headers = new HttpHeaders();
        if (response.getHeaders().getContentType() != null) {
            headers.setContentType(response.getHeaders().getContentType());
        }

        List<String> disposition = response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION);
        if (disposition != null) {
            headers.put(HttpHeaders.CONTENT_DISPOSITION, disposition);
        }

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(headers)
                .body(response.getBody());
    }

    private String reportesUrl() {
        return reportServiceUrl.replaceAll("/+$", "") + "/api/reportes";
    }
}
