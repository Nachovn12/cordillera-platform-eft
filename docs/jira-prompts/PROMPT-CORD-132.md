# Prompt de Implementación - CORD-132

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-132 — Crear repositorios GitHub y generar repositorios.txt
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El EP3 requiere que el evaluador pueda acceder públicamente al código del sistema de Grupo Cordillera para verificar la implementación. El checklist exige un `repositorios.txt` con las URLs de todos los repositorios y sus descripciones, y que cada repositorio tenga un `README.md` con instrucciones de instalación y ejecución.

La nota importante sobre el Indicador 2 EP3 ("2 microservicios con stacks distintos"): en este proyecto todos los microservicios son Java/Spring Boot, pero el frontend en React (JavaScript) es el segundo stack distinto. Esta combinación Java + JavaScript/React cumple la exigencia de dos stacks tecnológicos diferentes.

El `repositorios.txt` debe estar en la raíz del monorepo con nombre exacto — es un ítem de checklist verificado explícitamente por el evaluador.

## Historia de Usuario

**Como** equipo de Grupo Cordillera
**necesitamos** crear los repositorios GitHub individuales y actualizar el archivo repositorios.txt
**para** cumplir con el checklist EP3 (ítem "repositorios.txt con URL y descripción del propósito de cada uno")

### Regla de Negocio Crítica
El archivo debe llamarse exactamente `repositorios.txt` y estar en la raíz del monorepo. Cada repositorio debe estar en modo público en GitHub (Settings → Visibility → Public). Cada repo necesita un `README.md` con instrucciones de instalación.

> **Contexto EP3:** Los repositorios GitHub públicos son la entrega final del trabajo. El evaluador accederá a las URLs del repositorios.txt para revisar el código de BFF (Spring Boot), data-service (Spring Boot + JPA), kpi-service (Factory Method), report-service (ExportadorFactory) y frontend (React 19 + Vite) de Grupo Cordillera — Indicadores 2 y 6 EP3.


---


## Alineación con la Rúbrica EP3 y el Caso Grupo Cordillera

### Caso de Negocio (Sección 3 del Caso)
Grupo Cordillera tiene información dispersa en múltiples sistemas (POS, SAP, ERP, CRM, e-commerce, inventario, finanzas) y necesita una plataforma que consolide datos de todas las sucursales para que la alta gerencia pueda tomar decisiones en tiempo real. Esta historia contribuye directamente a esa consolidación.

### Arquitectura del Sistema (Indicador 1 — 5%)
La solución implementa:
- **BFF Gateway** (puerto 8081): único punto de entrada desde el frontend React
- **data-service** (8083): almacena datos operacionales de sucursales → MySQL `data_db`
- **kpi-service** (8084): calcula KPIs con Factory Method → MySQL `kpi_db`
- **report-service** (8085): genera reportes ejecutivos con ExportadorFactory → MySQL `report_db`

### Frontend + Backend con Stacks Distintos (Indicador 2 — 10%)
- **Frontend:** React 19 + Vite (JavaScript)
- **Backend:** Spring Boot 4 + Java 21 (BFF + 3 microservicios)
- Los dos stacks son distintos → cumple el requisito de la rúbrica

### API REST + Persistencia JPA (Indicador 3 — 5%)
Cada microservicio tiene su propia BD MySQL y repositorios Spring Data JPA:
- `DatoRepository.findBySistemaOrigen()`, `findBySucursalId()`
- `KpiRepository.findByCategoria()`
- `ReporteRepository.findByArea()`, `findByAreaAndTipoAndAnioAndMes()`

### Pruebas Unitarias ≥ 60% (Indicador 4 — 10%)
- JaCoCo 0.8.13 configurado en los 4 microservicios Java
- Tests: @DataJpaTest (repositorios), @ExtendWith(MockitoExtension) (services), @WebMvcTest (controllers)
- Quality gate: `mvn verify` falla si la cobertura baja del 60%

### Patrones de Diseño Aplicados (Indicadores 5-8 — 70% defensa oral)
| Patrón | Dónde | Ventaja en tests |
|---|---|---|
| Repository | DatoRepository, KpiRepository, ReporteRepository | Tests con H2 in-memory — no requieren MySQL real |
| Factory Method | KpiFactory.obtenerCalculador() | Tests directos sin Spring — alta cobertura |
| Factory Method | ExportadorFactory.crearExportador() | 5 tests cubren 100% de la clase |
| Strategy | VentasCalculator, InventarioCalculator, etc. | Cada estrategia es testeable independientemente |
| Circuit Breaker | KpiClienteService (@CircuitBreaker) | Fallback testeable con mock — rama cubierta |
| Observer | DashboardContext (React) | Estado reactivo sin prop drilling |
| BFF | bff-gateway | Agrega 3 microservicios en 1 llamada — testeable con RestTemplate mock |


## Referencia Técnica del Proyecto — Stack y Versiones Exactas

> ⚠️ **IMPORTANTE para la IA:** Usar SIEMPRE estos nombres, versiones y rutas exactas del código real. No inventar nombres de métodos ni rutas.

### Stack de Tecnologías

| Componente | Tecnología | Versión |
|---|---|---|
| BFF Gateway | Spring Boot | 4.0.6 |
| data-service | Spring Boot | 4.0.6 |
| kpi-service | Spring Boot | 4.0.6 |
| report-service | Spring Boot | 4.0.6 |
| Lenguaje backend | Java | 25 |
| Frontend | React | 19.2.5 |
| Build tool frontend | Vite | 8.0.10 |
| Swagger / OpenAPI | springdoc-openapi-starter-webmvc-ui | 3.0.2 |
| Íconos frontend | lucide-react | 1.14.0 |
| ORM | Spring Data JPA + Hibernate | (Spring Boot 4) |
| Base de datos | MySQL (XAMPP local) | host.docker.internal:3306 |
| Tests BD | H2 in-memory | scope=test |
| Cobertura | JaCoCo | 0.8.13 |
| Resiliencia | Resilience4j | 2.4.0 (report-service) |
| Contenedores | Docker Compose | — |

### Rutas Internas de los Microservicios (NO usar /api/v1/...)


### Swagger UI — Documentación de la API

Todos los microservicios exponen Swagger UI automáticamente en:

| Servicio | URL Swagger UI |
|---|---|
| BFF Gateway | http://localhost:8081/swagger-ui.html |
| data-service | http://localhost:8083/swagger-ui.html |
| kpi-service | http://localhost:8084/swagger-ui.html |
| report-service | http://localhost:8085/swagger-ui.html |

La dependencia en cada `pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.0.2</version>
</dependency>
```

Esto genera automáticamente:
- `GET /v3/api-docs` → JSON de la especificación OpenAPI 3.0
- `GET /swagger-ui.html` → interfaz visual interactiva
- Todos los `@Tag`, `@Operation`, `@ApiResponse` del código fuente ya documentan los endpoints

**Para la colección Postman/Swagger del checklist EP3:** exportar desde `http://localhost:8081/v3/api-docs` (BFF) como JSON y guardarlo en `api-rest/coleccion-postman.json`.

| Servicio | Puerto interno | Rutas base |
|---|---|---|
| BFF Gateway | 8081 (expuesto al host) | /api/v1/datos, /api/v1/kpis, /api/v1/reportes, /api/dashboard, /api/auth |
| data-service | 8083 (interno Docker) | /api/datos, /api/datos/sistema/{origen}, /api/datos/sucursal/{id} |
| kpi-service | 8084 (interno Docker) | /api/kpis, /api/kpis/categoria/{cat} |
| report-service | 8085 (interno Docker) | /api/reportes, /api/reportes/generar, /api/reportes/{id}/exportar?formato=X, /api/reportes/area/{area} |

### Nombres Exactos de Métodos del Proyecto

```
// KpiFactory — método REAL (NO getCalculator)
kpiFactory.obtenerCalculador(String categoria)
// Categorías válidas: "ventas", "inventario", "logistica", "rentabilidad"
// Hace toLowerCase() internamente
// Lanza: IllegalArgumentException si categoría inválida

// ExportadorFactory — método REAL (NO getExportador)
exportadorFactory.crearExportador(String formato)
// Formatos válidos: "pdf", "excel"/"xls"/"xlsx", "json"
// Lanza: ResponseStatusException(BAD_REQUEST) si formato inválido o null

// KpiService — métodos REALES (NO crear/actualizar/eliminar)
kpiService.create(Kpi kpi)
kpiService.update(Long id, Kpi kpi)     // Lanza ResponseStatusException(NOT_FOUND)
kpiService.delete(Long id)              // deleteById directo SIN verificar existencia
kpiService.findByCategoria(String)
kpiService.findAll()
kpiService.findById(Long id)

// DatoService — métodos REALES
datoService.crear(Dato dato)
datoService.actualizar(Long id, Dato dato)  // Lanza NoSuchElementException (java.util)
datoService.eliminar(Long id)               // Verifica existencia ANTES de deleteById
datoService.buscarPorSistemaOrigen(String)  // NO findBySistemaOrigen
datoService.buscarPorSucursalId(Long)       // NO findBySucursalId

// ReporteService — métodos REALES
reporteService.generarReporte(Reporte)   // Idempotencia: retorna existente si (area,tipo,anio,mes) ya existe
reporteService.exportar(Long id, String formato)
reporteService.listarPorArea(String)     // NO findByArea
reporteService.buscarPorId(Long)         // Lanza ResponseStatusException(NOT_FOUND)

// DashboardService — manejo de fallos
// fetchList() tiene try/catch(Exception) → retorna FetchResult.failure()
// ResourceAccessException (timeout) ya está capturada → status="Degradado"
```

### Excepciones Reales del Proyecto

```
DatoService.actualizar()     → lanza java.util.NoSuchElementException
DatoService.eliminar()       → lanza java.util.NoSuchElementException
KpiService.findById()        → lanza ResponseStatusException(HttpStatus.NOT_FOUND)
KpiService.update()          → lanza ResponseStatusException(HttpStatus.NOT_FOUND)
ExportadorFactory            → lanza ResponseStatusException(HttpStatus.BAD_REQUEST)
ReporteService.buscarPorId() → lanza ResponseStatusException(HttpStatus.NOT_FOUND)
```

### Configuración de Tests

```properties
# src/test/resources/application-test.properties (para @DataJpaTest)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

### Patrón de Test del Profesor (obligatorio)

```java
// Service test
@ExtendWith(MockitoExtension.class)
class NombreServiceTest {
    @Mock
    private NombreRepository repo;
    @InjectMocks
    private NombreService service;

    @Test
    void metodo_escenario_resultado() {
        // Escenario: [descripción real del negocio Grupo Cordillera]
        // Arrange
        when(repo.findById(1L)).thenReturn(Optional.of(entidad));
        // Act
        var resultado = service.metodo(1L);
        // Assert
        assertNotNull(resultado);
        verify(repo, times(1)).findById(1L);
    }
}

// Controller test
@WebMvcTest(NombreController.class)
class NombreControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean NombreService service;

    @Test
    void endpoint_retornaStatus() throws Exception {
        when(service.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/ruta"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}

// Repository test
@DataJpaTest
class NombreRepositoryTest {
    @Autowired NombreRepository repo;

    @Test
    void queryMethod_retornaResultado() {
        // Persistir con repo.save() — H2 in-memory, no toca MySQL
        // Llamar al query method
        // Verificar resultado
    }
}
```

## Plan de Trabajo (orden OBLIGATORIO)

**La IA y el desarrollador DEBEN seguir este orden exacto.**

### Paso 0 — Sincronización y Rama Git

```bash
git checkout main
git pull origin main
git checkout -b feature/cord-132-repositorios-github
```

### Paso 1 — Verificar/crear repos en GitHub y hacerlos públicos

Ver detalle en Sub-task 1. Verificar que todos los repositorios están públicos.

### Paso 2 — Push del código actualizado a GitHub

Ver detalle en Sub-task 2.

```bash
git add .
git commit -m "EP3: tests unitarios JaCoCo >=60%, estados UI, documentacion"
git push origin main
```

### Paso 3 — Actualizar repositorios.txt

Ver detalle en Sub-task 3. Actualizar con URLs reales y descripciones.

### Paso 4 — Verificar todos los repositorios

```bash
# Verificar desde el browser que cada URL abre correctamente
# Verificar que cada repo tiene README.md con instrucciones
# Verificar que el ZIP de entrega incluye repositorios.txt en la raíz
```

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add repositorios.txt
git commit -m "docs(cord-132): repositorios.txt con URLs reales y descripciones para entrega EP3"
git push origin feature/cord-132-repositorios-github
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-132-repositorios-github \
  --title "[CORD-132] Repositorios GitHub y repositorios.txt para entrega EP3" \
  --body "## Cambios realizados\nRepositorios GitHub públicos, repositorios.txt actualizado, README.md de cada componente verificado\n\n## Entregables\nrepositorios.txt con 6 URLs, READMEs de todos los componentes\n\n## Checklist EP3\nrepositorios.txt nombre exacto ✓, repos públicos ✓, instrucciones de instalación ✓" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-132 y sub-tasks a "Finalizada"
- Agregar comentario con todas las URLs de repositorios públicos

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-206]: Crear repos en GitHub y preparar README.md de cada componente

**Objetivo:** Repositorios públicos con README.md completo para el checklist EP3

**Estrategia recomendada:** Un único monorepo público que contiene todo el proyecto.

**Crear en GitHub:**
1. Ir a github.com → New repository
2. Nombre: `cordillera-platform-parcial-2`
3. Visibility: **Public** (obligatorio para el evaluador)
4. No inicializar con README (el proyecto ya tiene uno)

**Verificar que cada componente tiene README.md:**

**`frontend/README.md` — secciones obligatorias del checklist:**
```markdown
# Frontend — Grupo Cordillera Dashboard

## Instalación
```bash
npm install
```

## Desarrollo
```bash
npm run dev
# Servidor en http://localhost:3000
```

## Build de producción
```bash
npm run build
# Genera dist/ para servir con Nginx
```

## Variables de entorno
```
VITE_API_URL=http://localhost:8081
```

## Rutas disponibles
| Ruta | Screen |
|------|--------|
| / | Dashboard principal |
| /kpis | Indicadores KPI |
| /reports | Reportes ejecutivos |
| /datos | Datos operacionales |
| /alerts | Alertas del sistema |
| /services | Estado de microservicios |
| /settings | Configuración |
| /users | Gestión de usuarios |

## Stack tecnológico
- React 19.2.5 + Vite 8.0.10
- react-router-dom 6.x
- lucide-react (íconos)
- Nginx (producción Docker)
```

**Para cada microservicio Java (bff-gateway, data-service, kpi-service, report-service):**
```markdown
# [nombre-servicio] — Grupo Cordillera

## Requisitos
- Java 21
- Maven 3.9+
- MySQL/XAMPP (puerto 3306)

## Ejecución local
```bash
mvn spring-boot:run
```

## Tests
```bash
mvn clean test          # ejecutar tests
mvn clean verify        # tests + JaCoCo coverage report
```

## Docker
```bash
# Desde la raíz del monorepo:
docker-compose up --build [nombre-servicio]
```
```

**Configurar repos como públicos:**
```
GitHub → Settings → Danger Zone → Change visibility → Make public
```

### Sub-task 2 [CORD-207]: Push del código actualizado a GitHub

**Objetivo:** Subir el código completo del Sprint 3 a los repositorios públicos

**Pasos:**

**Opción A — Monorepo único (recomendada):**
```bash
# Desde la raíz del monorepo
git add .
git commit -m "EP3: tests unitarios JaCoCo >=60%, estados UI, documentacion Sprint 3"
git push origin main

# Verificar en GitHub:
# - Los tests src/test/java están incluidos
# - Los documentos docs/ están incluidos
# - Los archivos docs/jacoco-*.png están incluidos (si existen)
```

**Verificar en GitHub tras el push:**
```bash
# Fecha de commit: debe ser del Sprint 3 (16-21 jun)
# Archivos de test deben estar visibles en src/test/java/
# Documentos deben estar en docs/
```

**Importante:** El evaluador accede a los URLs del `repositorios.txt` para verificar que el código está actualizado y es accesible públicamente.

### Sub-task 3 [CORD-208]: Actualizar repositorios.txt con URLs definitivas y descripciones

**Objetivo:** Crear el archivo `repositorios.txt` con el formato requerido por el checklist EP3

**Archivo a crear/actualizar:** `repositorios.txt` (en la raíz del monorepo)

**Contenido del archivo — reemplazar [equipo] con el usuario real de GitHub:**

```
REPOSITORIOS DEL PROYECTO — GRUPO CORDILLERA EP3
DSY1106 — Desarrollo Fullstack III
Integrantes: [Nombres del equipo]
Fecha: [Fecha de entrega EP3]

========================================================

1. Repositorio Principal (Monorepo)
   URL: https://github.com/[equipo]/cordillera-platform-parcial-2
   Descripción: Monorepo con todos los componentes del sistema (BFF Gateway,
   data-service, kpi-service, report-service, frontend React). Incluye
   docker-compose.yml, documentación completa y diagramas de arquitectura.
   Stack principal: Java 21 + Spring Boot 4 + React 19

2. Frontend (React 19 + Vite)
   URL: https://github.com/[equipo]/cordillera-platform-parcial-2/tree/main/frontend
   Descripción: SPA con React 19 que consume el BFF Gateway. Implementa
   DashboardContext (patrón Observer), react-router-dom v6 con 8 rutas,
   y estados UI (loading, error, degradado) en 6 pantallas.
   Stack: JavaScript, React 19, Vite, lucide-react, Nginx

3. BFF Gateway (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-platform-parcial-2/tree/main/bff-gateway
   Descripción: Backend For Frontend — único punto de entrada al sistema.
   Agrega datos de los 3 microservicios (patrón Aggregator), gestiona
   autenticación en memoria y sirve el frontend React como recurso estático.
   Stack: Java 21, Spring Boot 4, RestTemplate con timeouts Fail-Fast

4. Data Service (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-platform-parcial-2/tree/main/data-service
   Descripción: Microservicio de datos operacionales. Gestiona ventas,
   inventario y datos de sucursales POS/SAP/ERP/CRM en MySQL (data_db).
   Patrón: Repository (Spring Data JPA) con @PrePersist para fechaRegistro.
   Cobertura JaCoCo: >=60%

5. KPI Service (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-platform-parcial-2/tree/main/kpi-service
   Descripción: Microservicio de indicadores KPI. Calcula valores automáticamente
   con KpiFactory (patrón Factory Method) + Strategy (4 calculadoras).
   Cobertura JaCoCo: >=60%

6. Report Service (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-platform-parcial-2/tree/main/report-service
   Descripción: Microservicio de reportes ejecutivos. Exporta en PDF, Excel y JSON
   usando ExportadorFactory (Factory Method + Strategy). Implementa Circuit Breaker
   (Resilience4j) hacia kpi-service e idempotencia con constraint UNIQUE.
   Cobertura JaCoCo: >=60%

========================================================
Nota sobre stacks distintos (Indicador 2 EP3):
- Backend: Java 21 + Spring Boot 4 (microservicios)
- Frontend: JavaScript + React 19 + Vite (SPA)
Los 2 stacks son distintos: Java/JVM vs JavaScript/Node.
```

**Verificar:**
```bash
cat repositorios.txt
# Verificar que cada URL abre correctamente en el browser
# Verificar encoding UTF-8
```

---

## Criterios de Aceptación

**AC1: Repositorios públicos en GitHub**
- **Dado** que el evaluador recibe el `repositorios.txt`
- **Cuando** accede a cada URL listada
- **Entonces** cada repositorio carga correctamente en GitHub como público con README.md

**AC2: repositorios.txt completo**
- **Dado** el archivo `repositorios.txt` en la raíz del monorepo
- **Cuando** el evaluador lo abre
- **Entonces** contiene las URLs con descripción breve de cada componente

## DoD (Definition of Done)

- [ ] Rama `feature/cord-132-repositorios-github` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] Repositorios GitHub configurados como públicos
- [ ] `repositorios.txt` actualizado con URLs reales y descripciones (en la raíz)
- [ ] `frontend/README.md` con instrucciones: npm install, npm run dev, npm run build, rutas
- [ ] Cada repo de microservicio Java tiene README.md con mvn + docker
- [ ] Código del Sprint 3 pusheado a GitHub con fecha visible
- [ ] Ticket CORD-132 en Jira en estado "Finalizado" con URLs de todos los repositorios
