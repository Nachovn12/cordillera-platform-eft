# Prompt de Implementación - CORD-127

## Contexto General del Proyecto

- **Proyecto:** Grupo Cordillera — Plataforma de Monitoreo Organizacional
- **Repositorio:** cordillera-platform-parcial-2
- **Sprint:** S3 — EP3 Integración + Testing (16 jun – 21 jun)
- **Rúbrica EP3:** Indicadores 1-8 (Arquitectura, Frontend+Backend, API REST, Pruebas ≥60%, Defensa oral)
- **Patrón de tests del profesor:** @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + when().thenReturn() + verify() + assertThrows()
- **HU padre:** CORD-127 — Migrar navegación a React Router con rutas declarativas
- **Reviewer en GitHub:** Nachovn12

## Contexto de Negocio — Grupo Cordillera

El frontend del dashboard de Grupo Cordillera actualmente usa `window.history.pushState` manual para la navegación — una solución no estándar que impide compartir URLs, no soporta correctamente los botones Atrás/Adelante del navegador, y hace imposible crear marcadores. Los ejecutivos que usan el sistema diariamente necesitan poder ir directamente a `/kpis` o `/reports` sin navegar desde el dashboard.

La migración a **React Router v6** con `<BrowserRouter>` y `<Routes>` declarativas transforma la URL del browser en la fuente de verdad de la navegación. Es el estándar de la industria para SPAs React y es evidencia del uso correcto del framework (Indicador 2 de la rúbrica EP3). El `nginx.conf` ya tiene `try_files` configurado — no necesita modificaciones.

## Historia de Usuario

**Como** usuario del Frontend del Grupo Cordillera
**quiero** poder navegar entre secciones mediante URLs reales (/kpis, /reportes, /servicios)
**para** compartir enlaces, usar los botones Atrás/Adelante del navegador y hacer marcadores

### Regla de Negocio Crítica
Las 8 rutas del sistema deben estar mapeadas en `AppRoutes.jsx`. Al recargar directamente `/kpis`, Nginx sirve `index.html` (ya configurado con `try_files`) y React Router renderiza `KpisScreen`. Eliminar el estado `activeScreen` y `pushState` manual de `App.jsx`.

> **Contexto EP3:** El frontend React 19 del dashboard ejecutivo de Grupo Cordillera necesita URLs declarativas para que los ejecutivos puedan marcar favoritos (ej: /kpis, /reports) y compartir enlaces. Sin react-router-dom, la navegación es frágil. Esta HU cumple el Indicador 2 EP3 (framework moderno) — KPIs, reportes y datos de sucursales accesibles via URL directa.


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
git checkout -b feature/cord-127-react-router-migration
```

### Paso 1 — Instalar react-router-dom@6

```bash
# En el directorio frontend/
npm install react-router-dom@6
```

Verificar en `package.json`:
```json
{
  "dependencies": {
    "react-router-dom": "^6.x.x"
  }
}
```

**Verificar:**
```bash
npm run dev
# La app debe compilar sin errores
```

### Paso 2 — Crear src/routes/AppRoutes.jsx

Ver detalle en Sub-task 1.

### Paso 3 — Refactorizar src/App.jsx

Ver detalle en Sub-task 2. Eliminar `pathToScreen`, `activeScreen` state y `handleNavigate` manual.

### Paso 4 — Actualizar AppShell.jsx con `<Link>`

Ver detalle en Sub-task 3. Reemplazar `onClick={() => onNavigate('ruta')}` por `<Link to="/ruta">`.

**Verificar:**
```bash
npm run build
# Esperado: compilación exitosa sin errores ni warnings
```

### Paso 5 — Push, Pull Request y Documentación Jira (Cierre)

**1. Commit y push:**
```bash
git add .
git commit -m "feat(cord-127): migrar a React Router v6 con BrowserRouter y rutas declarativas"
git push origin feature/cord-127-react-router-migration
```

**2. Crear Pull Request en GitHub:**

```bash
gh pr create --base main --head feature/cord-127-react-router-migration \
  --title "[CORD-127] Migración React Router v6 — navegación declarativa SPA" \
  --body "## Cambios realizados\nInstalación react-router-dom@6, creación AppRoutes.jsx, refactorización App.jsx, actualización AppShell.jsx\n\n## Tests\nVerificación manual: 8 rutas navegables, botón Atrás/Adelante funciona, recarga directa en /kpis\n\n## Cobertura JaCoCo\nN/A (frontend)" \
  --reviewer Nachovn12
```

**3. En Jira:**
- Cambiar estado de la HU CORD-127 y sub-tasks a "Finalizada"
- Agregar comentario con link al PR y rutas verificadas

---

## Sub-Tasks Detalle

### Sub-task 1 [CORD-186]: Instalar react-router-dom y crear src/routes/AppRoutes.jsx

**Objetivo:** Definir todas las rutas del sistema con `<Routes>` declarativo

**Archivo a crear:** `frontend/src/routes/AppRoutes.jsx`

**Código:**
```jsx
// src/routes/AppRoutes.jsx
import { Routes, Route, Navigate } from 'react-router-dom';

// Importar todos los screens desde sus rutas reales
import DashboardScreen from '../components/screens/DashboardScreen';
import KpisScreen from '../components/screens/KpisScreen';
import ReportsScreen from '../components/screens/ReportsScreen';
import DataScreen from '../components/screens/DataScreen';
import AlertsScreen from '../components/screens/AlertsScreen';
import ServicesScreen from '../components/screens/ServicesScreen';
import SettingsScreen from '../components/screens/SettingsScreen';
import UsersScreen from '../components/screens/UsersScreen';

/**
 * Definición centralizada de rutas del dashboard de Grupo Cordillera.
 * Mapea exactamente las rutas definidas en pathToScreen del App.jsx original.
 * React Router v6 — patrón SPA declarativo.
 */
const AppRoutes = ({ refreshToken, onBffStatusChange, sucursal }) => {
  const commonProps = { refreshToken, onBffStatusChange, sucursal };

  return (
    <Routes>
      <Route path="/" element={<DashboardScreen {...commonProps} />} />
      <Route path="/dashboard" element={<DashboardScreen {...commonProps} />} />
      <Route path="/kpis" element={<KpisScreen {...commonProps} />} />
      <Route path="/reports" element={<ReportsScreen {...commonProps} />} />
      <Route path="/datos" element={<DataScreen {...commonProps} />} />
      <Route path="/alerts" element={<AlertsScreen {...commonProps} />} />
      <Route path="/services" element={<ServicesScreen {...commonProps} />} />
      <Route path="/settings" element={<SettingsScreen {...commonProps} />} />
      <Route path="/users" element={<UsersScreen {...commonProps} />} />
      {/* Ruta catch-all: rutas desconocidas redirigen al dashboard */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
};

export default AppRoutes;
```

**Verificar:**
```bash
npm run dev
# Las 8 rutas deben ser navegables
```

### Sub-task 2 [CORD-187]: Refactorizar App.jsx con BrowserRouter y eliminar navegación manual

**Objetivo:** Reemplazar `pushState` manual por `<BrowserRouter>` + `<AppRoutes>`

**Archivo a modificar:** `frontend/src/App.jsx`

**Cambios principales:**
```jsx
// ANTES — navegación manual con pushState y estado activeScreen
// const [activeScreen, setActiveScreen] = useState(getInitialScreen());
// const screenComponents = { dashboard: DashboardScreen, kpis: KpisScreen, ... };
// const handleNavigate = (screenId) => { ... pushState ... };

// DESPUÉS — con React Router v6
import { BrowserRouter } from 'react-router-dom';
import AppRoutes from './routes/AppRoutes';
import AppShell from './components/layout/AppShell';

function App() {
  // Eliminar: activeScreen, screenComponents, pathToScreen, handleNavigate, popstate listener
  // React Router maneja el estado de navegación internamente

  return (
    <BrowserRouter>
      <AppShell>
        {/* AppRoutes renderiza el screen correcto según la URL actual */}
        <AppRoutes
          refreshToken={refreshToken}
          onBffStatusChange={handleBffStatusChange}
          sucursal={sucursalSeleccionada}
        />
      </AppShell>
    </BrowserRouter>
  );
}

export default App;
```

**Lo que se ELIMINA de App.jsx:**
- `const [activeScreen, setActiveScreen] = useState(...)`
- `const screenComponents = { ... }`
- `const pathToScreen = { ... }`
- `const getInitialScreen = () => { ... }`
- `const handleNavigate = (screenId) => { ... }`
- El listener `window.addEventListener('popstate', ...)`

**Verificar:**
```bash
npm run dev
# Navegar a /kpis, /reports, /alerts, /services — cada URL carga la pantalla correcta
```

### Sub-task 3 [CORD-188]: Verificar nginx.conf y documentar la configuración SPA

**Objetivo:** Confirmar que el `try_files` de Nginx ya soporta React Router en producción

**Verificar en `frontend/nginx.conf` que existe:**
```nginx
location / {
    try_files $uri $uri/ /index.html;
    # SPA fallback: todas las rutas sirven index.html
    # para que React Router maneje el enrutamiento en el cliente
}
```

**Actualizar `frontend/README.md` — sección Rutas disponibles:**
```markdown
## Rutas disponibles

| Ruta | Screen | Descripción |
|------|--------|-------------|
| `/` | DashboardScreen | Dashboard principal con KPIs consolidados |
| `/kpis` | KpisScreen | Indicadores KPI por categoría |
| `/reports` | ReportsScreen | Reportes ejecutivos PDF/Excel/JSON |
| `/datos` | DataScreen | Datos operacionales por sucursal/sistema |
| `/alerts` | AlertsScreen | Alertas del sistema y servicios degradados |
| `/services` | ServicesScreen | Estado de microservicios |
| `/settings` | SettingsScreen | Configuración de la plataforma |
| `/users` | UsersScreen | Gestión de usuarios |

## Instalación y ejecución

```bash
npm install          # instalar dependencias
npm run dev          # servidor de desarrollo en http://localhost:3000
npm run build        # build de producción (genera dist/)
```

## Variables de entorno

```
VITE_API_URL=http://localhost:8081
```
```

**Verificar:**
```bash
npm run build
# dist/ generado correctamente con index.html en la raíz
```

---

## Criterios de Aceptación

**AC1: Navegación con URLs reales**
- **Dado** que el usuario está en DashboardScreen
- **Cuando** hace clic en "KPIs" en la barra de navegación
- **Entonces** la URL cambia a /kpis y KpisScreen se renderiza sin recargar

**AC2: Recarga directa de ruta funciona**
- **Dado** que el usuario escribe directamente http://localhost:3000/reports
- **Cuando** el browser carga la URL
- **Entonces** Nginx sirve index.html y React Router renderiza ReportsScreen

**AC3: Botón Atrás/Adelante funciona**
- **Dado** que el usuario navegó Dashboard → KPIs → Reportes
- **Cuando** presiona el botón Atrás del browser
- **Entonces** regresa a KPIs y la URL muestra /kpis

## DoD (Definition of Done)

- [ ] Rama `feature/cord-127-react-router-migration` creada y pusheada
- [ ] PR creado apuntando a `main` con reviewer Nachovn12
- [ ] `npm install react-router-dom@6` ejecutado y `package.json` actualizado
- [ ] `src/routes/AppRoutes.jsx` creado con las 8 rutas del sistema
- [ ] `App.jsx` refactorizado — eliminado `pathToScreen`, `activeScreen` state y `handleNavigate` manual
- [ ] Navegación con URLs reales funcionando en `npm run dev`
- [ ] `npm run build` compila sin errores
- [ ] Ticket CORD-127 en Jira en estado "Finalizado" con comentario técnico
