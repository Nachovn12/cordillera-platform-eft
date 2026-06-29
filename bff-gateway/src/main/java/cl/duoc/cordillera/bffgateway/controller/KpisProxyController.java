package cl.duoc.cordillera.bffgateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Proxy BFF → KPI Service.
 * Expone /api/v1/kpis/** como único punto de acceso al KPI Service (puerto 8084).
 * El frontend nunca accede directamente al microservicio.
 */
@Tag(name = "KPIs (proxy)", description = "Proxy transparente hacia kpi-service. CRUD de indicadores clave de rendimiento.")
@RestController
@RequestMapping("/api/v1/kpis")
public class KpisProxyController {

    private final RestTemplate restTemplate;

    @Value("${services.kpi.url}")
    private String kpiServiceUrl;

    public KpisProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String EJEMPLO_KPI_LISTA = """
            [
              {
                "id": 1,
                "nombre": "Ventas Totales",
                "valor": 380000.00,
                "unidad": "CLP",
                "categoria": "Ventas",
                "estado": "Activo"
              },
              {
                "id": 2,
                "nombre": "Nivel de Stock",
                "valor": 87.50,
                "unidad": "%",
                "categoria": "Inventario",
                "estado": "Activo"
              }
            ]""";

    private static final String EJEMPLO_KPI = """
            {
              "id": 1,
              "nombre": "Ventas Totales",
              "valor": 380000.00,
              "unidad": "CLP",
              "categoria": "Ventas",
              "estado": "Activo"
            }""";

    private static final String EJEMPLO_KPI_REQUEST = """
            {
              "nombre": "Ventas Totales",
              "valor": 380000.00,
              "unidad": "CLP",
              "categoria": "Ventas",
              "estado": "Activo"
            }""";

    @Operation(summary = "Listar todos los KPIs")
    @ApiResponse(responseCode = "200", description = "Lista de KPIs",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Lista de KPIs", value = EJEMPLO_KPI_LISTA)))
    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return restTemplate.exchange(kpisUrl(), HttpMethod.GET, null, Object.class);
    }

    @Operation(summary = "Buscar KPI por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "KPI encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "KPI encontrado", value = EJEMPLO_KPI))),
        @ApiResponse(responseCode = "404", description = "KPI no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(
            @Parameter(description = "ID del KPI", required = true, example = "1") @PathVariable Long id) {
        return restTemplate.exchange(kpisUrl() + "/" + id, HttpMethod.GET, null, Object.class);
    }

    @Operation(summary = "Crear KPI")
    @ApiResponse(responseCode = "201", description = "KPI creado",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "KPI creado", value = EJEMPLO_KPI)))
    @PostMapping
    public ResponseEntity<Object> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(name = "Nuevo KPI", value = EJEMPLO_KPI_REQUEST)))
            @RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(kpisUrl(), payload, Object.class);
    }

    @Operation(summary = "Actualizar KPI")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "KPI actualizado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "KPI actualizado", value = EJEMPLO_KPI))),
        @ApiResponse(responseCode = "404", description = "KPI no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @Parameter(description = "ID del KPI", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(name = "KPI actualizado", value = EJEMPLO_KPI_REQUEST)))
            @RequestBody Map<String, Object> payload) {
        return restTemplate.exchange(
                kpisUrl() + "/" + id,
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(payload),
                Object.class
        );
    }

    @Operation(summary = "Eliminar KPI")
    @ApiResponse(responseCode = "204", description = "KPI eliminado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del KPI", required = true, example = "1") @PathVariable Long id) {
        restTemplate.delete(kpisUrl() + "/" + id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar KPIs por categoría", description = "Categorías válidas: Ventas, Inventario, Logística, Rentabilidad.")
    @ApiResponse(responseCode = "200", description = "KPIs de la categoría",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "KPIs Ventas", value = EJEMPLO_KPI_LISTA)))
    @GetMapping("/categoria/{cat}")
    public ResponseEntity<Object> buscarPorCategoria(
            @Parameter(description = "Categoría del KPI", required = true, example = "Ventas") @PathVariable String cat) {
        return restTemplate.exchange(kpisUrl() + "/categoria/" + cat, HttpMethod.GET, null, Object.class);
    }

    private String kpisUrl() {
        return kpiServiceUrl.replaceAll("/+$", "") + "/api/kpis";
    }
}
