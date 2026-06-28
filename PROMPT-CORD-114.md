# Prompt de Implementación - CORD-114

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-114 — Listar datos operacionales con filtros (sistema/sucursal)
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Grupo Cordillera tiene sucursales en múltiples regiones, cada una enviando datos desde distintos sistemas: POS (punto de venta), SAP (finanzas), ERP (inventario), CRM (clientes). Los ejecutivos necesitan consultar datos filtrados por sistema de origen o por sucursal para analizar el comportamiento individual de cada canal o tienda.

El `data-service` expone endpoints de filtrado que permiten al dashboard del BFF obtener datos específicos sin cargar todo el histórico. Esto es crítico para el rendimiento del sistema y para la toma de decisiones ejecutivas: un gerente de zona quiere ver solo los datos de sus sucursales, no los de toda la cadena.

Esta historia agrega los endpoints `GET /api/datos/sistema/{origen}` y `GET /api/datos/sucursal/{id}` con sus respectivos tests, verificando el patrón Query Methods de Spring Data JPA que genera las consultas SQL automáticamente a partir del nombre del método.

## Historia de Usuario

**Como** ejecutivo de operaciones del Grupo Cordillera
**quiero** consultar los datos operacionales filtrados por sistema de origen o por sucursal
**para** analizar el comportamiento de cada canal o tienda sin procesos manuales y mostrarlos en el dashboard

### Regla de Negocio Crítica
Lista vacía retorna HTTP 200 + `[]` — NUNCA 404. Un sistema de origen sin datos es un estado válido (el sistema existe pero aún no ha enviado datos). Esta decisión diferencia recursos tipo colección de recursos singulares.

> **Contexto EP3:** El BFF Gateway (React → Spring Boot) expone los filtros de datos al frontend ejecutivo. Los tests validan que los Query Methods de Spring Data JPA filtran correctamente por sistema de origen (POS, SAP, ERP) y por sucursal — Indicadores 2, 3 y 4 EP3.


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
git checkout -b feature/cord-114-dato-filtros-test
```

### Paso 1 — Verificar DatoController endpoints de filtrado

Confirmar que `DatoController.java` tiene los endpoints con path variables (NO query params):

```java
@GetMapping("/sistema/{origen}")
public ResponseEntity<List<Dato>> listarPorSistema(@PathVariable String origen) {
    return ResponseEntity.ok(datoService.buscarPorSistemaOrigen(origen));
}

@GetMapping("/sucursal/{id}")
public ResponseEntity<List<Dato>> listarPorSucursal(@PathVariable Long id) {
    return ResponseEntity.ok(datoService.buscarPorSucursalId(id));
}
```

**Verificar:**
```bash
mvn -pl data-service compile
```

### Paso 2 — Agregar tests de filtrado al DatoRepositoryTest

Añadir los tests `findBySistemaOrigen_retornaListaFiltrada` y `findBySucursalId_retornaListaFiltrada` al archivo existente `DatoRepositoryTest.java`.

### Paso 3 — Agregar tests MockMvc de filtros al DatoControllerTest

Añadir tests para `GET /api/datos/sistema/{origen}` y `GET /api/datos/sucursal/{id}`, incluyendo el caso de lista vacía que retorna 200.

```bash
mvn test -pl data-service -Dtest=DatoRepositoryTest
mvn test -pl data-service -Dtest=DatoControllerTest
```

### Paso 4 — Validación con JaCoCo

```bash
mvn clean verify -pl data-service
```

**OJO:** Si la cobertura JaCoCo es menor al 60%, la IA NO DEBE avanzar al Paso 5. Debe iterar agregando más tests hasta superar el 60%.

Abrir en el navegador:
```
data-service/target/site/jacoco/index.html
```

Capturar screenshot y guardar en `docs/jacoco-data-service.png`.

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-114): tests filtrado por sistemaOrigen y sucursalId en DatoRepository y DatoController"
git push origin feature/cord-114-dato-filtros-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-114-dato-filtros-test \
  --title "[CORD-114] Listar datos con filtros sistema/sucursal" \
  --body "## Cambios realizados\nTests de filtrado por sistemaOrigen y sucursalId\n\n## Tests\nDatoRepositoryTest (filtros), DatoControllerTest (rutas /sistema/{origen} y /sucursal/{id})\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-114 y sub-tasks a "Finalizada"
- Agregar comentario con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-137]: Cubrir buscarPorSistemaOrigen y buscarPorSucursalId en Repository test

**Objetivo:** Verificar query methods de Spring Data JPA con datos reales en H2

**Archivo a modificar:** `data-service/src/test/java/cl/duoc/cordillera/dataservice/repository/DatoRepositoryTest.java`

**Código a agregar:**
```java
@Test
void findBySistemaOrigen_retornaListaFiltrada() {
    // Arrange - Escenario: 2 ventas POS y 1 transacción SAP en sucursal Santiago
    Dato pos1 = crearDato("POS", "VENTA", "120000", 1L);
    Dato pos2 = crearDato("POS", "VENTA", "95000", 2L);
    Dato sap1 = crearDato("SAP", "FINANZAS", "500000", 1L);
    datoRepository.save(pos1);
    datoRepository.save(pos2);
    datoRepository.save(sap1);

    // Act
    List<Dato> resultado = datoRepository.findBySistemaOrigen("POS");

    // Assert
    assertEquals(2, resultado.size());
    assertTrue(resultado.stream().allMatch(d -> "POS".equals(d.getSistemaOrigen())));
}

@Test
void findBySucursalId_retornaListaFiltrada() {
    // Arrange - Escenario: sucursal 1 tiene 2 registros, sucursal 2 tiene 1
    Dato d1 = crearDato("POS", "VENTA", "120000", 1L);
    Dato d2 = crearDato("ERP", "INVENTARIO", "50", 1L);
    Dato d3 = crearDato("CRM", "CLIENTE", "1", 2L);
    datoRepository.save(d1);
    datoRepository.save(d2);
    datoRepository.save(d3);

    // Act
    List<Dato> resultado = datoRepository.findBySucursalId(1L);

    // Assert
    assertEquals(2, resultado.size());
    assertTrue(resultado.stream().allMatch(d -> Long.valueOf(1L).equals(d.getSucursalId())));
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoRepositoryTest
```

### Sub-task 2 [CORD-138]: Agregar tests MockMvc para endpoints /sistema/{origen} y /sucursal/{id}

**Objetivo:** Verificar contrato HTTP de los endpoints de filtrado con path variables

**Archivo a modificar:** `data-service/src/test/java/cl/duoc/cordillera/dataservice/controller/DatoControllerTest.java`

**Código a agregar:**
```java
@Test
void listarPorSistema_conResultados_retorna200() throws Exception {
    // Arrange - Escenario: BFF solicita datos del sistema SAP para el dashboard
    Dato d1 = new Dato();
    d1.setId(1L);
    d1.setSistemaOrigen("SAP");
    d1.setTipoDato("FINANZAS");
    Dato d2 = new Dato();
    d2.setId(2L);
    d2.setSistemaOrigen("SAP");
    d2.setTipoDato("FINANZAS");
    when(datoService.buscarPorSistemaOrigen("SAP")).thenReturn(List.of(d1, d2));

    // Act & Assert
    mockMvc.perform(get("/api/datos/sistema/SAP"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
}

@Test
void listarPorSucursal_conResultados_retorna200() throws Exception {
    // Arrange - Escenario: gerente de zona consulta datos de sucursal 1
    Dato d = new Dato();
    d.setId(1L);
    d.setSucursalId(1L);
    when(datoService.buscarPorSucursalId(1L)).thenReturn(List.of(d));

    // Act & Assert
    mockMvc.perform(get("/api/datos/sucursal/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoControllerTest
```

### Sub-task 3 [CORD-139]: Cubrir caso vacío (200 vacío) en Controller test

**Objetivo:** Confirmar que lista vacía retorna HTTP 200 + [] y NO 404

**Código a agregar en DatoControllerTest:**
```java
@Test
void buscarPorSistema_sinResultados_retorna200Vacio() throws Exception {
    // Arrange - Escenario: sistema CRM aún no ha enviado datos (estado válido)
    when(datoService.buscarPorSistemaOrigen("CRM")).thenReturn(List.of());

    // Act & Assert
    mockMvc.perform(get("/api/datos/sistema/CRM"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(0));
    // IMPORTANTE: 200 + [] es el contrato correcto para colecciones vacías
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoControllerTest
```

---

## Criterios de Aceptación

**AC1: Consulta con resultados válidos**
- **Dado** que existen registros con sistemaOrigen=POS en data_db
- **Cuando** se envía GET /api/datos/sistema/POS
- **Entonces** retorna 200 OK con la lista de datos correspondientes en JSON

**AC2: Consulta sin resultados retorna lista vacía**
- **Dado** que no existen registros con sistemaOrigen=CRM
- **Cuando** se envía GET /api/datos/sistema/CRM
- **Entonces** retorna 200 OK con `[]` (no 404)

## DoD (Definition of Done)

- [ ] Rama `feature/cord-114-dato-filtros-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] Tests findBySistemaOrigen y findBySucursalId en DatoRepositoryTest verdes
- [ ] Tests MockMvc /sistema/{origen} y /sucursal/{id} en DatoControllerTest verdes
- [ ] Test de lista vacía (200 + []) verificado
- [ ] Cobertura JaCoCo ≥ 60% verificada en data-service
- [ ] Ticket CORD-114 en Jira en estado "Finalizado" con comentario técnico
