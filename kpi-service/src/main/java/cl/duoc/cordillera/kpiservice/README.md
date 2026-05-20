# KPI Service — Cordillera Platform

## Descripción
Microservicio encargado de gestionar y calcular los KPIs organizacionales de Cordillera Platform.

## Requisitos
- Java 21
- Maven 3.9+
- MySQL 8+
- Spring Boot 4.0.6

## Puerto
8084

## Base de datos
kpi_db

## Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/kpis | Listar todos los KPIs |
| GET | /api/kpis/{id} | Obtener KPI por ID |
| POST | /api/kpis | Crear KPI |
| PUT | /api/kpis/{id} | Actualizar KPI |
| DELETE | /api/kpis/{id} | Eliminar KPI |
| GET | /api/kpis/categoria/{cat} | KPIs por categoría |

## Ejemplos JSON

### Crear KPI
```json
{
  "nombre": "Ventas Q1",
  "valor": 75000,
  "unidad": "CLP",
  "categoria": "ventas",
  "estado": "EN_PROGRESO"
}
```

### Respuesta
```json
{
  "id": 1,
  "nombre": "Ventas Q1",
  "valor": 75000,
  "unidad": "porcentaje",
  "categoria": "ventas",
  "estado": "EN_PROGRESO"
}
```

## Cómo ejecutar
```bash
cd kpi-service
mvn spring-boot:run
```

## Cómo probar
```powershell
Invoke-RestMethod -Uri "http://localhost:8084/api/kpis" -Method GET
```