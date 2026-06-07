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
 * Proxy BFF → Data Service.
 * Expone /api/v1/datos/** como único punto de acceso al Data Service (puerto 8083).
 * El frontend nunca accede directamente al microservicio.
 */
@RestController
@RequestMapping("/api/v1/datos")
public class DatosProxyController {

    private final RestTemplate restTemplate;

    @Value("${services.data.url}")
    private String dataServiceUrl;

    public DatosProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return restTemplate.exchange(
                datosUrl(),
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        return restTemplate.exchange(
                datosUrl() + "/" + id,
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Map<String, Object> payload) {
        return restTemplate.postForEntity(datosUrl(), payload, Object.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        return restTemplate.exchange(
                datosUrl() + "/" + id,
                HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(payload),
                Object.class
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        restTemplate.delete(datosUrl() + "/" + id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sistema/{origen}")
    public ResponseEntity<Object> buscarPorSistema(@PathVariable String origen) {
        return restTemplate.exchange(
                datosUrl() + "/sistema/" + origen,
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    @GetMapping("/sucursal/{id}")
    public ResponseEntity<Object> buscarPorSucursal(@PathVariable Long id) {
        return restTemplate.exchange(
                datosUrl() + "/sucursal/" + id,
                HttpMethod.GET,
                null,
                Object.class
        );
    }

    private String datosUrl() {
        return dataServiceUrl.replaceAll("/+$", "") + "/api/datos";
    }
}
