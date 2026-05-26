# Cordillera Platform - Parcial 2

Proyecto desarrollado para la asignatura **Desarrollo Full Stack III - DSY1106, seccion 001D**.

## Descripción

Cordillera Platform es una plataforma basada en microservicios para consolidar datos de la empresa retail **Grupo Cordillera**, calcular KPIs y generar reportes ejecutivos.

## Componentes

- `frontend`: aplicacion React 19 + Vite servida con Nginx en Docker.
- `bff-gateway`: punto de entrada API para el frontend.
- `data-service`: microservicio de datos organizacionales.
- `kpi-service`: microservicio de KPIs.
- `report-service`: microservicio de reportes ejecutivos.

## Stack actual

- Java 21
- Spring Boot 4.0.6
- Maven 3.9.15
- React 19 + Vite
- Nginx
- MySQL Docker
- Docker Compose
- Git + GitHub

## Arquitectura Docker

```txt
Usuario -> Frontend :3000 -> BFF Gateway :8081 -> Data/KPI/Report Service -> MySQL
```

## Puertos

| Componente | Puerto |
|---|---:|
| Frontend React 19 + Vite + Nginx | 3000 |
| BFF Gateway | 8081 |
| Data Service | 8083 |
| KPI Service | 8084 |
| Report Service | 8085 |
| MySQL host | 3307 |
| MySQL contenedor | 3306 |

## Ejecucion con Docker

Desde la raiz del repositorio:

```powershell
docker compose up -d --build
```

Acceso principal:

```txt
http://localhost:3000
```

El frontend consume exclusivamente el BFF Gateway en:

```txt
http://localhost:8081
```

## Documentación

La documentación técnica y las evidencias del proyecto se encuentran en la carpeta `docs/`.

## MySQL para ejecucion local sin Docker

La presentacion del proyecto usa MySQL en Docker mediante `docker compose`. Como alternativa para ejecutar microservicios manualmente, se puede usar una instalacion local compatible con MySQL/MariaDB:

```txt
host: localhost
puerto: 3306
usuario: root
password: sin password
```

Con esa configuracion local no necesitas exportar credenciales antes de ejecutar:

```powershell
cd data-service
mvn spring-boot:run
```

Si otro equipo usa una clave distinta, basta con definir variables de entorno en la misma terminal antes de levantar los servicios:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="mi_clave"
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
```

Cada servicio crea su base si no existe: `data_db`, `kpi_db` y `report_db`.

## Estado

Proyecto integrado con Docker Compose, frontend React 19 + Vite servido por Nginx y backend Java 21 + Spring Boot conectado a MySQL Docker.
