# HU-CORD-117 — Consultar KPIs filtrados por categoría (Factory Method)

## Historia de Usuario

Como BFF Gateway del Grupo Cordillera
quiero consultar KPIs específicos filtrados por categoría (Ventas, Logística, Rentabilidad)
para armar la sección de métricas particionadas en el dashboard del frontend

## Contexto Técnico e Integración

Petición GET /api/kpis/categoria/{cat} (ruta base: /api/kpis, ver @RequestMapping en KpiController). El método del service es findByCategoria(String categoria).

Las 4 categorías soportadas en KpiFactory: ventas, inventario, logistica, rentabilidad (todo en minúsculas — KpiFactory.obtenerCalculador() hace toLowerCase() internamente).

IMPORTANTE sobre el método real de la factory: es obtenerCalculador(String categoria), NO getCalculator(). El KpiController retorna lista vacía [] para categorías inexistentes — NO lanza 404.

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Factory Method (KpiFactory)**: centraliza la instanciación de calculadoras (VentasCalculator, InventarioCalculator, LogisticaCalculator, RentabilidadCalculator). Open/Closed: agregar nueva categoría = nueva clase + entrada en el mapa, sin modificar KpiService.
- **Strategy**: cada KpiCalculator implementa la interfaz calcular(BigDecimal, BigDecimal) — comportamiento intercambiable en tiempo de ejecución.
- **BFF Proxy**: el BFF reenvía GET /api/v1/kpis/categoria/{cat} → kpi-service:8084/api/kpis/categoria/{cat}.

## Criterios de Aceptación (Gherkin)

**AC1: Retorna KPIs de categoría válida**

```
Dado que existen KPIs con categoria="ventas" en kpi_db
Cuando solicito GET /api/kpis/categoria/ventas
Entonces retorna 200 OK con los KPIs de esa categoría únicamente
```

**AC2: Categoría inexistente retorna lista vacía**

```
Dado una petición a categoría no existente
Cuando solicito GET /api/kpis/categoria/operaciones
Entonces retorna 200 OK con lista vacía [] (el KpiController NO lanza 404)
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `KpiController.java` | Verificar | @GetMapping("/categoria/{cat}") retorna lista vacía para categoría inexistente |
| `KpiService.java` | Verificar | findByCategoria(String) y create(Kpi) con calcularValor() |
| `KpiFactory.java` | Verificar | obtenerCalculador(String) con 4 categorías registradas |
| `KpiFactoryTest.java` | Crear | 5 tests de las 4 categorías + categoría inválida |
| `KpiControllerTest.java` | Crear | @WebMvcTest con tests de /categoria/{cat} |
| `KpiServiceTest.java` | Crear | Mockito con tests de findByCategoria y create |

## Estrategia de Testing (Cobertura > 60%)

- **KpiFactoryTest**: 5 tests que cubren 100% de KpiFactory — alta densidad de líneas para el quality gate.
- **KpiServiceTest**: mockear KpiFactory.obtenerCalculador() para verificar que create() lo invoca exactamente 1 vez (patrón Factory Method en acción).
- **KpiControllerTest**: verificar que lista vacía retorna 200 (no 404).

## Definición de Hecho (DoD)

- [ ] KpiFactoryTest con 5 tests verdes
- [ ] KpiControllerTest con tests de /categoria/{cat} verdes
- [ ] KpiServiceTest con tests de findByCategoria y create verdes
- [ ] Cobertura JaCoCo > 60% en cl.duoc.cordillera.kpiservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-156]: Crear KpiFactoryTest con 5 tests (4 categorías + inválida)

Crear KpiFactoryTest.java (NO necesita Spring, instanciar new KpiFactory() directamente). El método real en KpiFactory es obtenerCalculador(String categoria) (NO getCalculator). Implementar 5 tests: (1) obtenerCalculador_ventas_retornaVentasCalculator: new KpiFactory().obtenerCalculador("ventas") instanceof VentasCalculator; (2) obtenerCalculador_inventario_retornaInventarioCalculator: instanceof InventarioCalculator; (3) obtenerCalculador_logistica_retornaLogisticaCalculator: instanceof LogisticaCalculator; (4) obtenerCalculador_rentabilidad_retornaRentabilidadCalculator: instanceof RentabilidadCalculator; (5) obtenerCalculador_categoriaInvalida_lanzaIllegalArgumentException: assertThrows(IllegalArgumentException.class, () -> factory.obtenerCalculador("operaciones")) — "operaciones" NO existe en el mapa, solo: ventas, inventario, logistica, rentabilidad. IMPORTANTE: el método hace categoria.toLowerCase() internamente, así que "Ventas" y "VENTAS" también funcionan. Open/Closed: agregar nueva categoría = nueva clase + nueva entrada en el mapa, sin modificar KpiService (Indicador 8 EP3).

### Sub-task 2 [CORD-157]: Crear KpiControllerTest con tests de /categoria/{cat}

Crear KpiControllerTest.java con @WebMvcTest(KpiController.class). Mockear KpiService con @MockBean. IMPORTANTE: la ruta base real del KpiController es /api/kpis (ver @RequestMapping("/api/kpis")), NO /api/v1/kpis. El endpoint de categoría es @GetMapping("/categoria/{cat}") → ruta completa: GET /api/kpis/categoria/{cat}. El método del service es findByCategoria(String categoria). Implementar: (1) getByCategoria_ventas_retorna200ConLista: GET /api/kpis/categoria/ventas, mockear kpiService.findByCategoria("ventas") para retornar lista con 1 Kpi (id=1, nombre="Ventas Totales", valor=380000, categoria="ventas"), verificar HTTP 200 y Content-Type application/json; (2) getByCategoria_categoriaInexistente_retorna200ListaVacia: GET /api/kpis/categoria/operaciones, mockear kpiService.findByCategoria("operaciones") para retornar lista vacía, verificar HTTP 200 con body []. El KpiController NO lanza 404 para categoría inexistente (Indicador 3 EP3).

### Sub-task 3 [CORD-158]: Crear KpiServiceTest con tests de findByCategoria y create con categoría inválida

Crear KpiServiceTest.java con @ExtendWith(MockitoExtension.class). Mockear KpiRepository (@Mock) y KpiFactory (@Mock). En KpiService, el método de creación es create(Kpi kpi) y llama internamente a kpiFactory.obtenerCalculador(kpi.getCategoria()). Implementar: (1) findByCategoria_conCategoriaValida_retornaLista: mockear kpiRepository.findByCategoria("ventas") para retornar lista con 1 Kpi, verificar que retorna lista de size=1; (2) create_conCategoriaValida_invocaObtenerCalculadorYPersiste: crear Kpi con categoria="ventas", mockear kpiFactory.obtenerCalculador("ventas") para retornar KpiCalculator mock que retorna BigDecimal.valueOf(95000) en calcular(), verificar con verify(kpiFactory, times(1)).obtenerCalculador("ventas") y que kpiRepository.save() es llamado; (3) create_conCategoriaInvalida_propagaIllegalArgumentException: mockear kpiFactory.obtenerCalculador("invalida") para lanzar new IllegalArgumentException("Categoria no soportada"), verificar assertThrows(IllegalArgumentException.class). ATENCIÓN: el método en KpiFactory es obtenerCalculador(), NO getCalculator() (Indicadores 4 y 8 EP3).
