# KPI Service - Cordillera Platform

Microservicio responsable de gestionar y consultar indicadores KPI de **Cordillera Platform**, correspondiente al Parcial 2 de la asignatura **Desarrollo Full Stack III**.

## Descripción

`kpi-service` permite registrar, consultar, actualizar, eliminar y filtrar KPIs organizacionales utilizados por Grupo Cordillera para evaluar el desempeño del negocio.

Este servicio forma parte de una arquitectura basada en microservicios y será consumido por el **BFF Gateway**.

## Stack utilizado

- Java 21
- Spring Boot 4.0.6
- Maven
- Spring Data JPA
- MySQL
- JUnit 5 + Mockito

## Puerto
8084

## Base de datos
kpi_db

## Configuración

```properties
server.port=8084
spring.datasource.url=${KPI_DB_URL:${DB_URL:jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/kpi_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
services.data.url=${DATA_SERVICE_URL:http://localhost:8083}
```

En Docker Compose usa el servicio interno `mysql:3306` y consume `data-service:8083`. Para ejecucion local sin Docker se puede usar MySQL/MariaDB en `localhost:3306`; si el equipo tiene otra clave, definir `DB_PASSWORD` antes de ejecutar.

## Ejecución

```bash
cd kpi-service
mvn spring-boot:run
```

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/kpis | Listar todos |
| GET | /api/kpis/{id} | Buscar por ID |
| POST | /api/kpis | Crear KPI |
| PUT | /api/kpis/{id} | Actualizar |
| DELETE | /api/kpis/{id} | Eliminar |
| GET | /api/kpis/categoria/{cat} | Filtrar por categoría |

## Ejemplo JSON

```json
{
  "nombre": "Ventas Q1",
  "valor": 75000,
  "unidad": "CLP",
  "categoria": "ventas",
  "estado": "EN_PROGRESO"
}
```

## Pruebas

```bash
mvn clean test
```

## Categorías sugeridas

- ventas
- inventario
- logistica
- rentabilidad
