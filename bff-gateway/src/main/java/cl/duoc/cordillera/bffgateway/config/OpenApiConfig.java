package cl.duoc.cordillera.bffgateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title       = "BFF Gateway — Cordillera Platform",
        version     = "1.0.0",
        description = """
            Backend For Frontend que actúa como único punto de entrada para el frontend React.
            Proxy transparente hacia data-service (8083), kpi-service (8084) y report-service (8085).
            También expone endpoints agregados de Dashboard y Configuración.
            """,
        contact = @Contact(
            name  = "Equipo Cordillera",
            email = "dev@duoc.cl"
        ),
        license = @License(name = "MIT")
    ),
    servers = {
        @Server(url = "http://localhost:8081", description = "Desarrollo local"),
        @Server(url = "http://bff-gateway:8081", description = "Docker Compose")
    }
)
@Configuration
public class OpenApiConfig {
    // springdoc-openapi escanea automáticamente los controllers.
    // Esta clase sólo aporta metadata global a la especificación.
}
