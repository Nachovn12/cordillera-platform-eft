# HU-CORD-118 — CRUD completo de KPIs con recálculo vía KpiFactory

## Historia de Usuario

Como administrador de negocio del Grupo Cordillera
quiero registrar y gestionar definiciones de KPIs (CRUD)
para que el sistema recalcule automáticamente sus valores usando KpiFactory antes de guardarlos en kpi_db

## Contexto Técnico e Integración

Endpoints POST, PUT, DELETE en /api/kpis (ruta base real: @RequestMapping("/api/kpis") en KpiController).

Al crear o actualizar un KPI, KpiService llama internamente al método privado calcularValor(Kpi kpi), que usa kpiFactory.obtenerCalculador(kpi.getCategoria()) para obtener el calculador correcto y setear el valor recalculado.

Métodos públicos del KpiService:
- create(Kpi kpi) — NO crear()
- update(Long id, Kpi kpi) — lanza ResponseStatusException(NOT_FOUND) si el id no existe
- delete(Long id) — llama directamente deleteById() SIN verificar existencia previa

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Factory Method**: KpiService delega el cálculo a KpiFactory.obtenerCalculador() — el service NO conoce la implementación concreta del cálculo.
- **Strategy**: cada KpiCalculator (VentasCalculator, etc.) implementa el comportamiento de cálculo — intercambiable sin modificar el service.
- **Service (DDD)**: aísla la lógica de negocio (recálculo) del Controller. El Controller solo maneja HTTP, el Repository solo maneja persistencia.

## Criterios de Aceptación (Gherkin)

**AC1: Recálculo automático en POST**

```
Dado un payload de creación de KPI con categoria="ventas" y valor=100
Cuando entra al KpiService.create()
Entonces el servicio llama a KpiFactory.obtenerCalculador("ventas"), obtiene el valor recalculado
Y persiste el registro en kpi_db con el valor final calculado
```

**AC2: ID inexistente retorna 404 en PUT**

```
Dado un ID 9999 que no existe en kpi_db
Cuando llamo KpiService.update(9999L, kpi)
Entonces se lanza ResponseStatusException(NOT_FOUND) — NO KpiNotFoundException
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `KpiController.java` | Verificar | POST, PUT, DELETE en /api/kpis |
| `KpiService.java` | Verificar | create(), update(), delete() con calcularValor() |
| `KpiServiceTest.java` | Modificar | Agregar tests de create, update y delete |

## Estrategia de Testing (Cobertura > 60%)

- **KpiServiceTest**: mockear KpiFactory.obtenerCalculador() con verify(times(1)) para confirmar que el recálculo se invoca exactamente una vez por transacción.
- Contrastar el comportamiento de delete() en KpiService (sin verificación previa) vs DatoService.eliminar() y ReporteService.eliminar() (con verificación) — diferencia de diseño relevante para la defensa oral.

## Definición de Hecho (DoD)

- [ ] Test create_invocaObtenerCalculador_exactamente1Vez verde
- [ ] Test update_conIdInexistente_lanzaResponseStatusException verde
- [ ] Test delete_conIdValido_invocaDeleteById verde
- [ ] Cobertura JaCoCo > 60% en cl.duoc.cordillera.kpiservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-159]: Crear test create con KpiFactory mock verificado

En KpiServiceTest.java, agregar test create_invocaObtenerCalculador_exactamente1Vez: dado un Kpi con categoria="ventas" y valor=BigDecimal.valueOf(100), mockear kpiFactory.obtenerCalculador("ventas") para retornar un KpiCalculator mock; mockear que ese calculator.calcular(any(), any()) retorna BigDecimal.valueOf(95000); llamar kpiService.create(kpi) y verificar con verify(kpiFactory, times(1)).obtenerCalculador("ventas") que la fábrica fue invocada exactamente una vez. Verificar también con verify(kpiRepository, times(1)).save(kpi) que el Kpi fue persistido. IMPORTANTE: el método en KpiFactory es obtenerCalculador(), NO getCalculator(). El método público del service es create(Kpi kpi), NO crear(). Este test demuestra el patrón Factory Method en acción (Indicador 8 EP3).

### Sub-task 2 [CORD-160]: Crear test update con id inexistente (lanza excepción)

En KpiServiceTest.java, agregar test update_conIdInexistente_lanzaResponseStatusException: mockear kpiRepository.findById(9999L) para retornar Optional.empty(), llamar kpiService.update(9999L, kpiDto) y verificar con assertThrows(ResponseStatusException.class) que se lanza la excepción. IMPORTANTE: KpiService.findById() lanza ResponseStatusException(HttpStatus.NOT_FOUND, "KPI no encontrado con id: " + id) — NO KpiNotFoundException. El método del service es update(Long id, Kpi kpi). Verificar que kpiRepository.save() NO es invocado: verify(kpiRepository, never()).save(any()) (Indicadores 2 y 4 EP3).

### Sub-task 3 [CORD-161]: Crear test delete invocando deleteById

En KpiServiceTest.java, agregar test delete_conIdValido_invocaDeleteById: IMPORTANTE: KpiService.delete(Long id) llama directamente a kpiRepository.deleteById(id) SIN verificar existencia primero. Por tanto: NO mockear findById. Simplemente llamar kpiService.delete(1L) y verificar con verify(kpiRepository, times(1)).deleteById(1L). No necesitas when() previo porque deleteById no retorna nada. Confirmar con mvn test -pl kpi-service -Dtest=KpiServiceTest que el test es verde. Contrastar con DatoService.eliminar() y ReporteService.eliminar() que SÍ verifican existencia antes — esta diferencia de diseño vale la pena mencionar en la defensa oral (Indicador 8 EP3).
