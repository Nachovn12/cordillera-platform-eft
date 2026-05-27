# Prueba de integración end-to-end - Cordillera Platform

## Objetivo

Validar que **Cordillera Platform** integra correctamente sus componentes principales:

```text
Postman → Microservicio → MySQL → BFF Gateway → Frontend React
```

Esta evidencia permite demostrar que un dato creado manualmente desde Postman puede ser persistido por un microservicio, consultado posteriormente desde el BFF Gateway y visualizado en el Frontend.

---

## Contexto técnico

| Componente     | Tecnología           | Puerto |
| -------------- | -------------------- | -----: |
| Frontend       | React + Vite + Nginx |   3000 |
| BFF Gateway    | Spring Boot + Maven  |   8081 |
| Data Service   | Spring Boot + Maven  |   8083 |
| KPI Service    | Spring Boot + Maven  |   8084 |
| Report Service | Spring Boot + Maven  |   8085 |
| MySQL          | MySQL 8.4            |   3307 |

---

## Flujo general validado

```text
Usuario / Postman
      ↓
Microservicio backend
      ↓
Base de datos MySQL
      ↓
BFF Gateway
      ↓
Frontend React
```

El Frontend no consume directamente los microservicios internos. La comunicación hacia el backend se centraliza mediante el **BFF Gateway**.

---

# 1. Levantar la plataforma

Desde la raíz del proyecto ejecutar:

```powershell
docker compose up -d --build
```

Validar contenedores:

```powershell
docker compose ps
```

Resultado esperado:

```text
frontend        Up
bff-gateway     Up
data-service    Up
kpi-service     Up
report-service  Up
mysql           Up
```

Validar acceso al Frontend:

```text
http://localhost:3000
```

Validar BFF Gateway:

```http
GET http://localhost:8081/api/dashboard/stats
```

---

# 2. Prueba de integración: crear reporte y visualizarlo en Frontend

## Objetivo de la prueba

Crear un reporte desde Postman, persistirlo en `report_db`, consultarlo desde el BFF Gateway y visualizarlo en el Frontend dentro del módulo **Centro de Reportes**.

## Flujo validado

```text
Postman → Report Service → report_db → BFF Gateway → Frontend React
```

---

## 2.1 Crear reporte desde Postman

### Endpoint

```http
POST http://localhost:8085/api/reportes
Content-Type: application/json
```

### Body

```json
{
  "titulo": "Reporte Defensa Integración",
  "tipo": "PDF",
  "area": "Gerencia",
  "valor": 1750000
}
```

> Si el backend solicita fecha de generación, utilizar este body alternativo:

```json
{
  "titulo": "Reporte Defensa Integración",
  "tipo": "PDF",
  "area": "Gerencia",
  "valor": 1750000,
  "fechaGeneracion": "2026-05-26T17:30:00"
}
```

### Resultado esperado

El Report Service debe responder con el reporte creado, incluyendo un `id`.

Ejemplo esperado:

```json
{
  "id": 1,
  "titulo": "Reporte Defensa Integración",
  "tipo": "PDF",
  "area": "Gerencia",
  "valor": 1750000
}
```

---

## 2.2 Validar reporte directamente en Report Service

### Endpoint

```http
GET http://localhost:8085/api/reportes
```

### Resultado esperado

El reporte creado debe aparecer en el listado retornado por `report-service`.

Ejemplo:

```json
[
  {
    "id": 1,
    "titulo": "Reporte Defensa Integración",
    "tipo": "PDF",
    "area": "Gerencia",
    "valor": 1750000
  }
]
```

---

## 2.3 Validar reporte desde BFF Gateway

### Endpoint

```http
GET http://localhost:8081/api/reportes
```

### Resultado esperado

El BFF Gateway debe retornar los reportes obtenidos desde Report Service.

Esto demuestra que el Frontend no necesita conectarse directamente al microservicio de reportes.

---

## 2.4 Validar reporte en Frontend

Abrir:

```text
http://localhost:3000
```

Ir al módulo:

```text
Reportes → Actualizar datos
```

### Resultado esperado

El reporte creado desde Postman debe aparecer en la **Biblioteca de reportes**.

Ejemplo visible esperado:

```text
Reporte Defensa Integración
Área: Gerencia
Formato: PDF
Estado: Generado / No informado
```

---

## 2.5 Validar exportación del reporte

Desde Postman o navegador:

```http
GET http://localhost:8081/api/reportes/{id}/exportar?formato=pdf
```

Si está habilitada la selección de formato:

```http
GET http://localhost:8081/api/reportes/{id}/exportar?formato=excel
GET http://localhost:8081/api/reportes/{id}/exportar?formato=json
```

### Resultado esperado

El archivo debe descargarse mediante el flujo:

```text
Frontend → BFF Gateway → Report Service → ExportadorFactory
```

Esta prueba permite evidenciar el uso del patrón **Factory Method** mediante:

```text
ExportadorFactory → PdfExportador / ExcelExportador / JsonExportador
```

---

# 3. Prueba de integración: crear KPI y visualizarlo en Frontend

## Objetivo de la prueba

Crear un KPI desde Postman, persistirlo en `kpi_db`, consultarlo desde el BFF Gateway y visualizarlo en el Frontend dentro del módulo **KPIs Estratégicos**.

## Flujo validado

```text
Postman → KPI Service → kpi_db → BFF Gateway → Frontend React
```

---

## 3.1 Crear KPI desde Postman

### Endpoint

```http
POST http://localhost:8084/api/kpis
Content-Type: application/json
```

### Body

```json
{
  "nombre": "Satisfacción cliente",
  "valor": 91.5,
  "unidad": "%",
  "categoria": "CRM",
  "estado": "Activo"
}
```

### Resultado esperado

El KPI debe crearse correctamente en KPI Service.

Ejemplo esperado:

```json
{
  "id": 1,
  "nombre": "Satisfacción cliente",
  "valor": 91.5,
  "unidad": "%",
  "categoria": "CRM",
  "estado": "Activo"
}
```

---

## 3.2 Validar KPI directamente en KPI Service

### Endpoint

```http
GET http://localhost:8084/api/kpis
```

### Resultado esperado

El KPI creado debe aparecer en el listado.

```text
Satisfacción cliente
```

---

## 3.3 Validar KPI desde BFF Gateway

### Endpoint

```http
GET http://localhost:8081/api/dashboard/kpis
```

También se puede validar con:

```http
GET http://localhost:8081/api/dashboard/stats
```

### Resultado esperado

El BFF Gateway debe retornar los KPIs obtenidos desde KPI Service.

---

## 3.4 Validar KPI en Frontend

Abrir:

```text
http://localhost:3000
```

Ir al módulo:

```text
KPIs → Actualizar
```

### Resultado esperado

El KPI creado desde Postman debe aparecer o quedar disponible dentro de la vista **KPIs Estratégicos**.

Ejemplo visible esperado:

```text
Satisfacción cliente
Categoría: CRM
Valor: 91.5 %
Estado: Activo
```

---

# 4. Prueba opcional: crear dato operacional

## Objetivo de la prueba

Crear un dato operacional desde Postman, persistirlo en `data_db` y validarlo desde Data Service y BFF Gateway.

## Flujo validado

```text
Postman → Data Service → data_db → BFF Gateway
```

---

## 4.1 Crear dato desde Postman

### Endpoint

```http
POST http://localhost:8083/api/datos
Content-Type: application/json
```

### Body

```json
{
  "sistemaOrigen": "POS",
  "tipoDato": "VENTA_DIARIA",
  "valor": "Venta registrada desde Postman: $890000",
  "sucursalId": 1
}
```

> Si el backend solicita fecha de registro, utilizar:

```json
{
  "sistemaOrigen": "POS",
  "tipoDato": "VENTA_DIARIA",
  "valor": "Venta registrada desde Postman: $890000",
  "fechaRegistro": "2026-05-26T17:45:00",
  "sucursalId": 1
}
```

---

## 4.2 Validar dato en Data Service

```http
GET http://localhost:8083/api/datos
```

También se puede validar por sucursal:

```http
GET http://localhost:8083/api/datos/sucursal/1
```

O por sistema de origen:

```http
GET http://localhost:8083/api/datos/sistema/POS
```

---

## 4.3 Validar dato desde BFF Gateway

```http
GET http://localhost:8081/api/dashboard/sucursal/1
```

### Resultado esperado

El BFF Gateway debe retornar información relacionada a la sucursal consultada.

---

# 5. Prueba opcional: degradación controlada / Circuit Breaker

## Objetivo de la prueba

Demostrar que la plataforma mantiene respuesta controlada si un microservicio interno no responde.

---

## 5.1 Detener KPI Service

```powershell
docker compose stop kpi-service
```

---

## 5.2 Consultar dashboard desde BFF Gateway

```http
GET http://localhost:8081/api/dashboard/stats
```

### Resultado esperado

El BFF Gateway debe responder de forma controlada, indicando estado degradado o alertas relacionadas al servicio no disponible.

Ejemplo esperado:

```json
{
  "statusBff": "Degradado",
  "kpis": [],
  "alertas": ["No fue posible obtener información desde KPI Service"]
}
```

---

## 5.3 Validar en Frontend

Abrir:

```text
http://localhost:3000
```

Presionar:

```text
Actualizar
```

### Resultado esperado

El Frontend debe mantenerse operativo y mostrar un estado controlado, sin caída total de la plataforma.

---

## 5.4 Levantar nuevamente KPI Service

```powershell
docker compose start kpi-service
```

Esperar unos segundos y volver a consultar:

```http
GET http://localhost:8081/api/dashboard/stats
```

---

# 6. Comandos útiles para validación rápida

## Validar Frontend

```text
http://localhost:3000
```

## Validar BFF Gateway

```powershell
Invoke-RestMethod http://localhost:8081/api/dashboard/stats
```

## Validar Data Service

```powershell
Invoke-RestMethod http://localhost:8083/api/datos
```

## Validar KPI Service

```powershell
Invoke-RestMethod http://localhost:8084/api/kpis
```

## Validar Report Service

```powershell
Invoke-RestMethod http://localhost:8085/api/reportes
```

## Validar contenedores

```powershell
docker compose ps
```

---

# 7. Evidencias sugeridas

Para respaldar la ejecución de esta prueba, se recomienda guardar capturas de:

- `docker compose ps` con todos los servicios activos.
- POST de creación de reporte en Postman.
- GET de reportes desde Report Service.
- GET de reportes desde BFF Gateway.
- Visualización del reporte en Frontend.
- Exportación de reporte.
- POST de creación de KPI en Postman.
- GET de KPIs desde KPI Service.
- GET de KPIs desde BFF Gateway.
- Visualización del KPI en Frontend.
- Prueba de degradación controlada con KPI Service detenido.

---

# 8. Relación con la rúbrica

Esta prueba permite evidenciar los siguientes puntos de la rúbrica del Parcial 2:

## Patrones de diseño

- **Repository Pattern:** persistencia y consulta mediante repositorios JPA.
- **Factory Method:** selección de exportadores en Report Service.
- **Circuit Breaker:** respuesta controlada ante fallos de servicios internos.
- **BFF / API Gateway:** centralización de consumo desde Frontend.

## Arquitectura

- Microservicios independientes.
- Comunicación HTTP REST / JSON.
- Database per Service.
- Separación por capas:
  - Controller
  - Service
  - Repository
  - Model

## Buenas prácticas

- Frontend consume únicamente BFF Gateway.
- Backend separado por responsabilidad.
- Servicios ejecutados con Docker Compose.
- Validación manual mediante Postman.
- Evidencia técnica documentada.
