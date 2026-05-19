# Data Service - Cordillera Platform

Microservicio responsable de gestionar datos organizacionales provenientes de sistemas internos de Grupo Cordillera, tales como POS, E-commerce, Inventario, Finanzas y CRM.

## Descripción

`data-service` centraliza datos operacionales para que otros servicios, como `kpi-service`, puedan consultar información consolidada y calcular indicadores estratégicos.

## Stack

- Java 21
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- MySQL
- H2 para pruebas
- Lombok
- Bean Validation

## Puerto

```txt
8083
```

## Base de datos

```txt
data_db
```

## Arquitectura interna

```txt
Controller → Service → Repository → Model
```

## Patrón aplicado

### Repository Pattern

`DatoRepository` extiende `JpaRepository`, separando la lógica de persistencia de la lógica de negocio.

## Configuración

Archivo principal:

```txt
src/main/resources/application.properties
```

Configuración esperada:

```properties
spring.application.name=data-service
server.port=8083

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/data_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:root}

spring.jpa.hibernate.ddl-auto=update
```

## Ejecutar servicio

Desde la carpeta `data-service`:

```bash
mvn clean install
mvn spring-boot:run
```

## Ejecutar pruebas

```bash
mvn clean test
```

Las pruebas utilizan H2 en memoria mediante `src/test/resources/application.properties`.

## Endpoints

### Listar datos

```http
GET /api/datos
```

### Buscar por ID

```http
GET /api/datos/{id}
```

### Crear dato

```http
POST /api/datos
```

Ejemplo JSON:

```json
{
  "sistemaOrigen": "POS",
  "tipoDato": "VENTA",
  "valor": "150000",
  "sucursalId": 1
}
```

### Actualizar dato

```http
PUT /api/datos/{id}
```

### Eliminar dato

```http
DELETE /api/datos/{id}
```

### Filtrar por sistema de origen

```http
GET /api/datos/sistema/{origen}
```

### Filtrar por sucursal

```http
GET /api/datos/sucursal/{id}
```

## Pruebas manuales con PowerShell

### GET todos

```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/datos" -Method Get
```

### POST crear dato

```powershell
$body = @{
  sistemaOrigen = "POS"
  tipoDato = "VENTA"
  valor = "150000"
  sucursalId = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8083/api/datos" -Method Post -ContentType "application/json" -Body $body
```

### GET por sistema

```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/datos/sistema/POS" -Method Get
```

### GET por sucursal

```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/datos/sucursal/1" -Method Get
```

## Datos semilla

El servicio incluye `DataLoader`, que carga datos iniciales si la tabla está vacía.

Sistemas incluidos:

- POS
- E-COMMERCE
- INVENTARIO
- FINANZAS
- CRM

## Estados esperados

- `200 OK` para consultas exitosas.
- `201 Created` para creación.
- `204 No Content` para eliminación.
- `400 Bad Request` para validaciones inválidas.
- `404 Not Found` para registros inexistentes.

## Consideraciones

- Este servicio no debe consumir directamente otros microservicios.
- `kpi-service` consume este servicio mediante REST.
- Cada microservicio mantiene su propia base de datos, cumpliendo database-per-service.