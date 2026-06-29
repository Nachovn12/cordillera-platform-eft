package cl.duoc.cordillera.bffgateway.controller;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import cl.duoc.cordillera.bffgateway.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Dashboard", description = "Datos agregados del dashboard ejecutivo (combina KPI, Data y Report Services)")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Dashboard general", description = "Retorna métricas consolidadas: KPIs, alertas, datos de sucursales, tendencia de ventas, reportes recientes y estado de servicios.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard retornado correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardResponse.class))),
        @ApiResponse(responseCode = "503", description = "Uno o más microservicios no están disponibles", content = @Content)
    })
    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @Operation(summary = "KPIs del dashboard", description = "Retorna únicamente los indicadores KPI obtenidos desde kpi-service.")
    @ApiResponse(responseCode = "200", description = "Lista de KPIs retornada",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardResponse.class)))
    @GetMapping("/kpis")
    public ResponseEntity<DashboardResponse> getKpis() {
        return ResponseEntity.ok(dashboardService.getDashboardKpis());
    }

    @Operation(summary = "Dashboard por sucursal", description = "Retorna datos operacionales filtrados por ID de sucursal.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Datos de sucursal retornados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardResponse.class))),
        @ApiResponse(responseCode = "404", description = "Sucursal no encontrada", content = @Content)
    })
    @GetMapping("/sucursal/{id}")
    public ResponseEntity<DashboardResponse> getSucursal(
            @Parameter(description = "ID de la sucursal", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getDashboardSucursal(id));
    }

    @Operation(summary = "Alertas del sistema", description = "Retorna alertas activas, historial y heatmap de todos los microservicios.")
    @ApiResponse(responseCode = "200", description = "Alertas retornadas",
        content = @Content(mediaType = "application/json"))
    @GetMapping("/alertas")
    public ResponseEntity<Map<String, Object>> getAlertas() {
        return ResponseEntity.ok(dashboardService.getAlertas());
    }

    @Operation(summary = "Estado de servicios", description = "Retorna el estado de salud, latencia y disponibilidad de cada microservicio.")
    @ApiResponse(responseCode = "200", description = "Estado de servicios retornado",
        content = @Content(mediaType = "application/json"))
    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> getServices() {
        return ResponseEntity.ok(dashboardService.getServices());
    }
}
