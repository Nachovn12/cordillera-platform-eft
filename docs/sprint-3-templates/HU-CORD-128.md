# HU-CORD-128 — Estados loading/empty/error/degradado en 6 screens

## Historia de Usuario

Como usuario del Frontend del Grupo Cordillera

quiero que todas las pantallas muestren estados visuales claros (cargando, sin datos, error, servicio degradado)

para nunca quedarme frente a una pantalla en blanco o sin entender qué está ocurriendo con el sistema.

## Contexto Técnico e Integración

El frontend React 19 consume el BFF Gateway en http://localhost:8081. Cuando algún microservicio falla, el BFF retorna status="Degradado" en el DashboardResponse. El frontend debe interpretar esa señal y mostrar el banner de degradación correspondiente.

Las 6 pantallas afectadas: DashboardScreen, KpisScreen, AlertsScreen, ReportsScreen, ServicesScreen, SettingsScreen.

Patrón de estado en cada screen: useState(false) para isLoading, useState(null) para error, useState(false) para isDegraded. El DashboardContext (patrón Observer) ya centraliza los fetches — los componentes solo leen el estado y renderizan.

Stack: React 19.2.5 · Vite 8.0.10 · DashboardContext con useReducer · lucide-react 1.14.0 (íconos del UI).

Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- Observer (DashboardContext): los screens "observan" el estado del contexto. El estado loading: true en el contexto es automáticamente recibido por todos los componentes suscritos — sin prop drilling.
- Tolerancia a fallos visible: el frontend hace visible el Circuit Breaker del BFF: cuando el dashboard está degradado, el usuario lo ve inmediatamente con el DegradedBanner.
- Framework moderno (React 19): renders condicionales, hooks de estado y componentes de UI son la evidencia del Indicador 2 de la rúbrica (framework moderno).
- Separación de responsabilidades: los componentes de estado (LoadingSpinner, ErrorMessage, etc.) son reutilizables en las 6 pantallas sin duplicar código.

## Criterios de Aceptación (Gherkin)

AC1: Estado de carga visible

Dado que el fetch al BFF tarda más de 300ms
Cuando la pantalla está cargando datos
Entonces se muestra el componente LoadingSpinner con el mensaje correspondiente
Y no se muestra el contenido vacío ni un error falso

AC2: Estado degradado visible

Dado que kpi-service está caído y el BFF retorna status="Degradado"
Cuando DashboardScreen recibe los datos
Entonces se muestra el DegradedBanner amarillo con el mensaje de aviso
Y los datos disponibles (datos operacionales) sí se muestran

AC3: Estado de error de red

Dado que el BFF no responde (error de red, fetch falla)
Cuando el frontend intenta cargar datos
Entonces se muestra ErrorMessage con título "Error de conexión" y el detalle técnico
Y nunca queda la pantalla en blanco

AC4: Estado vacío

Dado que la API retorna una lista vacía []
Cuando KpisScreen renderiza los datos
Entonces se muestra EmptyState con el mensaje "No hay KPIs registrados aún"
Y no se muestra una lista vacía sin contexto

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| src/components/estados/LoadingSpinner.jsx | Crear | Spinner animado con CSS, prop mensaje |
| src/components/estados/ErrorMessage.jsx | Crear | Mensaje de error con ícono lucide-react AlertCircle |
| src/components/estados/EmptyState.jsx | Crear | Pantalla de estado vacío con ilustración y texto |
| src/components/estados/DegradedBanner.jsx | Crear | Banner amarillo de servicio degradado |
| src/screens/DashboardScreen.jsx | Modificar | Aplicar los 4 estados |
| src/screens/KpisScreen.jsx | Modificar | Aplicar los 4 estados |
| src/screens/AlertsScreen.jsx | Modificar | Aplicar los 4 estados |
| src/screens/ReportsScreen.jsx | Modificar | Aplicar estados + caso degradado especial |
| src/screens/ServicesScreen.jsx | Modificar | Aplicar los 4 estados |
| src/screens/SettingsScreen.jsx | Modificar | Aplicar loading y error |

## Estrategia de Testing (Cobertura > 60%)

Los estados del frontend no se testean con JaCoCo (es Java), sino que se validan visualmente durante la demo de la defensa oral. La estrategia es:
- Demo en vivo: durante la defensa, hacer docker stop kpi-service y mostrar el DegradedBanner en tiempo real (Indicador 7 EP3).
- Screenshots: capturar cada estado de cada pantalla para el informe de pruebas.

## Definición de Hecho (DoD)

- [ ] Los 4 componentes de estado creados en src/components/estados/
- [ ] Las 6 pantallas implementan los renders condicionales
- [ ] DegradedBanner aparece cuando BFF retorna status="Degradado"
- [ ] Ninguna pantalla queda en blanco en ningún escenario de error
- [ ] Demo preparada para la defensa: script de docker stop kpi-service listo
- [ ] npm run dev compila sin errores ni warnings

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-194]: Crear 4 componentes de estado en components/estados/

Descripcion: Crear el directorio src/components/estados/ y los 4 componentes reutilizables: (1) LoadingSpinner.jsx: spinner animado con @keyframes spin en CSS inline o módulo CSS; acepta prop mensaje (default "Cargando..."); usa ícono Loader2 de lucide-react con clase animate-spin; (2) ErrorMessage.jsx: muestra banner rojo con ícono AlertCircle de lucide-react; acepta props titulo (default "Error") y detalle (mensaje técnico del error); (3) EmptyState.jsx: pantalla de estado vacío centrada con ícono PackageOpen de lucide-react, texto "Sin datos" como h3 y prop mensaje para el subtítulo; (4) DegradedBanner.jsx: banner horizontal color amarillo-ámbar con ícono AlertTriangle de lucide-react; texto "Servicio parcialmente disponible" + prop servicioAfectado; se posiciona como sticky en la parte superior de la pantalla. Todos los componentes son funcionales (arrow function), sin dependencias externas más allá de lucide-react (ya instalado). Verificar con npm run dev que compilan sin errores (Indicadores 2 y 7 EP3: frontend moderno + presentación de integración con feedback visual).

### Sub-task 2 [CORD-195]: Aplicar estados a Dashboard, KPIs y Alertas

Descripcion: En DashboardScreen.jsx, KpisScreen.jsx y AlertsScreen.jsx, integrar el manejo de estados usando el DashboardContext (patrón Observer ya implementado): cada screen usa const { dashboard, fetchDashboard } = useDashboardContext() (o el estado correspondiente). En el JSX, agregar renders condicionales: {dashboard.loading && <LoadingSpinner mensaje="Cargando dashboard..." />} antes del contenido; {dashboard.error && <ErrorMessage titulo="Error al cargar" detalle={dashboard.error?.message} />} si hay error; {dashboard.data?.status === "Degradado" && <DegradedBanner servicioAfectado="Microservicios" />} cuando el BFF reporta degradación; {!dashboard.loading && !dashboard.error && dashboard.data?.kpis?.length === 0 && <EmptyState mensaje="No hay KPIs disponibles aún" />} para lista vacía. Verificar en dev que: al cargar (antes del fetch) aparece el spinner, al recibir datos correctos desaparece, y al simular error de red (apagar BFF) aparece el ErrorMessage (Indicadores 2 y 7 EP3).

### Sub-task 3 [CORD-196]: Aplicar estados a Reportes, Servicios y Configuracion

Descripcion: Aplicar el mismo patrón de manejo de estados de CORD-195 en ReportsScreen.jsx, ServicesScreen.jsx y SettingsScreen.jsx. Para ReportsScreen.jsx hay un caso especial: después de llamar POST /api/v1/reportes/generar, si la respuesta tiene status="DEGRADADO" (kpi-service caído al momento de generar), mostrar <DegradedBanner servicioAfectado="KPI Service" /> con el mensaje específico "Reporte generado en modo degradado: los KPIs no estaban disponibles". Para ServicesScreen.jsx: usar isDegraded si alguno de los servicios en la lista tiene estado !== "Operativo", mostrando el banner a nivel de pantalla. Demo para la defensa oral: preparar el script docker stop kpi-service y mostrar en vivo cómo el frontend muestra el DegradedBanner en DashboardScreen y ReportsScreen en tiempo real — esto demuestra la tolerancia a fallos del Circuit Breaker del BFF (Indicador 7 EP3: demostración de escalabilidad y tolerancia a fallos ante el evaluador).
