# HU-CORD-121 — Exportar reporte PDF/Excel/JSON con ExportadorFactory

## Historia de Usuario

Como directivo del Grupo Cordillera
quiero descargar mis reportes ejecutivos en distintos formatos (PDF, Excel, JSON)
para manipularlos o presentarlos según la situación usando una fábrica de exportación

## Contexto Técnico e Integración

Endpoint: GET /api/reportes/{id}/exportar?formato=X (query param con defaultValue="pdf"). El BFF lo expone como GET /api/v1/reportes/{id}/exportar?formato=X al frontend.

La ExportadorFactory.crearExportador(String formato) soporta: pdf (default), excel/xls/xlsx, json.

IMPORTANTE sobre excepciones reales:
- Formato inválido → lanza ResponseStatusException(HttpStatus.BAD_REQUEST) — NO IllegalArgumentException
- Formato null/blank → lanza ResponseStatusException(HttpStatus.BAD_REQUEST)
- El método de la factory es crearExportador(), NO getExportador()

El método de ReporteService es exportar(Long id, String formato) — llama buscarPorId() y luego exportadorFactory.crearExportador().

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Factory Method (ExportadorFactory)**: selecciona en tiempo de ejecución el exportador concreto (PdfExportador, ExcelExportador, JsonExportador) según el formato solicitado — sin acoplar el Controller.
- **Strategy**: cada exportador implementa Exportador.exportar(Reporte) → byte[] — comportamiento intercambiable.
- **Open/Closed**: agregar nuevo formato = nueva clase + nueva entrada en el switch, sin modificar el Controller ni el Service.

## Criterios de Aceptación (Gherkin)

**AC1: Exportación exitosa en los 3 formatos**

```
Dado un reporte existente con id=1
Cuando llamo GET /api/reportes/1/exportar?formato=pdf
Entonces recibo los bytes del archivo con Content-Type=application/pdf y Content-Disposition=attachment
Y la Factory selecciona PdfExportador automáticamente
```

**AC2: Formato inválido retorna 400**

```
Dado un formato no soportado "xml"
Cuando llamo GET /api/reportes/1/exportar?formato=xml
Entonces retorna HTTP 400 con ResponseStatusException(BAD_REQUEST)
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `ExportadorFactory.java` | Verificar | crearExportador() lanza ResponseStatusException para inválidos |
| `ExportadorFactoryTest.java` | Crear | 5 tests: pdf, excel, json, inválido, null |
| `ReporteServiceTest.java` | Modificar | Tests de exportar() con factory mock |
| `ReporteControllerTest.java` | Modificar | Tests MockMvc GET /{id}/exportar con los 3 formatos |

## Estrategia de Testing (Cobertura > 60%)

- **ExportadorFactoryTest**: 5 tests que cubren 100% de ExportadorFactory + 3 clases exportadoras — alta densidad para el quality gate.
- **ReporteServiceTest**: mockear ExportadorFactory.crearExportador() para retornar Exportador mock que retorna byte[].
- **ReporteControllerTest**: verificar Content-Type correcto para cada formato y HTTP 400 para formato inválido.

## Definición de Hecho (DoD)

- [ ] ExportadorFactoryTest con 5 tests verdes
- [ ] Tests de exportar() en ReporteServiceTest verdes
- [ ] Tests MockMvc GET /{id}/exportar con los 3 formatos + inválido verdes
- [ ] Cobertura JaCoCo > 60% en cl.duoc.cordillera.reportservice
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-168]: Crear ExportadorFactoryTest con 5 cases (pdf, excel, json, inválido, null/blank)

Crear ExportadorFactoryTest.java (sin Spring, instanciar new ExportadorFactory() directamente). El método real en ExportadorFactory es crearExportador(String formato), NO getExportador(). Para formatos inválidos lanza ResponseStatusException(HttpStatus.BAD_REQUEST), NO IllegalArgumentException. Implementar 5 tests: (1) crearExportador_PDF_retornaPdfExportador: factory.crearExportador("pdf") instanceof PdfExportador (también "PDF" en mayúsculas, por el toLowerCase interno); (2) crearExportador_EXCEL_retornaExcelExportador: factory.crearExportador("excel") instanceof ExcelExportador (también "xls", "xlsx"); (3) crearExportador_JSON_retornaJsonExportador: instanceof JsonExportador; (4) crearExportador_formatoInvalido_lanzaResponseStatusException: assertThrows(ResponseStatusException.class, () -> factory.crearExportador("xml")) y verificar que el status es HttpStatus.BAD_REQUEST; (5) crearExportador_formatoNull_lanzaResponseStatusException: assertThrows(ResponseStatusException.class, () -> factory.crearExportador(null)). IMPORTANTE: NO usar assertThrows(IllegalArgumentException.class) — la excepción real es ResponseStatusException (Indicador 8 EP3: Factory+Strategy).

### Sub-task 2 [CORD-169]: Agregar tests de exportar en ReporteServiceTest

En ReporteServiceTest.java, agregar tests del método exportar(Long id, String formato): el ReporteService.exportar() llama a buscarPorId(id) y luego a exportadorFactory.crearExportador(formato). IMPORTANTE: el método de la factory es crearExportador(), NO getExportador(). Implementar: (1) exportar_conReporteExistente_retornaBytes: mockear reporteRepository.findById(1L) para retornar Optional.of(reporte), mockear exportadorFactory.crearExportador("pdf") para retornar un Exportador mock que retorna new byte[]{1,2,3} en exportar(reporte), llamar reporteService.exportar(1L, "pdf") y verificar que el resultado es un byte[] con length=3; (2) exportar_conFormatoInvalido_propagaResponseStatusException: mockear exportadorFactory.crearExportador("xml") para lanzar ResponseStatusException(BAD_REQUEST), verificar assertThrows(ResponseStatusException.class) (Indicadores 2 y 4 EP3).

### Sub-task 3 [CORD-170]: Agregar tests MockMvc de GET /{id}/exportar con los 3 formatos + inválido

En ReporteControllerTest.java, agregar tests MockMvc. IMPORTANTE: la ruta real es GET /api/reportes/{id}/exportar?formato=X (query param con defaultValue="pdf"). Implementar: (1) exportar_formatoPDF_retorna200ConBytesYContentType: GET /api/reportes/1/exportar?formato=pdf, mockear reporteService.exportar(1L,"pdf") para retornar new byte[]{1,2,3}, verificar HTTP 200, Content-Type=application/pdf y header Content-Disposition contiene "attachment"; (2) exportar_formatoEXCEL_retorna200: GET /api/reportes/1/exportar?formato=excel, verificar Content-Type=application/vnd.ms-excel; (3) exportar_formatoJSON_retorna200: verificar Content-Type=application/json; (4) exportar_formatoInvalido_retorna400: mockear reporteService.exportar(1L,"xml") para lanzar ResponseStatusException(BAD_REQUEST), GET /api/reportes/1/exportar?formato=xml, verificar HTTP 400. El formato default es "pdf" si no se pasa el param (Indicador 3 EP3).
