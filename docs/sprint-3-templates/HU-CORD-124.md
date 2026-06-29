# HU-CORD-124 — Dashboard consolidado en BFF con Circuit Breaker

## Historia de Usuario

Como Frontend del Grupo Cordillera

quiero llamar a un único endpoint consolidado /api/dashboard/stats

para obtener en una sola respuesta los KPIs, datos operacionales, alertas y estado de los microservicios — sin que el browser necesite llamar a cada microservicio por separado.

## Contexto Técnico e Integración

El BFF Gateway expone /api/dashboard/stats (en DashboardController → DashboardService). Internamente, DashboardService hace 3 llamadas HTTP simultáneas mediante RestTemplate:
- → http://data-service:8083/api/datos (obtiene datos operacionales)
- → http://kpi-service:8084/api/kpis (obtiene indicadores)
- → http://report-service:8085/api/reportes (obtiene últimos reportes)

Los resultados se agregan en un DashboardResponse que incluye: status, ventasTotales, kpis, alertas, datosSucursal, tendenciaVentas, ultimosReportes, servicios.

Si algún microservicio falla, DashboardService marca el dashboard como status="Degradado" y genera una alerta automática del servicio caído — sin propagar la excepción al frontend.

Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- BFF (Backend For Frontend): patrón explícito del CASO sección 1. El frontend hace 1 llamada en vez de 3, evitando "chatty behavior" y ocultando la topología interna.
- Circuit Breaker (tolerancia a fallos): si kpi-service cae, el BFF retorna los datos disponibles + alerta de degradación, en vez de propagar el error al usuario.
- Aggregator Pattern: DashboardService consolida datos heterogéneos de 3 fuentes en un DTO unificado.
- Testabilidad: al estar encapsulada en DashboardService, la lógica de aggregation es completamente testeable con Mockito sin levantar ningún microservicio.

## Criterios de Aceptación (Gherkin)

AC1: Respuesta unificada con todos los servicios online

Dado que data-service, kpi-service y report-service están operativos
Cuando el frontend llama a GET /api/dashboard/stats
Entonces el BFF retorna HTTP 200 con un JSON que contiene: status="Operativo",
ventasTotales≥0, lista de kpis no vacía, lista de alertas (al menos informativas),
lista de servicios con 4 entradas

AC2: Respuesta degradada con kpi-service caído

Dado que kpi-service no responde (timeout o 500)
Cuando el frontend llama a GET /api/dashboard/stats
Entonces el BFF retorna HTTP 200 (no 503)
Y el JSON contiene status="Degradado"
Y la lista de alertas incluye una alerta con origen="KPI Service" y severidad="Critica"
Y el frontend muestra el DegradedBanner en vez de una pantalla en blanco

AC3: Filtrado por sucursal

Dado que existen datos para sucursalId=1
Cuando el frontend llama a GET /api/dashboard/sucursal/1
Entonces retorna HTTP 200 con datos filtrados solo de la sucursal 1

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| DashboardController.java | Verificar | Endpoints /stats, /kpis, /sucursal/{id}, /alertas, /services |
| DashboardService.java | Verificar | Lógica de aggregation + manejo de fallos |
| DashboardResponse.java | Verificar | DTO de respuesta consolidada |
| DashboardServiceTest.java | Crear | Tests con RestTemplate mockeado |
| DashboardControllerTest.java | Crear | @WebMvcTest para los endpoints del dashboard |

## Estrategia de Testing (Cobertura > 60%)

Mockear los 3 servicios downstream en DashboardServiceTest para probar:
1. Caso feliz (todos online) → status="Operativo"
2. kpi-service caído → status="Degradado", alerta generada
3. Todos caídos → status="Degradado", 3 alertas críticas

## Definición de Hecho (DoD)

- [ ] GET /api/dashboard/stats responde 200 con todos los servicios online (verificar en Postman)
- [ ] GET /api/dashboard/stats responde 200 con status="Degradado" cuando un servicio está caído
- [ ] Tests del DashboardService pasan con Mockito
- [ ] Cobertura JaCoCo > 60% en el bff-gateway
- [ ] Endpoint documentado en la colección Postman/Swagger

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-177]: Crear DashboardServiceTest con RestTemplate mockeado

Descripcion: Crear DashboardServiceTest.java con @ExtendWith(MockitoExtension.class). Usar @Mock sobre RestTemplate (el bean inyectado en DashboardService). Implementar 3 tests: (1) getDashboard_conTodosLosServiciosOnline_retornaStatusOperativo: mockear restTemplate.exchange(kpiUrl, ...) para retornar lista de 2 KPIs, restTemplate.exchange(dataUrl, ...) para retornar lista de 3 datos, y restTemplate.exchange(reportUrl, ...) para retornar lista de 1 reporte; verificar que el DashboardResponse retornado tiene status="Operativo" y alertas.isEmpty()=true; (2) getDashboard_conKpiServiceCaido_retornaStatusDegradado: mockear kpiUrl para lanzar ResourceAccessException; verificar status="Degradado" y que la lista de alertas contiene una alerta con severidad="Critica" y origen="KPI Service"; (3) getDashboardSucursal_conSucursalValida_retornaFiltroCorrecto: mockear dataUrl + /sucursal/1 para retornar lista no vacía, verificar que DashboardResponse.datosSucursal tiene los datos. Cubre AC1, AC2 y AC3 (Indicadores 2, 4 y 7 EP3: desarrollo backend, cobertura y presentación de integración).

### Sub-task 2 [CORD-178]: Cubrir caso feliz y caso degradado (kpi-service caído) con DashboardControllerTest

Descripcion: Crear DashboardControllerTest.java con @WebMvcTest(DashboardController.class). Usar @MockBean DashboardService. Implementar: (1) getStats_retorna200ConDashboardCompleto: mockear dashboardService.getDashboard() para retornar un DashboardResponse con status="Operativo" y lista de kpis con 2 elementos; hacer GET /api/dashboard/stats; verificar HTTP 200, Content-Type application/json, y que el body contiene "status":"Operativo" y un array "kpis" con size=2; (2) getStats_degradado_retorna200ConAlerta: mockear para retornar DashboardResponse con status="Degradado" y lista de alertas con 1 elemento; verificar HTTP 200 y que el body contiene "status":"Degradado" — el BFF nunca retorna 5xx al frontend (resiliencia). Este comportamiento es clave para la defensa oral: demostrar que el sistema es tolerante a fallos parciales (Indicadores 7 y 8 EP3: integración y Circuit Breaker).

### Sub-task 3 [CORD-179]: Crear AuthServiceTest con casos de login, crear y eliminar usuario

Descripcion: Crear AuthServiceTest.java con @ExtendWith(MockitoExtension.class). El AuthService usa un ConcurrentHashMap interno (no JPA), por lo que se puede testear directamente sin mocks: instanciar new AuthService(). Implementar 5 tests: (1) autenticar_conCredencialesValidas_retornaAuthResponse: llamar authService.autenticar(new LoginRequestDTO("a.gatica@cordillera.cl", "gerencia2026")), verificar que el token no es null y que nombre="A. Gatica" y rol="GERENTE_GENERAL"; (2) autenticar_conContrasenaIncorrecta_lanzaCustomUnauthorizedException: usar credenciales con contraseña incorrecta, verificar con assertThrows(CustomUnauthorizedException.class, ...); (3) crearUsuario_conEmailNuevo_retornaUsuarioConId: crear usuario nuevo y verificar que el id tiene formato "USR-00X"; (4) crearUsuario_conEmailDuplicado_lanzaUsuarioYaExisteException: intentar crear con email ya existente y verificar UsuarioYaExisteException; (5) eliminarUsuario_conIdInexistente_lanzaUsuarioNoEncontradoException. Estos 5 tests cubren el 100% de AuthService contribuyendo significativamente a superar el quality gate del bff-gateway (Indicadores 2 y 4 EP3).
