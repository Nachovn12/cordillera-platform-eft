# Prompt de Implementación - CORD-126

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-126 — Configurar timeouts y manejo de errores en RestTemplate BFF
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Sin timeouts configurados, si un microservicio de Grupo Cordillera responde lentamente, el hilo del servidor BFF queda bloqueado indefinidamente esperando la respuesta. Con múltiples usuarios simultáneos, esto agota el pool de threads y el BFF deja de responder para todos. El patrón **Fail-Fast** resuelve esto: si un microservicio no responde en 2 segundos (connect) o 5 segundos (read), el BFF corta la conexión y retorna `status="Degradado"` inmediatamente.

El `RestTemplateConfig.java` actual solo tiene `return new RestTemplate()` sin timeouts. Esta historia corrige ese problema configurando `SimpleClientHttpRequestFactory` con `connectTimeout=2000ms` y `readTimeout=5000ms`. Al ser un único `@Bean`, todos los clientes HTTP del BFF heredan los timeouts automáticamente.

La ventaja de testear con `SimpleClientHttpRequestFactory` es que permite verificar los valores directamente instanciando el Bean — sin necesidad de MockWebServer ni WireMock, exactamente el patrón de tests simple del profesor.

## Historia de Usuario

**Como** Arquitecto del equipo Grupo Cordillera
**quiero** configurar timeouts en el RestTemplate del BFF Gateway
**para** que los hilos del servidor no queden bloqueados indefinidamente si un microservicio no responde

### Regla de Negocio Crítica
`SimpleClientHttpRequestFactory` está incluida en `spring-web` — **NO** se agregan dependencias nuevas al `pom.xml`. `connectTimeout=2000ms` (2 segundos) y `readTimeout=5000ms` (5 segundos). El Bean debe mantener el nombre `restTemplate` para que `DashboardService` lo reciba correctamente.

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
git checkout -b feature/cord-126-resttemplate-timeout-config
```

### Paso 1 — Modificar RestTemplateConfig.java

**Archivo:** `bff-gateway/src/main/java/cl/duoc/cordillera/bffgateway/config/RestTemplateConfig.java`

**Estado ANTES (lo que hay que corregir):**
```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate(); // SIN timeout — hay que corregir
}
```

**Estado DESPUÉS (la implementación correcta):**
```java
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // SimpleClientHttpRequestFactory está en spring-web — NO agregar dependencias
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);  // 2 segundos — Fail-Fast connect
        factory.setReadTimeout(5000);     // 5 segundos — Fail-Fast read
        return new RestTemplate(factory);
    }
}
```

**Verificar:**
```bash
mvn -pl bff-gateway compile
# Esperado: BUILD SUCCESS sin agregar dependencias nuevas
```

### Paso 2 — Crear RestTemplateConfigTest

Ver detalle en Sub-task 2. Ejecutar:

```bash
mvn test -pl bff-gateway -Dtest=RestTemplateConfigTest
```

### Paso 3 — Verificar test de timeout en DashboardServiceTest

Confirmar que `DashboardServiceTest` tiene el test `getDashboard_conTimeoutEnKpiService_retornaStatusDegradado` (creado en CORD-124/Sub-task 1). Si no existe, agregarlo.

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
git commit -m "feat(cord-126): configurar timeouts Fail-Fast en RestTemplate BFF con SimpleClientHttpRequestFactory"
git push origin feature/cord-126-resttemplate-timeout-config
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-126-resttemplate-timeout-config \
  --title "[CORD-126] Timeouts Fail-Fast en RestTemplate BFF — connectTimeout=2s readTimeout=5s" \
  --body "## Cambios realizados\nRestTemplateConfig con SimpleClientHttpRequestFactory, connectTimeout=2000, readTimeout=5000\n\n## Tests\nRestTemplateConfigTest (3 tests directos), DashboardServiceTest (timeout → Degradado)\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-126 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link, verificación de compilación y cobertura

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-183]: Configurar connectTimeout y readTimeout en RestTemplateConfig

**Objetivo:** Corregir RestTemplateConfig para implementar el patrón Fail-Fast

**Archivo a modificar:** `bff-gateway/src/main/java/cl/duoc/cordillera/bffgateway/config/RestTemplateConfig.java`

**Código completo:**
```java
package cl.duoc.cordillera.bffgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        // SimpleClientHttpRequestFactory está incluida en spring-web
        // NO requiere dependencias adicionales en pom.xml
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);  // 2 segundos — Fail-Fast connect
        factory.setReadTimeout(5000);     // 5 segundos — Fail-Fast read

        // El Bean mantiene el nombre "restTemplate" para que DashboardService lo reciba
        return new RestTemplate(factory);
    }
}
```

**Verificar:**
```bash
mvn -pl bff-gateway compile
# BUILD SUCCESS — SimpleClientHttpRequestFactory ya está en spring-web, sin dependencias nuevas

# Verificar también que DashboardService sigue funcionando:
# (con docker-compose up) GET http://localhost:8081/api/dashboard/stats → 200
```

### Sub-task 2 [CORD-184]: Crear RestTemplateConfigTest verificando valores del factory

**Objetivo:** Verificar los valores de timeout directamente — sin MockWebServer ni WireMock

**Archivo a crear:** `bff-gateway/src/test/java/cl/duoc/cordillera/bffgateway/config/RestTemplateConfigTest.java`

**Código:**
```java
package cl.duoc.cordillera.bffgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {
    // Tests directos — instanciar RestTemplateConfig sin Spring Context
    // Exactamente el patrón de tests simple del profesor

    @Test
    void restTemplate_tieneConnectTimeoutConfigurado() {
        // Arrange — instanciar directamente
        RestTemplateConfig config = new RestTemplateConfig();

        // Act
        RestTemplate rt = config.restTemplate();

        // Assert — castear el factory para acceder a los valores
        SimpleClientHttpRequestFactory factory =
            (SimpleClientHttpRequestFactory) rt.getRequestFactory();
        assertEquals(2000, factory.getConnectTimeout());
    }

    @Test
    void restTemplate_tieneReadTimeoutConfigurado() {
        // Arrange
        RestTemplateConfig config = new RestTemplateConfig();

        // Act
        RestTemplate rt = config.restTemplate();

        // Assert
        SimpleClientHttpRequestFactory factory =
            (SimpleClientHttpRequestFactory) rt.getRequestFactory();
        assertEquals(5000, factory.getReadTimeout());
    }

    @Test
    void restTemplate_factoryEsSimpleClientHttpRequestFactory() {
        // Arrange — verificar el tipo del factory (Fail-Fast pattern)
        RestTemplateConfig config = new RestTemplateConfig();

        // Act
        RestTemplate rt = config.restTemplate();

        // Assert
        assertInstanceOf(SimpleClientHttpRequestFactory.class, rt.getRequestFactory());
    }
}
```

**Verificar:**
```bash
mvn test -pl bff-gateway -Dtest=RestTemplateConfigTest
# Los 3 tests deben ser verdes
```

### Sub-task 3 [CORD-185]: Verificar manejo de ResourceAccessException por timeout en DashboardService

**Objetivo:** Confirmar que el timeout del factory activa el mecanismo de resiliencia del BFF

**En DashboardServiceTest.java**, agregar o verificar que existe este test:
```java
@Test
void getDashboard_conTimeoutEnKpiService_retornaStatusDegradado() {
    // Arrange - Escenario: timeout de 5s activa el Fail-Fast configurado en CORD-126
    // ResourceAccessException es lo que lanza RestTemplate cuando hay timeout
    when(restTemplate.exchange(
            org.mockito.ArgumentMatchers.contains("kpi-service"),
            any(), any(), any(Class.class)))
        .thenThrow(new org.springframework.web.client.ResourceAccessException(
            "Connection timed out after 5000ms"));

    // Act
    DashboardResponse resultado = dashboardService.getDashboard();

    // Assert — el Fail-Fast del timeout activa el mecanismo de resiliencia
    assertEquals("Degradado", resultado.getStatus());
    assertFalse(resultado.getAlertas().isEmpty());
    // La alerta debe tener severidad Critica
    assertTrue(resultado.getAlertas().stream()
        .anyMatch(a -> "Critica".equals(a.getSeveridad())));
}
```

**Verificar:**
```bash
mvn test -pl bff-gateway -Dtest=DashboardServiceTest
```

---

## Criterios de Aceptación

**AC1: Timeouts configurados correctamente**
- **Dado** que RestTemplateConfig.java usa SimpleClientHttpRequestFactory
- **Cuando** se instancia el Bean restTemplate
- **Entonces** `factory.getConnectTimeout()` retorna 2000 y `factory.getReadTimeout()` retorna 5000

**AC2: El bean compila sin dependencias adicionales**
- **Dado** que el pom.xml del bff-gateway solo tiene spring-boot-starter-web
- **Cuando** se ejecuta `mvn -pl bff-gateway compile`
- **Entonces** BUILD SUCCESS sin agregar ninguna dependencia nueva

**AC3: DashboardService sigue funcionando**
- **Dado** que DashboardService inyecta RestTemplate por @Bean
- **Cuando** el BFF arranca con docker-compose up
- **Entonces** GET /api/dashboard/stats retorna 200 normalmente

## DoD (Definition of Done)

- [ ] Rama `feature/cord-126-resttemplate-timeout-config` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] `RestTemplateConfig.java` modificado con `SimpleClientHttpRequestFactory`
- [ ] `mvn -pl bff-gateway compile` → BUILD SUCCESS sin dependencias nuevas
- [ ] `RestTemplateConfigTest` verde: assertEquals(2000, factory.getConnectTimeout()) y assertEquals(5000, factory.getReadTimeout())
- [ ] Cobertura JaCoCo ≥ 60% verificada en bff-gateway
- [ ] Ticket CORD-126 en Jira en estado "Finalizado" con comentario técnico
