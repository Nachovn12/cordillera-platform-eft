# HU-CORD-115 — Actualizar y eliminar dato operacional (PUT/DELETE)

## Historia de Usuario

Como administrador de la plataforma
quiero actualizar o eliminar datos operacionales erróneos via API REST
para mantener la integridad y veracidad de la base de datos data_db

## Contexto Técnico e Integración

Métodos HTTP PUT y DELETE en /api/datos/{id} (ruta interna del data-service). El BFF los expone como /api/v1/datos/{id}.

IMPORTANTE sobre las excepciones reales: DatoService.buscarPorId() lanza NoSuchElementException (java.util) — NO una excepción personalizada. El GlobalExceptionHandler del data-service mapea NoSuchElementException a HTTP 404.

Métodos del DatoService:
- actualizar(Long id, Dato dato) → llama buscarPorId() antes de modificar
- eliminar(Long id) → llama buscarPorId() antes de deleteById()

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Buenas prácticas RESTful**: PUT retorna 200 OK con el objeto modificado, DELETE retorna 204 No Content sin body.
- **GlobalExceptionHandler**: captura NoSuchElementException y retorna 404 estructurado — desacopla el manejo de errores del Controller.
- **Validación de existencia**: el Service verifica que el id existe antes de mutar — patron Guard Clause.

## Criterios de Aceptación (Gherkin)

**AC1: Actualización exitosa**

```
Dado un ID válido existente en data_db
Cuando envío PUT /api/v1/datos/{id} con payload actualizado
Entonces retorna 200 OK con el objeto modificado
```

**AC2: ID inexistente retorna 404**

```
Dado un ID que no existe (ej. 9999)
Cuando envío DELETE /api/v1/datos/9999
Entonces retorna 404 Not Found con mensaje claro del GlobalExceptionHandler
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `DatoController.java` | Verificar | Endpoints PUT /{id} y DELETE /{id} |
| `DatoService.java` | Verificar | actualizar() y eliminar() con validación previa |
| `DatoServiceTest.java` | Modificar | Tests de actualizar con id existente e inexistente |
| `DatoControllerTest.java` | Modificar | Tests MockMvc PUT 200/404 y DELETE 204/404 |

## Estrategia de Testing (Cobertura > 60%)

- **DatoServiceTest**: mockear findById() para Optional.of() y Optional.empty(), verificar comportamiento de actualizar() con assertThrows(NoSuchElementException.class).
- **DatoControllerTest**: PUT retorna 200 con objeto modificado, PUT con id inexistente retorna 404, DELETE retorna 204, DELETE con id inexistente retorna 404.

## Definición de Hecho (DoD)

- [ ] Tests de DatoServiceTest para actualizar() con id existente e inexistente verdes
- [ ] Tests MockMvc PUT /api/datos/{id} (200 y 404) verdes
- [ ] Tests MockMvc DELETE /api/datos/{id} (204 y 404) verdes
- [ ] Cobertura JaCoCo > 60% en cl.duoc.cordillera.dataservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-150]: Agregar tests de DatoService.actualizar con id existente e inexistente

En DatoServiceTest.java, agregar: IMPORTANTE: DatoService.buscarPorId() lanza NoSuchElementException (java.util), NO una excepción personalizada. (1) actualizar_conIdExistente_debeRetornarDatoActualizado: mockear datoRepository.findById(1L) para retornar Optional.of(datoExistente con sistemaOrigen="POS"), crear nuevoDato con sistemaOrigen="ERP"; llamar datoService.actualizar(1L, nuevoDato); verificar que datoRepository.save() es llamado y el resultado tiene sistemaOrigen="ERP"; (2) actualizar_conIdInexistente_debeLanzarNoSuchElementException: mockear datoRepository.findById(9999L) para retornar Optional.empty(), llamar datoService.actualizar(9999L, dato) y verificar assertThrows(NoSuchElementException.class). El GlobalExceptionHandler mapea esta excepción a HTTP 404. Cubre AC1 y AC2 (Indicadores 2 y 4 EP3).

### Sub-task 2 [CORD-141]: Agregar tests MockMvc de PUT /api/datos/{id} (200 OK y 404)

En DatoControllerTest.java, agregar: IMPORTANTE: la ruta base real del DatoController es /api/datos (NO /api/v1/datos — ese prefijo es del BFF proxy). (1) actualizar_conIdValido_retorna200: mockear datoService.actualizar(1L, any()) para retornar dato con sistemaOrigen="ERP", hacer PUT /api/datos/1 con body JSON {"sistemaOrigen":"ERP","tipoDato":"VENTA","valor":"150000","sucursalId":1}, verificar HTTP 200 y body con "sistemaOrigen":"ERP"; (2) actualizar_conIdInexistente_retorna404: mockear datoService.actualizar(9999L, any()) para lanzar NoSuchElementException, hacer PUT /api/datos/9999, verificar HTTP 404. El GlobalExceptionHandler debe mapear NoSuchElementException a 404 (Indicador 3 EP3: API REST).

### Sub-task 3 [CORD-142]: Agregar tests MockMvc de DELETE /api/datos/{id} (204)

En DatoControllerTest.java, agregar: (1) eliminar_conIdValido_retorna204: mockear datoService.eliminar(1L) sin excepción (doNothing().when(datoService).eliminar(1L)), hacer DELETE /api/datos/1, verificar HTTP 204 No Content (body vacío); (2) eliminar_conIdInexistente_retorna404: mockear datoService.eliminar(9999L) para lanzar NoSuchElementException (NO DatoNoEncontradoException — el DatoService lanza NoSuchElementException), hacer DELETE /api/datos/9999, verificar HTTP 404. NOTA: la ruta interna es /api/datos/{id}, NO /api/v1/datos/{id}. El 204 sin body es el estándar REST para DELETE exitoso (Indicadores 3 y 4 EP3).
