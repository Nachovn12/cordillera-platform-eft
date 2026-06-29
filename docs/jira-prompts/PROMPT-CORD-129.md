# Prompt de Implementación - CORD-129

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-129 — Documento docs/persistencia.pdf con JPA y diagrama ER
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

Grupo Cordillera implementa el patrón **Database per Service**: cada microservicio tiene su propia base de datos MySQL independiente (`data_db`, `kpi_db`, `report_db`). No hay foreign keys entre bases de datos, eliminando el acoplamiento de datos entre servicios (common coupling). Cada microservicio gestiona su propia persistencia con Spring Data JPA.

Este documento justifica técnicamente cómo se implementó la capa de persistencia del proyecto: las entidades JPA con sus anotaciones reales, los repositorios con sus query methods generados automáticamente por Spring, el diagrama ER de las 3 bases de datos, y el principio de idempotencia implementado con `@UniqueConstraint` en la entidad `Reporte`.

Este documento es un artefacto del checklist EP3 — el evaluador verificará que `descripcion-persistencia.pdf` existe y contiene contenido real del proyecto.

## Historia de Usuario

**Como** equipo de Grupo Cordillera
**necesitamos** crear el documento docs/descripcion-persistencia.pdf que valide el uso de JPA, entidades y repositorios
**para** cumplir con el checklist de entrega del EP3 (ítem "descripcion-persistencia.pdf explica JPA con entidades, repositorios y ejemplos reales")

### Regla de Negocio Crítica
El archivo debe llamarse exactamente `docs/descripcion-persistencia.pdf` (ese nombre exacto verifica el checklist EP3). Debe tener mínimo 4 páginas con contenido real del proyecto, no páginas en blanco.

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
git checkout -b feature/cord-129-doc-persistencia-pdf
```

### Paso 1 — Redactar docs/persistencia.md

Ver detalle en Sub-task 1. Crear el archivo fuente en Markdown con todas las secciones.

### Paso 2 — Crear diagrama ER en dbdiagram.io

Ver detalle en Sub-task 2. Exportar como PNG y PDF.

### Paso 3 — Generar docs/descripcion-persistencia.pdf

Ver detalle en Sub-task 3. Convertir el Markdown a PDF con las imágenes incrustadas.

### Paso 4 — Verificar el PDF final

```bash
# Verificar que el archivo existe con nombre exacto
ls docs/descripcion-persistencia.pdf

# Abrir y verificar contenido:
# - Portada con nombre del proyecto
# - Las 3 entidades con anotaciones JPA reales
# - Los 3 repositorios con query methods reales
# - Diagrama ER de las 3 BDs
# - Mínimo 4 páginas de contenido real
```

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "docs(cord-129): descripcion-persistencia.pdf con entidades JPA, repositorios y diagrama ER"
git push origin feature/cord-129-doc-persistencia-pdf
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-129-doc-persistencia-pdf \
  --title "[CORD-129] Documento descripcion-persistencia.pdf — JPA, entidades y diagrama ER" \
  --body "## Cambios realizados\nCreación de docs/persistencia.md, diagrama ER en dbdiagram.io, PDF final\n\n## Entregables\ndocs/persistencia.md, docs/diagrama-er.png, docs/diagrama-er.pdf, docs/descripcion-persistencia.pdf\n\n## Checklist EP3\nNombre exacto verificado: descripcion-persistencia.pdf ✓" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-129 y sub-tasks a "Finalizada"
- Agregar comentario con link al PR y confirmar que el PDF tiene las 4 secciones

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-197]: Redactar docs/persistencia.md con secciones de entidades y repositorios

**Objetivo:** Crear el documento fuente en Markdown con el contenido real del proyecto

**Archivo a crear:** `docs/persistencia.md`

**Contenido del archivo:**

```markdown
# Descripción de la Capa de Persistencia — Grupo Cordillera

**Proyecto:** Plataforma de Monitoreo Organizacional
**Sprint:** S3 — EP3
**Equipo:** [Nombres del equipo]

---

## 1. Introducción

El sistema implementa el patrón **Database per Service** con Spring Data JPA:
cada microservicio gestiona su propia base de datos MySQL de forma independiente,
sin foreign keys entre bases de datos. Esto elimina el common coupling y
permite que cada servicio evolucione de forma autónoma.

Los tres schemas MySQL son: `data_db` (datos operacionales), `kpi_db` (indicadores)
y `report_db` (reportes ejecutivos). Spring Data JPA implementa el patrón
Repository automáticamente a partir de interfaces Java.

---

## 2. Entidades JPA

### 2.1 Dato.java (data-service → data_db.datos)

```java
@Entity
@Table(name = "datos")
public class Dato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "sistema_origen", nullable = false)
    private String sistemaOrigen;  // SAP, POS, ERP, CRM, E-COMMERCE, INVENTARIO, FINANZAS

    @NotBlank
    private String tipoDato;

    @NotBlank
    private String valor;

    @NotNull
    private Long sucursalId;

    private LocalDateTime fechaRegistro;

    @PrePersist
    public void prePersist() {
        // La fecha se asigna automáticamente al persistir
        this.fechaRegistro = LocalDateTime.now();
    }
}
```

### 2.2 Kpi.java (kpi-service → kpi_db.kpis)

```java
@Entity
@Table(name = "kpis")
public class Kpi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;  // calculado automáticamente por KpiFactory

    private String unidad;
    private String categoria;  // ventas, inventario, logistica, rentabilidad
    private String estado;
}
```

### 2.3 Reporte.java (report-service → report_db.reportes)

```java
@Entity
@Table(name = "reportes",
    uniqueConstraints = {
        // Idempotencia: no se puede generar el mismo reporte dos veces
        @UniqueConstraint(columnNames = {"area", "tipo", "anio", "mes"})
    })
public class Reporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String tipo;    // EJECUTIVO, OPERATIVO
    private String area;    // Finanzas, Ventas, Logística, RRHH
    private BigDecimal valor;
    private LocalDate fechaGeneracion;
    private Integer anio;
    private Integer mes;
}
```

---

## 3. Repositorios Spring Data JPA

| Repositorio | Query Methods reales | SQL generado automáticamente |
|-------------|---------------------|------------------------------|
| DatoRepository | findBySistemaOrigen(String) | SELECT * FROM datos WHERE sistema_origen = ? |
| DatoRepository | findBySucursalId(Long) | SELECT * FROM datos WHERE sucursal_id = ? |
| KpiRepository | findByCategoria(String) | SELECT * FROM kpis WHERE categoria = ? |
| ReporteRepository | findByArea(String) | SELECT * FROM reportes WHERE area = ? |
| ReporteRepository | existsByAreaAndTipoAndAnioAndMes(...) | SELECT COUNT(*) FROM reportes WHERE area=? AND tipo=? AND anio=? AND mes=? |

---

## 4. Diagrama ER

![Diagrama ER](diagrama-er.png)

---

## 5. Patrones de Diseño en la Persistencia

- **Repository Pattern**: Spring genera implementaciones SQL a partir de interfaces Java
- **Database per Service**: 3 schemas independientes sin FK cross-service
- **@PrePersist**: lógica de dominio en la entidad (fecha auto-asignada)
- **@UniqueConstraint**: idempotencia garantizada a nivel de base de datos
```

**Verificar:** El archivo debe tener mínimo 60 líneas de contenido real.

### Sub-task 2 [CORD-198]: Crear diagrama ER de las 3 BDs

**Objetivo:** Diagrama visual de las 3 tablas independientes con sus columnas

**Herramienta recomendada:** [dbdiagram.io](https://dbdiagram.io) (gratuito, sin instalación)

**DBML para dbdiagram.io:**
```
// Grupo Cordillera — Database per Service
// NOTA: estas 3 tablas son de BDs SEPARADAS — NO hay FK entre ellas

Table datos {
  id bigint [pk, increment]
  sistema_origen varchar(50) [not null]
  tipo_dato varchar(50) [not null]
  valor varchar(255) [not null]
  sucursal_id bigint [not null]
  fecha_registro datetime
  Note: 'data_db — datos operacionales de sucursales'
}

Table kpis {
  id bigint [pk, increment]
  nombre varchar(255) [not null]
  valor decimal(15,2)
  unidad varchar(50)
  categoria varchar(50)
  estado varchar(50)
  Note: 'kpi_db — indicadores calculados por KpiFactory'
}

Table reportes {
  id bigint [pk, increment]
  titulo varchar(255)
  tipo varchar(50)
  area varchar(100)
  valor decimal(15,2)
  fecha_generacion date
  anio int
  mes int
  indexes {
    (area, tipo, anio, mes) [unique, name: 'uk_reporte_periodo']
  }
  Note: 'report_db — unique(area,tipo,anio,mes) garantiza idempotencia'
}
```

**Exportar:**
- `docs/diagrama-er.png` (alta resolución — mínimo 1200px de ancho)
- `docs/diagrama-er.pdf` (vectorial)

### Sub-task 3 [CORD-199]: Generar docs/descripcion-persistencia.pdf

**Objetivo:** Convertir `persistencia.md` a PDF con diagrama ER incrustado

**Opción A — Con extensión VS Code "Markdown PDF":**
1. Abrir `docs/persistencia.md` en VS Code
2. Click derecho → "Markdown PDF: Export (pdf)"
3. Renombrar el archivo generado a `descripcion-persistencia.pdf`

**Opción B — Con Pandoc (si está instalado):**
```bash
pandoc docs/persistencia.md -o docs/descripcion-persistencia.pdf \
  --pdf-engine=wkhtmltopdf \
  --metadata title="Grupo Cordillera - Persistencia JPA"
```

**Opción C — Online:**
- Usar [markdowntopdf.com](https://www.markdowntopdf.com)

**Verificar el PDF:**
- [ ] Nombre exacto: `docs/descripcion-persistencia.pdf`
- [ ] Mínimo 4 páginas con contenido real
- [ ] Portada con nombre del proyecto y equipo
- [ ] Las 3 entidades con anotaciones JPA reales
- [ ] Los 3 repositorios con query methods
- [ ] Diagrama ER visible
- [ ] El PDF se abre correctamente

---

## Criterios de Aceptación

**AC1: Documento PDF entregable**
- **Dado** que el equipo completa las 3 sub-tasks
- **Cuando** el ZIP de entrega EP3 se arma
- **Entonces** existe `docs/descripcion-persistencia.pdf` con las 3 entidades, los 3 repositorios y el diagrama ER

**AC2: Diagrama ER preciso**
- **Dado** el diagrama ER creado con dbdiagram.io
- **Cuando** el evaluador lo revisa
- **Entonces** se ven las 3 tablas (datos, kpis, reportes) y queda claro que las 3 BDs son independientes

## DoD (Definition of Done)

- [ ] Rama `feature/cord-129-doc-persistencia-pdf` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] `docs/persistencia.md` creado con contenido real del proyecto
- [ ] `docs/diagrama-er.png` y `docs/diagrama-er.pdf` exportados de dbdiagram.io
- [ ] `docs/descripcion-persistencia.pdf` creado con nombre exacto
- [ ] PDF tiene mínimo 4 páginas con código JPA real
- [ ] Ticket CORD-129 en Jira en estado "Finalizado" con comentario técnico
