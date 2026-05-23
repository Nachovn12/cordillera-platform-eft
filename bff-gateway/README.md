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

### Dashboard por sucursal

```http
GET /api/dashboard/sucursal/{id}
```

Este endpoint consulta Data Service mediante:

```http
GET /api/datos/sucursal/{id}
```

La respuesta mantiene el DTO general del dashboard, pero entrega los datos específicos de sucursal en el campo:

```json
"datosSucursal": []
```

El campo `kpis` queda vacío en esta respuesta porque la información retornada corresponde a datos operacionales de sucursal, no a indicadores KPI.

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

## Ejemplos cURL

### Dashboard general
```bash
curl -X GET http://localhost:8081/api/dashboard/stats
```

### KPIs del dashboard
```bash
curl -X GET http://localhost:8081/api/dashboard/kpis
```

### Dashboard por sucursal
```bash
curl -X GET http://localhost:8081/api/dashboard/sucursal/1
```

## Evidencia de pruebas manuales

### Respuesta exitosa GET /api/dashboard/stats
```json
{
  "statusBff": "Operativo",
  "ventasTotales": 0,
  "kpis": [],
  "alertas": [],
  "datosSucursal": []
}
```

### Respuesta exitosa GET /api/dashboard/sucursal/1
```json
{
  "statusBff": "Operativo",
  "ventasTotales": 0,
  "kpis": [],
  "alertas": ["Datos de sucursal 1 obtenidos desde Data Service"],
  "datosSucursal": []
}
```

### Respuesta degradada (KPI Service caído)
```json
{
  "statusBff": "Degradado",
  "ventasTotales": 0,
  "kpis": [],
  "alertas": ["No fue posible obtener información desde KPI Service"],
  "datosSucursal": []
}
```