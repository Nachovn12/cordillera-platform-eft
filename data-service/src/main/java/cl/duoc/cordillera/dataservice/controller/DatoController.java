package cl.duoc.cordillera.dataservice.controller;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.service.DatoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Datos", description = "CRUD de datos operacionales por sucursal y sistema de origen")
@RestController
@RequestMapping("/api/datos")
public class DatoController {

    private final DatoService datoService;

    public DatoController(DatoService datoService) {
        this.datoService = datoService;
    }

    private static final String EJEMPLO_DATO = """
            {
              "id": 1,
              "sistemaOrigen": "SAP",
              "tipoDato": "Ventas",
              "valor": "450000",
              "fechaRegistro": "2026-06-10T10:30:00",
              "sucursalId": 1
            }""";

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

    private static final String EJEMPLO_DATO_REQUEST = """
            {
              "sistemaOrigen": "SAP",
              "tipoDato": "Ventas",
              "valor": "450000",
              "sucursalId": 1
            }""";

    private static final String EJEMPLO_404 = """
            {
              "status": 404,
              "error": "Not Found",
              "message": "Dato con id 99 no encontrado"
            }""";

    private static final String EJEMPLO_400 = """
            {
              "status": 400,
              "error": "Bad Request",
              "message": "El sistema de origen no puede estar vacío"
            }""";

    @Operation(summary = "Listar todos los datos")
    @ApiResponse(responseCode = "200", description = "Lista de datos retornada",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Dato.class)),
            examples = @ExampleObject(name = "Lista de datos", value = EJEMPLO_DATO_LISTA)))
    @GetMapping
    public ResponseEntity<List<Dato>> listarTodos() {
        return ResponseEntity.ok(datoService.listarTodos());
    }

    @Operation(summary = "Buscar dato por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dato encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Dato.class),
                examples = @ExampleObject(name = "Dato encontrado", value = EJEMPLO_DATO))),
        @ApiResponse(responseCode = "404", description = "Dato no encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "No encontrado", value = EJEMPLO_404)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Dato> buscarPorId(
            @Parameter(description = "ID del dato", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(datoService.buscarPorId(id));
    }

    @Operation(summary = "Crear dato", description = "Crea un nuevo registro de dato operacional. Se aplican validaciones de bean.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Dato creado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Dato.class),
                examples = @ExampleObject(name = "Dato creado", value = EJEMPLO_DATO))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "Error validación", value = EJEMPLO_400)))
    })
    @PostMapping
    public ResponseEntity<Dato> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Campos requeridos: sistemaOrigen, tipoDato, valor, sucursalId",
                required = true,
                content = @Content(schema = @Schema(implementation = Dato.class),
                    examples = @ExampleObject(name = "Nuevo dato", value = EJEMPLO_DATO_REQUEST)))
            @Valid @RequestBody Dato dato) {
        return ResponseEntity.status(HttpStatus.CREATED).body(datoService.crear(dato));
    }

    @Operation(summary = "Actualizar dato")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dato actualizado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Dato.class),
                examples = @ExampleObject(name = "Dato actualizado", value = EJEMPLO_DATO))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "Error validación", value = EJEMPLO_400))),
        @ApiResponse(responseCode = "404", description = "Dato no encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "No encontrado", value = EJEMPLO_404)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Dato> actualizar(
            @Parameter(description = "ID del dato", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody Dato dato) {
        return ResponseEntity.ok(datoService.actualizar(id, dato));
    }

    @Operation(summary = "Eliminar dato")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Dato eliminado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Dato no encontrado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(name = "No encontrado", value = EJEMPLO_404)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del dato", required = true, example = "1") @PathVariable Long id) {
        datoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar por sistema de origen", description = "Filtra datos por el campo sistemaOrigen (p.ej. SAP, ERP, CRM).")
    @ApiResponse(responseCode = "200", description = "Datos filtrados",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Dato.class)),
            examples = @ExampleObject(name = "Datos SAP", value = """
                    [
                      {
                        "id": 1,
                        "sistemaOrigen": "SAP",
                        "tipoDato": "Ventas",
                        "valor": "450000",
                        "fechaRegistro": "2026-06-10T10:30:00",
                        "sucursalId": 1
                      }
                    ]""")))
    @GetMapping("/sistema/{origen}")
    public ResponseEntity<List<Dato>> buscarPorSistema(
            @Parameter(description = "Sistema de origen", required = true, example = "SAP") @PathVariable String origen) {
        return ResponseEntity.ok(datoService.buscarPorSistemaOrigen(origen));
    }

    @Operation(summary = "Buscar por sucursal", description = "Filtra datos asociados a una sucursal específica.")
    @ApiResponse(responseCode = "200", description = "Datos de la sucursal",
        content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = Dato.class)),
            examples = @ExampleObject(name = "Datos sucursal 1", value = EJEMPLO_DATO_LISTA)))
    @GetMapping("/sucursal/{id}")
    public ResponseEntity<List<Dato>> buscarPorSucursal(
            @Parameter(description = "ID de la sucursal", required = true, example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(datoService.buscarPorSucursalId(id));
    }
}