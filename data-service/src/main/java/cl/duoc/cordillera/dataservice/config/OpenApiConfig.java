package cl.duoc.cordillera.dataservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title       = "Data Service — Cordillera Platform",
        version     = "1.0.0",
        description = """
            Microservicio de datos operacionales.
            Gestiona la entidad Dato (registros con sistema de origen y sucursal).
            Persistencia en MySQL — base de datos: data_db.
            """,
        contact = @Contact(
            name  = "Equipo Cordillera",
            email = "dev@duoc.cl"
        ),
        license = @License(name = "MIT")
    ),
    servers = {
        @Server(url = "http://localhost:8083", description = "Desarrollo local"),
        @Server(url = "http://data-service:8083",  description = "Docker Compose")
    }
)
@Configuration
public class OpenApiConfig {
}
