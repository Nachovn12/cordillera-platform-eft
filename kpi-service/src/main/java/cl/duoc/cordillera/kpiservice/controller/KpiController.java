package cl.duoc.cordillera.kpiservice.controller;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.service.KpiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    private final KpiService kpiService;

    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    @GetMapping
    public ResponseEntity<List<Kpi>> getAll() {
        return ResponseEntity.ok(kpiService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kpi> getById(@PathVariable Long id) {
        return ResponseEntity.ok(kpiService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Kpi> create(@RequestBody Kpi kpi) {
        return ResponseEntity.status(HttpStatus.CREATED).body(kpiService.create(kpi));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Kpi> update(@PathVariable Long id, @RequestBody Kpi kpi) {
        return ResponseEntity.ok(kpiService.update(id, kpi));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        kpiService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{cat}")
    public ResponseEntity<List<Kpi>> getByCategoria(@PathVariable String cat) {
        return ResponseEntity.ok(kpiService.findByCategoria(cat));
    }
}