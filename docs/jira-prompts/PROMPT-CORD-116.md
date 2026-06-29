# Prompt de Implementación - CORD-116

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-116 — Configurar quality gate JaCoCo 60% en data-service
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El `data-service` es el punto de ingesta de todos los datos operacionales de las sucursales de Grupo Cordillera. Si este servicio tiene bugs no detectados, los KPIs y reportes ejecutivos se generarán con datos incorrectos, llevando a decisiones de negocio equivocadas. La cobertura mínima del 60% con JaCoCo garantiza que la lógica crítica de validación, persistencia y consulta de datos está verificada antes de cada despliegue.

El quality gate JaCoCo actúa como una red de seguridad automática: si alguien elimina tests o reduce la cobertura al hacer merge a `main`, el build falla y alerta al equipo. Esto implementa el principio de Integración Continua mencionado en el CASO sección 3 del proyecto.

La BD de pruebas es H2 en memoria — los tests `@DataJpaTest` no requieren MySQL levantado, lo que permite ejecutar el pipeline CI en cualquier entorno sin infraestructura de base de datos real.

## Historia de Usuario

**Como** desarrollador backend del equipo Grupo Cordillera
**quiero** configurar el plugin JaCoCo en el data-service con una compuerta de calidad mínima del 60%
**para** verificar objetivamente la cobertura requerida por la rúbrica EP3 y asegurar tests válidos antes del despliegue

### Regla de Negocio Crítica
El quality gate JaCoCo debe fallar el build con `BUILD FAILURE` si la cobertura cae bajo el 60%. El mensaje debe indicar exactamente: `"Coverage check failed for project: data-service"`.

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
git checkout -b feature/cord-116-jacoco-data-service
```

### Paso 1 — Modificar data-service/pom.xml

Agregar la ejecución `check` con regla de cobertura mínima al plugin jacoco-maven-plugin ya existente:

**Archivo:** `data-service/pom.xml`

```xml
<!-- Dentro del plugin jacoco-maven-plugin, agregar esta tercera ejecución: -->
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
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.60</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

También verificar que la dependencia H2 está presente en `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
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

### Paso 3 — Agregar maven-antrun-plugin para copiar reporte a docs/

**En data-service/pom.xml**, dentro de `<build><plugins>`, agregar:

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
                          tofile="${project.basedir}/../docs/jacoco-data-service.html"
                          failonerror="false"/>
                </target>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Paso 4 — Validación con JaCoCo

```bash
mvn clean verify -pl data-service
```

**OJO:** Si la cobertura JaCoCo es menor al 60%, la IA NO DEBE avanzar al Paso 5. Debe agregar más tests y volver a ejecutar.

Abrir en el navegador:
```
data-service/target/site/jacoco/index.html
```

Capturar screenshot y guardar en `docs/jacoco-data-service.png`.

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-116): configurar JaCoCo quality gate 60% en data-service"
git push origin feature/cord-116-jacoco-data-service
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-116-jacoco-data-service \
  --title "[CORD-116] Quality gate JaCoCo 60% data-service" \
  --body "## Cambios realizados\nConfiguración quality gate JaCoCo en pom.xml del data-service\n\n## Tests\nDatoRepositoryTest, DatoServiceTest, DatoControllerTest\n\n## Cobertura JaCoCo\n>=60%" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-116 y sub-tasks a "Finalizada"
- Agregar comentario con porcentaje de cobertura obtenido y screenshot adjunto

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-143]: Agregar bloque check-coverage al pom.xml del data-service

**Objetivo:** Configurar el quality gate JaCoCo con mínimo 60% en la fase verify

**Archivo a modificar:** `data-service/pom.xml`

**Bloque XML completo del plugin jacoco-maven-plugin:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <executions>
        <!-- Ejecución 1: instrumentar agente JaCoCo -->
        <execution>
            <id>prepare-agent</id>
            <goals><goal>prepare-agent</goal></goals>
        </execution>
        <!-- Ejecución 2: generar reporte HTML -->
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals><goal>report</goal></goals>
        </execution>
        <!-- Ejecución 3: quality gate mínimo 60% -->
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
                                <counter>INSTRUCTION</counter>
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
mvn clean verify -pl data-service
# Esperado: BUILD SUCCESS con cobertura >= 60%
```

### Sub-task 2 [CORD-154]: Agregar maven-antrun-plugin para copiar reporte a docs/

**Objetivo:** Copiar automáticamente el reporte HTML de JaCoCo a la carpeta docs/ del monorepo

**Verificar tras ejecutar mvn verify:**
```bash
ls docs/jacoco-data-service.html
# El archivo debe existir y mostrar las métricas reales del data-service
```

### Sub-task 3 [CORD-155]: Validar que mvn verify falla con cobertura menor al 60%

**Objetivo:** Demostrar que el quality gate actúa como compuerta real de CI

**Pasos de validación:**
1. Comentar temporalmente `DatoServiceTest.java` y `DatoControllerTest.java`
2. Ejecutar:
```bash
mvn verify -pl data-service
# Esperado: BUILD FAILURE con mensaje "Coverage check failed for project: data-service"
```
3. Restaurar todos los tests
4. Ejecutar nuevamente:
```bash
mvn clean verify -pl data-service
# Esperado: BUILD SUCCESS con cobertura >= 60%
```
5. Capturar screenshot de `target/site/jacoco/index.html`
6. Guardar como `docs/jacoco-data-service.png`

---

## Criterios de Aceptación

**AC1: Reporte HTML generado exitosamente**
- **Dado** el data-service con DatoRepositoryTest, DatoServiceTest y DatoControllerTest
- **Cuando** se ejecuta `mvn clean verify -pl data-service`
- **Entonces** genera `target/site/jacoco/index.html` con cobertura ≥ 60% y BUILD SUCCESS

**AC2: Quality gate rechaza cobertura insuficiente**
- **Dado** que se comentan los tests del data-service
- **Cuando** se ejecuta `mvn verify -pl data-service`
- **Entonces** BUILD FAILURE con "Coverage check failed for project: data-service"

## DoD (Definition of Done)

- [ ] Rama `feature/cord-116-jacoco-data-service` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] `mvn clean verify -pl data-service` → BUILD SUCCESS con JaCoCo ≥ 60%
- [ ] `target/site/jacoco/index.html` generado y navegable
- [ ] H2 en `application-test.properties` confirmado (sin MySQL)
- [ ] Screenshot guardado en `docs/jacoco-data-service.png`
- [ ] Ticket CORD-116 en Jira en estado "Finalizado" con comentario técnico y screenshot
