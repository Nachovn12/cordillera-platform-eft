package cl.duoc.cordillera.bffgateway.controller;

import cl.duoc.cordillera.bffgateway.dto.DashboardResponse;
import cl.duoc.cordillera.bffgateway.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardResponse> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }

    @GetMapping("/kpis")
    public ResponseEntity<DashboardResponse> getKpis() {
        return ResponseEntity.ok(dashboardService.getDashboardKpis());
    }

    @GetMapping("/sucursal/{id}")
    public ResponseEntity<DashboardResponse> getSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(dashboardService.getDashboardSucursal(id));
    }

    @GetMapping("/alertas")
    public ResponseEntity<Map<String, Object>> getAlertas() {
        return ResponseEntity.ok(dashboardService.getAlertas());
    }

    @GetMapping("/services")
    public ResponseEntity<Map<String, Object>> getServices() {
        return ResponseEntity.ok(dashboardService.getServices());
    }
}
