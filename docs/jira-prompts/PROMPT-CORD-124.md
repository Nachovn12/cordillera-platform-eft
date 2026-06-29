# Prompt de Implementación - CORD-124

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-124 — Dashboard consolidado en BFF con Circuit Breaker
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El frontend del Grupo Cordillera necesita mostrar un dashboard completo con KPIs, datos operacionales y reportes en una sola pantalla. Sin el BFF Gateway, el browser tendría que hacer 3 llamadas HTTP separadas a data-service, kpi-service y report-service — exponiendo la topología interna del sistema y generando "chatty behavior" que degrada el rendimiento.

El `DashboardService` del BFF resuelve este problema implementando el patrón **Aggregator**: hace las 3 llamadas HTTP simultáneamente via `RestTemplate` y consolida los resultados en un `DashboardResponse` unificado. Si algún microservicio falla, el BFF retorna `status="Degradado"` con una alerta crítica — implementando el patrón **Circuit Breaker** a nivel de BFF.

Esta historia implementa los tests del `DashboardService`, `DashboardController` y `AuthService` del BFF, verificando tanto el caso feliz como la tolerancia a fallos parciales.

## Historia de Usuario

**Como** Frontend del Grupo Cordillera
**quiero** llamar a un único endpoint consolidado `/api/dashboard/stats`
**para** obtener en una sola respuesta los KPIs, datos operacionales, alertas y estado de los microservicios

### Regla de Negocio Crítica
Si algún microservicio falla (timeout o 500), el BFF retorna HTTP 200 con `status="Degradado"` y una alerta crítica del servicio caído — **NUNCA** propaga el error al frontend como 5xx. Esto garantiza que el frontend siempre recibe una respuesta utilizable.

> **Contexto EP3:** El BFF Gateway es el único punto de entrada del frontend React al sistema. Agrega datos de data-service, kpi-service y report-service en una sola llamada, evitando que el browser haga múltiples peticiones. Los tests de DashboardService y AuthService son críticos para el Indicador 4 EP3.


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
git checkout -b feature/cord-124-dashboard-auth-service-test
```

### Paso 1 — Verificar DashboardService y DashboardController

Confirmar que `DashboardService` usa `RestTemplate` para hacer las 3 llamadas y que maneja excepciones retornando `status="Degradado"`.

```java
// DashboardService.java — estructura esperada
@Service
public class DashboardService {
    @Autowired
    private RestTemplate restTemplate;

    public DashboardResponse getDashboard() {
        // Llamadas a los 3 microservicios con manejo de fallos
        try {
            // llamada a data-service, kpi-service, report-service
        } catch (Exception e) {
            // retornar status="Degradado" con alerta crítica
        }
    }
}
```

**Verificar:**
```bash
mvn -pl bff-gateway compile
```

### Paso 2 — Crear DashboardServiceTest con RestTemplate mockeado

Ver detalle en Sub-task 1.

```bash
mvn test -pl bff-gateway -Dtest=DashboardServiceTest
```

### Paso 3 — Crear DashboardControllerTest y AuthServiceTest

Ver detalles en Sub-tasks 2 y 3.

```bash
mvn test -pl bff-gateway -Dtest=DashboardControllerTest
mvn test -pl bff-gateway -Dtest=AuthServiceTest
```

### Paso 4 — Validación con JaCoCo

```bash
mvn clean verify -pl bff-gateway
```

**OJO:** Si la cobertura JaCoCo es menor al 60%, la IA NO DEBE avanzar al Paso 5.

Abrir en el navegador:
```
bff-gateway/target/site/jacoco/index.html
```

Capturar screenshot y guardar en `docs/jacoco-bff-gateway.png`.

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-124): tests DashboardService (Aggregator+CircuitBreaker), DashboardController y AuthService"
git push origin feature/cord-124-dashboard-auth-service-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-124-dashboard-auth-service-test \
  --title "[CORD-124] Tests Dashboard BFF con Circuit Breaker y AuthService" \
  --body "## Cambios realizados\nDashboardServiceTest (Operativo+Degradado), DashboardControllerTest (@WebMvcTest), AuthServiceTest (login+CRUD)\n\n## Tests\n3 tests DashboardService + 2 tests DashboardController + 5 tests AuthService\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-124 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-177]: Crear DashboardServiceTest con RestTemplate mockeado

**Objetivo:** Verificar agregación de datos y manejo de fallos parciales del DashboardService

**Archivo a crear:** `bff-gateway/src/test/java/cl/duoc/cordillera/bffgateway/service/DashboardServiceTest.java`

**Código:**
```java
package cl.duoc.cordillera.bffgateway.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboard_conTodosLosServiciosOnline_retornaStatusOperativo() {
        // Arrange - Escenario: todos los microservicios responden correctamente
        // Mockear las 3 llamadas HTTP a los microservicios downstream
        when(restTemplate.exchange(
                contains("data-service"), eq(GET), any(), eq(Object[].class)))
            .thenReturn(ResponseEntity.ok(new Object[]{new Object(), new Object()}));
        when(restTemplate.exchange(
                contains("kpi-service"), eq(GET), any(), eq(Object[].class)))
            .thenReturn(ResponseEntity.ok(new Object[]{new Object()}));
        when(restTemplate.exchange(
                contains("report-service"), eq(GET), any(), eq(Object[].class)))
            .thenReturn(ResponseEntity.ok(new Object[]{new Object()}));

        // Act
        DashboardResponse resultado = dashboardService.getDashboard();

        // Assert
        assertEquals("Operativo", resultado.getStatus());
    }

    @Test
    void getDashboard_conKpiServiceCaido_retornaStatusDegradado() {
        // Arrange - Escenario: kpi-service no responde (timeout o 500)
        // Circuit Breaker a nivel BFF: si kpi-service falla, retornar Degradado
        when(restTemplate.exchange(
                contains("kpi-service"), any(), any(), any(Class.class)))
            .thenThrow(new ResourceAccessException("Connection refused: kpi-service"));

        // Act
        DashboardResponse resultado = dashboardService.getDashboard();

        // Assert — BFF nunca propaga el 5xx al frontend
        assertEquals("Degradado", resultado.getStatus());
        assertFalse(resultado.getAlertas().isEmpty());
        assertTrue(resultado.getAlertas().stream()
            .anyMatch(a -> "Critica".equals(a.getSeveridad())));
    }

    @Test
    void getDashboard_conTimeoutEnKpiService_retornaStatusDegradado() {
        // Arrange - Escenario: timeout configurado por CORD-126 activa el Fail-Fast
        when(restTemplate.exchange(
                contains("kpi-service"), any(), any(), any(Class.class)))
            .thenThrow(new ResourceAccessException("Connection timed out"));

        // Act
        DashboardResponse resultado = dashboardService.getDashboard();

        // Assert — el timeout activa el mismo mecanismo de resiliencia
        assertEquals("Degradado", resultado.getStatus());
    }
}
```

**Verificar:**
```bash
mvn test -pl bff-gateway -Dtest=DashboardServiceTest
```

### Sub-task 2 [CORD-178]: Crear DashboardControllerTest con casos Operativo y Degradado

**Objetivo:** Verificar que el BFF siempre retorna HTTP 200, nunca 5xx al frontend

**Archivo a crear:** `bff-gateway/src/test/java/cl/duoc/cordillera/bffgateway/controller/DashboardControllerTest.java`

**Código:**
```java
package cl.duoc.cordillera.bffgateway.controller;

import cl.duoc.cordillera.bffgateway.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void getStats_retorna200ConDashboardCompleto() throws Exception {
        // Arrange - Escenario: todos los microservicios operativos
        DashboardResponse response = new DashboardResponse();
        response.setStatus("Operativo");
        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("Operativo"));
    }

    @Test
    void getStats_degradado_retorna200ConAlerta() throws Exception {
        // Arrange - Escenario: kpi-service caído — BFF retorna 200 con status Degradado
        DashboardResponse response = new DashboardResponse();
        response.setStatus("Degradado");
        // BFF NUNCA retorna 5xx al frontend — siempre 200
        when(dashboardService.getDashboard()).thenReturn(response);

        mockMvc.perform(get("/api/dashboard/stats"))
            .andExpect(status().isOk()) // 200, NO 503
            .andExpect(jsonPath("$.status").value("Degradado"));
    }
}
```

**Verificar:**
```bash
mvn test -pl bff-gateway -Dtest=DashboardControllerTest
```

### Sub-task 3 [CORD-179]: Crear AuthServiceTest con casos de login, crear y eliminar usuario

**Objetivo:** Cubrir AuthService (in-memory ConcurrentHashMap) directamente sin mocks

**Archivo a crear:** `bff-gateway/src/test/java/cl/duoc/cordillera/bffgateway/auth/service/AuthServiceTest.java`

**Código:**
```java
package cl.duoc.cordillera.bffgateway.auth.service;

import cl.duoc.cordillera.bffgateway.auth.dto.LoginRequestDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {
    // AuthService usa ConcurrentHashMap interno — instanciar directamente, sin mocks
    private final AuthService authService = new AuthService();

    @Test
    void autenticar_conCredencialesValidas_retornaAuthResponse() {
        // Escenario: gerente general accede al sistema
        var response = authService.autenticar(
            new LoginRequestDTO("a.gatica@cordillera.cl", "gerencia2026"));
        assertNotNull(response.getToken());
        assertEquals("GERENTE_GENERAL", response.getRol());
    }

    @Test
    void autenticar_conContrasenaIncorrecta_lanzaCustomUnauthorizedException() {
        // Escenario: intento de acceso con contraseña incorrecta
        assertThrows(Exception.class,
            () -> authService.autenticar(
                new LoginRequestDTO("a.gatica@cordillera.cl", "wrongpassword")));
    }

    @Test
    void crearUsuario_conEmailNuevo_retornaUsuarioConId() {
        // Escenario: crear nuevo usuario para sucursal Valparaíso
        var request = new cl.duoc.cordillera.bffgateway.auth.dto.CrearUsuarioRequestDTO();
        request.setEmail("nuevo@cordillera.cl");
        request.setNombre("Nuevo Usuario");
        request.setRol("ANALISTA");
        request.setPassword("pass123");
        var usuario = authService.crearUsuario(request);
        assertNotNull(usuario.getId());
        assertTrue(usuario.getId().startsWith("USR-"));
    }

    @Test
    void crearUsuario_conEmailDuplicado_lanzaExcepcion() {
        // Escenario: email ya registrado en el sistema
        assertThrows(Exception.class,
            () -> authService.crearUsuario(
                buildRequest("a.gatica@cordillera.cl", "gerencia2026")));
    }

    @Test
    void eliminarUsuario_conIdInexistente_lanzaExcepcion() {
        // Escenario: intento eliminar usuario que no existe
        assertThrows(Exception.class,
            () -> authService.eliminarUsuario("USR-9999"));
    }

    private cl.duoc.cordillera.bffgateway.auth.dto.CrearUsuarioRequestDTO buildRequest(
            String email, String password) {
        var r = new cl.duoc.cordillera.bffgateway.auth.dto.CrearUsuarioRequestDTO();
        r.setEmail(email);
        r.setPassword(password);
        r.setNombre("Test");
        r.setRol("ANALISTA");
        return r;
    }
}
```

**Verificar:**
```bash
mvn test -pl bff-gateway -Dtest=AuthServiceTest
```

---

## Criterios de Aceptación

**AC1: Respuesta unificada con todos los servicios online**
- **Dado** que data-service, kpi-service y report-service están operativos
- **Cuando** el frontend llama a GET /api/dashboard/stats
- **Entonces** retorna HTTP 200 con status="Operativo"

**AC2: Respuesta degradada con kpi-service caído**
- **Dado** que kpi-service no responde
- **Cuando** el frontend llama a GET /api/dashboard/stats
- **Entonces** retorna HTTP 200 (NO 503) con status="Degradado" y alerta crítica

**AC3: Filtrado por sucursal**
- **Dado** que existen datos para sucursalId=1
- **Cuando** el frontend llama a GET /api/dashboard/sucursal/1
- **Entonces** retorna HTTP 200 con datos filtrados de la sucursal 1

## DoD (Definition of Done)

- [ ] Rama `feature/cord-124-dashboard-auth-service-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] DashboardServiceTest con tests Operativo y Degradado verdes
- [ ] DashboardControllerTest verificando que BFF siempre retorna 200 verdes
- [ ] AuthServiceTest con 5 tests verdes
- [ ] Cobertura JaCoCo ≥ 60% verificada en bff-gateway
- [ ] Ticket CORD-124 en Jira en estado "Finalizado" con comentario técnico
