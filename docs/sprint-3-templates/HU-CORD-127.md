# HU-CORD-127 — Migrar navegación a React Router con rutas declarativas

## Historia de Usuario

Como usuario del Frontend del Grupo Cordillera

quiero poder navegar entre secciones mediante URLs reales (/kpis, /reportes, /servicios)

para poder compartir enlaces, usar los botones Atrás/Adelante del navegador y hacer marcadores en el browser.

## Contexto Técnico e Integración

Situación actual (analizada en el código):

El App.jsx actual implementa navegación con window.history.pushState manualmente — no usa react-router-dom. El package.json confirma que react-router-dom no está instalado. Las pantallas están en un mapa screenComponents y se activan con setActiveScreen(screenId) + pushState.

El nginx.conf en producción ya tiene try_files $uri $uri/ /index.html configurado correctamente — esto no hay que tocarlo.

Las rutas actuales definidas en pathToScreen del App.jsx:

"/" → dashboard, "/dashboard" → dashboard, "/kpis" → kpis,
"/reports" → reports, "/datos" → datos, "/alerts" → alerts,
"/services" → services, "/settings" → settings, "/users" → users

Decisión de esta historia: instalar react-router-dom@6 y migrar de la navegación manual con pushState + useState(activeScreen) a <BrowserRouter> + <Routes> declarativo.

Stack: React 19.2.5 · react-router-dom 6.x (a instalar) · Vite 8.0.10 · nginx.conf ya correcto.

Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- SPA (Single Page Application): el ruteo ocurre 100% en el cliente — el servidor (BFF o Nginx) siempre sirve index.html y React Router maneja el estado de navegación en el browser sin recargar la página.
- Separación de responsabilidades: con React Router, la lógica de qué pantalla mostrar pasa del estado de componente (useState) a la URL del browser — la URL es la fuente de verdad de la navegación.
- Framework moderno (Indicador 2): react-router-dom es la solución estándar de enrutamiento en el ecosistema React, evidencia del uso correcto del framework.
- Nginx + try_files: el nginx.conf ya tiene try_files $uri $uri/ /index.html — este es el complemento server-side que hace que la SPA funcione al recargar cualquier ruta directamente.

## Criterios de Aceptación (Gherkin)

AC1: Navegación con URLs reales

Dado que el usuario está en DashboardScreen
Cuando hace clic en "KPIs" en la barra de navegación
Entonces la URL del browser cambia a /kpis
Y KpisScreen se renderiza sin recargar la página

AC2: Recarga directa de ruta funciona

Dado que el usuario escribe directamente http://localhost:3000/reports en el browser
Cuando el browser carga la URL
Entonces Nginx sirve index.html (ya configurado con try_files)
Y React Router renderiza ReportsScreen correctamente

AC3: Botón Atrás/Adelante funciona

Dado que el usuario navegó Dashboard → KPIs → Reportes
Cuando presiona el botón Atrás del browser
Entonces regresa a KPIs y la URL muestra /kpis

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| package.json | Modificar | Agregar react-router-dom en dependencies |
| src/routes/AppRoutes.jsx | Crear | Definición de todas las rutas con <Routes> |
| src/App.jsx | Refactorizar | Reemplazar pushState manual por <BrowserRouter> + <AppRoutes> |
| src/components/layout/AppShell.jsx | Modificar | Actualizar links de navegación a <Link to="/ruta"> |
| frontend/nginx.conf | Sin cambios | Ya tiene try_files correcto — NO modificar |

## Definición de Hecho (DoD)

- [ ] npm install react-router-dom@6 ejecutado y package.json actualizado
- [ ] src/routes/AppRoutes.jsx creado con las 8 rutas del sistema
- [ ] App.jsx refactorizado — eliminado pathToScreen, activeScreen state y handleNavigate manual
- [ ] Navegación con URLs reales funcionando en npm run dev
- [ ] Recarga en /kpis directamente no da 404 (Nginx ya lo maneja)
- [ ] npm run build compila sin errores

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-186]: Instalar react-router-dom y crear src/routes/AppRoutes.jsx

Descripcion: En el directorio frontend/, ejecutar npm install react-router-dom@6. Verificar que package.json tiene "react-router-dom": "^6.x.x" en dependencies. Crear src/routes/AppRoutes.jsx con todas las rutas del sistema. Mapear las rutas existentes en pathToScreen del App.jsx actual: <Routes><Route path="/" element={<DashboardScreen/>}/><Route path="/dashboard" element={<DashboardScreen/>}/><Route path="/kpis" element={<KpisScreen/>}/><Route path="/reports" element={<ReportsScreen/>}/><Route path="/datos" element={<DataScreen/>}/><Route path="/alerts" element={<AlertsScreen/>}/><Route path="/services" element={<ServicesScreen/>}/><Route path="/settings" element={<SettingsScreen/>}/><Route path="/users" element={<UsersScreen/>}/><Route path="*" element={<Navigate to="/" replace/>}/></Routes>. Importar todos los componentes Screen desde sus rutas reales en src/components/screens/. Ejecutar npm run dev y verificar que la app compila sin errores. Cumple Indicador 2 EP3 (framework moderno React con enrutamiento declarativo) y CASO sección 2 (interfaz moderna).

### Sub-task 2 [CORD-187]: Refactorizar App.jsx con BrowserRouter y eliminar navegación manual

Descripcion: En src/App.jsx, aplicar la migración de navegación manual a React Router: (1) Envolver el contenido en <BrowserRouter> e incluir <AppRoutes/> (creado en CORD-186) en lugar del renderizado manual const ActiveScreen = screenComponents[activeScreen]. (2) Eliminar o simplificar el estado activeScreen y handleNavigate — con React Router, la URL es la fuente de verdad, no el estado. Mantener los props que los screens necesitan (refreshToken, onBffStatusChange, sucursal) pasándolos a través de un <Outlet> o context de React Router. (3) Eliminar el mapa screenComponents, pathToScreen y getInitialScreen(). El popstate listener tampoco es necesario (React Router lo maneja internamente). (4) Actualizar AppShell.jsx para que los items de navegación usen <Link to="/kpis"> de react-router-dom en lugar de onClick={() => onNavigate('kpis')}. Verificar navegación con npm run dev: ir a /kpis, /reports, /alerts, /services y confirmar que cada ruta carga la pantalla correcta (Indicadores 2 y 7 EP3: presentación de integración).

### Sub-task 3 [CORD-188]: Verificar nginx.conf y documentar la configuración SPA en producción

Descripcion: El frontend/nginx.conf ya tiene try_files $uri $uri/ /index.html; correctamente configurado en el bloque location /. Esta subtarea consiste en: (1) Confirmar que la configuración está funcionando correctamente haciendo docker build de la imagen frontend y probando con docker run -p 3000:3000 <imagen> acceder directamente a http://localhost:3000/kpis — debe cargar KpisScreen sin 404. (2) Agregar un comentario en nginx.conf explicando por qué try_files es crítico para SPAs: # SPA fallback: todas las rutas sirven index.html para que React Router maneje el ruteo. (3) Documentar en frontend/README.md la sección "Rutas disponibles" listando las 8 rutas del sistema con descripción (requerimiento explícito del checklist EP3: frontend debe tener README.md con instrucciones). Verificar que npm run build genera el directorio dist/ correctamente con index.html en la raíz. Esto cumple el checklist EP3 item "frontend/README.md con instrucciones de instalación, ejecución y pruebas" (Indicadores 2 y 7 EP3).
