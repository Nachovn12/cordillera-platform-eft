# Prompt de Implementación - CORD-115

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-115 — Actualizar y eliminar dato operacional (PUT/DELETE)
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Los sistemas POS, SAP y ERP de las sucursales de Grupo Cordillera ocasionalmente envían datos erróneos: una venta con importe incorrecto, un registro de inventario con unidades equivocadas, o datos duplicados por fallas de conectividad. Los administradores de la plataforma necesitan poder corregir o eliminar esos datos directamente desde la API.

El `data-service` implementa PUT `/api/datos/{id}` para actualizar y DELETE `/api/datos/{id}` para eliminar. Ambas operaciones verifican primero que el dato existe antes de mutar — si el id no existe, `DatoService.buscarPorId()` lanza `NoSuchElementException` que el `GlobalExceptionHandler` convierte en HTTP 404.

Esta historia es crítica para la integridad de los datos que alimentan los KPIs ejecutivos: un dato erróneo en `data_db` genera KPIs incorrectos y reportes inexactos que podrían llevar a decisiones de negocio equivocadas.

## Historia de Usuario

**Como** administrador de la plataforma
**quiero** actualizar o eliminar datos operacionales erróneos via API REST
**para** mantener la integridad y veracidad de la base de datos data_db

### Regla de Negocio Crítica
`DatoService.buscarPorId()` lanza `NoSuchElementException` (java.util) — **NO** una excepción personalizada. El `GlobalExceptionHandler` mapea esa excepción a HTTP 404. PUT retorna 200 con el objeto modificado; DELETE retorna 204 sin body.

> **Contexto EP3:** Los ejecutivos necesitan corregir datos erróneos ingresados desde sistemas como POS o ERP. Esta HU valida la semántica REST correcta (PUT 200, DELETE 204, 404 para ID inexistente) del data-service — Indicadores 2, 3 y 4 EP3.


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
git checkout -b feature/cord-115-dato-put-delete-test
```

### Paso 1 — Verificar DatoService.actualizar() y eliminar()

Confirmar que ambos métodos llaman `buscarPorId()` antes de mutar y que lanzan `NoSuchElementException` cuando el id no existe.

```java
// En DatoService.java
public Dato actualizar(Long id, Dato datos) {
    Dato existente = buscarPorId(id); // lanza NoSuchElementException si no existe
    existente.setSistemaOrigen(datos.getSistemaOrigen());
    existente.setTipoDato(datos.getTipoDato());
    existente.setValor(datos.getValor());
    existente.setSucursalId(datos.getSucursalId());
    return datoRepository.save(existente);
}

public void eliminar(Long id) {
    buscarPorId(id); // lanza NoSuchElementException si no existe
    datoRepository.deleteById(id);
}

private Dato buscarPorId(Long id) {
    return datoRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Dato no encontrado con id: " + id));
}
```

**Verificar:**
```bash
mvn -pl data-service compile
```

### Paso 2 — Agregar tests de actualizar a DatoServiceTest

```bash
mvn test -pl data-service -Dtest=DatoServiceTest
```

### Paso 3 — Agregar tests MockMvc de PUT y DELETE a DatoControllerTest

```bash
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
git commit -m "feat(cord-115): tests PUT y DELETE en DatoService y DatoController con manejo NoSuchElementException"
git push origin feature/cord-115-dato-put-delete-test
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-115-dato-put-delete-test \
  --title "[CORD-115] Actualizar y eliminar dato operacional PUT/DELETE" \
  --body "## Cambios realizados\nTests PUT y DELETE con casos feliz y error 404\n\n## Tests\nDatoServiceTest (actualizar con id existente/inexistente), DatoControllerTest (PUT 200/404, DELETE 204/404)\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-115 y sub-tasks a "Finalizada"
- Agregar comentario técnico con PR link y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-150]: Agregar tests de DatoService.actualizar con id existente e inexistente

**Objetivo:** Verificar comportamiento de actualizar() con Optional presente y vacío

**Archivo a modificar:** `data-service/src/test/java/cl/duoc/cordillera/dataservice/service/DatoServiceTest.java`

**Código a agregar:**
```java
@Test
void actualizar_conIdExistente_debeRetornarDatoActualizado() {
    // Arrange - Escenario: corregir importe de venta POS a ERP en sucursal Santiago
    Dato datoExistente = new Dato();
    datoExistente.setId(1L);
    datoExistente.setSistemaOrigen("POS");
    datoExistente.setTipoDato("VENTA");
    datoExistente.setValor("125000");
    datoExistente.setSucursalId(1L);

    Dato nuevoDato = new Dato();
    nuevoDato.setSistemaOrigen("ERP");
    nuevoDato.setTipoDato("INVENTARIO");
    nuevoDato.setValor("150000");
    nuevoDato.setSucursalId(1L);

    Dato datoActualizado = new Dato();
    datoActualizado.setId(1L);
    datoActualizado.setSistemaOrigen("ERP");

    when(datoRepository.findById(1L)).thenReturn(Optional.of(datoExistente));
    when(datoRepository.save(any())).thenReturn(datoActualizado);

    // Act
    Dato resultado = datoService.actualizar(1L, nuevoDato);

    // Assert
    verify(datoRepository, times(1)).save(any());
    assertEquals("ERP", resultado.getSistemaOrigen());
}

@Test
void actualizar_conIdInexistente_debeLanzarNoSuchElementException() {
    // Arrange - Escenario: intento corregir dato con id que no existe en data_db
    when(datoRepository.findById(9999L)).thenReturn(Optional.empty());

    // Act & Assert
    // IMPORTANTE: DatoService lanza NoSuchElementException (java.util), NO excepción personalizada
    assertThrows(NoSuchElementException.class,
        () -> datoService.actualizar(9999L, new Dato()));
    verify(datoRepository, never()).save(any());
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoServiceTest
```

### Sub-task 2 [CORD-141]: Agregar tests MockMvc de PUT /api/datos/{id} (200 OK y 404)

**Objetivo:** Verificar contrato HTTP del endpoint PUT

**Archivo a modificar:** `data-service/src/test/java/cl/duoc/cordillera/dataservice/controller/DatoControllerTest.java`

**Código a agregar:**
```java
@Test
void actualizar_conIdValido_retorna200() throws Exception {
    // Arrange - Escenario: corrección de dato en sucursal Santiago
    Dato datos = new Dato();
    datos.setSistemaOrigen("ERP");
    datos.setTipoDato("INVENTARIO");
    datos.setValor("150000");
    datos.setSucursalId(1L);

    Dato actualizado = new Dato();
    actualizado.setId(1L);
    actualizado.setSistemaOrigen("ERP");

    when(datoService.actualizar(eq(1L), any())).thenReturn(actualizado);

    // Act & Assert
    // NOTA: ruta interna es /api/datos/{id}, NO /api/v1/datos/{id}
    mockMvc.perform(put("/api/datos/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(datos)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sistemaOrigen").value("ERP"));
}

@Test
void actualizar_conIdInexistente_retorna404() throws Exception {
    // Arrange - Escenario: intento actualizar dato inexistente
    when(datoService.actualizar(eq(9999L), any()))
        .thenThrow(new NoSuchElementException("Dato no encontrado"));

    // Act & Assert
    mockMvc.perform(put("/api/datos/9999")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new Dato())))
        .andExpect(status().isNotFound());
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoControllerTest
```

### Sub-task 3 [CORD-142]: Agregar tests MockMvc de DELETE /api/datos/{id} (204)

**Objetivo:** Verificar que DELETE retorna 204 sin body para id válido y 404 para inexistente

**Código a agregar en DatoControllerTest:**
```java
@Test
void eliminar_conIdValido_retorna204() throws Exception {
    // Arrange - Escenario: eliminar dato duplicado de sucursal
    doNothing().when(datoService).eliminar(1L);

    // Act & Assert
    // El estándar REST para DELETE exitoso es 204 No Content sin body
    mockMvc.perform(delete("/api/datos/1"))
        .andExpect(status().isNoContent());
}

@Test
void eliminar_conIdInexistente_retorna404() throws Exception {
    // Arrange
    // IMPORTANTE: DatoService lanza NoSuchElementException — NO DatoNoEncontradoException
    doThrow(new NoSuchElementException("Dato no encontrado"))
        .when(datoService).eliminar(9999L);

    // Act & Assert
    mockMvc.perform(delete("/api/datos/9999"))
        .andExpect(status().isNotFound());
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoControllerTest
```

---

## Criterios de Aceptación

**AC1: Actualización exitosa**
- **Dado** un ID válido existente en data_db
- **Cuando** se envía PUT /api/datos/{id} con payload actualizado
- **Entonces** retorna 200 OK con el objeto modificado

**AC2: ID inexistente retorna 404**
- **Dado** un ID que no existe (ej. 9999)
- **Cuando** se envía DELETE /api/datos/9999
- **Entonces** retorna 404 Not Found con mensaje del GlobalExceptionHandler

## DoD (Definition of Done)

- [ ] Rama `feature/cord-115-dato-put-delete-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] Tests actualizar con id existente e inexistente en DatoServiceTest verdes
- [ ] Tests MockMvc PUT 200/404 y DELETE 204/404 en DatoControllerTest verdes
- [ ] Cobertura JaCoCo ≥ 60% verificada en data-service
- [ ] Ticket CORD-115 en Jira en estado "Finalizado" con comentario técnico
