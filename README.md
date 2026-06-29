# Cordillera Platform — Plataforma de Microservicios para Monitoreo Ejecutivo

## 1. Información del proyecto

| Campo               | Detalle                                     |
| ------------------- | ------------------------------------------- |
| Proyecto            | Cordillera Platform                         |
| Empresa caso        | Grupo Cordillera                            |
| Asignatura          | Desarrollo Full Stack III — DSY1106         |
| Sección             | 001D                                        |
| Institución         | Duoc UC                                     |
| Tipo de entrega     | Parcial 3 — EP3 Integración + Testing      |
| Tipo de trabajo     | Grupal                                      |
| Arquitectura        | Microservicios con BFF / API Gateway        |
| Frontend            | React + Vite + Nginx                        |
| Backend             | Java 25, Spring Boot 4.0.6, Maven           |
| Swagger / OpenAPI   | Springdoc OpenAPI 3.0                       |
| Persistencia        | MySQL 8.4 con base lógica por microservicio |
| Comunicación        | HTTP REST / JSON                            |
| Tolerancia a fallos | Resilience4j Circuit Breaker                |
| Pruebas             | JUnit 5, Mockito, MockMvc, JaCoCo 0.8.13, H2 in-memory |
| Cobertura mínima    | 60% (quality gate JaCoCo en los 4 servicios) |

---

## 2. Integrantes del equipo

| Integrante      | Responsabilidad principal                                                                                                                                          |
| --------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Ignacio Valeria | Frontend React + Report Service. Implementación de interfaz ejecutiva, generación/exportación de reportes, documentación técnica, diagramas y apoyo en evidencias. |
| Benjamín Palma  | BFF Gateway + KPI Service. Implementación del punto de entrada central, orquestación de datos para dashboard y cálculo de indicadores ejecutivos.                  |
| Benjamín Flores | Data Service. Implementación del servicio de datos operacionales, persistencia y endpoints para información consolidada desde sistemas origen.                     |

---

## 3. Descripción general

Cordillera Platform es una solución basada en microservicios para la empresa retail **Grupo Cordillera**, cuyo problema principal es la dispersión de información en distintos sistemas internos:

```text
- POS
- E-commerce
- Inventario
- Finanzas
- CRM
```

La plataforma permite centralizar datos operacionales, calcular KPIs ejecutivos y generar reportes para apoyar la toma de decisiones.
El flujo principal del sistema es:

```text
Usuario → Frontend → BFF Gateway → Microservicios → MySQL
```

La solución aplica arquitectura de microservicios, patrón BFF / API Gateway, Database per Service, Repository Pattern, Factory Method y Circuit Breaker con Resilience4j.

---

## 4. Objetivo del proyecto

Desarrollar una plataforma fullstack basada en microservicios que permita:

```text
1. Consolidar datos operacionales provenientes de distintos sistemas (POS, SAP, ERP, CRM).
2. Calcular indicadores ejecutivos (KPIs) para apoyar la toma de decisiones.
3. Generar reportes ejecutivos exportables en PDF, Excel y JSON.
4. Exponer una interfaz frontend React centralizada para el usuario.
5. Aplicar patrones de diseño (Factory Method, Repository, Circuit Breaker, Observer, BFF).
6. Implementar pruebas unitarias con cobertura JaCoCo ≥ 60% en los 4 microservicios Java.
7. Evidenciar buenas prácticas de desarrollo, pruebas y versionamiento Git Flow.
8. Documentar la arquitectura con diagramas, informe de persistencia e informe de pruebas.
```

---

## 5. Arquitectura general

### 5.1 Diagrama de arquitectura de microservicios

![Diagrama de arquitectura de microservicios](docs/diagramas/01-arquitectura-ep3.png)

### 5.2 Diagrama de despliegue

![Diagrama de despliegue](docs/diagramas/02-despliegue-ep3.png)

### 5.3 Diagramas de clases

| Servicio       | Diagrama                                   |
| -------------- | ------------------------------------------ |
| BFF Gateway    | `docs/diagramas/bff-gateway-clases-ep3.png`|
| Data Service   | `docs/diagramas/data-service-clases-ep3.png`|
| KPI Service    | `docs/diagramas/kpi-service-clases-ep3.png`|
| Report Service | `docs/diagramas/report-service-clases-ep3.png`|

### 5.4 Diagramas de casos de uso

| Servicio       | Diagrama                                      |
| -------------- | --------------------------------------------- |
| BFF Gateway    | `docs/diagramas/bff-service-casos-uso.png`    |
| Data Service   | `docs/diagramas/data-service-casos-uso.png`   |
| KPI Service    | `docs/diagramas/kpi-service-casos-uso.png`    |
| Report Service | `docs/diagramas/report-service-casos-uso.png` |

---

## 6. Componentes del sistema

| Componente     | Carpeta          |    Puerto | Base de datos                    | Responsabilidad                                                                                 |
| -------------- | ---------------- | --------: | -------------------------------- | ----------------------------------------------------------------------------------------------- |
| Frontend       | `frontend`       |      3000 | No aplica                        | Interfaz ejecutiva web para consultar dashboard, KPIs, reportes, alertas y estado de servicios. |
| BFF Gateway    | `bff-gateway`    |      8081 | No aplica                        | Punto único de entrada para el frontend. Centraliza y orquesta solicitudes.                     |
| Data Service   | `data-service`   |      8083 | `data_db`                        | Gestiona datos operacionales desde POS, E-commerce, Inventario, Finanzas y CRM.                 |
| KPI Service    | `kpi-service`    |      8084 | `kpi_db`                         | Calcula indicadores ejecutivos usando `KpiFactory` y calculadores especializados.               |
| Report Service | `report-service` |      8085 | `report_db`                      | Genera, consulta y exporta reportes ejecutivos en PDF, Excel y JSON.                            |
| MySQL          | `mysql`          | 3307:3306 | `data_db`, `kpi_db`, `report_db` | Motor de persistencia relacional utilizado por los microservicios.                              |

---

## 7. Tecnologías utilizadas

| Categoría               | Tecnología                   |
| ----------------------- | ---------------------------- |
| Lenguaje backend        | Java 25                      |
| Framework backend       | Spring Boot 4.0.6            |
| Gestión de dependencias | Maven                        |
| Persistencia            | Spring Data JPA / Hibernate  |
| Base de datos           | MySQL 8.4                    |
| Frontend                | React 19 + Vite 8            |
| Servidor frontend       | Nginx (con try_files SPA)    |
| Comunicación            | REST / JSON                  |
| Tolerancia a fallos     | Resilience4j Circuit Breaker |
| Pruebas unitarias       | JUnit 5                      |
| Mocks                   | Mockito                      |
| Pruebas controller      | MockMvc / @WebMvcTest        |
| Pruebas repositorio     | @DataJpaTest + H2 in-memory  |
| Cobertura               | JaCoCo 0.8.13 (quality gate ≥ 60%) |
| Documentación API       | springdoc-openapi / Swagger UI 3.0.2 |
| Contenedores            | Docker                       |
| Orquestación local      | Docker Compose               |
| Control de versiones    | Git + GitHub                 |

---

## 8. Patrones implementados

### 8.1 API Gateway / BFF

Implementado en `bff-gateway`.

El BFF actúa como punto único de entrada para el frontend, centralizando solicitudes hacia Data Service, KPI Service y Report Service.

```text
Frontend → BFF Gateway → Microservicios
```

Endpoints principales:

```text
GET /api/dashboard/stats
GET /api/dashboard/kpis
GET /api/dashboard/sucursal/{id}
GET /api/reportes
GET /api/configuracion
```

---

### 8.2 Repository Pattern

Implementado en los microservicios con Spring Data JPA.

| Servicio       | Repository          |
| -------------- | ------------------- |
| Data Service   | `DatoRepository`    |
| KPI Service    | `KpiRepository`     |
| Report Service | `ReporteRepository` |

Ejemplos:

```java
findBySistemaOrigen(String sistemaOrigen)
findBySucursalId(Long sucursalId)
findByCategoria(String categoria)
findByArea(String area)
```

---

### 8.3 Factory Method

Implementado en KPI Service y Report Service.

#### KPI Service

```text
KpiFactory
├── VentasCalculator
├── InventarioCalculator
├── LogisticaCalculator
└── RentabilidadCalculator
```

Permite seleccionar el cálculo correspondiente según la categoría del KPI.

#### Report Service

```text
ExportadorFactory
├── PdfExportador
├── ExcelExportador
└── JsonExportador
```

Permite seleccionar el exportador adecuado según el formato solicitado.

---

### 8.4 Circuit Breaker

Implementado con Resilience4j.

| Origen         | Destino      | Circuit Breaker |
| -------------- | ------------ | --------------- |
| KPI Service    | Data Service | `dataService`   |
| Report Service | KPI Service  | `kpiService`    |

El objetivo es evitar que una falla en un servicio interno detenga toda la plataforma.

Estados principales:

```text
CLOSED → OPEN → HALF-OPEN
```

---

### 8.5 Database per Service

Cada microservicio posee su propia base lógica dentro de MySQL.

```text
data-service   → data_db
kpi-service    → kpi_db
report-service → report_db
```

Esto reduce el acoplamiento entre servicios y permite separar responsabilidades de persistencia.

---

## 9. Estructura general del repositorio

```text
cordillera-platform/
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── vite.config.js
│   └── Dockerfile
│
├── bff-gateway/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── data-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── kpi-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── report-service/
│   ├── src/
│   ├── pom.xml
│   └── Dockerfile
│
├── docs/
│   ├── diagramas/
│   ├── evidencias/
│   ├── git-flow/
│   └── pruebas/
│
├── docker-compose.yml
└── README.md
```

---

## 10. Estructura interna de los microservicios backend

Cada microservicio Spring Boot mantiene una estructura por capas:

```text
src/
├── main/
│   ├── java/
│   │   └── cl/cordillera/...
│   │       ├── controller/
│   │       ├── service/
│   │       ├── repository/
│   │       ├── model/
│   │       ├── dto/
│   │       ├── factory/
│   │       ├── config/
│   │       └── exception/
│   │
│   └── resources/
│       └── application.properties
│
└── test/
    └── java/
```

Flujo interno aplicado:

```text
Controller → Service → Repository → Model
```

---

## 11. Requisitos previos

Antes de ejecutar el proyecto, se debe contar con:

| Herramienta    | Uso                                   |
| -------------- | ------------------------------------- |
| Java JDK 25    | Ejecutar microservicios Spring Boot   |
| Maven 3.9+     | Compilar, probar y empaquetar backend |
| Node.js 20+    | Ejecutar frontend en entorno local    |
| npm            | Gestión de dependencias frontend      |
| Docker         | Crear y ejecutar contenedores         |
| Docker Compose | Levantar la plataforma completa       |
| Git            | Control de versiones                  |
| Postman        | Pruebas manuales de endpoints         |

Verificar instalaciones:

```powershell
java -version
mvn -version
node -v
npm -v
docker --version
docker compose version
git --version
```

---

## 12. Clonar el repositorio

```powershell
git clone https://github.com/Nachovn12/cordillera-platform-parcial-2.git
cd cordillera-platform-parcial-2
git status
```

Resultado esperado:

```text
On branch main
nothing to commit, working tree clean
```

Si se trabaja desde integración:

```powershell
git checkout develop
git pull origin develop
```

---

## 13. Ejecución recomendada con Docker Compose

La forma recomendada de ejecutar el proyecto completo es usando Docker Compose.

### 13.1 Levantar todos los servicios

Desde la raíz del proyecto:

```powershell
docker compose up --build
```

Servicios levantados:

```text
frontend       → http://localhost:3000
bff-gateway    → http://localhost:8081
data-service   → http://localhost:8083
kpi-service    → http://localhost:8084
report-service → http://localhost:8085
mysql          → localhost:3307
```

### 13.2 Levantar en segundo plano

```powershell
docker compose up --build -d
```

### 13.3 Ver contenedores activos

```powershell
docker compose ps
```

### 13.4 Ver logs

```powershell
docker compose logs -f
```

Ver logs de un servicio específico:

```powershell
docker compose logs -f report-service
```

### 13.5 Detener la plataforma

```powershell
docker compose down
```

### 13.6 Detener y eliminar volúmenes

Usar solo si se desea reiniciar completamente las bases de datos:

```powershell
docker compose down -v
```

---

## 14. Bases de datos MySQL

El contenedor MySQL utiliza:

```text
Imagen: mysql:8.4
Host: 3307
Contenedor: 3306
```

Bases lógicas utilizadas:

```text
data_db
kpi_db
report_db
```

Relación por microservicio:

| Microservicio  | Base de datos |
| -------------- | ------------- |
| Data Service   | `data_db`     |
| KPI Service    | `kpi_db`      |
| Report Service | `report_db`   |

---

## 15. Variables internas de servicios

Dentro de Docker Compose, los servicios se comunican usando el nombre del contenedor.

```text
DATA_SERVICE_URL=http://data-service:8083
KPI_SERVICE_URL=http://kpi-service:8084
REPORT_SERVICE_URL=http://report-service:8085
```

El frontend consume el BFF mediante:

```text
VITE_API_BASE_URL=http://localhost:8081
```

---

## 16. Ejecución manual sin Docker

También es posible ejecutar cada componente de forma independiente.

Se recomienda abrir una terminal por cada microservicio que se desee levantar.
En los equipos del instituto, la forma recomendada es usar el Maven Wrapper incluido en cada servicio:

### 16.1 Ejecutar Data Service

```powershell
cd .\data-service\
.\mvnw.cmd spring-boot:run
```

### 16.2 Ejecutar KPI Service

```powershell
cd .\kpi-service\
.\mvnw.cmd spring-boot:run
```

### 16.3 Ejecutar Report Service

```powershell
cd .\report-service\
.\mvnw.cmd spring-boot:run
```

### 16.4 Ejecutar BFF Gateway

```powershell
cd .\bff-gateway\
.\mvnw.cmd spring-boot:run
```

### 16.5 Ejecutar Frontend

```powershell
cd frontend
npm install
npm run dev
```

---

## 17. Orden recomendado de ejecución manual

Si no se utiliza Docker Compose, se recomienda levantar los servicios en este orden:

```text
1. MySQL.
2. Data Service.
3. KPI Service.
4. Report Service.
5. BFF Gateway.
6. Frontend.
```

Esto evita errores de conexión, ya que KPI Service depende de Data Service y Report Service depende de KPI Service.

---

## 18. Endpoints principales

### 18.1 BFF Gateway

Base URL:

```text
http://localhost:8081
```

| Método | Endpoint                       | Descripción                               |
| ------ | ------------------------------ | ----------------------------------------- |
| GET    | `/api/dashboard/stats`         | Obtiene datos consolidados del dashboard. |
| GET    | `/api/dashboard/kpis`          | Obtiene KPIs consolidados.                |
| GET    | `/api/dashboard/sucursal/{id}` | Obtiene datos filtrados por sucursal.     |
| GET    | `/api/reportes`                | Proxy o consulta de reportes.             |
| GET    | `/api/configuracion`           | Consulta configuración general.           |

---

### 18.2 Data Service

Base URL:

```text
http://localhost:8083
```

| Método | Endpoint                      | Descripción                          |
| ------ | ----------------------------- | ------------------------------------ |
| GET    | `/api/datos`                  | Lista todos los datos operacionales. |
| POST   | `/api/datos`                  | Registra un nuevo dato operacional.  |
| GET    | `/api/datos/{id}`             | Consulta un dato por ID.             |
| PUT    | `/api/datos/{id}`             | Actualiza un dato operacional.       |
| DELETE | `/api/datos/{id}`             | Elimina un dato operacional.         |
| GET    | `/api/datos/sistema/{origen}` | Filtra datos por sistema origen.     |
| GET    | `/api/datos/sucursal/{id}`    | Filtra datos por sucursal.           |

---

### 18.3 KPI Service

Base URL:

```text
http://localhost:8084
```

| Método | Endpoint                    | Descripción                |
| ------ | --------------------------- | -------------------------- |
| GET    | `/api/kpis`                 | Lista todos los KPIs.      |
| POST   | `/api/kpis`                 | Registra un nuevo KPI.     |
| GET    | `/api/kpis/{id}`            | Consulta un KPI por ID.    |
| PUT    | `/api/kpis/{id}`            | Actualiza un KPI.          |
| DELETE | `/api/kpis/{id}`            | Elimina un KPI.            |
| GET    | `/api/kpis/categoria/{cat}` | Filtra KPIs por categoría. |

---

### 18.4 Report Service

Base URL:

```text
http://localhost:8085
```

| Método | Endpoint                                   | Descripción                                |
| ------ | ------------------------------------------ | ------------------------------------------ |
| GET    | `/api/reportes`                            | Lista todos los reportes.                  |
| POST   | `/api/reportes`                            | Crea un reporte.                           |
| GET    | `/api/reportes/{id}`                       | Consulta un reporte por ID.                |
| PUT    | `/api/reportes/{id}`                       | Actualiza un reporte.                      |
| DELETE | `/api/reportes/{id}`                       | Elimina un reporte.                        |
| GET    | `/api/reportes/area/{area}`                | Filtra reportes por área.                  |
| POST   | `/api/reportes/generar`                    | Genera un reporte ejecutivo.               |
| GET    | `/api/reportes/{id}/exportar`              | Exporta un reporte.                        |
| GET    | `/api/reportes/kpis`                       | Consulta KPIs desde KPI Service.           |
| GET    | `/api/reportes/kpis/categoria/{categoria}` | Consulta KPIs por categoría para reportes. |

---

## 19. Ejemplos de prueba con PowerShell

### 19.1 Probar BFF Gateway

```powershell
Invoke-RestMethod http://localhost:8081/api/dashboard/stats
```

### 19.2 Probar Data Service

```powershell
Invoke-RestMethod http://localhost:8083/api/datos
```

### 19.3 Probar KPI Service

```powershell
Invoke-RestMethod http://localhost:8084/api/kpis
```

### 19.4 Probar Report Service

```powershell
Invoke-RestMethod http://localhost:8085/api/reportes
```

---

## 20. Pruebas automatizadas — EP3 Integración + Testing

El proyecto implementa pruebas unitarias en los 4 microservicios Java siguiendo el patrón del profesor:

```java
// Service test
@ExtendWith(MockitoExtension.class)          // sin contexto Spring
class NombreServiceTest {
    @Mock NombreRepository repo;
    @InjectMocks NombreService service;
    // when().thenReturn() + verify() + assertThrows()
}

// Controller test
@WebMvcTest(NombreController.class)          // contexto web parcial
class NombreControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean NombreService service;
    // mockMvc.perform().andExpect()
}

// Repository test
@DataJpaTest                                  // H2 in-memory, no toca MySQL
class NombreRepositoryTest {
    @Autowired NombreRepository repo;
    // persistir con repo.save() + verificar query methods
}
```

### 20.1 Ejecutar pruebas por microservicio

Desde la raíz del repositorio:

```powershell
mvn -f bff-gateway/pom.xml clean test
mvn -f data-service/pom.xml clean test
mvn -f kpi-service/pom.xml clean test
mvn -f report-service/pom.xml clean test
```

Resultado esperado:

```text
BUILD SUCCESS
```

---

## 21. Cobertura con JaCoCo — Quality Gate EP3

La cobertura mínima exigida por la rúbrica EP3 es **≥ 60%** en todos los componentes. El quality gate está configurado en cada `pom.xml` — si la cobertura baja del 60%, el build **falla automáticamente** con `BUILD FAILURE`.

```text
≥ 60%  ← quality gate mínimo obligatorio (Indicador 4 EP3)
```

### 21.1 Ejecutar verify + quality gate en todos los microservicios

```powershell
mvn clean verify -pl bff-gateway
mvn clean verify -pl data-service
mvn clean verify -pl kpi-service
mvn clean verify -pl report-service
```

### 21.2 Ejecutar cobertura en un servicio específico

```powershell
mvn -f report-service/pom.xml clean verify
```

Reporte generado en:

```text
report-service/target/site/jacoco/index.html
```

Hacer lo mismo para los otros servicios:

```text
bff-gateway/target/site/jacoco/index.html
data-service/target/site/jacoco/index.html
kpi-service/target/site/jacoco/index.html
```

### 21.3 Reportes de cobertura para el informe PDF (checklist EP3)

Los reportes JaCoCo HTML se copian automáticamente a `docs/` via `maven-antrun-plugin`:

```text
docs/jacoco-bff-gateway.html
docs/jacoco-data-service.html
docs/jacoco-kpi-service.html
docs/jacoco-report-service.html
```

Hacer screenshot de cada `index.html` mostrando el porcentaje ≥ 60% para incluir en `docs/informe-pruebas-unitarias.pdf`.

---

## 22. Pruebas prioritarias por microservicio — EP3

### 22.1 Data Service

```text
- DatoRepositoryTest   (@DataJpaTest + H2) → save, findBySistemaOrigen, findBySucursalId
- DatoServiceTest      (@ExtendWith + Mockito) → crear, actualizar(404 NoSuchElementException), eliminar
- DatoControllerTest   (@WebMvcTest + MockMvc) → POST 201, POST 400, GET /sistema/{origen}, GET /sucursal/{id}, PUT 200, DELETE 204
```

### 22.2 KPI Service

```text
- KpiFactoryTest       (instancia directa) → 4 categorías + categoría inválida (IllegalArgumentException)
- KpiServiceTest       (@ExtendWith + Mockito) → create (obtenerCalculador ×1), update (ResponseStatusException), delete (deleteById directo)
- KpiControllerTest    (@WebMvcTest) → GET /categoria/{cat} retorna 200 + lista vacía para inexistente
```

### 22.3 Report Service

```text
- ExportadorFactoryTest (instancia directa) → pdf, excel, json, inválido, null (ResponseStatusException BAD_REQUEST)
- ReporteServiceTest    (@ExtendWith + Mockito) → generarReporte (nuevo + idempotencia), exportar, buscarPorId 404
- KpiClienteServiceTest (@SpringBootTest + MockRestServiceServer) → online + fallback Circuit Breaker
- ReporteControllerTest (@WebMvcTest) → POST /generar 201, GET /{id}/exportar (3 formatos + 400), GET /area/{area}
```

### 22.4 BFF Gateway

```text
- RestTemplateConfigTest (instancia directa) → connectTimeout=2000, readTimeout=5000, factory=SimpleClientHttpRequestFactory
- DashboardServiceTest   (@ExtendWith + Mockito) → status Operativo, status Degradado (ResourceAccessException)
- AuthServiceTest        (instancia directa) → login OK, login inválido, crear usuario, email duplicado, id inexistente
- DashboardControllerTest (@WebMvcTest) → GET /api/dashboard/stats 200
```

---

## 23. Swagger UI — Documentación interactiva de la API

Todos los microservicios exponen Swagger UI automáticamente gracias a `springdoc-openapi-starter-webmvc-ui 3.0.2`:

| Servicio       | Swagger UI                              | OpenAPI JSON                       |
| -------------- | --------------------------------------- | ---------------------------------- |
| BFF Gateway    | http://localhost:8081/swagger-ui.html   | http://localhost:8081/v3/api-docs  |
| Data Service   | http://localhost:8083/swagger-ui.html   | http://localhost:8083/v3/api-docs  |
| KPI Service    | http://localhost:8084/swagger-ui.html   | http://localhost:8084/v3/api-docs  |
| Report Service | http://localhost:8085/swagger-ui.html   | http://localhost:8085/v3/api-docs  |

Para generar la colección Postman del checklist EP3 exportar desde:

```text
http://localhost:8081/v3/api-docs → guardar como api-rest/coleccion-postman.json
```

---

## 24. Pruebas manuales con Postman

Para validar con Postman:

```text
1. Levantar la plataforma con Docker Compose.
2. Verificar que todos los contenedores estén activos.
3. Importar la colección desde http://localhost:8081/v3/api-docs o usar Swagger UI.
4. Configurar variables de entorno.
5. Ejecutar endpoints por servicio.
6. Capturar evidencia de respuestas exitosas.
```

Variables sugeridas:

| Variable       | Valor                   |
| -------------- | ----------------------- |
| `frontend_url` | `http://localhost:3000` |
| `bff_url`      | `http://localhost:8081` |
| `data_url`     | `http://localhost:8083` |
| `kpi_url`      | `http://localhost:8084` |
| `report_url`   | `http://localhost:8085` |

---

## 24. Comunicación entre microservicios

| Origen         | Destino        | Propósito                               |
| -------------- | -------------- | --------------------------------------- |
| Frontend       | BFF Gateway    | Consumir datos consolidados para la UI. |
| BFF Gateway    | Data Service   | Obtener datos operacionales.            |
| BFF Gateway    | KPI Service    | Obtener indicadores ejecutivos.         |
| BFF Gateway    | Report Service | Obtener y gestionar reportes.           |
| KPI Service    | Data Service   | Consultar datos para cálculo de KPIs.   |
| Report Service | KPI Service    | Consultar KPIs para generar reportes.   |

---

## 25. Manejo de errores y tolerancia a fallos

El sistema utiliza respuestas HTTP estándar y Circuit Breaker en llamadas internas.

| Código | Significado           | Uso                              |
| -----: | --------------------- | -------------------------------- |
|    200 | OK                    | Consulta exitosa.                |
|    201 | Created               | Recurso creado correctamente.    |
|    204 | No Content            | Operación exitosa sin contenido. |
|    400 | Bad Request           | Datos inválidos.                 |
|    404 | Not Found             | Recurso no encontrado.           |
|    500 | Internal Server Error | Error interno.                   |

Circuit Breaker aplicado en:

```text
KPI Service → Data Service
Report Service → KPI Service
```

---

## 26. Flujo Git utilizado

El proyecto sigue Git Flow simplificado según la rúbrica.

| Rama                        | Propósito                                     |
| --------------------------- | --------------------------------------------- |
| `main`                      | Rama estable de producción / entrega final.   |
| `develop`                   | Rama de integración del equipo.               |
| `feature/nombre-componente` | Desarrollo de funcionalidades por componente. |
| `hotfix/nombre`             | Correcciones urgentes.                        |

Flujo esperado:

```text
feature/* → Pull Request → develop → Pull Request → main
```

Reglas aplicadas:

```text
- Todo cambio entra mediante Pull Request.
- Al menos un integrante revisa el Pull Request.
- Se documentan merges.
- Se mantiene evidencia de ramas.
- Se documenta al menos un conflicto resuelto.
```

---

## 27. Buenas prácticas aplicadas

```text
- Separación por capas: Controller → Service → Repository → Model.
- DTOs para transportar respuestas.
- Repository Pattern con Spring Data JPA.
- Factory Method para KPIs y exportadores.
- Circuit Breaker para tolerancia a fallos.
- Database per Service.
- Docker Compose para ejecución unificada.
- README por componente.
- Pruebas unitarias y de controlador.
- Cobertura con JaCoCo.
- Código versionado con Git Flow.
```

---

## 28. Documentación técnica del proyecto — EP3

| Documento                              | Ruta                                                    |
| -------------------------------------- | ------------------------------------------------------- |
| Diagrama de arquitectura (PNG/PDF)     | `docs/diagrama-arquitectura.png` / `.pdf`               |
| Diagrama ER de las 3 BDs               | `docs/diagrama-er.png` / `.pdf`                         |
| Descripción de persistencia JPA        | `docs/descripcion-persistencia.pdf`                     |
| Informe de pruebas unitarias + JaCoCo  | `docs/informe-pruebas-unitarias.pdf`                    |
| Reportes JaCoCo HTML por servicio      | `docs/jacoco-bff-gateway.html` / `jacoco-data-service.html` / `jacoco-kpi-service.html` / `jacoco-report-service.html` |
| Colección Postman / Swagger JSON       | `api-rest/coleccion-postman.json`                       |
| Diagramas de clases                    | `docs/diagramas/*-clases.png`                           |
| Diagramas de casos de uso              | `docs/diagramas/*-casos-uso.png`                        |
| Evidencias de Git Flow                 | `docs/git-flow/`                                        |
| Repositorios GitHub                    | `repositorios.txt`                                      |
| Sprint 3 — Historias de usuario        | `docs/sprint-3-templates/HU-CORD-113.md` … `HU-CORD-132.md` |
| Prompts de implementación IA           | `docs/jira-prompts/PROMPT-CORD-113.md` … `PROMPT-CORD-132.md` |

---

## 29. Checklist de entrega EP3

Antes de comprimir y enviar, verificar:

```text
DOCUMENTACIÓN
[ ] docs/diagrama-arquitectura.png y .pdf (Frontend ↔ BFF ↔ MS ↔ BD)
[ ] docs/descripcion-persistencia.pdf (entidades JPA + repositorios + diagrama ER)
[ ] docs/informe-pruebas-unitarias.pdf (screenshots JaCoCo ≥ 60% + tabla CA-Test + patrones)
[ ] api-rest/coleccion-postman.json (exportar desde http://localhost:8081/v3/api-docs)
[ ] repositorios.txt con URLs de todos los repos GitHub públicos

CÓDIGO
[ ] frontend/README.md con instrucciones de instalación y ejecución
[ ] README.md de cada microservicio con instrucciones mvn + docker
[ ] Los 4 microservicios compilan con mvn clean compile
[ ] Los 4 microservicios pasan mvn clean verify con JaCoCo ≥ 60%

PRUEBAS
[ ] DatoRepositoryTest / DatoServiceTest / DatoControllerTest verdes
[ ] KpiFactoryTest / KpiServiceTest / KpiControllerTest verdes
[ ] ExportadorFactoryTest / ReporteServiceTest / ReporteControllerTest verdes
[ ] RestTemplateConfigTest / DashboardServiceTest / AuthServiceTest verdes
[ ] Screenshots de JaCoCo de los 4 servicios guardados en docs/

GIT
[ ] Todos los repositorios actualizados y públicos en GitHub
[ ] PR mergeados en main con reviewer Nachovn12
[ ] rama main limpia y funcional con docker-compose up --build
```

No incluir en el ZIP:

```text
target/         node_modules/       .idea/
.vscode/        *.log               .env con credenciales
```

---

## 30. Comandos útiles antes de entrega

### 30.1 Ver estado Git

```powershell
git status
```

### 30.2 Ver ramas

```powershell
git branch -a
```

### 30.3 Ver últimos commits

```powershell
git log --oneline -10
```

### 30.4 Limpiar carpetas target

```powershell
Get-ChildItem -Path . -Recurse -Directory -Filter target | Remove-Item -Recurse -Force
```

### 30.5 Limpiar node_modules

```powershell
Remove-Item -Recurse -Force frontend/node_modules
```

---

## 31. Comandos rápidos de validación final — EP3

### 31.1 Backend — pruebas y quality gate JaCoCo

```powershell
mvn clean verify -pl bff-gateway
mvn clean verify -pl data-service
mvn clean verify -pl kpi-service
mvn clean verify -pl report-service
```

Resultado esperado en cada uno:

```text
BUILD SUCCESS  (cobertura ≥ 60%)
```

### 31.2 Frontend

```powershell
cd frontend
npm install
npm run dev     # desarrollo local (puerto 3000)
npm run build   # build producción
```

### 31.3 Docker Compose

```powershell
docker compose up --build
```

---

## 32. Estado esperado de validación — EP3

Resultado esperado de backend:

```text
BUILD SUCCESS  (todos los servicios con JaCoCo ≥ 60%)
```

Resultado esperado de frontend:

```text
vite build completed  (dist/ generado)
```

Resultado esperado de Docker Compose:

```text
frontend       running  → http://localhost:3000
bff-gateway    running  → http://localhost:8081
data-service   running  (interno Docker)
kpi-service    running  (interno Docker)
report-service running  (interno Docker)
```

Swagger UI disponible en todos los servicios:

```text
http://localhost:8081/swagger-ui.html  (BFF — punto principal)
http://localhost:8083/swagger-ui.html
http://localhost:8084/swagger-ui.html
http://localhost:8085/swagger-ui.html
```

Resultado esperado de cobertura:

```text
bff-gateway    ≥ 60%
data-service   ≥ 60%
kpi-service    ≥ 60%
report-service ≥ 60%
```

---

## 33. Preparación para defensa oral — EP3

Puntos clave para explicar (Indicadores 5-8, 70% de la nota):

```text
ARQUITECTURA (Ind. 1 y 5)
1. La plataforma resuelve la dispersión de datos del Grupo Cordillera (POS, SAP, ERP, CRM).
2. El BFF Gateway centraliza las solicitudes del Frontend React — único punto de entrada.
3. Database per Service: data_db, kpi_db, report_db — sin acoplamiento entre servicios.

TECNOLOGÍA (Ind. 2 y 6)
4. Frontend: React 19 + Vite 8 — framework moderno (JavaScript).
5. Backend: Java 25 + Spring Boot 4.0.6 — stack distinto al frontend.
6. JPA + H2 in-memory para tests — los tests no requieren MySQL real.
7. Swagger UI en todos los microservicios — documentación API interactiva.

INTEGRACIÓN Y ESCALABILIDAD (Ind. 7)
8. Demo en vivo: docker stop kpi-service → DegradedBanner aparece en el frontend.
9. Circuit Breaker (Resilience4j) evita que una falla detenga toda la plataforma.
10. Docker Compose levanta todo el ecosistema con un solo comando.

PRUEBAS Y PATRONES (Ind. 4 y 8)
11. Repository Pattern: tests con H2 in-memory — independientes del entorno real.
12. Factory Method (KpiFactory.obtenerCalculador): 5 tests cubren 100% de la clase.
13. Factory Method (ExportadorFactory.crearExportador): PDF, Excel, JSON + inválido + null.
14. Circuit Breaker: fallback testeable con mock — rama else cubierta por JaCoCo.
15. JaCoCo ≥ 60% en los 4 microservicios — quality gate automático en el pipeline.
```

---

## 34. Conclusión

Cordillera Platform implementa una solución fullstack basada en microservicios para consolidar datos operacionales, calcular KPIs y generar reportes ejecutivos para Grupo Cordillera.

La solución integra un frontend React 19, un BFF Gateway, tres microservicios Spring Boot 4 con Java 25, persistencia MySQL con patrón Database per Service, comunicación REST, Swagger UI en todos los servicios, y mecanismos de tolerancia a fallos con Resilience4j Circuit Breaker.

El Parcial 3 agrega la capa de calidad: pruebas unitarias con @DataJpaTest, @WebMvcTest y Mockito sobre los 4 componentes Java, cobertura verificada con JaCoCo ≥ 60% (quality gate automático), y la documentación completa de entrega: diagrama de arquitectura, descripción de persistencia JPA, informe de pruebas con screenshots y colección Postman generada desde Swagger.

El proyecto queda preparado para entrega EP3 en AVA y defensa oral individual, evidenciando arquitectura escalable con BFF y microservicios, separación de responsabilidades, patrones Factory Method y Strategy en KPI y Report Service, pruebas automatizadas alineadas con el patrón del profesor, y control de versiones mediante Git Flow con PRs revisados por el equipo.
