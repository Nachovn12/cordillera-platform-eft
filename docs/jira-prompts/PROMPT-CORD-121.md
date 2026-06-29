# Prompt de Implementación - CORD-121

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-121 — Exportar reporte PDF/Excel/JSON con ExportadorFactory
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Los directivos de Grupo Cordillera consumen los reportes ejecutivos en distintos contextos: el gerente general los necesita en PDF para presentaciones al directorio, el equipo de análisis los prefiere en Excel para cruzarlos con otras fuentes de datos, y los sistemas automatizados los consumen en JSON. El `report-service` soporta los tres formatos mediante el patrón **Factory Method + Strategy** implementado en `ExportadorFactory`.

`ExportadorFactory.crearExportador(String formato)` selecciona en tiempo de ejecución el exportador concreto: `PdfExportador`, `ExcelExportador` o `JsonExportador`. Si el formato es inválido, lanza `ResponseStatusException(BAD_REQUEST)` — no `IllegalArgumentException`. Este diseño permite agregar nuevos formatos sin modificar el `ReporteService` ni el `ReporteController`.

Esta historia implementa los tests exhaustivos de la factory y sus integraciones, cubriendo el 100% de `ExportadorFactory` con 5 tests que contribuyen significativamente al quality gate JaCoCo del `report-service`.

## Historia de Usuario

**Como** directivo del Grupo Cordillera
**quiero** descargar mis reportes ejecutivos en distintos formatos (PDF, Excel, JSON)
**para** manipularlos o presentarlos según la situación usando una fábrica de exportación

### Regla de Negocio Crítica
El método de la factory es `crearExportador(String formato)` — **NO** `getExportador()`. Formato inválido lanza `ResponseStatusException(HttpStatus.BAD_REQUEST)` — **NO** `IllegalArgumentException`.

> **Contexto EP3:** Los directivos de Grupo Cordillera necesitan exportar reportes en PDF para presentaciones ejecutivas, Excel para análisis financiero y JSON para integración con otros sistemas. El patrón Factory Method + Strategy de ExportadorFactory es el más evaluado en la defensa oral — Indicadores 2, 4 y 8 EP3.


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
git checkout -b feature/cord-121-exportador-factory-test
```

### Paso 1 — Verificar ExportadorFactory.crearExportador()

Confirmar que `ExportadorFactory.java` tiene el método `crearExportador(String formato)` y lanza `ResponseStatusException(BAD_REQUEST)` para formatos inválidos.

```java
// ExportadorFactory.java — estructura esperada
@Component
public class ExportadorFactory {
    public Exportador crearExportador(String formato) {
        // IMPORTANTE: método es crearExportador(), NO getExportador()
        if (formato == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato no puede ser null");
        }
        return switch (formato.toLowerCase()) {
            case "pdf" -> new PdfExportador();
            case "excel", "xls", "xlsx" -> new ExcelExportador();
            case "json" -> new JsonExportador();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Formato no soportado: " + formato);
        };
    }
}
```

**Verificar:**
```bash
mvn -pl report-service compile
```

### Paso 2 — Crear ExportadorFactoryTest con 5 tests

Ver detalle en Sub-task 1. Ejecutar:
```bash
mvn test -pl report-service -Dtest=ExportadorFactoryTest
```

### Paso 3 — Agregar tests de exportar() a ReporteServiceTest y ReporteControllerTest

Ver detalles en Sub-tasks 2 y 3. Ejecutar:
```bash
mvn test -pl report-service -Dtest=ReporteServiceTest
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
git commit -m "feat(cord-121): tests ExportadorFactory (Factory+Strategy) y exportar en ReporteService y Controller"
git push origin feature/cord-121-exportador-factory-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-121-exportador-factory-test \
  --title "[CORD-121] Tests ExportadorFactory PDF/Excel/JSON con Factory Method + Strategy" \
  --body "## Cambios realizados\nExportadorFactoryTest (5 tests), ReporteServiceTest (exportar), ReporteControllerTest (formatos + inválido)\n\n## Tests\n5 tests factory + tests service y controller\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-121 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-168]: Crear ExportadorFactoryTest con 5 cases

**Objetivo:** Cubrir 100% de ExportadorFactory sin Spring — instanciar directamente

**Archivo a crear:** `report-service/src/test/java/cl/duoc/cordillera/reportservice/factory/ExportadorFactoryTest.java`

**Código:**
```java
package cl.duoc.cordillera.reportservice.factory;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class ExportadorFactoryTest {
    // Sin @ExtendWith ni Spring — se instancia directamente
    private final ExportadorFactory factory = new ExportadorFactory();

    @Test
    void crearExportador_PDF_retornaPdfExportador() {
        // Escenario: directivo solicita reporte en PDF para presentación al directorio
        // IMPORTANTE: método es crearExportador(), NO getExportador()
        Exportador exportador = factory.crearExportador("pdf");
        assertInstanceOf(PdfExportador.class, exportador);
    }

    @Test
    void crearExportador_EXCEL_retornaExcelExportador() {
        // Escenario: analista solicita reporte en Excel para análisis de datos
        // También acepta "xls" y "xlsx"
        Exportador exportador = factory.crearExportador("excel");
        assertInstanceOf(ExcelExportador.class, exportador);
    }

    @Test
    void crearExportador_JSON_retornaJsonExportador() {
        // Escenario: sistema automatizado consume el reporte en JSON
        Exportador exportador = factory.crearExportador("json");
        assertInstanceOf(JsonExportador.class, exportador);
    }

    @Test
    void crearExportador_formatoInvalido_lanzaResponseStatusException() {
        // Escenario: cliente envía formato "xml" que no está soportado
        // IMPORTANTE: lanza ResponseStatusException(BAD_REQUEST), NO IllegalArgumentException
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> factory.crearExportador("xml"));
        assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void crearExportador_formatoNull_lanzaResponseStatusException() {
        // Escenario: cliente no envía el parámetro formato
        assertThrows(ResponseStatusException.class,
            () -> factory.crearExportador(null));
    }
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ExportadorFactoryTest
```

### Sub-task 2 [CORD-169]: Agregar tests de exportar en ReporteServiceTest

**Objetivo:** Verificar que exportar() invoca crearExportador() y retorna bytes

**Código a agregar en ReporteServiceTest:**
```java
@Test
void exportar_conReporteExistente_retornaBytes() {
    // Arrange - Escenario: directivo descarga reporte en PDF
    Reporte reporte = new Reporte();
    reporte.setId(1L);
    reporte.setArea("Finanzas");

    Exportador mockExportador = mock(Exportador.class);
    when(mockExportador.exportar(reporte)).thenReturn(new byte[]{1, 2, 3});
    // IMPORTANTE: método es crearExportador(), NO getExportador()
    when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
    when(exportadorFactory.crearExportador("pdf")).thenReturn(mockExportador);

    // Act
    byte[] resultado = reporteService.exportar(1L, "pdf");

    // Assert
    assertNotNull(resultado);
    assertEquals(3, resultado.length);
}

@Test
void exportar_conFormatoInvalido_propagaResponseStatusException() {
    // Arrange - Escenario: solicitud de exportación con formato no soportado
    Reporte reporte = new Reporte();
    when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));
    when(exportadorFactory.crearExportador("xml"))
        .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

    // Act & Assert
    assertThrows(ResponseStatusException.class,
        () -> reporteService.exportar(1L, "xml"));
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ReporteServiceTest
```

### Sub-task 3 [CORD-170]: Agregar tests MockMvc de GET /{id}/exportar

**Objetivo:** Verificar Content-Type correcto para cada formato y 400 para formato inválido

**Código a agregar en ReporteControllerTest:**
```java
@Test
void exportar_formatoPDF_retorna200ConBytesYContentType() throws Exception {
    // Arrange - ruta real: GET /api/reportes/{id}/exportar?formato=X (query param)
    when(reporteService.exportar(1L, "pdf")).thenReturn(new byte[]{1, 2, 3});

    mockMvc.perform(get("/api/reportes/1/exportar?formato=pdf"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type",
            org.hamcrest.Matchers.containsString("application/pdf")))
        .andExpect(header().exists("Content-Disposition"));
}

@Test
void exportar_formatoEXCEL_retorna200() throws Exception {
    when(reporteService.exportar(1L, "excel")).thenReturn(new byte[]{1, 2, 3});

    mockMvc.perform(get("/api/reportes/1/exportar?formato=excel"))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type",
            org.hamcrest.Matchers.containsString("application/vnd.ms-excel")));
}

@Test
void exportar_formatoInvalido_retorna400() throws Exception {
    // IMPORTANTE: ResponseStatusException(BAD_REQUEST), NO IllegalArgumentException
    when(reporteService.exportar(1L, "xml"))
        .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

    mockMvc.perform(get("/api/reportes/1/exportar?formato=xml"))
        .andExpect(status().isBadRequest());
}
```

**Verificar:**
```bash
mvn test -pl report-service -Dtest=ReporteControllerTest
```

---

## Criterios de Aceptación

**AC1: Exportación exitosa en los 3 formatos**
- **Dado** un reporte existente con id=1
- **Cuando** llamo GET /api/reportes/1/exportar?formato=pdf
- **Entonces** recibo bytes con Content-Type=application/pdf y Content-Disposition=attachment

**AC2: Formato inválido retorna 400**
- **Dado** un formato no soportado "xml"
- **Cuando** llamo GET /api/reportes/1/exportar?formato=xml
- **Entonces** retorna HTTP 400 con ResponseStatusException(BAD_REQUEST)

## DoD (Definition of Done)

- [ ] Rama `feature/cord-121-exportador-factory-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] ExportadorFactoryTest con 5 tests verdes
- [ ] Tests de exportar() en ReporteServiceTest verdes
- [ ] Tests MockMvc GET /{id}/exportar con los 3 formatos + inválido verdes
- [ ] Cobertura JaCoCo ≥ 60% verificada en report-service
- [ ] Ticket CORD-121 en Jira en estado "Finalizado" con comentario técnico
