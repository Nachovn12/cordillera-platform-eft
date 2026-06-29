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
 * Proxy BFF → Data Service.
 * Expone /api/v1/datos/** como único punto de acceso al Data Service (puerto 8083).
 * El frontend nunca accede directamente al microservicio.
 */
@Tag(name = "Datos (proxy)", description = "Proxy transparente hacia data-service. Gestión de datos operacionales por sucursal y sistema de origen.")
@RestController
@RequestMapping("/api/v1/datos")
public class DatosProxyController {

    private final RestTemplate restTemplate;

    @Value("${services.data.url}")
    private String dataServiceUrl;

    public DatosProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String EJEMPLO_DATO_LISTA = """
            [
              {
                "id": 1,
                "sistemaOrigen": "SAP",
                "tipoDato": "Ventas",
                "valor": "450000",
                "fechaRegistro": "2026-06-10T10:30:00",
                "sucursalId": 1
              },
              {
                "id": 2,
                "sistemaOrigen": "ERP",
                "tipoDato": "Inventario",
                "valor": "12500",
                "fechaRegistro": "2026-06-10T11:00:00",
                "sucursalId": 2
              }
            ]""";

    private static final String EJEMPLO_DATO = """
            {
              "id": 1,
              "sistemaOrigen": "SAP",
              "tipoDato": "Ventas",
              "valor": "450000",
              "fechaRegistro": "2026-06-10T10:30:00",
              "sucursalId": 1
            }""";

    private static final String EJEMPLO_DATO_REQUEST = """
            {
              "sistemaOrigen": "SAP",
              "tipoDato": "Ventas",
              "valor": "450000",
              "sucursalId": 1
            }""";

    @Operation(summary = "Listar todos los datos", description = "Retorna todos los datos operacionales almacenados en data-service.")
    @ApiResponse(responseCode = "200", description = "Lista retornada",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Lista de datos", value = EJEMPLO_DATO_LISTA)))
    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return restTemplate.exchange(datosUrl(), HttpMethod.GET, null, Object.class);
    }

    @Operation(summary = "Buscar dato por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dato encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "Dato encontrado", value = EJEMPLO_DATO))),
        @ApiResponse(responseCode = "404", description = "Dato no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(
            @Parameter(description = "ID del dato", required = true, example = "1") @PathVariable Long id) {
        return restTemplate.exchange(datosUrl() + "/" + id, HttpMethod.GET, null, Object.class);
    }

    @Operation(summary = "Crear dato", description = "Crea un nuevo registro de dato operacional.")
    @ApiResponse(responseCode = "201", description = "Dato creado",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Dato creado", value = EJEMPLO_DATO)))
    @PostMapping
    public ResponseEntity<Object> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(name = "Nuevo dato", value = EJEMPLO_DATO_REQUEST)))
            @RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(datosUrl(), payload, Object.class);
    }

    @Operation(summary = "Actualizar dato")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dato actualizado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "Dato actualizado", value = EJEMPLO_DATO))),
        @ApiResponse(responseCode = "404", description = "Dato no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @Parameter(description = "ID del dato", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(name = "Datos actualizados", value = EJEMPLO_DATO_REQUEST)))
            @RequestBody Map<String, Object> payload) {
        return restTemplate.exchange(
                datosUrl() + "/" + id,
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(payload),
                Object.class
        );
    }

    @Operation(summary = "Eliminar dato")
    @ApiResponse(responseCode = "204", description = "Dato eliminado", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del dato", required = true, example = "1") @PathVariable Long id) {
        restTemplate.delete(datosUrl() + "/" + id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar datos por sistema de origen", description = "Filtra datos por el campo `sistemaOrigen` (p.ej. SAP, ERP, CRM).")
    @ApiResponse(responseCode = "200", description = "Datos filtrados",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Datos SAP", value = EJEMPLO_DATO_LISTA)))
    @GetMapping("/sistema/{origen}")
    public ResponseEntity<Object> buscarPorSistema(
            @Parameter(description = "Sistema de origen (p.ej. SAP)", required = true, example = "SAP")
            @PathVariable String origen) {
        return restTemplate.exchange(datosUrl() + "/sistema/" + origen, HttpMethod.GET, null, Object.class);
    }

    @Operation(summary = "Buscar datos por sucursal", description = "Filtra datos por ID de sucursal.")
    @ApiResponse(responseCode = "200", description = "Datos de la sucursal",
        content = @Content(mediaType = "application/json",
            examples = @ExampleObject(name = "Datos sucursal 1", value = EJEMPLO_DATO_LISTA)))
    @GetMapping("/sucursal/{id}")
    public ResponseEntity<Object> buscarPorSucursal(
            @Parameter(description = "ID de la sucursal", required = true, example = "1") @PathVariable Long id) {
        return restTemplate.exchange(datosUrl() + "/sucursal/" + id, HttpMethod.GET, null, Object.class);
    }

    private String datosUrl() {
        return dataServiceUrl.replaceAll("/+$", "") + "/api/datos";
    }
}
