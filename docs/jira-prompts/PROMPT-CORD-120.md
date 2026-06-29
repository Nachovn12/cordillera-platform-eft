# Prompt de Implementación - CORD-120

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-120 — Generar reporte ejecutivo consolidado (POST /api/reportes/generar)
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Los directivos de Grupo Cordillera necesitan reportes ejecutivos que consoliden el rendimiento de toda la cadena retail. El `report-service` genera estos reportes consultando datos de KPIs al `kpi-service` vía HTTP, protegiéndose de caídas con un Circuit Breaker Resilience4j. Si el `kpi-service` falla, el reporte se genera igualmente en modo degradado (sin KPIs), garantizando disponibilidad.

La regla de idempotencia es crítica para el negocio: un reporte ya generado para un período (área + tipo + año + mes) no se duplica — el sistema retorna el existente. Esto evita reportes duplicados que confundirían a los directivos y garantiza unicidad en el historial de reportes.

Esta historia implementa los tests del proceso de generación de reportes, verificando tanto el caso feliz como la idempotencia y la tolerancia a fallos del Circuit Breaker.

## Historia de Usuario

**Como** ejecutivo del Grupo Cordillera
**quiero** generar un reporte que consolide datos remotos solicitando información al KPI Service via HTTP
**para** tener una visión centralizada del desempeño organizacional

### Regla de Negocio Crítica
Si ya existe un reporte para (area, tipo, anio, mes), `ReporteService.generarReporte()` retorna el existente sin invocar `saveAndFlush()`. Esta idempotencia está garantizada por la restricción UNIQUE en la tabla `reportes` de `report_db`.

> **Contexto EP3:** Los reportes ejecutivos consolidan KPIs y datos operacionales de todas las sucursales. El Circuit Breaker protege al report-service si el kpi-service falla — el reporte se genera igual en modo degradado. Tests validan la idempotencia: mismo reporte no se genera dos veces — Indicadores 2, 3, 4 y 8 EP3.


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
git checkout -b feature/cord-120-reporte-service-circuit-breaker-test
```

### Paso 1 — Verificar dependencias de ReporteService

Confirmar que `ReporteService.java` recibe `ReporteRepository` y `ExportadorFactory` como dependencias — **NO** `KpiClienteService`. El `KpiClienteService` es inyectado en el Controller.

```java
// ReporteService.java — dependencias esperadas
@Service
public class ReporteService {
    @Autowired
    private ReporteRepository reporteRepository;
    @Autowired
    private ExportadorFactory exportadorFactory;
    // NO tiene KpiClienteService aquí — está en el Controller
}
```

**Verificar:**
```bash
mvn -pl report-service compile
```

### Paso 2 — Crear ReporteServiceTest

Ver detalle en Sub-task 1. Ejecutar:
```bash
mvn test -pl report-service -Dtest=ReporteServiceTest
```

### Paso 3 — Crear KpiClienteServiceTest y ReporteControllerTest

Ver detalles en Sub-tasks 2 y 3. Ejecutar:
```bash
mvn test -pl report-service -Dtest=KpiClienteServiceTest
mvn test -pl report-service -Dtest=ReporteControllerTest
```

### Paso 4 — Validación con JaCoCo

```bash
mvn clean verify -pl report-service
```

**OJO:** Si la cobertura JaCoCo es menor al 60%, la IA NO DEBE avanzar al Paso 5. Debe iterar agregando más tests hasta superar el 60%.

Abrir en el navegador:
```
report-service/target/site/jacoco/index.html
```

Capturar screenshot y guardar en `docs/jacoco-report-service.png`.

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-120): tests ReporteService (idempotencia), KpiClienteService (circuit breaker) y ReporteController"
git push origin feature/cord-120-reporte-service-circuit-breaker-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-120-reporte-service-circuit-breaker-test \
  --title "[CORD-120] Tests generación reporte con Circuit Breaker e idempotencia" \
  --body "## Cambios realizados\nReporteServiceTest (idempotencia), KpiClienteServiceTest (circuit breaker fallback), ReporteControllerTest\n\n## Tests\nGenerarReporte caso nuevo + caso existente + fallback Circuit Breaker\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-120 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-165]: Crear ReporteServiceTest con generarReporte y mocks

**Objetivo:** Verificar idempotencia de generarReporte() — reporte nuevo vs reporte ya existente

**Archivo a crear:** `report-service/src/test/java/cl/duoc/cordillera/reportservice/service/ReporteServiceTest.java`

**Código:**
```java
package cl.duoc.cordillera.reportservice.service;

import cl.duoc.cordillera.reportservice.factory.ExportadorFactory;
import cl.duoc.cordillera.reportservice.model.Reporte;
import cl.duoc.cordillera.reportservice.repository.ReporteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private ExportadorFactory exportadorFactory;

    // IMPORTANTE: ReporteService NO recibe KpiClienteService — eso está en el Controller
    @InjectMocks
    private ReporteService reporteService;

    @Test
    void generarReporte_conAreaYValorValidos_guardaReporteNuevo() {
        // Arrange - Escenario: generar reporte ejecutivo de Finanzas para junio 2026
        Reporte reporte = new Reporte();
        reporte.setArea("Finanzas");
        reporte.setTipo("EJECUTIVO");
        reporte.setValor(BigDecimal.valueOf(380000));
        reporte.setAnio(2026);
        reporte.setMes(6);

        Reporte reporteConId = new Reporte();
        reporteConId.setId(1L);
        reporteConId.setArea("Finanzas");

        when(reporteRepository.findByAreaAndTipoAndAnioAndMes("Finanzas", "EJECUTIVO", 2026, 6))
            .thenReturn(Optional.empty());
        when(reporteRepository.saveAndFlush(any())).thenReturn(reporteConId);

        // Act
        Reporte resultado = reporteService.generarReporte(reporte);

        // Assert
        assertNotNull(resultado.getId());
        verify(reporteRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void generarReporte_conMismoPeriodo_retornaExistente() {
        // Arrange - Escenario: reporte de Finanzas junio 2026 ya fue generado (idempotencia)
        Reporte reporteExistente = new Reporte();
        reporteExistente.setId(99L);
        reporteExistente.setArea("Finanzas");
        reporteExistente.setTipo("EJECUTIVO");
        reporteExistente.setAnio(2026);
        reporteExistente.setMes(6);

        Reporte nuevoIntento = new Reporte();
        nuevoIntento.setArea("Finanzas");
        nuevoIntento.setTipo("EJECUTIVO");
        nuevoIntento.setAnio(2026);
        nuevoIntento.setMes(6);

        when(reporteRepository.findByAreaAndTipoAndAnioAndMes("Finanzas", "EJECUTIVO", 2026, 6))
            .thenReturn(Optional.of(reporteExistente));

        // Act
        Reporte resultado = reporteService.generarReporte(nuevoIntento);

        // Assert — saveAndFlush NO debe invocarse (idempotencia)
        verify(reporteRepository, never()).saveAndFlush(any());
        assertEquals(99L, resultado.getId());
    }
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ReporteServiceTest
```

### Sub-task 2 [CORD-166]: Crear KpiClienteServiceTest con Circuit Breaker fallback

**Objetivo:** Verificar que el fallback del Circuit Breaker retorna lista vacía cuando kpi-service falla

**Archivo a crear:** `report-service/src/test/java/cl/duoc/cordillera/reportservice/client/KpiClienteServiceTest.java`

**Código:**
```java
package cl.duoc.cordillera.reportservice.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
@ActiveProfiles("test")
class KpiClienteServiceTest {

    @Autowired
    private KpiClienteService kpiClienteService;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void obtenerKpis_conServidorDisponible_retornaLista() {
        // Arrange - Escenario: kpi-service responde correctamente
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo(org.hamcrest.Matchers.containsString("/api/kpis")))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess("[{\"id\":1,\"nombre\":\"Ventas\",\"valor\":380000}]",
                org.springframework.http.MediaType.APPLICATION_JSON));

        // Act
        List<?> resultado = kpiClienteService.obtenerKpis();

        // Assert
        assertNotNull(resultado);
    }

    @Test
    void obtenerKpis_conServidorCaido_retornaListaVaciaFallback() {
        // Arrange - Escenario: kpi-service no responde — Circuit Breaker activa fallback
        MockRestServiceServer server = MockRestServiceServer.createServer(restTemplate);
        server.expect(requestTo(org.hamcrest.Matchers.containsString("/api/kpis")))
            .andRespond(withServerError());

        // Act — @CircuitBreaker activa el fallback que retorna lista vacía
        List<?> resultado = kpiClienteService.obtenerKpis();

        // Assert — el fallback retorna lista vacía (NO propaga excepción)
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=KpiClienteServiceTest
```

### Sub-task 3 [CORD-167]: Crear ReporteControllerTest con validaciones de payload

**Objetivo:** Verificar contrato HTTP del ReporteController para generar y buscar reportes

**Archivo a crear:** `report-service/src/test/java/cl/duoc/cordillera/reportservice/controller/ReporteControllerTest.java`

**Código:**
```java
package cl.duoc.cordillera.reportservice.controller;

import cl.duoc.cordillera.reportservice.client.KpiClienteService;
import cl.duoc.cordillera.reportservice.model.Reporte;
import cl.duoc.cordillera.reportservice.service.ReporteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReporteService reporteService;

    // IMPORTANTE: ReporteController inyecta KpiClienteService — debe mockearse también
    @MockBean
    private KpiClienteService kpiClienteService;

    @Test
    void generar_conAreaYValorValidos_retorna201() throws Exception {
        // Arrange - Escenario: directivo genera reporte ejecutivo de Finanzas
        Reporte reporte = new Reporte();
        reporte.setArea("Finanzas");
        reporte.setValor(BigDecimal.valueOf(380000.00));

        Reporte reporteConId = new Reporte();
        reporteConId.setId(1L);
        reporteConId.setArea("Finanzas");
        when(reporteService.generarReporte(any())).thenReturn(reporteConId);

        // Act & Assert — ruta real: /api/reportes/generar (NO /api/v1/reportes)
        mockMvc.perform(post("/api/reportes/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reporte)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void buscarPorId_conIdExistente_retorna200() throws Exception {
        // Arrange
        Reporte reporte = new Reporte();
        reporte.setId(1L);
        reporte.setArea("Finanzas");
        when(reporteService.buscarPorId(1L)).thenReturn(reporte);

        // Act & Assert
        mockMvc.perform(get("/api/reportes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.area").value("Finanzas"));
    }
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ReporteControllerTest
```

---

## Criterios de Aceptación

**AC1: Invocación remota exitosa — reporte nuevo**
- **Dado** el kpi-service online y no existe reporte para (area="Finanzas", tipo="EJECUTIVO", anio=2026, mes=6)
- **Cuando** report-service genera reporte con POST /api/reportes/generar
- **Entonces** llama saveAndFlush() y retorna reporte con id generado

**AC2: Idempotencia — reporte ya existente**
- **Dado** que ya existe un reporte para (area="Finanzas", tipo="EJECUTIVO", anio=2026, mes=6)
- **Cuando** se intenta generar el mismo reporte
- **Entonces** ReporteService retorna el reporte existente sin invocar saveAndFlush()

**AC3: Fallback ante caída del kpi-service**
- **Dado** que kpi-service está caído
- **Cuando** se genera el reporte
- **Entonces** Resilience4j activa el fallback de KpiClienteService retornando lista vacía

## DoD (Definition of Done)

- [ ] Rama `feature/cord-120-reporte-service-circuit-breaker-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] ReporteServiceTest con generarReporte (caso nuevo e idempotencia) verde
- [ ] KpiClienteServiceTest con fallback del Circuit Breaker verde
- [ ] ReporteControllerTest con POST /api/reportes/generar verde
- [ ] Cobertura JaCoCo ≥ 60% verificada en report-service
- [ ] Ticket CORD-120 en Jira en estado "Finalizado" con comentario técnico
