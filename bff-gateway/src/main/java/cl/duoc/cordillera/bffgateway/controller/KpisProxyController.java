package cl.duoc.cordillera.bffgateway.controller;

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
@RestController
@RequestMapping("/api/v1/kpis")
public class KpisProxyController {

    private final RestTemplate restTemplate;

    @Value("${services.kpi.url}")
    private String kpiServiceUrl;

    public KpisProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return restTemplate.exchange(
                kpisUrl(),
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        return restTemplate.exchange(
                kpisUrl() + "/" + id,
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(kpisUrl(), payload, Object.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        return restTemplate.exchange(
                kpisUrl() + "/" + id,
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(payload),
                Object.class
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        restTemplate.delete(kpisUrl() + "/" + id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categoria/{cat}")
    public ResponseEntity<Object> buscarPorCategoria(@PathVariable String cat) {
        return restTemplate.exchange(
                kpisUrl() + "/categoria/" + cat,
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    private String kpisUrl() {
        return kpiServiceUrl.replaceAll("/+$", "") + "/api/kpis";
    }
}
