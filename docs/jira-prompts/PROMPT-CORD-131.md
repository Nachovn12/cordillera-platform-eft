# Prompt de Implementación - CORD-131

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-131 — Diagrama de arquitectura actualizado (FE+BE+REST+persistencia)
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El diagrama de arquitectura es el artefacto más importante para la defensa oral del EP3: cuando el evaluador pregunta "¿cómo está estructurado el sistema?", el integrante señala el diagrama y explica cada componente. Un diagrama claro y correcto puede ser la diferencia entre pasar y reprobar la defensa.

El sistema de Grupo Cordillera tiene una arquitectura de microservicios real que debe quedar visible: el frontend React comunicándose exclusivamente con el BFF Gateway (patrón BFF), el BFF consultando los 3 microservicios internos (Aggregator Pattern), cada microservicio con su propia base de datos MySQL (Database per Service), todo corriendo en una red Docker interna con solo el BFF expuesto al host.

El checklist EP3 requiere exactamente `diagrama-arquitectura.png` (o .jpg/.pdf) en el ZIP de entrega. El Indicador 1 de la rúbrica evalúa específicamente este artefacto.

## Historia de Usuario

**Como** equipo de Grupo Cordillera
**necesitamos** crear el diagrama de arquitectura final que muestre Frontend, BFF, los 3 microservicios, API REST y bases de datos
**para** cumplir con el checklist EP3 (ítem "diagrama-arquitectura.png") y el Indicador 1 de la rúbrica

### Regla de Negocio Crítica
El diagrama debe mostrar los patrones implementados: **BFF** (único punto de entrada), **Database per Service** (3 BDs separadas), **Circuit Breaker** (flechas con icono CB), **API REST** (todas las flechas etiquetadas). El archivo debe llamarse exactamente `docs/diagrama-arquitectura.png`.

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
git checkout -b feature/cord-131-diagrama-arquitectura
```

### Paso 1 — Crear el diagrama en draw.io (app.diagrams.net)

Ver detalle en Sub-task 1. Herramienta: [app.diagrams.net](https://app.diagrams.net) (gratuito, online, sin instalación).

### Paso 2 — Exportar a PNG y PDF

Ver detalle en Sub-task 2.

### Paso 3 — Actualizar README.md con sección Arquitectura

Ver detalle en Sub-task 3.

### Paso 4 — Verificar archivos

```bash
ls docs/diagrama-arquitectura.png
ls docs/diagrama-arquitectura.pdf
ls docs/diagrama-arquitectura.drawio

# Verificar que el PNG tiene buena resolución (mínimo 1200px de ancho)
```

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "docs(cord-131): diagrama arquitectura BFF+microservicios+BD con patrones anotados"
git push origin feature/cord-131-diagrama-arquitectura
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-131-diagrama-arquitectura \
  --title "[CORD-131] Diagrama de arquitectura — BFF, microservicios, persistencia y patrones" \
  --body "## Cambios realizados\ndraw.io fuente, exportación PNG y PDF, README.md actualizado con sección Arquitectura\n\n## Entregables\ndocs/diagrama-arquitectura.drawio, docs/diagrama-arquitectura.png, docs/diagrama-arquitectura.pdf\n\n## Checklist EP3\nNombre exacto verificado: diagrama-arquitectura.png ✓, patrones anotados ✓" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-131 y sub-tasks a "Finalizada"
- Agregar comentario con link al PR y confirmar que el diagrama muestra todos los componentes

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-203]: Crear el diagrama en draw.io o Excalidraw

**Objetivo:** Diagrama de arquitectura completo que cumpla el Indicador 1 EP3

**Herramienta:** [app.diagrams.net](https://app.diagrams.net) → File → New → en blanco

**Componentes OBLIGATORIOS del diagrama:**

```
┌──────────────────────────────────────────────────────────────────────┐
│  Docker Network: cordillera-network (red interna)                   │
│                                                                      │
│  ┌─────────────────┐     HTTP/REST     ┌─────────────────────────┐  │
│  │  Browser        │ ←────────────────→│  BFF Gateway            │  │
│  │  React 19+Vite  │                   │  Spring Boot 4 · Java 21│  │
│  │  Nginx          │                   │  Puerto: 8081 (expuesto)│  │
│  │  Puerto: 3000   │                   │  Patrón: BFF/API Gateway│  │
│  └─────────────────┘                   └────────┬────────────────┘  │
│                                                 │ HTTP/REST          │
│                          ┌──────────────────────┼──────────┐         │
│                          │                      │          │         │
│                    ┌─────▼──────┐  ┌────────────▼─┐  ┌────▼───────┐ │
│                    │data-service│  │ kpi-service  │  │report-serv │ │
│                    │Spring Boot │  │ Spring Boot  │  │Spring Boot │ │
│                    │Puerto:8083 │  │ Puerto:8084  │  │Puerto:8085 │ │
│                    │           │  │ 🔄CircuitBrkr│  │🔄CircuitBrk│ │
│                    └─────┬──────┘  └──────┬───────┘  └──────┬─────┘ │
│                          │               │                  │        │
└──────────────────────────│───────────────│──────────────────│────────┘
                           │ JDBC/JPA      │ JDBC/JPA         │JDBC/JPA
                    ┌──────▼──────┐ ┌──────▼──────┐ ┌────────▼──────┐
                    │  data_db    │ │   kpi_db    │ │  report_db    │
                    │  MySQL      │ │   MySQL     │ │  MySQL        │
                    │  Host:3306  │ │  Host:3306  │ │  Host:3306    │
                    │  (XAMPP)    │ │  (XAMPP)    │ │  (XAMPP)      │
                    └─────────────┘ └─────────────┘ └───────────────┘

Leyenda de patrones:
• BFF: único punto de entrada al sistema
• Database per Service: 3 BDs independientes sin FK cross-service
• Circuit Breaker 🔄: kpi-service y report-service tienen CB
• Factory Method: KpiFactory + ExportadorFactory (anotar en los servicios)
• Repository: Spring Data JPA abstrae la persistencia
```

**En draw.io, usar colores:**
- Azul: Frontend React
- Verde: BFF Gateway
- Naranja: 3 microservicios
- Gris: 3 bases de datos MySQL
- Rectángulo punteado: Docker Network

**Guardar como:** `docs/diagrama-arquitectura.drawio`

### Sub-task 2 [CORD-204]: Exportar a PNG y PDF e incluir en docs/

**Objetivo:** Generar los archivos de entrega en los formatos requeridos

**Exportar PNG:**
1. En draw.io: File → Export As → PNG
2. Activar "Scale" 2x o 3x (mínimo 1200px de ancho)
3. Fondo blanco activado
4. Guardar como `docs/diagrama-arquitectura.png`

**Exportar PDF:**
1. File → Export As → PDF
2. Escala "fit page" (todo el diagrama en una página)
3. Guardar como `docs/diagrama-arquitectura.pdf`

**Verificar calidad:**
```
- Abrir PNG: se leen claramente los nombres de todos los componentes y puertos
- Abrir PDF: hacer zoom — las letras no se pixelan (es vectorial)
- Tamaño PNG: mínimo 1200px de ancho
```

**Nota del checklist EP3:** El evaluador busca "diagrama-arquitectura.png (o .jpg / .pdf) mostrando frontend ↔ BFF ↔ microservicio1 ↔ microservicio2 ↔ BD". Tener los 3 microservicios es mejor que el mínimo de 2 que pide la rúbrica.

### Sub-task 3 [CORD-205]: Actualizar README.md principal con sección Arquitectura

**Objetivo:** El README del monorepo debe mostrar el diagrama renderizado en GitHub

**Archivo a modificar:** `cordillera-platform-parcial-2/README.md`

**Sección a agregar:**
```markdown
## Arquitectura del Sistema

![Diagrama de Arquitectura Cordillera](docs/diagrama-arquitectura.png)

El Frontend React se comunica exclusivamente con el **BFF Gateway** (puerto 8081),
que actúa como único punto de entrada. El BFF agrega datos de los 3 microservicios
internos (data-service, kpi-service, report-service), cada uno con su propia
base de datos MySQL (patrón **Database per Service**).

### Puertos

| Componente | Puerto | Acceso |
|------------|--------|--------|
| Frontend React | 3000 | Expuesto al host |
| BFF Gateway | 8081 | Expuesto al host |
| data-service | 8083 | Solo red Docker interna |
| kpi-service | 8084 | Solo red Docker interna |
| report-service | 8085 | Solo red Docker interna |
| MySQL/XAMPP | 3306 | Host Windows |

### Patrones implementados

| Patrón | Ubicación | Beneficio |
|--------|-----------|-----------|
| BFF (Backend For Frontend) | bff-gateway | Frontend hace 1 llamada en vez de 3 |
| Factory Method | KpiFactory, ExportadorFactory | Agregar calculadora/exportador sin modificar Service |
| Strategy | VentasCalculator, PdfExportador... | Comportamiento intercambiable en runtime |
| Circuit Breaker | kpi-service → data-service, report-service → kpi-service | Tolerancia a fallos parciales |
| Repository | DatoRepository, KpiRepository, ReporteRepository | Abstracción de la capa de datos |
| Observer | DashboardContext (React) | Estado centralizado sin prop drilling |

[Editar diagrama en draw.io](docs/diagrama-arquitectura.drawio)
```

**Verificar en GitHub:**
```bash
git push
# Verificar en GitHub que el README renderiza la imagen correctamente
# La imagen debe estar en docs/diagrama-arquitectura.png (ruta relativa)
```

---

## Criterios de Aceptación

**AC1: Diagrama correcto y completo**
- **Dado** el diagrama de arquitectura exportado
- **Cuando** el evaluador lo revisa
- **Entonces** se ven: Frontend, BFF Gateway, 3 microservicios, 3 bases de datos, flechas con protocolo, puertos, y patrones anotados

**AC2: Archivos en docs/ con nombres exactos**
- **Dado** que el equipo exportó el diagrama
- **Cuando** se arma el ZIP de entrega EP3
- **Entonces** existen `docs/diagrama-arquitectura.png` Y `docs/diagrama-arquitectura.pdf`

## DoD (Definition of Done)

- [ ] Rama `feature/cord-131-diagrama-arquitectura` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] `docs/diagrama-arquitectura.drawio` creado (fuente editable)
- [ ] `docs/diagrama-arquitectura.png` exportado con buena resolución
- [ ] `docs/diagrama-arquitectura.pdf` exportado vectorial
- [ ] Diagrama muestra Frontend + BFF + 3 microservicios + 3 BDs + Docker network
- [ ] Patrones BFF, Database per Service y Circuit Breaker identificables
- [ ] README.md principal muestra el diagrama con imagen renderizada
- [ ] Ticket CORD-131 en Jira en estado "Finalizado" con comentario técnico
