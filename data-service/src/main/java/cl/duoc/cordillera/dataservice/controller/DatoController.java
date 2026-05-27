package cl.duoc.cordillera.dataservice.controller;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.service.DatoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datos")
public class DatoController {

    private final DatoService datoService;

    public DatoController(DatoService datoService) {
        this.datoService = datoService;
    }

    @GetMapping
    public ResponseEntity<List<Dato>> listarTodos() {
        return ResponseEntity.ok(datoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dato> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(datoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Dato> crear(@Valid @RequestBody Dato dato) {
        return ResponseEntity.status(HttpStatus.CREATED).body(datoService.crear(dato));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dato> actualizar(@PathVariable Long id, @Valid @RequestBody Dato dato) {
        return ResponseEntity.ok(datoService.actualizar(id, dato));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        datoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sistema/{origen}")
    public ResponseEntity<List<Dato>> buscarPorSistema(@PathVariable String origen) {
        return ResponseEntity.ok(datoService.buscarPorSistemaOrigen(origen));
    }

    @GetMapping("/sucursal/{id}")
    public ResponseEntity<List<Dato>> buscarPorSucursal(@PathVariable Long id) {
        return ResponseEntity.ok(datoService.buscarPorSucursalId(id));
    }
}