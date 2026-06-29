# Prompt de Implementación - CORD-130

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-130 — Documento docs/informe-pruebas-unitarias.pdf con cobertura y CA-Test
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El EP3 exige evidencia objetiva de la calidad del código: no basta con decir "los tests pasan", hay que demostrarlo con reportes de cobertura JaCoCo de los 4 microservicios Java y una tabla que mapea cada Criterio de Aceptación de las HUs al test Java que lo verifica.

El `informe-pruebas-unitarias.pdf` es el artefacto central del Indicador 4 del EP3 (encargo grupal) y el material de apoyo principal para el Indicador 8 (defensa oral sobre patrones de diseño). Un evaluador que lee este informe debe poder ver qué tests cubren qué comportamientos y por qué los patrones de diseño elegidos (Factory Method, Repository, Circuit Breaker) facilitan la escritura de tests unitarios.

## Historia de Usuario

**Como** equipo de Grupo Cordillera
**necesitamos** crear el documento docs/informe-pruebas-unitarias.pdf con cobertura JaCoCo y tabla CA-Test
**para** cumplir con el checklist de entrega del EP3 y presentar durante la defensa oral

### Regla de Negocio Crítica
El archivo debe llamarse exactamente `docs/informe-pruebas-unitarias.pdf`. Debe incluir screenshots de los 4 reportes JaCoCo con porcentajes visibles ≥ 60%, tabla CA-Test con mínimo 12 filas, y sección de patrones de diseño.

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
git checkout -b feature/cord-130-informe-pruebas-unitarias-pdf
```

### Paso 1 — Ejecutar mvn verify en los 4 microservicios y capturar screenshots

```bash
# Ejecutar en orden:
mvn clean verify -pl data-service
mvn clean verify -pl kpi-service
mvn clean verify -pl report-service
mvn clean verify -pl bff-gateway
```

Para cada uno, abrir el reporte en el navegador y capturar screenshot:
- `data-service/target/site/jacoco/index.html` → guardar como `docs/jacoco-data-service.png`
- `kpi-service/target/site/jacoco/index.html` → guardar como `docs/jacoco-kpi-service.png`
- `report-service/target/site/jacoco/index.html` → guardar como `docs/jacoco-report-service.png`
- `bff-gateway/target/site/jacoco/index.html` → guardar como `docs/jacoco-bff-gateway.png`

### Paso 2 — Crear tools/build_test_report.py

Ver detalle en Sub-task 1.

```bash
python tools/build_test_report.py
# Genera docs/resumen-cobertura.md con tabla consolidada
```

### Paso 3 — Redactar docs/informe-pruebas-unitarias.md

Ver detalle en Sub-task 2.

### Paso 4 — Generar docs/informe-pruebas-unitarias.pdf

Ver detalle en Sub-task 3.

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "docs(cord-130): informe-pruebas-unitarias.pdf con tabla CA-Test y screenshots JaCoCo de 4 microservicios"
git push origin feature/cord-130-informe-pruebas-unitarias-pdf
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-130-informe-pruebas-unitarias-pdf \
  --title "[CORD-130] informe-pruebas-unitarias.pdf — cobertura JaCoCo y tabla CA-Test" \
  --body "## Cambios realizados\nScript Python consolidación JaCoCo, informe-pruebas-unitarias.md y PDF final\n\n## Entregables\ntools/build_test_report.py, docs/resumen-cobertura.md, docs/informe-pruebas-unitarias.pdf\n\n## Checklist EP3\nNombre exacto verificado: informe-pruebas-unitarias.pdf ✓, 4 screenshots JaCoCo incluidos ✓" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-130 y sub-tasks a "Finalizada"
- Agregar comentario confirmando que el PDF tiene tabla CA-Test + screenshots de los 4 MS

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-200]: Crear tools/build_test_report.py para consolidar HTML de JaCoCo

**Objetivo:** Script Python para extraer métricas de cobertura de los 4 reportes HTML

**Archivo a crear:** `tools/build_test_report.py`

**Código:**
```python
#!/usr/bin/env python3
"""
build_test_report.py — Grupo Cordillera EP3
Consolida los 4 reportes JaCoCo HTML en una tabla Markdown.
Uso: python tools/build_test_report.py (desde la raíz del monorepo)
"""
import re
import os

DOCS_DIR = os.path.join(os.path.dirname(__file__), '..', 'docs')

REPORTES = [
    ('data-service',   'jacoco-data-service.html',   'DatoService, DatoController, DatoRepository'),
    ('kpi-service',    'jacoco-kpi-service.html',    'KpiFactory, KpiService, KpiController'),
    ('report-service', 'jacoco-report-service.html', 'ExportadorFactory, ReporteService, ReporteController'),
    ('bff-gateway',    'jacoco-bff-gateway.html',    'DashboardService, AuthService, DashboardController'),
]

def extraer_cobertura(html_path):
    """Extrae el porcentaje de cobertura de líneas del HTML de JaCoCo."""
    if not os.path.exists(html_path):
        return None, None, None
    with open(html_path, 'r', encoding='utf-8') as f:
        contenido = f.read()
    # JaCoCo muestra en la primera fila: "X of Y missed" o el porcentaje directamente
    patron = r'(\d+)\s+of\s+(\d+)\s+lines'
    coincidencias = re.findall(patron, contenido)
    if coincidencias:
        missed, total = int(coincidencias[0][0]), int(coincidencias[0][1])
        covered = total - missed
        porcentaje = (covered / total * 100) if total > 0 else 0
        return covered, total, round(porcentaje, 1)
    return None, None, None

def generar_tabla():
    filas = []
    for servicio, archivo, clases in REPORTES:
        html_path = os.path.join(DOCS_DIR, archivo)
        covered, total, pct = extraer_cobertura(html_path)
        if pct is not None:
            estado = '✅' if pct >= 60 else '❌'
            fila = f"| {servicio} | {covered}/{total} | {pct}% | {estado} | {clases} |"
        else:
            fila = f"| {servicio} | PENDIENTE | — | ⏳ | {clases} |"
        filas.append(fila)
    return filas

def main():
    tabla = [
        "# Resumen de Cobertura JaCoCo — Grupo Cordillera EP3",
        "",
        "| Microservicio | Líneas cubiertas | Cobertura | Estado | Clases principales |",
        "|---------------|------------------|-----------|--------|--------------------|",
    ] + generar_tabla() + [
        "",
        "> Generado automáticamente por tools/build_test_report.py",
        f"> Fecha: {__import__('datetime').date.today()}",
    ]

    output_path = os.path.join(DOCS_DIR, 'resumen-cobertura.md')
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(tabla))
    print(f"✓ Generado: {output_path}")

if __name__ == '__main__':
    main()
```

**Verificar:**
```bash
python tools/build_test_report.py
cat docs/resumen-cobertura.md
```

### Sub-task 2 [CORD-201]: Redactar docs/informe-pruebas-unitarias.md con tabla CA-Test

**Objetivo:** Crear el informe completo con tabla CA-Test y sección de patrones

**Archivo a crear:** `docs/informe-pruebas-unitarias.md`

**Estructura del documento:**

```markdown
# Informe de Pruebas Unitarias — Grupo Cordillera EP3

**Proyecto:** Plataforma de Monitoreo Organizacional
**Cobertura promedio:** ≥ 60% en los 4 microservicios Java
**Herramientas:** JaCoCo 0.8.13 + Mockito + @WebMvcTest + @DataJpaTest + H2

---

## 1. Resumen Ejecutivo

Los 4 microservicios Java del sistema tienen cobertura ≥ 60% verificada con
JaCoCo. Los tests usan H2 en memoria para @DataJpaTest (sin MySQL real),
MockitoExtension para tests de servicio y @WebMvcTest para tests de controller.

## 2. Tabla CA-Test

| Criterio de Aceptación | Test Java que lo cubre |
|------------------------|----------------------|
| AC1 CORD-113: POST /api/datos retorna 201 | DatoControllerTest.crear_conPayloadValido_retorna201() |
| AC2 CORD-113: sucursalId null retorna 400 | DatoControllerTest.crear_conSucursalIdNulo_retorna400() |
| AC1 CORD-114: filtrado por sistemaOrigen | DatoRepositoryTest.findBySistemaOrigen_retornaListaFiltrada() |
| AC2 CORD-114: lista vacía retorna 200 | DatoControllerTest.buscarPorSistema_sinResultados_retorna200Vacio() |
| AC2 CORD-115: id inexistente retorna 404 | DatoServiceTest.actualizar_conIdInexistente_debeLanzarNoSuchElementException() |
| AC1 CORD-117: KpiFactory categoría ventas | KpiFactoryTest.obtenerCalculador_ventas_retornaVentasCalculator() |
| AC1 CORD-118: create invoca KpiFactory 1x | KpiServiceTest.create_invocaObtenerCalculador_exactamente1Vez() |
| AC2 CORD-118: update id inexistente 404 | KpiServiceTest.update_conIdInexistente_lanzaResponseStatusException() |
| AC1 CORD-120: generarReporte nuevo | ReporteServiceTest.generarReporte_conAreaYValorValidos_guardaReporteNuevo() |
| AC2 CORD-120: idempotencia reporte | ReporteServiceTest.generarReporte_conMismoPeriodo_retornaExistente() |
| AC1 CORD-121: exportar PDF retorna bytes | ExportadorFactoryTest.crearExportador_PDF_retornaPdfExportador() |
| AC2 CORD-121: formato inválido 400 | ExportadorFactoryTest.crearExportador_formatoInvalido_lanzaResponseStatusException() |
| AC2 CORD-124: BFF degradado retorna 200 | DashboardControllerTest.getStats_degradado_retorna200ConAlerta() |
| AC1 CORD-126: connectTimeout=2000 | RestTemplateConfigTest.restTemplate_tieneConnectTimeoutConfigurado() |

## 3. Tabla de Cobertura Consolidada

[Contenido de docs/resumen-cobertura.md — incluir tabla aquí]

## 4. Patrones de Diseño y su Impacto en la Calidad

### Repository Pattern
Los tests de repositorios usan H2 en memoria (no MySQL real).
Los tests son independientes del entorno → fáciles de ejecutar en CI.

### Factory Method (KpiFactory, ExportadorFactory)
Cada calculadora/exportador es una clase simple testeable directamente.
KpiFactoryTest cubre 100% de KpiFactory con 5 tests — alta densidad de cobertura.

### Strategy (VentasCalculator, etc.)
Cada estrategia es aislable en test unitario sin Spring — tests rápidos.

### Circuit Breaker (DashboardService, KpiClienteService)
El fallback es una rama del código → mockear la excepción cubre esa rama → más cobertura.
Testeable con Mockito sin levantar microservicios reales.

## 5. Screenshots JaCoCo

![JaCoCo data-service](jacoco-data-service.png)
![JaCoCo kpi-service](jacoco-kpi-service.png)
![JaCoCo report-service](jacoco-report-service.png)
![JaCoCo bff-gateway](jacoco-bff-gateway.png)
```

### Sub-task 3 [CORD-202]: Generar docs/informe-pruebas-unitarias.pdf

**Objetivo:** Convertir el Markdown a PDF con imágenes JaCoCo incrustadas

**Pasos:**
1. Verificar que los 4 screenshots existen en `docs/`:
   ```bash
   ls docs/jacoco-*.png
   ```
2. Convertir a PDF (elegir una opción):
   - **VS Code "Markdown PDF"**: Click derecho en el archivo → Export (pdf)
   - **Pandoc**: `pandoc docs/informe-pruebas-unitarias.md -o docs/informe-pruebas-unitarias.pdf`
   - **Online**: markdowntopdf.com

3. Verificar el PDF:
   ```bash
   ls docs/informe-pruebas-unitarias.pdf
   # Nombre exacto verificado ✓
   ```

4. Abrir y confirmar que contiene:
   - [ ] Portada con nombre del proyecto y equipo
   - [ ] Tabla CA-Test con mínimo 12 filas
   - [ ] 4 screenshots de JaCoCo con porcentajes visibles ≥ 60%
   - [ ] Tabla de cobertura consolidada
   - [ ] Sección de patrones de diseño

---

## Criterios de Aceptación

**AC1: Informe PDF entregable**
- **Dado** que los 4 microservicios tienen JaCoCo configurado y los tests pasan
- **Cuando** el equipo genera `docs/informe-pruebas-unitarias.pdf`
- **Entonces** el PDF contiene tabla CA-Test, screenshots de JaCoCo, tabla consolidada y patrones

**AC2: Evidencia visual de cobertura**
- **Dado** los 4 reportes JaCoCo HTML generados
- **Cuando** el evaluador revisa el PDF
- **Entonces** puede ver screenshots del index.html de JaCoCo de cada microservicio con cobertura ≥ 60%

## DoD (Definition of Done)

- [ ] Rama `feature/cord-130-informe-pruebas-unitarias-pdf` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] `docs/informe-pruebas-unitarias.pdf` creado con nombre exacto
- [ ] PDF incluye tabla CA-Test completa (mínimo 12 filas)
- [ ] PDF incluye 4 screenshots de JaCoCo con porcentajes visibles
- [ ] PDF incluye tabla de cobertura consolidada
- [ ] PDF incluye sección de patrones de diseño
- [ ] Ticket CORD-130 en Jira en estado "Finalizado" con comentario técnico
