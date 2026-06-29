# HU-CORD-114 — Listar datos operacionales con filtros (sistema/sucursal)

## Historia de Usuario

Como ejecutivo de operaciones del Grupo Cordillera
quiero consultar los datos operacionales filtrados por sistema de origen (POS, SAP, ERP, etc.) o por sucursal
para analizar el comportamiento de cada canal o tienda sin procesos manuales y mostrarlos en el dashboard

## Contexto Técnico e Integración

Flujo end-to-end: El BFF expone GET /api/v1/datos/sistema/{origen} y GET /api/v1/datos/sucursal/{id}. Internamente consulta a data-service:8083/api/datos/sistema/{origen} y /api/datos/sucursal/{id}.

IMPORTANTE: Las rutas reales del DatoController son path variables, NO query params:
- @GetMapping("/sistema/{origen}") → GET /api/datos/sistema/SAP
- @GetMapping("/sucursal/{id}") → GET /api/datos/sucursal/1

Los métodos del DatoService son buscarPorSistemaOrigen(String) y buscarPorSucursalId(Long).

Sistemas de origen reales del proyecto: SAP, ERP, CRM, POS, E-COMMERCE, INVENTARIO, FINANZAS.

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Query Methods (Spring Data)**: findBySistemaOrigen y findBySucursalId generados automáticamente por Spring — no se escribe JPQL.
- **Separación de responsabilidades**: Controller expone la API, Service orquesta la lógica, Repository accede a los datos.
- **BFF Proxy**: el BFF reenvía la petición al data-service sin transformar los datos, actuando como proxy transparente.

## Criterios de Aceptación (Gherkin)

**AC1: Consulta con resultados válidos**

```
Dado que existen registros con sistemaOrigen=POS en data_db
Cuando se envía GET /api/v1/datos/sistema/POS al BFF
Entonces se retorna 200 OK con la lista de datos correspondientes en JSON
```

**AC2: Consulta sin resultados retorna lista vacía**

```
Dado que no existen registros con sistemaOrigen=CRM
Cuando se envía GET /api/v1/datos/sistema/CRM
Entonces se retorna 200 OK con [] (no 404)
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `DatoController.java` | Verificar | @GetMapping("/sistema/{origen}") y @GetMapping("/sucursal/{id}") como path variables |
| `DatoService.java` | Verificar | Métodos buscarPorSistemaOrigen() y buscarPorSucursalId() |
| `DatoRepository.java` | Verificar | Query methods findBySistemaOrigen y findBySucursalId |
| `DatoRepositoryTest.java` | Modificar | Agregar tests de filtrado |
| `DatoControllerTest.java` | Modificar | Agregar tests MockMvc para rutas /sistema/{origen} y /sucursal/{id} |

## Estrategia de Testing (Cobertura > 60%)

- **DatoRepositoryTest**: persistir datos variados y verificar que los query methods filtran correctamente.
- **DatoControllerTest**: MockMvc para validar las rutas reales /api/datos/sistema/{origen} y /api/datos/sucursal/{id} (path variables, no query params).

## Definición de Hecho (DoD)

- [ ] Tests de DatoRepositoryTest para findBySistemaOrigen y findBySucursalId verdes
- [ ] Tests MockMvc de DatoController para /sistema/{origen} y /sucursal/{id} verdes
- [ ] Cobertura JaCoCo > 60% en cl.duoc.cordillera.dataservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-137]: Cubrir buscarPorSistemaOrigen y buscarPorSucursalId en Repository test

En DatoRepositoryTest.java (creado en CORD-134), agregar métodos: (1) findBySistemaOrigen_retornaListaFiltrada: persistir 3 datos (2x sistemaOrigen="POS", 1x sistemaOrigen="ECOMMERCE") via repository.save() y verificar que findBySistemaOrigen("POS") retorna exactamente 2 resultados; (2) findBySucursalId_retornaListaFiltrada: persistir 2 datos de sucursalId=1 y 1 de sucursalId=2, verificar que findBySucursalId(1L) retorna size=2. NOTA: los sistemas de origen reales del proyecto son: SAP, ERP, CRM, POS, E-COMMERCE, INVENTARIO, FINANZAS — usar valores consistentes con el DataLoader. Cubre el Query Method Pattern de Spring Data JPA (Indicador 3 EP3: persistencia).

### Sub-task 2 [CORD-138]: Agregar tests MockMvc para endpoints /sistema/{origen} y /sucursal/{id}

En DatoControllerTest.java, agregar: IMPORTANTE: las rutas reales del DatoController son /api/datos/sistema/{origen} y /api/datos/sucursal/{id} (path variables, NO query params). Ver DatoController.java: @GetMapping("/sistema/{origen}") y @GetMapping("/sucursal/{id}"). Implementar: (1) listarPorSistema_conResultados_retorna200: mockear datoService.buscarPorSistemaOrigen("SAP") para retornar lista de 2 elementos, hacer GET /api/datos/sistema/SAP, verificar HTTP 200 y body con array de 2 items; (2) listarPorSucursal_conResultados_retorna200: mockear datoService.buscarPorSucursalId(1L), hacer GET /api/datos/sucursal/1, verificar HTTP 200. NOTA: los métodos del DatoService son buscarPorSistemaOrigen() y buscarPorSucursalId(), NO findBySistemaOrigen() (Indicadores 2 y 3 EP3).

### Sub-task 3 [CORD-139]: Cubrir caso vacío (200 vacío) en Controller test

IMPORTANTE: el DatoController usa path variables, NO query params. Agregar test buscarPorSistema_sinResultados_retorna200Vacio: mockear datoService.buscarPorSistemaOrigen("CRM") para retornar lista vacía, hacer GET /api/datos/sistema/CRM, verificar HTTP 200 con body [] (array vacío, NO 404). El método del DatoService es buscarPorSistemaOrigen(String origen). Este comportamiento (200+[] en vez de 404 para lista vacía) es un requisito explícito del AC2 y debe documentarse en la colección Postman como nota al endpoint (Indicador 3 EP3: integración API REST).
