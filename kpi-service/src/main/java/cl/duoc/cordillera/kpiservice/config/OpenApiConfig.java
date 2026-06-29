package cl.duoc.cordillera.kpiservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title       = "KPI Service — Cordillera Platform",
        version     = "1.0.0",
        description = """
            Microservicio de indicadores clave de rendimiento (KPIs).
            Calcula KPIs usando un patrón Factory Method según categoría
            (Ventas, Inventario, Logística, Rentabilidad).
            Persistencia en MySQL — base de datos: kpi_db.
            """,
        contact = @Contact(
            name  = "Equipo Cordillera",
            email = "dev@duoc.cl"
        ),
        license = @License(name = "MIT")
    ),
    servers = {
        @Server(url = "http://localhost:8084", description = "Desarrollo local"),
        @Server(url = "http://kpi-service:8084",  description = "Docker Compose")
    }
)
@Configuration
public class OpenApiConfig {
}
