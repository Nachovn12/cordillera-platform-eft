# BFF Gateway - Cordillera Platform

Microservicio Backend For Frontend de **Cordillera Platform**, correspondiente al Parcial 2 de la asignatura **Desarrollo Full Stack III**.

## Descripción

`bff-gateway` actúa como punto de entrada para el frontend ejecutivo. Su responsabilidad es exponer endpoints consolidados para el dashboard y orquestar llamadas hacia los microservicios internos.

El frontend no consume directamente `data-service`, `kpi-service` ni `report-service`; consume únicamente este BFF Gateway.

## Flujo general
Frontend React + Vite → BFF Gateway → Data Service / KPI Service / Report Service

## Stack utilizado

- Java 21
- Spring Boot 4.0.6
- Maven
- Spring Web MVC
- RestTemplate

## Puerto
8081

> Nota: Se usa `8081` porque el puerto `8080` está ocupado en los computadores del instituto/TAITE.

## Microservicios integrados
Data Service:   http://localhost:8083
KPI Service:    http://localhost:8084
Report Service: http://localhost:8085

## Configuración

```properties
server.port=8081
spring.application.name=bff-gateway

services.kpi.url=${KPI_SERVICE_URL:http://localhost:8084}
services.data.url=${DATA_SERVICE_URL:http://localhost:8083}
services.report.url=${REPORT_SERVICE_URL:http://localhost:8085}

logging.level.cl.duoc.cordillera=DEBUG
```

## Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/dashboard/stats | Dashboard general |
| GET | /api/dashboard/kpis | KPIs desde KPI Service |
| GET | /api/dashboard/sucursal/{id} | Datos por sucursal desde Data Service |

## Ejecución

```bash
cd bff-gateway
mvn spring-boot:run
```

## Pruebas

```bash
mvn clean test
```

## Pruebas manuales PowerShell

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/dashboard/stats" -Method Get
Invoke-RestMethod -Uri "http://localhost:8081/api/dashboard/kpis" -Method Get
Invoke-RestMethod -Uri "http://localhost:8081/api/dashboard/sucursal/1" -Method Get
```

## Consideraciones

- Este servicio corresponde a CORD-21 — HU-BFF-01 Endpoints de dashboard.
- El frontend debe configurar `VITE_API_BASE_URL=http://localhost:8081`.

## Respuestas degradadas

El BFF Gateway entrega respuestas controladas cuando un microservicio interno no responde.

Ejemplo de respuesta operativa:

```json
{
  "statusBff": "Operativo",
  "ventasTotales": 0,
  "kpis": [],
  "alertas": []
}
```

Ejemplo de respuesta degradada:

```json
{
  "statusBff": "Degradado",
  "ventasTotales": 0,
  "kpis": [],
  "alertas": [
    "No fue posible obtener información desde KPI Service"
  ]
}
```

Esto permite que el frontend mantenga una experiencia controlada y no se rompa ante fallos parciales de `data-service` o `kpi-service`.