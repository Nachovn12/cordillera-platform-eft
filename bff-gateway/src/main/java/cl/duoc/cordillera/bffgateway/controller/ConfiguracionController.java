package cl.duoc.cordillera.bffgateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerConfiguracion() {
        return ResponseEntity.ok(defaultConfig());
    }

    @PutMapping
    public ResponseEntity<Map<String, Object>> actualizarConfiguracion(@RequestBody Map<String, Object> payload) {
        Map<String, Object> response = defaultConfig();
        response.put("parametros", payload);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> defaultConfig() {
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("parametros", Map.of(
                "periodo", "Mayo 2026",
                "sucursal", "Todas las sucursales",
                "gateway", "Operativo"
        ));
        config.put("integraciones", List.of(
                item("bff", "BFF Gateway", "Rutas ejecutivas disponibles", "Operativo", "Backend For Frontend"),
                item("mysql", "MySQL/MariaDB", "Persistencia via MySQL Docker", "Operativo", "Base de datos")
        ));
        config.put("usuarios", List.of(
                item("admin", "Administrador", "Perfil de presentacion", "Activo", "Usuario")
        ));
        config.put("perfiles", List.of(
                item("ejecutivo", "Ejecutivo", "Acceso a dashboard, KPIs y reportes", "Activo", "Rol")
        ));
        return config;
    }

    private Map<String, Object> item(String id, String title, String description, String status, String type) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", id);
        item.put("titulo", title);
        item.put("descripcion", description);
        item.put("estado", status);
        item.put("tipo", type);
        return item;
    }
}
