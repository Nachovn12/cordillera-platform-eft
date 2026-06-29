# Prompt de Implementación - CORD-117

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-117 — Consultar KPIs filtrados por categoría (Factory Method)
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Grupo Cordillera necesita segmentar sus indicadores de rendimiento por categoría para que cada área del negocio vea sus métricas relevantes: el área comercial ve KPIs de Ventas, logística ve KPIs de Logística, la gerencia financiera ve KPIs de Rentabilidad. Sin este filtrado, el dashboard mostraría todos los indicadores mezclados, dificultando el análisis ejecutivo.

El `kpi-service` implementa el patrón **Factory Method** mediante `KpiFactory.obtenerCalculador(String categoria)`, que devuelve el calculador correcto (VentasCalculator, InventarioCalculator, LogisticaCalculator, RentabilidadCalculator) según la categoría solicitada. Esta arquitectura permite agregar nuevas categorías de KPIs sin modificar `KpiService` — principio Open/Closed que el evaluador verificará durante la defensa oral.

Esta historia cubre los tests del patrón Factory Method, incluyendo el caso donde se solicita una categoría inválida — que debe lanzar `IllegalArgumentException` porque no existe una calculadora para ese caso.

## Historia de Usuario

**Como** BFF Gateway del Grupo Cordillera
**quiero** consultar KPIs específicos filtrados por categoría (Ventas, Logística, Rentabilidad)
**para** armar la sección de métricas particionadas en el dashboard del frontend

### Regla de Negocio Crítica
El método de la factory es `obtenerCalculador(String categoria)` — **NO** `getCalculator()`. Las 4 categorías válidas son: `ventas`, `inventario`, `logistica`, `rentabilidad` (minúsculas — la factory hace `toLowerCase()` internamente). Categoría inválida lanza `IllegalArgumentException`.

> **Contexto EP3:** Los KPIs son el corazón del sistema de monitoreo de Grupo Cordillera: Ventas, Inventario, Logística y Rentabilidad. El patrón Factory Method permite que el BFF solicite KPIs por categoría sin conocer la implementación concreta del cálculo — Indicadores 2, 4 y 8 EP3.


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
git checkout -b feature/cord-117-kpi-factory-test
```

### Paso 1 — Verificar KpiFactory.obtenerCalculador()

Confirmar que `KpiFactory.java` tiene el método `obtenerCalculador(String categoria)` con las 4 calculadoras registradas y que hace `toLowerCase()` internamente.

```java
// KpiFactory.java — estructura esperada
@Component
public class KpiFactory {
    private final Map<String, KpiCalculator> calculadores = new HashMap<>();

    public KpiFactory() {
        calculadores.put("ventas", new VentasCalculator());
        calculadores.put("inventario", new InventarioCalculator());
        calculadores.put("logistica", new LogisticaCalculator());
        calculadores.put("rentabilidad", new RentabilidadCalculator());
    }

    public KpiCalculator obtenerCalculador(String categoria) {
        // IMPORTANTE: el método se llama obtenerCalculador, NO getCalculator
        KpiCalculator calc = calculadores.get(categoria.toLowerCase());
        if (calc == null) {
            throw new IllegalArgumentException("Categoria no soportada: " + categoria);
        }
        return calc;
    }
}
```

**Verificar:**
```bash
mvn -pl kpi-service compile
```

### Paso 2 — Crear KpiFactoryTest con 5 tests

Ver detalle en Sub-task 1. Ejecutar:
```bash
mvn test -pl kpi-service -Dtest=KpiFactoryTest
```

### Paso 3 — Crear KpiControllerTest y KpiServiceTest

Ver detalles en Sub-tasks 2 y 3. Ejecutar:
```bash
mvn test -pl kpi-service -Dtest=KpiControllerTest
mvn test -pl kpi-service -Dtest=KpiServiceTest
```

### Paso 4 — Validación con JaCoCo

```bash
mvn clean verify -pl kpi-service
```

**OJO:** Si la cobertura JaCoCo es menor al 60%, la IA NO DEBE avanzar al Paso 5. Debe iterar agregando más tests hasta superar el 60%.

Abrir en el navegador:
```
kpi-service/target/site/jacoco/index.html
```

Capturar screenshot y guardar en `docs/jacoco-kpi-service.png`.

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-117): tests KpiFactory, KpiController y KpiService con patrón Factory Method"
git push origin feature/cord-117-kpi-factory-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-117-kpi-factory-test \
  --title "[CORD-117] Tests KpiFactory (Factory Method) y filtrado por categoría" \
  --body "## Cambios realizados\nKpiFactoryTest (5 tests), KpiControllerTest (filtrado categoría), KpiServiceTest (findByCategoria + create)\n\n## Tests\n5 tests KpiFactory + tests Controller y Service\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-117 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-156]: Crear KpiFactoryTest con 5 tests (4 categorías + inválida)

**Objetivo:** Cubrir 100% de KpiFactory con tests directos — sin Spring, sin mocks

**Archivo a crear:** `kpi-service/src/test/java/cl/duoc/cordillera/kpiservice/factory/KpiFactoryTest.java`

**Código:**
```java
package cl.duoc.cordillera.kpiservice.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class KpiFactoryTest {
    // Sin @ExtendWith ni @SpringBootTest — se instancia directamente
    private final KpiFactory factory = new KpiFactory();

    @Test
    void obtenerCalculador_ventas_retornaVentasCalculator() {
        // Escenario: dashboard solicita KPIs de ventas para el área comercial
        // IMPORTANTE: método es obtenerCalculador(), NO getCalculator()
        KpiCalculator calc = factory.obtenerCalculador("ventas");
        assertInstanceOf(VentasCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_inventario_retornaInventarioCalculator() {
        // Escenario: área de logística consulta KPIs de inventario
        KpiCalculator calc = factory.obtenerCalculador("inventario");
        assertInstanceOf(InventarioCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_logistica_retornaLogisticaCalculator() {
        // Escenario: gerente de operaciones consulta KPIs de logística
        KpiCalculator calc = factory.obtenerCalculador("logistica");
        assertInstanceOf(LogisticaCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_rentabilidad_retornaRentabilidadCalculator() {
        // Escenario: gerencia financiera consulta KPIs de rentabilidad
        KpiCalculator calc = factory.obtenerCalculador("rentabilidad");
        assertInstanceOf(RentabilidadCalculator.class, calc);
    }

    @Test
    void obtenerCalculador_categoriaInvalida_lanzaIllegalArgumentException() {
        // Escenario: sistema envía categoría inexistente "operaciones"
        // "operaciones" NO existe en el mapa — solo: ventas, inventario, logistica, rentabilidad
        assertThrows(IllegalArgumentException.class,
            () -> factory.obtenerCalculador("operaciones"));
    }
}
```

**Verificar:**
```bash
mvn test -pl kpi-service -Dtest=KpiFactoryTest
```

### Sub-task 2 [CORD-157]: Crear KpiControllerTest con tests de /categoria/{cat}

**Objetivo:** Verificar contrato HTTP del endpoint de filtrado por categoría

**Archivo a crear:** `kpi-service/src/test/java/cl/duoc/cordillera/kpiservice/controller/KpiControllerTest.java`

**Código:**
```java
package cl.duoc.cordillera.kpiservice.controller;

import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.service.KpiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KpiController.class)
class KpiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KpiService kpiService;

    @Test
    void getByCategoria_ventas_retorna200ConLista() throws Exception {
        // Arrange - Escenario: BFF solicita KPIs de ventas para el dashboard
        // IMPORTANTE: ruta base real es /api/kpis, NO /api/v1/kpis
        Kpi kpi = new Kpi();
        kpi.setId(1L);
        kpi.setNombre("Ventas Totales");
        kpi.setValor(BigDecimal.valueOf(380000));
        kpi.setCategoria("ventas");
        when(kpiService.findByCategoria("ventas")).thenReturn(List.of(kpi));

        mockMvc.perform(get("/api/kpis/categoria/ventas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getByCategoria_categoriaInexistente_retorna200ListaVacia() throws Exception {
        // Arrange - Escenario: categoría "operaciones" no existe — retorna lista vacía, NO 404
        when(kpiService.findByCategoria("operaciones")).thenReturn(List.of());

        // El KpiController NO lanza 404 para categoría inexistente
        mockMvc.perform(get("/api/kpis/categoria/operaciones"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
```

**Verificar:**
```bash
mvn test -pl kpi-service -Dtest=KpiControllerTest
```

### Sub-task 3 [CORD-158]: Crear KpiServiceTest con tests de findByCategoria y create

**Objetivo:** Verificar que create() invoca obtenerCalculador() y que findByCategoria retorna lista

**Archivo a crear:** `kpi-service/src/test/java/cl/duoc/cordillera/kpiservice/service/KpiServiceTest.java`

**Código:**
```java
package cl.duoc.cordillera.kpiservice.service;

import cl.duoc.cordillera.kpiservice.factory.KpiCalculator;
import cl.duoc.cordillera.kpiservice.factory.KpiFactory;
import cl.duoc.cordillera.kpiservice.model.Kpi;
import cl.duoc.cordillera.kpiservice.repository.KpiRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiServiceTest {

    @Mock
    private KpiRepository kpiRepository;

    @Mock
    private KpiFactory kpiFactory;

    @InjectMocks
    private KpiService kpiService;

    @Test
    void findByCategoria_conCategoriaValida_retornaLista() {
        // Arrange - Escenario: consulta de KPIs de ventas para el área comercial
        Kpi kpi = new Kpi();
        kpi.setId(1L);
        kpi.setCategoria("ventas");
        when(kpiRepository.findByCategoria("ventas")).thenReturn(List.of(kpi));

        // Act
        List<Kpi> resultado = kpiService.findByCategoria("ventas");

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("ventas", resultado.get(0).getCategoria());
    }

    @Test
    void create_conCategoriaValida_invocaObtenerCalculadorYPersiste() {
        // Arrange - Escenario: registrar nuevo KPI de ventas con recálculo automático
        Kpi kpi = new Kpi();
        kpi.setCategoria("ventas");
        kpi.setValor(BigDecimal.valueOf(100));

        KpiCalculator mockCalc = mock(KpiCalculator.class);
        when(mockCalc.calcular(any(), any())).thenReturn(BigDecimal.valueOf(95000));
        // IMPORTANTE: método es obtenerCalculador(), NO getCalculator()
        when(kpiFactory.obtenerCalculador("ventas")).thenReturn(mockCalc);
        when(kpiRepository.save(any())).thenReturn(kpi);

        // Act
        kpiService.create(kpi);

        // Assert
        verify(kpiFactory, times(1)).obtenerCalculador("ventas");
        verify(kpiRepository, times(1)).save(kpi);
    }

    @Test
    void create_conCategoriaInvalida_propagaIllegalArgumentException() {
        // Arrange - Escenario: sistema envía KPI con categoría no soportada
        Kpi kpi = new Kpi();
        kpi.setCategoria("invalida");
        when(kpiFactory.obtenerCalculador("invalida"))
            .thenThrow(new IllegalArgumentException("Categoria no soportada"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> kpiService.create(kpi));
    }
}
```

**Verificar:**
```bash
mvn test -pl kpi-service -Dtest=KpiServiceTest
```

---

## Criterios de Aceptación

**AC1: Retorna KPIs de categoría válida**
- **Dado** que existen KPIs con categoria="ventas" en kpi_db
- **Cuando** solicito GET /api/kpis/categoria/ventas
- **Entonces** retorna 200 OK con los KPIs de esa categoría únicamente

**AC2: Categoría inexistente retorna lista vacía**
- **Dado** una petición a categoría "operaciones" que no existe
- **Cuando** solicito GET /api/kpis/categoria/operaciones
- **Entonces** retorna 200 OK con lista vacía `[]` (el KpiController NO lanza 404)

## DoD (Definition of Done)

- [ ] Rama `feature/cord-117-kpi-factory-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] KpiFactoryTest con 5 tests verdes
- [ ] KpiControllerTest con tests de /categoria/{cat} verdes
- [ ] KpiServiceTest con tests de findByCategoria y create verdes
- [ ] Cobertura JaCoCo ≥ 60% verificada en kpi-service
- [ ] Ticket CORD-117 en Jira en estado "Finalizado" con comentario técnico
