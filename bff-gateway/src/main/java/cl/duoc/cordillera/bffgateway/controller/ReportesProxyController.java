package cl.duoc.cordillera.bffgateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

/**
 * Proxy BFF → Report Service.
 * Expone /api/v1/reportes/** como único punto de acceso al Report Service (puerto 8085).
 * El frontend nunca accede directamente al microservicio.
 */
@Tag(name = "Reportes (proxy)", description = "Proxy transparente hacia report-service. Generación y exportación de reportes ejecutivos.")
@RestController
@RequestMapping("/api/v1/reportes")
public class ReportesProxyController {

    private final RestTemplate restTemplate;

    @Value("${services.report.url}")
    private String reportServiceUrl;

    public ReportesProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String EJEMPLO_REPORTE_LISTA = """
            [
              {
                "id": 1,
                "titulo": "Reporte Mensual Ventas Mayo",
                "tipo": "Mensual",
                "area": "Ventas",
                "valor": 380000.00,
                "fechaGeneracion": "2026-06-10T09:00:00"
              },
              {
                "id": 2,
                "titulo": "Reporte Logística Q2",
                "tipo": "Trimestral",
                "area": "Logística",
                "valor": 94200.00,
                "fechaGeneracion": "2026-06-10T09:30:00"
              }
            ]""";

    private static final String EJEMPLO_REPORTE = """
            {
              "id": 1,
              "titulo": "Reporte Mensual Ventas Mayo",
              "tipo": "Mensual",
              "area": "Ventas",
              "valor": 380000.00,
              "fechaGeneracion": "2026-06-10T09:00:00"
            }""";

    private static final String EJEMPLO_REPORTE_REQUEST = """
            {
              "titulo": "Reporte Mensual Ventas Mayo",
              "tipo": "Mensual",
              "area": "Ventas",
              "valor": 380000.00
            }""";

    @Operation(summary = "Listar todos los reportes")
    @ApiResponse(responseCode = "200", description = "Lista de reportes",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Lista de reportes", value = EJEMPLO_REPORTE_LISTA)))
    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return restTemplate.exchange(reportesUrl(), HttpMethod.GET, null, Object.class);
    }

    @Operation(summary = "Generar reporte ejecutivo", description = "Genera un reporte a partir de los datos enviados en el body.")
    @ApiResponse(responseCode = "201", description = "Reporte generado",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Reporte generado", value = EJEMPLO_REPORTE)))
    @PostMapping("/generar")
    public ResponseEntity<Object> generar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(name = "Datos para generar", value = EJEMPLO_REPORTE_REQUEST)))
            @RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(reportesUrl() + "/generar", payload, Object.class);
    }

    @Operation(summary = "Crear reporte")
    @ApiResponse(responseCode = "201", description = "Reporte creado",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Reporte creado", value = EJEMPLO_REPORTE)))
    @PostMapping
    public ResponseEntity<Object> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(name = "Nuevo reporte", value = EJEMPLO_REPORTE_REQUEST)))
            @RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(reportesUrl(), payload, Object.class);
    }

    @Operation(summary = "Exportar reporte", description = "Descarga el reporte en el formato especificado (pdf, excel, json).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Archivo descargado", content = @Content(mediaType = "application/octet-stream")),
        @ApiResponse(responseCode = "404", description = "Reporte no encontrado", content = @Content)
    })
    @GetMapping("/{id}/exportar")
    public ResponseEntity<byte[]> exportar(
            @Parameter(description = "ID del reporte", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "Formato de exportación: pdf (default), excel, json", example = "pdf")
            @RequestParam(defaultValue = "pdf") String formato) {
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

    @Operation(summary = "Eliminar reporte", description = "Elimina un reporte por su ID.")
    @ApiResponse(responseCode = "204", description = "Reporte eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del reporte", required = true, example = "1") @PathVariable Long id) {
        return restTemplate.exchange(
                reportesUrl() + "/" + id,
                HttpMethod.DELETE,
                null,
                Void.class
        );
    }

    private String reportesUrl() {
        return reportServiceUrl.replaceAll("/+$", "") + "/api/reportes";
    }
}
