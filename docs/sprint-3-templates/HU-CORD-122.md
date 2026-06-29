# HU-CORD-122 — CRUD reportes y filtrado por área

## Historia de Usuario

Como usuario del Grupo Cordillera
quiero acceder al historial de reportes y filtrarlos por área (Finanzas, Ventas, Logística)
para agilizar mis búsquedas históricas sin procesos manuales

## Contexto Técnico e Integración

El ReporteController expone GET /api/reportes/area/{area} (path variable, ver @GetMapping("/area/{area}")).
El BFF lo expone como GET /api/v1/reportes/area/{area}.

El método del ReporteService es listarPorArea(String area) — NO findByArea().
El método del ReporteRepository es findByArea(String area) — query method de Spring Data JPA.

El contrato REST: lista vacía retorna HTTP 200 + [] (NO 404). Documentar este contrato en la colección Postman.

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Repository Pattern**: findByArea() es un query method generado automáticamente por Spring Data JPA a partir del nombre del método — no se escribe JPQL.
- **Spring Data Query Methods**: convención de nombre → SQL generado → separación limpia entre capa de dominio y persistencia.
- **Contrato REST**: 200 + [] para lista vacía es la decisión de diseño correcta para colecciones — diferente al 404 que se usaría para un recurso singular inexistente.

## Criterios de Aceptación (Gherkin)

**AC1: Filtrado exitoso por área**

```
Dado reportes generados con area="Finanzas" en report_db
Cuando busco GET /api/reportes/area/Finanzas
Entonces el JPA Repository filtra correctamente y retorna 200 OK con la lista
```

**AC2: Área inexistente retorna lista vacía**

```
Dado que no existen reportes con area="RRHH"
Cuando busco GET /api/reportes/area/RRHH
Entonces retorna 200 OK con [] (NO 404)
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `ReporteRepository.java` | Verificar | findByArea(String area) — query method |
| `ReporteRepositoryTest.java` | Crear | @DataJpaTest con findByArea |
| `ReporteControllerTest.java` | Modificar | Tests MockMvc GET /area/{area} |

## Estrategia de Testing (Cobertura > 60%)

- **ReporteRepositoryTest**: @DataJpaTest + H2 — persistir reportes variados y verificar que findByArea filtra correctamente.
- **ReporteControllerTest**: mockear listarPorArea() con 2 items (lista llena) y con lista vacía, verificar HTTP 200 en ambos casos.

## Definición de Hecho (DoD)

- [ ] ReporteRepositoryTest con findByArea verdes (área con resultados y área vacía)
- [ ] Tests MockMvc GET /api/reportes/area/{area} verdes (200 con lista y 200 con [])
- [ ] Cobertura JaCoCo > 60% en cl.duoc.cordillera.reportservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-171]: Crear ReporteRepositoryTest con @DataJpaTest y findByArea

Crear ReporteRepositoryTest.java con @DataJpaTest. Configurar application-test.properties con H2 en memoria. Implementar: (1) findByArea_conAreaExistente_retornaLista: persistir 3 reportes (2x area="Finanzas", 1x area="Ventas") via repository.save(), llamar findByArea("Finanzas"), verificar que retorna lista de size=2 con los datos correctos; (2) findByArea_conAreaInexistente_retornaListaVacia: llamar findByArea("RRHH"), verificar lista vacía (assertNotNull + assertEquals(0, result.size())). Valida el Query Method Pattern de Spring Data JPA del report-service (Indicadores 3 y 4 EP3, CASO sección 1: Repository Pattern).

### Sub-task 2 [CORD-172]: Agregar tests MockMvc de GET /api/reportes/area/{area}

En ReporteControllerTest.java, agregar: IMPORTANTE: la ruta real del ReporteController es /api/reportes/area/{area} (path variable, ver @GetMapping("/area/{area}")). (1) listarPorArea_Finanzas_retorna200ConLista: GET /api/reportes/area/Finanzas, mockear reporteService.listarPorArea("Finanzas") para retornar lista de 2 reportes, verificar HTTP 200 y body con array de 2 elementos con campos area, anio, mes; (2) listarPorArea_AreaInexistente_retorna200Vacio: GET /api/reportes/area/RRHH, mockear listarPorArea("RRHH") para retornar lista vacía, verificar HTTP 200 con body []. El método del service es listarPorArea(String area), NO findByArea() (Indicador 3 EP3: API REST).

### Sub-task 3 [CORD-173]: Cubrir caso vacío en listado por área

En ReporteRepositoryTest.java y ReporteControllerTest.java, agregar test explícito para el caso de lista vacía: (1) En Repository: findByArea_sinResultados_retornaListaVaciaNoNull: verificar assertNotNull(result) y assertEquals(0, result.size()) — la lista vacía no debe ser null ni lanzar excepción; (2) En Controller con MockMvc: verificar que el JSON retornado es "[]" (array vacío) y NO un 404. Esta decisión de contrato (200+[] vs 404) debe documentarse en la colección Postman como nota al endpoint. La decisión es correcta para recursos tipo colección — 404 sería para un recurso singular (Indicadores 3 y 4 EP3).
