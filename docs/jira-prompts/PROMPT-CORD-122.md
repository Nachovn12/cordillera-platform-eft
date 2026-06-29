# Prompt de Implementación - CORD-122

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-122 — CRUD reportes y filtrado por área
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Grupo Cordillera tiene múltiples áreas funcionales (Finanzas, Ventas, Logística, RRHH) que generan reportes ejecutivos independientes. Los directores de cada área necesitan poder consultar el historial de reportes de su área sin ver los de otras áreas. El filtrado por área es la funcionalidad más usada del módulo de reportes.

El `report-service` expone `GET /api/reportes/area/{area}` que usa el query method `findByArea(String area)` de Spring Data JPA — Spring genera la consulta SQL automáticamente a partir del nombre del método, sin escribir JPQL. La clave de este diseño es el patrón Repository: la capa de datos está completamente abstraída, el service solo llama `listarPorArea(String area)` y el controller expone el endpoint.

Esta historia verifica con `@DataJpaTest` + H2 que el query method funciona correctamente y con `@WebMvcTest` que el contrato HTTP respeta el acuerdo de lista vacía = 200 + [].

## Historia de Usuario

**Como** usuario del Grupo Cordillera
**quiero** acceder al historial de reportes y filtrarlos por área (Finanzas, Ventas, Logística)
**para** agilizar mis búsquedas históricas sin procesos manuales

### Regla de Negocio Crítica
El método del service es `listarPorArea(String area)` — **NO** `findByArea()`. El contrato REST: lista vacía retorna HTTP 200 + `[]` — **NUNCA** 404. Área inexistente es un estado válido del sistema.

> **Contexto EP3:** El historial de reportes por área (Finanzas, Ventas, Logística) permite al equipo ejecutivo auditar el desempeño histórico de cada división de Grupo Cordillera. Los tests de Repository Pattern con H2 demuestran que JPA es independiente del entorno — Indicadores 3 y 4 EP3.


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
git checkout -b feature/cord-122-reporte-repository-area-test
```

### Paso 1 — Verificar ReporteRepository y ReporteController

Confirmar que `ReporteRepository` tiene el query method `findByArea(String area)` y que `ReporteController` expone `@GetMapping("/area/{area}")`.

```java
// ReporteRepository.java
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByArea(String area);
    // Otros query methods...
}

// En ReporteController.java
@GetMapping("/area/{area}")
public ResponseEntity<List<Reporte>> listarPorArea(@PathVariable String area) {
    return ResponseEntity.ok(reporteService.listarPorArea(area));
}
```

**Verificar:**
```bash
mvn -pl report-service compile
```

### Paso 2 — Crear ReporteRepositoryTest con @DataJpaTest

Ver detalle en Sub-task 1.

```bash
mvn test -pl report-service -Dtest=ReporteRepositoryTest
```

### Paso 3 — Agregar tests MockMvc de /area/{area} a ReporteControllerTest

Ver detalle en Sub-tasks 2 y 3.

```bash
mvn test -pl report-service -Dtest=ReporteControllerTest
```

### Paso 4 — Validación con JaCoCo

```bash
mvn clean verify -pl report-service
```

**OJO:** Si la cobertura JaCoCo es menor al 60%, la IA NO DEBE avanzar al Paso 5.

Abrir en el navegador:
```
report-service/target/site/jacoco/index.html
```

Capturar screenshot y guardar en `docs/jacoco-report-service.png`.

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-122): tests ReporteRepository findByArea y Controller /area/{area} con contrato 200+[]"
git push origin feature/cord-122-reporte-repository-area-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-122-reporte-repository-area-test \
  --title "[CORD-122] Tests filtrado reportes por área — Repository + Controller" \
  --body "## Cambios realizados\nReporteRepositoryTest (@DataJpaTest findByArea), ReporteControllerTest (/area/{area})\n\n## Tests\nfindByArea con resultados + area vacía, MockMvc 200+lista y 200+[]\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-122 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-171]: Crear ReporteRepositoryTest con @DataJpaTest y findByArea

**Objetivo:** Verificar query method findByArea() con datos reales en H2 en memoria

**Archivo a crear:** `report-service/src/test/java/cl/duoc/cordillera/reportservice/repository/ReporteRepositoryTest.java`

**Código:**
```java
package cl.duoc.cordillera.reportservice.repository;

import cl.duoc.cordillera.reportservice.model.Reporte;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReporteRepositoryTest {

    @Autowired
    private ReporteRepository reporteRepository;

    @Test
    void findByArea_conAreaExistente_retornaLista() {
        // Arrange - Escenario: 2 reportes de Finanzas y 1 de Ventas
        Reporte r1 = crearReporte("Finanzas", "EJECUTIVO", 2026, 5, BigDecimal.valueOf(350000));
        Reporte r2 = crearReporte("Finanzas", "OPERATIVO", 2026, 6, BigDecimal.valueOf(380000));
        Reporte r3 = crearReporte("Ventas", "EJECUTIVO", 2026, 6, BigDecimal.valueOf(420000));
        reporteRepository.save(r1);
        reporteRepository.save(r2);
        reporteRepository.save(r3);

        // Act — Spring Data JPA genera el SQL automáticamente por nombre del método
        List<Reporte> resultado = reporteRepository.findByArea("Finanzas");

        // Assert
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(r -> "Finanzas".equals(r.getArea())));
    }

    @Test
    void findByArea_conAreaInexistente_retornaListaVacia() {
        // Arrange - Escenario: área RRHH no tiene reportes aún (estado válido)
        Reporte r = crearReporte("Finanzas", "EJECUTIVO", 2026, 6, BigDecimal.valueOf(380000));
        reporteRepository.save(r);

        // Act
        List<Reporte> resultado = reporteRepository.findByArea("RRHH");

        // Assert — lista vacía no debe ser null
        assertNotNull(resultado);
        assertEquals(0, resultado.size());
    }

    private Reporte crearReporte(String area, String tipo, int anio, int mes, BigDecimal valor) {
        Reporte r = new Reporte();
        r.setArea(area);
        r.setTipo(tipo);
        r.setAnio(anio);
        r.setMes(mes);
        r.setValor(valor);
        r.setTitulo("Reporte " + area + " " + mes + "/" + anio);
        return r;
    }
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ReporteRepositoryTest
```

### Sub-task 2 [CORD-172]: Agregar tests MockMvc de GET /api/reportes/area/{area}

**Objetivo:** Verificar contrato HTTP del endpoint de filtrado por área

**Código a agregar en ReporteControllerTest:**
```java
@Test
void listarPorArea_Finanzas_retorna200ConLista() throws Exception {
    // Arrange - Escenario: director de finanzas consulta sus reportes
    Reporte r1 = new Reporte();
    r1.setId(1L);
    r1.setArea("Finanzas");
    r1.setAnio(2026);
    r1.setMes(6);

    Reporte r2 = new Reporte();
    r2.setId(2L);
    r2.setArea("Finanzas");
    r2.setAnio(2026);
    r2.setMes(5);

    // IMPORTANTE: método del service es listarPorArea(), NO findByArea()
    when(reporteService.listarPorArea("Finanzas")).thenReturn(List.of(r1, r2));

    // ruta real: /api/reportes/area/{area} (path variable)
    mockMvc.perform(get("/api/reportes/area/Finanzas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2));
}

@Test
void listarPorArea_AreaInexistente_retorna200Vacio() throws Exception {
    // Arrange - Escenario: área RRHH sin reportes (estado válido del negocio)
    when(reporteService.listarPorArea("RRHH")).thenReturn(List.of());

    // CONTRATO: lista vacía = 200 + [] (NO 404)
    mockMvc.perform(get("/api/reportes/area/RRHH"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ReporteControllerTest
```

### Sub-task 3 [CORD-173]: Cubrir caso vacío en listado por área

**Objetivo:** Asegurar que la lista vacía no es null y retorna 200 explícito en ambas capas

**Código a agregar en ReporteRepositoryTest:**
```java
@Test
void findByArea_sinResultados_retornaListaVaciaNoNull() {
    // Escenario: nuevo área sin reportes — la lista vacía no debe ser null ni lanzar excepción
    List<Reporte> resultado = reporteRepository.findByArea("RRHH");

    // IMPORTANTE: assertNotNull primero — la lista vacía es distinta a null
    assertNotNull(resultado);
    assertEquals(0, resultado.size());
    // El contrato REST (200 + []) está garantizado por el Controller
}
```

**Nota para documentar en Postman:** El endpoint `GET /api/reportes/area/{area}` retorna 200 + [] para áreas sin reportes. La decisión es correcta para recursos tipo colección — 404 se usaría para un recurso singular inexistente.

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ReporteRepositoryTest
```

---

## Criterios de Aceptación

**AC1: Filtrado exitoso por área**
- **Dado** reportes generados con area="Finanzas" en report_db
- **Cuando** busco GET /api/reportes/area/Finanzas
- **Entonces** retorna 200 OK con la lista correcta

**AC2: Área inexistente retorna lista vacía**
- **Dado** que no existen reportes con area="RRHH"
- **Cuando** busco GET /api/reportes/area/RRHH
- **Entonces** retorna 200 OK con `[]` (NO 404)

## DoD (Definition of Done)

- [ ] Rama `feature/cord-122-reporte-repository-area-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] ReporteRepositoryTest con findByArea (área con resultados y área vacía) verdes
- [ ] Tests MockMvc GET /api/reportes/area/{area} (200 con lista y 200 con []) verdes
- [ ] Cobertura JaCoCo ≥ 60% verificada en report-service
- [ ] Ticket CORD-122 en Jira en estado "Finalizado" con comentario técnico
