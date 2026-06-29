package cl.duoc.cordillera.bffgateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Configuración", description = "Parámetros, integraciones, usuarios y perfiles del sistema")
@RestController
@RequestMapping("/api/configuracion")
public class ConfiguracionController {

    @Operation(summary = "Obtener configuración", description = "Retorna los parámetros globales de la plataforma, integraciones activas, usuarios y perfiles.")
    @ApiResponse(responseCode = "200", description = "Configuración retornada",
        content = @Content(mediaType = "application/json"))
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerConfiguracion() {
        return ResponseEntity.ok(defaultConfig());
    }

    @Operation(summary = "Actualizar configuración", description = "Reemplaza el bloque de parámetros con los valores enviados en el body.")
    @ApiResponse(responseCode = "200", description = "Configuración actualizada",
        content = @Content(mediaType = "application/json"))
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
