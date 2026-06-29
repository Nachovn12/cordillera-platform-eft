# Prompt de Implementación - CORD-123

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-123 — Configurar quality gate JaCoCo 60% en report-service
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El `report-service` es el microservicio más complejo del sistema de Grupo Cordillera: integra el patrón Factory Method (`ExportadorFactory`), el patrón Circuit Breaker (Resilience4j hacia `kpi-service`), la restricción de idempotencia (UNIQUE constraint en `reportes`) y el filtrado por área. Si cualquiera de estas lógicas tiene bugs, los directivos reciben reportes incorrectos o duplicados.

El quality gate JaCoCo al 60% garantiza cobertura objetiva de toda esa lógica crítica antes de cada despliegue. Es la "red de seguridad" del `report-service` en el pipeline CI del EP3. Los tests usan H2 en memoria para `@DataJpaTest` — no requieren MySQL levantado.

## Historia de Usuario

**Como** desarrollador backend del equipo Grupo Cordillera
**quiero** configurar el plugin JaCoCo en el report-service con una compuerta de calidad mínima del 60%
**para** garantizar que el código de reportes (PDF/Excel/JSON, Circuit Breaker, idempotencia) tiene cobertura verificada

### Regla de Negocio Crítica
El quality gate usa `LINE` como counter (cobertura de líneas) — mínimo 0.60. El build falla con `"Coverage check failed for project: report-service"` si la cobertura cae bajo ese umbral.

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
git checkout -b feature/cord-123-jacoco-report-service
```

### Paso 1 — Modificar report-service/pom.xml

Agregar ejecución `check` al jacoco-maven-plugin y verificar dependencia H2:

**Archivo:** `report-service/pom.xml`

```xml
<!-- Dentro del plugin jacoco-maven-plugin, agregar: -->
<execution>
    <id>check</id>
    <phase>verify</phase>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.60</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

Verificar H2 en pom.xml:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

### Paso 2 — Crear application-test.properties con H2

**Archivo:** `report-service/src/test/resources/application-test.properties`

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

### Paso 3 — Agregar maven-antrun-plugin para copiar reporte a docs/

**En report-service/pom.xml:**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-antrun-plugin</artifactId>
    <version>3.1.0</version>
    <executions>
        <execution>
            <id>copy-jacoco-report</id>
            <phase>verify</phase>
            <goals><goal>run</goal></goals>
            <configuration>
                <target>
                    <copy file="${project.build.directory}/site/jacoco/index.html"
                          tofile="${project.basedir}/../docs/jacoco-report-service.html"
                          failonerror="false"/>
                </target>
            </configuration>
        </execution>
    </executions>
</plugin>
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
git commit -m "feat(cord-123): configurar JaCoCo quality gate 60% en report-service"
git push origin feature/cord-123-jacoco-report-service
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-123-jacoco-report-service \
  --title "[CORD-123] Quality gate JaCoCo 60% report-service" \
  --body "## Cambios realizados\nConfiguración quality gate JaCoCo 60% en report-service/pom.xml\n\n## Tests\nExportadorFactoryTest, ReporteServiceTest, ReporteRepositoryTest, ReporteControllerTest\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-123 y sub-tasks a "Finalizada"
- Agregar comentario con porcentaje de cobertura y screenshot adjunto

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-174]: Agregar bloque check-coverage al pom.xml de report-service

**Objetivo:** Configurar el quality gate JaCoCo con mínimo 60% en la fase verify

**Bloque XML completo del plugin jacoco-maven-plugin:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals><goal>check</goal></goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.60</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Verificar:**
```bash
mvn clean verify -pl report-service
# Esperado: BUILD SUCCESS con cobertura >= 60%
```

### Sub-task 2 [CORD-175]: Configurar post-verify para copiar reporte a docs/

**Objetivo:** Generar `docs/jacoco-report-service.html` automáticamente tras cada `mvn verify`

Este es el tercer reporte de los cuatro necesarios (junto con data-service, kpi-service y bff-gateway) para el `informe-pruebas-unitarias.pdf` del checklist EP3.

**Verificar tras ejecutar mvn verify:**
```bash
ls docs/jacoco-report-service.html
# El archivo debe existir con métricas de ExportadorFactory, ReporteService, ReporteController
```

### Sub-task 3 [CORD-176]: Validar mvn verify con cobertura menor al 60%

**Objetivo:** Demostrar que el quality gate es efectivo como compuerta CI

**Pasos:**
1. Comentar temporalmente `ExportadorFactoryTest.java` y `ReporteServiceTest.java`
2. Ejecutar:
```bash
mvn verify -pl report-service
# Esperado: BUILD FAILURE — "Coverage check failed for project: report-service"
```
3. Restaurar todos los tests
4. Ejecutar nuevamente:
```bash
mvn clean verify -pl report-service
# Esperado: BUILD SUCCESS con cobertura >= 60%
```
5. Capturar screenshot de `target/site/jacoco/index.html` con porcentaje visible
6. Guardar como `docs/jacoco-report-service.png` para el informe PDF

**Nota para la defensa oral:** Este ciclo demuestra la Integración Continua del CASO sección 3 — el pipeline CI valida cobertura automáticamente en cada actualización del código.

---

## Criterios de Aceptación

**AC1: Reporte HTML generado con cobertura ≥ 60%**
- **Dado** que el report-service tiene tests para ReporteService, ExportadorFactory y ReporteRepository
- **Cuando** se ejecuta `mvn clean verify -pl report-service`
- **Entonces** BUILD SUCCESS con `target/site/jacoco/index.html` con cobertura ≥ 60%

**AC2: Quality gate rechaza cobertura insuficiente**
- **Dado** que se comentan ExportadorFactoryTest y ReporteServiceTest
- **Cuando** se ejecuta `mvn verify -pl report-service`
- **Entonces** BUILD FAILURE con "Coverage check failed for project: report-service"

**AC3: BD de pruebas usa H2 (no toca report_db MySQL)**
- **Dado** que `application-test.properties` tiene `spring.datasource.url=jdbc:h2:mem:testdb`
- **Cuando** los tests @DataJpaTest se ejecutan
- **Entonces** las operaciones ocurren en H2 en memoria

## DoD (Definition of Done)

- [ ] Rama `feature/cord-123-jacoco-report-service` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] `mvn clean verify -pl report-service` → BUILD SUCCESS con JaCoCo ≥ 60%
- [ ] `target/site/jacoco/index.html` generado y navegable
- [ ] H2 en `application-test.properties` confirmado (sin MySQL)
- [ ] Screenshot guardado en `docs/jacoco-report-service.png`
- [ ] Ticket CORD-123 en Jira en estado "Finalizado" con comentario técnico y screenshot
