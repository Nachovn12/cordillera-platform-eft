# Prompt de Implementación - CORD-113

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-113 — Registrar dato operacional desde sistema externo (POS/ERP/CRM)
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Grupo Cordillera opera una cadena retail con múltiples sucursales distribuidas en distintas regiones. Cada sucursal genera datos operacionales diarios desde sistemas heterogéneos: el sistema POS registra ventas en tiempo real, SAP maneja finanzas, el ERP controla inventario y el CRM gestiona clientes. El problema central es que estos datos están dispersos y los ejecutivos no tienen visibilidad consolidada del desempeño global.

El microservicio `data-service` resuelve exactamente este problema: actúa como repositorio central de datos operacionales, recibiendo información desde cualquier sistema externo vía API REST y persistiéndola en `data_db`. Esto permite que el `kpi-service` y el `report-service` consuman datos homogéneos, sin importar el sistema de origen.

Esta historia implementa el endpoint de ingesta de datos (POST /api/datos), el primero y más crítico del sistema, pues sin datos operacionales no hay KPIs ni reportes. La validación Bean Validation garantiza la calidad de los datos desde su ingreso al sistema.

## Historia de Usuario

**Como** sistema externo POS/ERP/CRM del Grupo Cordillera
**quiero** registrar un dato operacional (venta, inventario, finanzas, CRM) via API REST
**para** consolidar la información dispersa de las sucursales en data_db y disponibilizarla para KPIs y reportes ejecutivos

### Regla de Negocio Crítica
Todo dato operacional debe tener `sistemaOrigen`, `tipoDato`, `valor` y `sucursalId` no nulos. La `fechaRegistro` se asigna automáticamente via `@PrePersist` en la entidad — nunca la envía el cliente externo.

> **Contexto EP3:** Esta HU es el punto de entrada al sistema. Sin datos operacionales de las sucursales (POS, SAP, ERP, CRM), no hay KPIs ni reportes. Los tests del data-service validan que la consolidación de datos funciona correctamente — Indicadores 2, 3 y 4 de la rúbrica EP3.


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

**OBLIGATORIO:** Siempre traer la última actualización desde `main` antes de empezar.

```bash
git checkout main
git pull origin main
git checkout -b feature/cord-113-dato-repository-service-controller-test
```

### Paso 1 — Verificar entidad Dato y DatoController

Verificar que `Dato.java` tiene `@PrePersist` para auto-asignar `fechaRegistro` y que `DatoController.java` tiene `@Valid` en el método `crear()`.

**Archivo:** `data-service/src/main/java/cl/duoc/cordillera/dataservice/model/Dato.java`

```java
@Entity
@Table(name = "datos")
public class Dato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String sistemaOrigen;

    @NotBlank
    private String tipoDato;

    @NotBlank
    private String valor;

    @NotNull
    private Long sucursalId;

    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        this.fechaRegistro = LocalDateTime.now();
    }
    // getters y setters
}
```

**Verificar:**
```bash
mvn -pl data-service compile
```

### Paso 2 — Crear application-test.properties con H2

**Archivo:** `data-service/src/test/resources/application-test.properties`

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Paso 3 — Crear DatoRepositoryTest, DatoServiceTest y DatoControllerTest

Ver detalle completo en la sección Sub-Tasks. Ejecutar:

```bash
mvn test -pl data-service -Dtest=DatoRepositoryTest
mvn test -pl data-service -Dtest=DatoServiceTest
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
git commit -m "feat(cord-113): tests DatoRepository, DatoService y DatoController con cobertura JaCoCo"
git push origin feature/cord-113-dato-repository-service-controller-test
```

**2. Crear Pull Request en GitHub:**
- Base branch: `main`
- Title: `[CORD-113] Registrar dato operacional desde sistema externo`
- Description: incluir qué se hizo, tests creados, cobertura obtenida
- **Asignar como Reviewer OBLIGATORIO: Nachovn12**

```bash
gh pr create --base main --head feature/cord-113-dato-repository-service-controller-test \
  --title "[CORD-113] Registrar dato operacional desde sistema externo" \
  --body "## Cambios realizados\nTests unitarios para DatoRepository, DatoService y DatoController\n\n## Tests\nDatoRepositoryTest (3 tests), DatoServiceTest (3 tests), DatoControllerTest (3 tests)\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-113 y sus sub-tasks de "Por Hacer" a "Finalizada"
- Agregar comentario técnico con PR link, tests creados y cobertura obtenida

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-134]: Crear DatoRepositoryTest con @DataJpaTest y H2

**Objetivo:** Verificar que DatoRepository persiste y consulta datos usando H2 en memoria

**Archivo a crear:** `data-service/src/test/java/cl/duoc/cordillera/dataservice/repository/DatoRepositoryTest.java`

**Código:**
```java
package cl.duoc.cordillera.dataservice.repository;

import cl.duoc.cordillera.dataservice.model.Dato;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class DatoRepositoryTest {

    @Autowired
    private DatoRepository datoRepository;

    @Test
    void save_debeRetornarDatoConId() {
        // Arrange - Escenario: Sistema POS de sucursal Santiago registra nueva venta
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        dato.setSucursalId(1L);

        // Act
        Dato guardado = datoRepository.save(dato);

        // Assert
        assertNotNull(guardado.getId());
        assertEquals("POS", guardado.getSistemaOrigen());
    }

    @Test
    void findBySistemaOrigen_debeRetornarLista() {
        // Arrange - Escenario: 2 ventas POS de distintas sucursales
        Dato d1 = crearDato("POS", "VENTA", "120000", 1L);
        Dato d2 = crearDato("POS", "VENTA", "95000", 2L);
        Dato d3 = crearDato("SAP", "FINANZAS", "500000", 1L);
        datoRepository.save(d1);
        datoRepository.save(d2);
        datoRepository.save(d3);

        // Act
        List<Dato> resultado = datoRepository.findBySistemaOrigen("POS");

        // Assert
        assertEquals(2, resultado.size());
    }

    @Test
    void findBySucursalId_debeRetornarLista() {
        // Arrange - Escenario: datos de sucursal Santiago (id=1)
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
    }

    private Dato crearDato(String origen, String tipo, String valor, Long sucursalId) {
        Dato d = new Dato();
        d.setSistemaOrigen(origen);
        d.setTipoDato(tipo);
        d.setValor(valor);
        d.setSucursalId(sucursalId);
        return d;
    }
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoRepositoryTest
```

### Sub-task 2 [CORD-135]: Crear DatoServiceTest con Mockito

**Objetivo:** Verificar lógica de negocio de DatoService con DatoRepository mockeado

**Archivo a crear:** `data-service/src/test/java/cl/duoc/cordillera/dataservice/service/DatoServiceTest.java`

**Código:**
```java
package cl.duoc.cordillera.dataservice.service;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.repository.DatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatoServiceTest {

    @Mock
    private DatoRepository datoRepository;

    @InjectMocks
    private DatoService datoService;

    @Test
    void crear_conPayloadValido_debePersistirYRetornar() {
        // Arrange - Escenario: Sistema POS registra venta en sucursal Santiago
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        dato.setSucursalId(1L);
        Dato datoConId = new Dato();
        datoConId.setId(1L);
        datoConId.setSistemaOrigen("POS");
        when(datoRepository.save(any())).thenReturn(datoConId);

        // Act
        Dato resultado = datoService.crear(dato);

        // Assert
        verify(datoRepository, times(1)).save(dato);
        assertNotNull(resultado.getId());
    }

    @Test
    void actualizar_conIdInexistente_debeLanzarNoSuchElementException() {
        // Arrange - Escenario: intento corregir dato con id incorrecto
        when(datoRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
            () -> datoService.actualizar(9999L, new Dato()));
    }

    @Test
    void eliminar_debeInvocarDeleteById() {
        // Arrange - Escenario: eliminar dato duplicado de sucursal
        Dato existente = new Dato();
        existente.setId(1L);
        when(datoRepository.findById(1L)).thenReturn(Optional.of(existente));

        // Act
        datoService.eliminar(1L);

        // Assert
        verify(datoRepository).deleteById(1L);
    }
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoServiceTest
```

### Sub-task 3 [CORD-136]: Crear DatoControllerTest con @WebMvcTest y MockMvc

**Objetivo:** Verificar contrato HTTP del DatoController — POST 201, POST inválido 400, GET 200

**Archivo a crear:** `data-service/src/test/java/cl/duoc/cordillera/dataservice/controller/DatoControllerTest.java`

**Código:**
```java
package cl.duoc.cordillera.dataservice.controller;

import cl.duoc.cordillera.dataservice.model.Dato;
import cl.duoc.cordillera.dataservice.service.DatoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatoController.class)
class DatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DatoService datoService;

    @Test
    void crear_conPayloadValido_retorna201() throws Exception {
        // Arrange - Escenario: POS sucursal Santiago envía venta correctamente
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        dato.setSucursalId(1L);
        Dato datoConId = new Dato();
        datoConId.setId(1L);
        datoConId.setSistemaOrigen("POS");
        when(datoService.crear(any())).thenReturn(datoConId);

        // Act & Assert
        mockMvc.perform(post("/api/datos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dato)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void crear_conSucursalIdNulo_retorna400() throws Exception {
        // Arrange - Escenario: POS envía dato sin identificar la sucursal
        Dato dato = new Dato();
        dato.setSistemaOrigen("POS");
        dato.setTipoDato("VENTA");
        dato.setValor("125000");
        // sucursalId = null → debe fallar @NotNull

        // Act & Assert
        mockMvc.perform(post("/api/datos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dato)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void listar_retorna200ConLista() throws Exception {
        // Arrange - Escenario: dashboard solicita todos los datos operacionales
        Dato d = new Dato();
        d.setId(1L);
        d.setSistemaOrigen("POS");
        when(datoService.listar()).thenReturn(List.of(d));

        // Act & Assert
        mockMvc.perform(get("/api/datos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}
```

**Verificar:**
```bash
mvn test -pl data-service -Dtest=DatoControllerTest
```

---

## Criterios de Aceptación

**AC1: Caso feliz de registro de venta POS**
- **Dado** que el BFF está levantado y data-service conectado a data_db
- **Cuando** el sistema POS envía POST /api/datos con payload válido
- **Entonces** retorna 201 Created con el Dato persistido incluyendo id y fechaRegistro

**AC2: Validación Bean Validation rechaza payload inválido**
- **Dado** que se envía POST con sucursalId null
- **Cuando** el GlobalExceptionHandler procesa la MethodArgumentNotValidException
- **Entonces** retorna 400 Bad Request con detalle de campos violados

## DoD (Definition of Done)

- [ ] Rama `feature/cord-113-dato-repository-service-controller-test` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] DatoRepositoryTest, DatoServiceTest y DatoControllerTest verdes
- [ ] Cobertura JaCoCo ≥ 60% verificada en data-service
- [ ] Ticket CORD-113 en Jira en estado "Finalizado" con comentario técnico
