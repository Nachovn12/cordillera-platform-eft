# Prompt de Implementación - CORD-128

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-128 — Estados loading/empty/error/degradado en 6 screens
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El frontend del dashboard de Grupo Cordillera debe ser robusto ante cualquier fallo de red o de microservicio. Si el BFF no responde, la pantalla no puede quedar en blanco — el usuario necesita saber qué está pasando. Si el `kpi-service` está caído y el BFF retorna `status="Degradado"`, el usuario debe ver un banner de aviso pero aún puede ver los datos disponibles.

Los 4 estados de UI a implementar son: **Loading** (fetch en progreso), **Error** (fallo de red o BFF no disponible), **Empty** (API retorna lista vacía), y **Degradado** (BFF retorna status="Degradado" porque algún microservicio falló). Estos estados se aplican a las 6 pantallas principales del dashboard.

Esta historia es clave para el **Indicador 7** del EP3 (presentación de integración en la defensa oral): el evaluador esperará ver el `DegradedBanner` aparecer al ejecutar `docker stop kpi-service`.

## Historia de Usuario

**Como** usuario del Frontend del Grupo Cordillera
**quiero** que todas las pantallas muestren estados visuales claros (cargando, sin datos, error, servicio degradado)
**para** nunca quedarme frente a una pantalla en blanco o sin entender qué está ocurriendo

### Regla de Negocio Crítica
Cuando el BFF retorna `status="Degradado"`, el frontend DEBE mostrar el `DegradedBanner` amarillo. Cuando el fetch falla completamente (error de red), DEBE mostrar `ErrorMessage`. Ninguna pantalla puede quedar en blanco en ningún escenario.

> **Contexto EP3:** Los ejecutivos de Grupo Cordillera no pueden quedarse frente a una pantalla en blanco mientras cargan los KPIs o cuando el kpi-service está caído. Los estados visuales (loading, error, degradado) demuestran que el sistema React es robusto y profesional — Indicadores 2 y 7 EP3. Demo en vivo: docker stop kpi-service → DegradedBanner aparece.


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
git checkout -b feature/cord-128-estados-ui-loading-error-degraded
```

### Paso 1 — Crear directorio y 4 componentes de estado

Ver detalle en Sub-task 1. Ejecutar:
```bash
npm run dev
# Los 4 componentes deben compilar sin errores
```

### Paso 2 — Aplicar estados en Dashboard, KPIs y Alertas

Ver detalle en Sub-task 2.

### Paso 3 — Aplicar estados en Reportes, Servicios y Configuración

Ver detalle en Sub-task 3.

**Verificar:**
```bash
npm run build
# Compilación exitosa sin errores ni warnings
```

### Paso 4 — Demo para defensa oral

Preparar el script para la defensa:
```bash
# En terminal separada:
docker stop kpi-service
# El DashboardScreen debe mostrar DegradedBanner amarillo en tiempo real
# El BFF retorna status="Degradado" — el frontend lo detecta automáticamente

# Para restaurar:
docker start kpi-service
```

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-128): estados UI loading/error/empty/degradado en 6 screens con componentes reutilizables"
git push origin feature/cord-128-estados-ui-loading-error-degraded
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-128-estados-ui-loading-error-degraded \
  --title "[CORD-128] Estados UI loading/error/empty/degradado en 6 screens" \
  --body "## Cambios realizados\n4 componentes de estado (LoadingSpinner, ErrorMessage, EmptyState, DegradedBanner), aplicados en 6 screens\n\n## Tests\nVerificación visual: demo docker stop kpi-service → DegradedBanner aparece\n\n## Cobertura JaCoCo\nN/A (frontend)" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-128 y sub-tasks a "Finalizada"
- Agregar comentario con link al PR y screenshots de los 4 estados

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-194]: Crear 4 componentes de estado en components/estados/

**Objetivo:** Crear los componentes reutilizables de estado visual para todas las pantallas

**Archivo a crear:** `frontend/src/components/estados/LoadingSpinner.jsx`
```jsx
import { Loader2 } from 'lucide-react';

/**
 * Spinner animado para estados de carga.
 * Usado en todas las screens mientras se espera respuesta del BFF.
 */
const LoadingSpinner = ({ mensaje = 'Cargando...' }) => {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '3rem',
      gap: '1rem'
    }}>
      <Loader2
        size={48}
        style={{
          color: '#3b82f6',
          animation: 'spin 1s linear infinite'
        }}
      />
      <p style={{ color: '#6b7280', fontSize: '0.875rem' }}>{mensaje}</p>
      <style>{`@keyframes spin { from { transform: rotate(0deg); } to { transform: rotate(360deg); } }`}</style>
    </div>
  );
};

export default LoadingSpinner;
```

**Archivo a crear:** `frontend/src/components/estados/ErrorMessage.jsx`
```jsx
import { AlertCircle } from 'lucide-react';

/**
 * Mensaje de error de red o BFF no disponible.
 * Nunca debe quedar la pantalla en blanco ante un error.
 */
const ErrorMessage = ({ titulo = 'Error', detalle = 'No se pudo conectar con el servidor' }) => {
  return (
    <div style={{
      display: 'flex',
      alignItems: 'flex-start',
      gap: '0.75rem',
      padding: '1rem',
      backgroundColor: '#fef2f2',
      border: '1px solid #fecaca',
      borderRadius: '0.5rem',
      margin: '1rem 0'
    }}>
      <AlertCircle size={20} style={{ color: '#ef4444', flexShrink: 0, marginTop: '0.125rem' }} />
      <div>
        <p style={{ fontWeight: '600', color: '#dc2626', margin: 0 }}>{titulo}</p>
        <p style={{ fontSize: '0.875rem', color: '#6b7280', margin: '0.25rem 0 0' }}>{detalle}</p>
      </div>
    </div>
  );
};

export default ErrorMessage;
```

**Archivo a crear:** `frontend/src/components/estados/EmptyState.jsx`
```jsx
import { PackageOpen } from 'lucide-react';

/**
 * Estado vacío cuando la API retorna lista vacía [].
 * Evita mostrar listas sin contexto al usuario.
 */
const EmptyState = ({ mensaje = 'No hay datos disponibles aún' }) => {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '3rem',
      gap: '1rem',
      color: '#9ca3af'
    }}>
      <PackageOpen size={48} />
      <h3 style={{ margin: 0, fontSize: '1rem', fontWeight: '500' }}>Sin datos</h3>
      <p style={{ margin: 0, fontSize: '0.875rem' }}>{mensaje}</p>
    </div>
  );
};

export default EmptyState;
```

**Archivo a crear:** `frontend/src/components/estados/DegradedBanner.jsx`
```jsx
import { AlertTriangle } from 'lucide-react';

/**
 * Banner amarillo de servicio degradado.
 * Aparece cuando el BFF retorna status="Degradado" (Circuit Breaker activado).
 * Posición sticky para que sea visible mientras se scrollea.
 */
const DegradedBanner = ({ servicioAfectado = 'Microservicios' }) => {
  return (
    <div style={{
      position: 'sticky',
      top: 0,
      zIndex: 100,
      display: 'flex',
      alignItems: 'center',
      gap: '0.75rem',
      padding: '0.75rem 1rem',
      backgroundColor: '#fef3c7',
      borderBottom: '1px solid #fcd34d',
      color: '#92400e'
    }}>
      <AlertTriangle size={18} style={{ flexShrink: 0 }} />
      <span style={{ fontWeight: '600' }}>Servicio parcialmente disponible</span>
      <span style={{ fontSize: '0.875rem' }}>
        — {servicioAfectado} no responde. Mostrando datos disponibles.
      </span>
    </div>
  );
};

export default DegradedBanner;
```

**Verificar:**
```bash
npm run dev
# Los 4 componentes deben compilar sin errores
```

### Sub-task 2 [CORD-195]: Aplicar estados a Dashboard, KPIs y Alertas

**Objetivo:** Integrar los 4 estados en las 3 screens más importantes

**Patrón a aplicar en DashboardScreen.jsx:**
```jsx
import LoadingSpinner from '../estados/LoadingSpinner';
import ErrorMessage from '../estados/ErrorMessage';
import EmptyState from '../estados/EmptyState';
import DegradedBanner from '../estados/DegradedBanner';

const DashboardScreen = () => {
  const { dashboard, fetchDashboard } = useDashboardContext();

  return (
    <div>
      {/* Estado degradado: BFF retorna status="Degradado" (Circuit Breaker) */}
      {dashboard.data?.status === 'Degradado' && (
        <DegradedBanner servicioAfectado="KPI Service" />
      )}

      {/* Estado cargando */}
      {dashboard.loading && (
        <LoadingSpinner mensaje="Cargando dashboard..." />
      )}

      {/* Estado error de red */}
      {!dashboard.loading && dashboard.error && (
        <ErrorMessage
          titulo="Error de conexión"
          detalle={dashboard.error?.message || 'No se pudo conectar con el BFF Gateway'}
        />
      )}

      {/* Estado vacío */}
      {!dashboard.loading && !dashboard.error && dashboard.data?.kpis?.length === 0 && (
        <EmptyState mensaje="No hay KPIs registrados aún" />
      )}

      {/* Contenido normal */}
      {!dashboard.loading && !dashboard.error && dashboard.data && (
        <div>{/* renderizar datos del dashboard */}</div>
      )}
    </div>
  );
};
```

Aplicar el mismo patrón en `KpisScreen.jsx` y `AlertsScreen.jsx`.

**Verificar:**
```bash
npm run dev
# Simular error: apagar BFF temporalmente → debe aparecer ErrorMessage
# Verificar: al cargar, antes del fetch → debe aparecer LoadingSpinner
```

### Sub-task 3 [CORD-196]: Aplicar estados a Reportes, Servicios y Configuración

**Objetivo:** Completar la cobertura de estados en las 3 screens restantes

**Para ReportsScreen.jsx** — caso especial de reporte degradado:
```jsx
// Después de POST /api/v1/reportes/generar, si la respuesta tiene status="DEGRADADO":
{reporteGenerado?.status === 'DEGRADADO' && (
  <DegradedBanner
    servicioAfectado="KPI Service"
  />
)}
// Mensaje específico: "Reporte generado en modo degradado: los KPIs no estaban disponibles"
```

**Para ServicesScreen.jsx** — degradado si algún servicio no está Operativo:
```jsx
{servicios.some(s => s.estado !== 'Operativo') && (
  <DegradedBanner servicioAfectado="Uno o más servicios" />
)}
```

**Demo para la defensa oral (preparar script):**
```bash
# Paso 1: levantar el sistema completo
docker-compose up -d

# Paso 2: verificar que DashboardScreen muestra "Operativo"
# → ir a http://localhost:3000

# Paso 3: durante la defensa, ejecutar:
docker stop kpi-service
# → DegradedBanner amarillo aparece en DashboardScreen y ReportsScreen
# → Demostrar tolerancia a fallos del Circuit Breaker BFF

# Paso 4: restaurar para continuar la demo
docker start kpi-service
```

**Verificar:**
```bash
npm run build
# Sin errores ni warnings en los 4 componentes ni en las 6 screens
```

---

## Criterios de Aceptación

**AC1: Estado de carga visible**
- **Dado** que el fetch al BFF tarda más de 300ms
- **Cuando** la pantalla está cargando
- **Entonces** se muestra LoadingSpinner y no se muestra contenido vacío

**AC2: Estado degradado visible**
- **Dado** que kpi-service está caído y el BFF retorna status="Degradado"
- **Cuando** DashboardScreen recibe los datos
- **Entonces** se muestra el DegradedBanner amarillo y los datos disponibles sí se muestran

**AC3: Estado de error de red**
- **Dado** que el BFF no responde (fetch falla)
- **Cuando** el frontend intenta cargar datos
- **Entonces** se muestra ErrorMessage y nunca queda la pantalla en blanco

**AC4: Estado vacío**
- **Dado** que la API retorna una lista vacía []
- **Cuando** KpisScreen renderiza los datos
- **Entonces** se muestra EmptyState con mensaje "No hay KPIs registrados aún"

## DoD (Definition of Done)

- [ ] Rama `feature/cord-128-estados-ui-loading-error-degraded` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] Los 4 componentes de estado creados en `src/components/estados/`
- [ ] Las 6 pantallas implementan los renders condicionales
- [ ] `DegradedBanner` aparece cuando BFF retorna `status="Degradado"`
- [ ] Ninguna pantalla queda en blanco en ningún escenario de error
- [ ] Demo preparada: script `docker stop kpi-service` listo para la defensa
- [ ] `npm run build` compila sin errores
- [ ] Ticket CORD-128 en Jira en estado "Finalizado" con comentario técnico y screenshots
