# Prompt de Implementación - CORD-118

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-118 — CRUD completo de KPIs con recálculo vía KpiFactory
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Los KPIs de Grupo Cordillera no son valores estáticos ingresados manualmente — son calculados automáticamente por el sistema a partir de datos operacionales. Cuando un administrador registra o actualiza un KPI, el `kpi-service` usa `KpiFactory.obtenerCalculador()` para seleccionar el algoritmo de cálculo correcto según la categoría (Ventas usa fórmula de margen, Rentabilidad usa ROI, etc.) y recalcula el valor automáticamente.

Esta arquitectura con Factory Method desacopla completamente la lógica de cálculo del `KpiService`: si mañana la fórmula de ventas cambia, solo se modifica `VentasCalculator` — el service y el controller no necesitan cambios. Este principio Open/Closed es clave para la mantenibilidad del sistema a largo plazo.

Esta historia implementa los tests del CRUD completo de KPIs, verificando que el recálculo via factory se invoca exactamente una vez por operación y que las excepciones se manejan correctamente.

## Historia de Usuario

**Como** administrador de negocio del Grupo Cordillera
**quiero** registrar y gestionar definiciones de KPIs (CRUD)
**para** que el sistema recalcule automáticamente sus valores usando KpiFactory antes de guardarlos en kpi_db

### Regla de Negocio Crítica
Los métodos públicos del `KpiService` son `create(Kpi)`, `update(Long, Kpi)` y `delete(Long)` — **NO** `crear()`, `actualizar()`. `update()` lanza `ResponseStatusException(NOT_FOUND)` si el id no existe. `delete()` llama directamente `deleteById()` sin verificar existencia previa.

> **Contexto EP3:** El recálculo automático de KPIs (KpiFactory + KpiService.create()) garantiza que los indicadores ejecutivos siempre reflejan los datos más recientes de las sucursales de Grupo Cordillera. Los tests con mock de KpiFactory demuestran el Factory Method en acción — Indicadores 2, 4 y 8 EP3.


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
git checkout -b feature/cord-118-kpi-service-crud-test
```

### Paso 1 — Verificar métodos KpiService

Confirmar nombres exactos de métodos: `create(Kpi)`, `update(Long, Kpi)`, `delete(Long)` y que `update()` lanza `ResponseStatusException(NOT_FOUND)`.

```java
// En KpiService.java — estructura esperada
public Kpi update(Long id, Kpi kpiDto) {
    Kpi existente = kpiRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "KPI no encontrado con id: " + id));
    // actualizar campos...
    calcularValor(existente);
    return kpiRepository.save(existente);
}

public void delete(Long id) {
    // IMPORTANTE: delete() llama directamente deleteById() SIN verificar existencia
    kpiRepository.deleteById(id);
}

private void calcularValor(Kpi kpi) {
    KpiCalculator calc = kpiFactory.obtenerCalculador(kpi.getCategoria());
    kpi.setValor(calc.calcular(kpi.getValor(), BigDecimal.ZERO));
}
```

**Verificar:**
```bash
mvn -pl kpi-service compile
```

### Paso 2 — Agregar tests de create, update y delete a KpiServiceTest

Ver detalle en Sub-tasks 1, 2 y 3.

```bash
mvn test -pl kpi-service -Dtest=KpiServiceTest
```

### Paso 3 — Ejecutar todos los tests del kpi-service

```bash
mvn test -pl kpi-service
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
git commit -m "feat(cord-118): tests CRUD KpiService con KpiFactory mock y verify(times(1))"
git push origin feature/cord-118-kpi-service-crud-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-118-kpi-service-crud-test \
  --title "[CORD-118] CRUD KPIs con recálculo KpiFactory — tests" \
  --body "## Cambios realizados\nTests create, update y delete en KpiServiceTest\n\n## Tests\ncreate (invoca obtenerCalculador 1x), update (id inexistente → ResponseStatusException), delete (invoca deleteById)\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-118 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-159]: Crear test create con KpiFactory mock verificado

**Objetivo:** Verificar que create() invoca obtenerCalculador() exactamente una vez y persiste el KPI

**Archivo a modificar:** `kpi-service/src/test/java/cl/duoc/cordillera/kpiservice/service/KpiServiceTest.java`

**Código a agregar:**
```java
@Test
void create_invocaObtenerCalculador_exactamente1Vez() {
    // Arrange - Escenario: registrar KPI de ventas — el sistema recalcula automáticamente
    Kpi kpi = new Kpi();
    kpi.setCategoria("ventas");
    kpi.setValor(BigDecimal.valueOf(100));

    KpiCalculator mockCalc = mock(KpiCalculator.class);
    when(mockCalc.calcular(any(), any())).thenReturn(BigDecimal.valueOf(95000));
    // IMPORTANTE: el método es obtenerCalculador(), NO getCalculator()
    when(kpiFactory.obtenerCalculador("ventas")).thenReturn(mockCalc);
    when(kpiRepository.save(any())).thenReturn(kpi);

    // Act
    kpiService.create(kpi);

    // Assert — Factory Method invocado exactamente 1 vez
    verify(kpiFactory, times(1)).obtenerCalculador("ventas");
    // Repository.save invocado exactamente 1 vez
    verify(kpiRepository, times(1)).save(kpi);
}
```

**Verificar:**
```bash
mvn test -pl kpi-service -Dtest=KpiServiceTest#create_invocaObtenerCalculador_exactamente1Vez
```

### Sub-task 2 [CORD-160]: Crear test update con id inexistente (lanza ResponseStatusException)

**Objetivo:** Verificar que update() lanza ResponseStatusException(NOT_FOUND) para id inexistente

**Código a agregar en KpiServiceTest:**
```java
@Test
void update_conIdInexistente_lanzaResponseStatusException() {
    // Arrange - Escenario: intento actualizar KPI con id incorrecto
    when(kpiRepository.findById(9999L)).thenReturn(Optional.empty());

    // Act & Assert
    // IMPORTANTE: KpiService lanza ResponseStatusException(NOT_FOUND), NO KpiNotFoundException
    assertThrows(ResponseStatusException.class,
        () -> kpiService.update(9999L, new Kpi()));

    // El save no debe ser invocado
    verify(kpiRepository, never()).save(any());
}
```

**Verificar:**
```bash
mvn test -pl kpi-service -Dtest=KpiServiceTest#update_conIdInexistente_lanzaResponseStatusException
```

### Sub-task 3 [CORD-161]: Crear test delete invocando deleteById

**Objetivo:** Verificar que delete() llama directamente deleteById() sin verificar existencia

**Código a agregar en KpiServiceTest:**
```java
@Test
void delete_conIdValido_invocaDeleteById() {
    // Arrange - Escenario: eliminar KPI obsoleto
    // IMPORTANTE: KpiService.delete() llama directamente deleteById() SIN verificar existencia
    // Por tanto: NO mockear findById — no es necesario

    // Act
    kpiService.delete(1L);

    // Assert
    verify(kpiRepository, times(1)).deleteById(1L);
    // Contraste: DatoService.eliminar() SÍ verifica existencia antes (diferencia de diseño)
}
```

**Nota para la defensa oral:** La diferencia de diseño entre `KpiService.delete()` (sin verificar) y `DatoService.eliminar()` (con verificación) es un punto válido de discusión sobre decisiones de diseño en la defensa.

**Verificar:**
```bash
mvn test -pl kpi-service -Dtest=KpiServiceTest#delete_conIdValido_invocaDeleteById
```

---

## Criterios de Aceptación

**AC1: Recálculo automático en POST**
- **Dado** un payload de creación de KPI con categoria="ventas" y valor=100
- **Cuando** entra al KpiService.create()
- **Entonces** el servicio llama a KpiFactory.obtenerCalculador("ventas") y persiste con valor recalculado

**AC2: ID inexistente retorna 404 en PUT**
- **Dado** un ID 9999 que no existe en kpi_db
- **Cuando** llamo KpiService.update(9999L, kpi)
- **Entonces** se lanza ResponseStatusException(NOT_FOUND) — NO KpiNotFoundException

## DoD (Definition of Done)

- [ ] Rama `feature/cord-118-kpi-service-crud-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] Test `create_invocaObtenerCalculador_exactamente1Vez` verde
- [ ] Test `update_conIdInexistente_lanzaResponseStatusException` verde
- [ ] Test `delete_conIdValido_invocaDeleteById` verde
- [ ] Cobertura JaCoCo ≥ 60% verificada en kpi-service
- [ ] Ticket CORD-118 en Jira en estado "Finalizado" con comentario técnico
