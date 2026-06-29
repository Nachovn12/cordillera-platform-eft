package cl.duoc.cordillera.kpiservice.controller;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.service.KpiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "KPIs", description = "CRUD de indicadores clave de rendimiento. Categorías: Ventas, Inventario, Logística, Rentabilidad.")
@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    private final KpiService kpiService;

    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    private static final String EJEMPLO_KPI = """
            {
              "id": 1,
              "nombre": "Ventas Totales",
              "valor": 380000.00,
              "unidad": "CLP",
              "categoria": "Ventas",
              "estado": "Activo"
            }""";

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
              },
              {
                "id": 3,
                "nombre": "Eficiencia Logística",
                "valor": 94.20,
                "unidad": "%",
                "categoria": "Logística",
                "estado": "Activo"
              },
              {
                "id": 4,
                "nombre": "Margen Rentabilidad",
                "valor": 18.50,
                "unidad": "%",
                "categoria": "Rentabilidad",
                "estado": "Activo"
              }
            ]""";

    private static final String EJEMPLO_KPI_REQUEST = """
            {
              "nombre": "Ventas Totales",
              "valor": 380000.00,
              "unidad": "CLP",
              "categoria": "Ventas",
              "estado": "Activo"
            }""";

    private static final String EJEMPLO_404 = """
            {
              "status": 404,
              "error": "Not Found",
              "message": "KPI con id 99 no encontrado"
            }""";

    @Operation(summary = "Listar todos los KPIs")
    @ApiResponse(responseCode = "200", description = "Lista de KPIs",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Kpi.class)),
            examples = @ExampleObject(name = "Lista de KPIs", value = EJEMPLO_KPI_LISTA)))
    @GetMapping
    public ResponseEntity<List<Kpi>> getAll() {
        return ResponseEntity.ok(kpiService.findAll());
    }

    @Operation(summary = "Buscar KPI por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "KPI encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Kpi.class),
                examples = @ExampleObject(name = "KPI encontrado", value = EJEMPLO_KPI))),
        @ApiResponse(responseCode = "404", description = "KPI no encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "No encontrado", value = EJEMPLO_404)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Kpi> getById(
            @Parameter(description = "ID del KPI", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(kpiService.findById(id));
    }

    @Operation(summary = "Crear KPI", description = "El campo `categoria` determina la calculadora Factory a utilizar (Ventas, Inventario, Logística, Rentabilidad).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "KPI creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Kpi.class),
                examples = @ExampleObject(name = "KPI creado", value = EJEMPLO_KPI))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Kpi> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Campos requeridos: nombre, valor, unidad, categoria, estado",
                required = true,
                content = @Content(schema = @Schema(implementation = Kpi.class),
                    examples = @ExampleObject(name = "Nuevo KPI", value = EJEMPLO_KPI_REQUEST)))
            @RequestBody Kpi kpi) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kpiService.create(kpi));
    }

    @Operation(summary = "Actualizar KPI")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "KPI actualizado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Kpi.class),
                examples = @ExampleObject(name = "KPI actualizado", value = EJEMPLO_KPI))),
        @ApiResponse(responseCode = "404", description = "KPI no encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "No encontrado", value = EJEMPLO_404)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Kpi> update(
            @Parameter(description = "ID del KPI", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                content = @Content(examples = @ExampleObject(name = "KPI actualizado", value = EJEMPLO_KPI_REQUEST)))
            @RequestBody Kpi kpi) {
        return ResponseEntity.ok(kpiService.update(id, kpi));
    }

    @Operation(summary = "Eliminar KPI")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "KPI eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "KPI no encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "No encontrado", value = EJEMPLO_404)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del KPI", required = true, example = "1") @PathVariable Long id) {
        kpiService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar KPIs por categoría", description = "Valores válidos: Ventas, Inventario, Logística, Rentabilidad.")
    @ApiResponse(responseCode = "200", description = "KPIs de la categoría",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Kpi.class)),
            examples = @ExampleObject(name = "KPIs categoría Ventas", value = """
                    [
                      {
                        "id": 1,
                        "nombre": "Ventas Totales",
                        "valor": 380000.00,
                        "unidad": "CLP",
                        "categoria": "Ventas",
                        "estado": "Activo"
                      }
                    ]""")))
    @GetMapping("/categoria/{cat}")
    public ResponseEntity<List<Kpi>> getByCategoria(
            @Parameter(description = "Categoría del KPI", required = true, example = "Ventas") @PathVariable String cat) {
        return ResponseEntity.ok(kpiService.findByCategoria(cat));
    }
}