# HU-CORD-120 — Generar reporte ejecutivo consolidado (POST /api/reportes/generar)

## Historia de Usuario

Como ejecutivo del Grupo Cordillera
quiero generar un reporte que consolide datos remotos solicitando información al KPI Service via HTTP
para tener una visión centralizada del desempeño organizacional

## Contexto Técnico e Integración

El report-service invoca al kpi-service (puerto 8084) mediante KpiClienteService (wrapper de RestTemplate) protegido con @CircuitBreaker de Resilience4j. El fallback retorna lista vacía.

Rutas reales del ReporteController (ruta base: @RequestMapping("/api/reportes")):
- POST /api/reportes/generar → genera reporte ejecutivo
- POST /api/reportes → crea reporte (delega a generarReporte())
- GET /api/reportes/{id} → buscar por id

IMPORTANTE sobre ReporteService: recibe ReporteRepository y ExportadorFactory como dependencias — NO KpiClienteService (eso está en el Controller). La regla de idempotencia: si ya existe un reporte para (area, tipo, anio, mes), retorna el existente sin crear duplicado.

Stack: Spring Boot 4.0.6 · Java 21 · Resilience4j 2.4.0 · MockRestServiceServer (ya en spring-test).

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Circuit Breaker (Resilience4j)**: protege al report-service de caídas en cadena del kpi-service. Si falla, el fallback retorna lista vacía y el reporte se genera en modo degradado.
- **Idempotencia**: la restricción UNIQUE (area, tipo, anio, mes) en la tabla reportes garantiza que el mismo reporte no se genera dos veces — regla de negocio crítica del Grupo Cordillera.
- **Repository Pattern**: ReporteRepository.findByAreaAndTipoAndAnioAndMes() verifica existencia antes de persistir.

## Criterios de Aceptación (Gherkin)

**AC1: Invocación remota exitosa — reporte nuevo**

```
Dado el kpi-service online y que no existe reporte para (area="Finanzas", tipo="EJECUTIVO", anio=2026, mes=6)
Cuando report-service genera reporte con POST /api/reportes/generar
Entonces ReporteService.generarReporte() llama saveAndFlush() y retorna reporte con id generado
```

**AC2: Idempotencia — reporte ya existente**

```
Dado que ya existe un reporte para (area="Finanzas", tipo="EJECUTIVO", anio=2026, mes=6)
Cuando se intenta generar el mismo reporte
Entonces ReporteService retorna el reporte existente sin invocar saveAndFlush()
```

**AC3: Fallback ante caída del kpi-service**

```
Dado que kpi-service está caído
Cuando se genera el reporte
Entonces Resilience4j activa el fallback de KpiClienteService retornando lista vacía
Y el reporte se guarda igualmente (sin KPIs)
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `ReporteController.java` | Verificar | POST /api/reportes/generar y POST /api/reportes |
| `KpiClienteService.java` | Verificar | @CircuitBreaker con fallback retornando lista vacía |
| `ReporteServiceTest.java` | Crear | Mockito: ReporteRepository + ExportadorFactory |
| `KpiClienteServiceTest.java` | Crear | MockRestServiceServer para simular kpi-service |
| `ReporteControllerTest.java` | Crear | @WebMvcTest(ReporteController.class) |

## Estrategia de Testing (Cobertura > 60%)

- **ReporteServiceTest**: mockear ReporteRepository y ExportadorFactory — NO KpiClienteService (no es dependencia del service). Cubrir la regla de idempotencia: verificar que saveAndFlush NO se invoca si ya existe el reporte.
- **KpiClienteServiceTest**: usar MockRestServiceServer para simular kpi-service online (200) y caído (500/timeout) — verificar que el fallback retorna lista vacía.
- **ReporteControllerTest**: @WebMvcTest con @MockBean para ReporteService Y KpiClienteService (el controller inyecta ambos).

## Definición de Hecho (DoD)

- [ ] ReporteServiceTest con generarReporte (caso nuevo e idempotencia) verde
- [ ] KpiClienteServiceTest con fallback del Circuit Breaker verde
- [ ] ReporteControllerTest con POST /api/reportes/generar verde
- [ ] Cobertura JaCoCo > 60% en cl.duoc.cordillera.reportservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-165]: Crear ReporteServiceTest con generarReporte y mocks

Crear ReporteServiceTest.java con @ExtendWith(MockitoExtension.class). Mockear ReporteRepository (@Mock) y ExportadorFactory (@Mock). IMPORTANTE: ReporteService NO recibe KpiClienteService como dependencia — recibe ReporteRepository y ExportadorFactory. Implementar: (1) generarReporte_conAreaYValorValidos_guardaReporteNuevo: crear Reporte con area="Finanzas", valor=BigDecimal.valueOf(380000), anio=2026, mes=6; mockear reporteRepository.findByAreaAndTipoAndAnioAndMes("Finanzas","EJECUTIVO",2026,6) para retornar Optional.empty(); mockear reporteRepository.saveAndFlush(any()) para retornar el reporte con id=1L; llamar reporteService.generarReporte(reporte) y verificar que el resultado tiene id=1L; (2) generarReporte_conMismoPeriodo_retornaExistente: mockear findByAreaAndTipoAndAnioAndMes para retornar Optional.of(reporteExistente), verificar que saveAndFlush NO es invocado (verify(reporteRepository, never()).saveAndFlush(any())) y que se retorna el reporteExistente. Cubre la regla de negocio crítica de idempotencia del Grupo Cordillera (Indicadores 2, 3 y 4 EP3).

### Sub-task 2 [CORD-166]: Crear KpiClienteServiceTest con Circuit Breaker fallback

Crear KpiClienteServiceTest.java. Usar @SpringBootTest con perfil de test y MockRestServiceServer (de spring-test, ya incluido en spring-boot-starter-test) para simular el kpi-service. Implementar: (1) obtenerKpis_conServidorDisponible_retornaLista: configurar MockRestServiceServer para responder 200 con array JSON de KPIs, llamar KpiClienteService.obtenerKpis(), verificar que retorna lista no vacía; (2) obtenerKpis_conServidorCaido_retornaListaVaciaFallback: configurar MockRestServiceServer para responder 500, verificar que @CircuitBreaker activa el fallback y retorna lista vacía en vez de propagar excepción. Demuestra tolerancia a fallos (CASO sección 1, Indicadores 7 y 8 EP3).

### Sub-task 3 [CORD-167]: Crear ReporteControllerTest con validaciones de payload

Crear ReporteControllerTest.java con @WebMvcTest(ReporteController.class). Mockear ReporteService con @MockBean Y KpiClienteService con @MockBean (el Controller inyecta ambos). IMPORTANTE: las rutas reales del ReporteController son /api/reportes (NO /api/v1/reportes). Implementar: (1) generar_conAreaYValorValidos_retorna201: POST /api/reportes/generar con body {"area":"Finanzas","valor":380000.00}, mockear reporteService.generarReporte(any()) para retornar reporte con id=1L, verificar HTTP 201 y body con campo "id"; (2) crear_sinArea_retorna400: POST /api/reportes con body sin area, mockear para lanzar ResponseStatusException(BAD_REQUEST, "El area del reporte es obligatoria"), verificar HTTP 400; (3) buscarPorId_conIdExistente_retorna200: GET /api/reportes/1, mockear reporteService.buscarPorId(1L) para retornar reporte, verificar HTTP 200. Cubre el contrato REST del report-service para la colección Postman (Indicador 3 EP3).
