# HU-CORD-130 — Documento docs/informe-pruebas-unitarias.pdf con cobertura y CA-Test

## Historia de Usuario

Como equipo de Grupo Cordillera

necesitamos crear el documento docs/informe-pruebas-unitarias.pdf que evidencie la cobertura JaCoCo ≥ 60% de los 4 microservicios Java

para cumplir con el checklist de entrega del EP3 (ítem "informe de pruebas unitarias incluye gráfico o reporte de cobertura ≥ 60%") y para presentar durante la defensa oral (Indicador 4 y 8).

## Contexto Técnico e Integración

Los 4 microservicios Java del sistema generan reportes JaCoCo individuales tras mvn clean verify:
- data-service → docs/jacoco-data-service.html (DatoRepositoryTest, DatoServiceTest, DatoControllerTest)
- kpi-service → docs/jacoco-kpi-service.html (KpiFactoryTest, KpiServiceTest, KpiControllerTest)
- report-service → docs/jacoco-report-service.html (ExportadorFactoryTest, ReporteServiceTest, ReporteControllerTest)
- bff-gateway → docs/jacoco-bff-gateway.html (DashboardServiceTest, AuthServiceTest, DashboardControllerTest)

Este informe consolida los 4 reportes en un único PDF con tabla de cobertura, screenshots de JaCoCo, tabla CA-Test (Criterio de Aceptación → Test que lo cubre) y sección de patrones de diseño.

Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- Repository Pattern → desacopla la capa de datos; los tests de repositorios usan H2 en memoria en vez de MySQL real — demostrar esta ventaja en la defensa.
- Factory Method (KpiFactory, ExportadorFactory) → cada calculadora/exportador es aislable en unit test sin Spring — alta cobertura con tests pequeños.
- Strategy (VentasCalculator, InventarioCalculator, etc.) → cada estrategia es una clase simple testeable de forma independiente.
- Circuit Breaker → el fallback es una rama del código → mockear la excepción en tests cubre esa rama → mayor cobertura.

## Criterios de Aceptación (Gherkin)

AC1: Informe PDF entregable

Dado que los 4 microservicios tienen JaCoCo configurado y los tests pasan
Cuando el equipo genera docs/informe-pruebas-unitarias.pdf
Entonces el PDF contiene: tabla CA-Test, screenshots de JaCoCo de los 4 MS,
tabla de cobertura consolidada con porcentajes reales, y sección de patrones de diseño

AC2: Evidencia visual de cobertura

Dado los 4 reportes JaCoCo HTML generados
Cuando el evaluador revisa el PDF
Entonces puede ver screenshots del index.html de JaCoCo de cada microservicio
Y cada screenshot muestra un porcentaje de cobertura ≥ 60%

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| tools/build_test_report.py | Crear | Script Python para extraer métricas de los HTML JaCoCo |
| docs/resumen-cobertura.md | Crear (auto) | Generado por el script con tabla consolidada |
| docs/informe-pruebas-unitarias.md | Crear | Fuente del informe en Markdown |
| docs/informe-pruebas-unitarias.pdf | Crear | PDF final para entregar |

## Definición de Hecho (DoD)

- [ ] docs/informe-pruebas-unitarias.pdf existe con contenido real
- [ ] El PDF incluye tabla CA-Test completa (todos los tests del Sprint 3)
- [ ] El PDF incluye screenshots de JaCoCo de los 4 MS con porcentaje visible
- [ ] El PDF incluye tabla de cobertura consolidada (4 servicios, % real)
- [ ] El PDF incluye sección de patrones de diseño con justificación de mantenibilidad
- [ ] El nombre es exactamente informe-pruebas-unitarias.pdf (checklist EP3)

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-200]: Crear tools/build_test_report.py para consolidar HTML de JaCoCo

Descripcion: Crear el script tools/build_test_report.py en Python 3 (sin dependencias externas, solo re y os). El script hace lo siguiente: (1) lee los 4 archivos HTML de JaCoCo desde docs/ (jacoco-data-service.html, jacoco-kpi-service.html, jacoco-report-service.html, jacoco-bff-gateway.html); (2) extrae las métricas de cobertura con regex buscando el patrón de texto de JaCoCo (ej: "Lines: X of Y (Z%)") en cada HTML; (3) genera una tabla Markdown con los 4 servicios, sus líneas cubiertas, total de líneas y porcentaje; (4) escribe docs/resumen-cobertura.md. Si algún HTML no existe (porque el servicio aún no corrió verify), el script genera la fila con "PENDIENTE". Ejecutar con python tools/build_test_report.py desde la raíz del proyecto. Este script automatiza la consolidación del informe y es evidencia de la "Integración Continua" del CASO sección 3 (Indicador 4 EP3: calidad asegurada en todos los componentes).

### Sub-task 2 [CORD-201]: Redactar docs/informe-pruebas-unitarias.md con tabla CA-Test

Descripcion: Crear docs/informe-pruebas-unitarias.md con las secciones: (1) Resumen ejecutivo (1 párrafo): cobertura promedio de los 4 microservicios, herramientas usadas (JaCoCo 0.8.13 + Mockito + @WebMvcTest + @DataJpaTest + H2 en memoria); (2) Tabla CA-Test — mapeando cada Criterio de Aceptación de las historias del Sprint 3 al test Java que lo cubre, por ejemplo: AC1 CORD-113 → DatoControllerTest.crear_conPayloadValido_retorna201(), AC2 CORD-113 → DatoControllerTest.crear_conSucursalIdNulo_retorna400(), AC2 CORD-120 → ReporteServiceTest.generarReporte_conKpiServiceCaido_retornaReporteDegradado(), etc. (mínimo 12 filas cubriendo los ACs de cada HU del sprint); (3) Tabla de cobertura consolidada: incluir datos del resumen-cobertura.md generado por el script de CORD-200; (4) Patrones de diseño aplicados: para cada patrón (Repository, Factory Method, Strategy, Circuit Breaker, Observer) explicar cómo mejora la calidad y la mantenibilidad: ej. "Repository Pattern → los tests usan H2 en memoria (no MySQL real) → los tests son independientes del entorno → fácil ejecutar en CI". Esta conexión patrón→ventaja en tests→ventaja en mantenibilidad es exactamente lo que evalúa el Indicador 8 EP3 en la defensa oral.

### Sub-task 3 [CORD-202]: Generar docs/informe-pruebas-unitarias.pdf

Descripcion: Convertir docs/informe-pruebas-unitarias.md a PDF e incrustar los screenshots de JaCoCo. Pasos: (1) Con todos los microservicios habiendo ejecutado mvn verify, tomar screenshots de los reportes JaCoCo de cada servicio (abrir target/site/jacoco/index.html en el navegador y hacer captura de pantalla con el porcentaje visible). Guardar las 4 imágenes como docs/jacoco-data-service.png, docs/jacoco-kpi-service.png, docs/jacoco-report-service.png, docs/jacoco-bff-gateway.png. (2) En el informe-pruebas-unitarias.md, incrustar las imágenes con ![JaCoCo data-service](jacoco-data-service.png). (3) Convertir a PDF con Pandoc (pandoc docs/informe-pruebas-unitarias.md -o docs/informe-pruebas-unitarias.pdf) o con la extensión Markdown PDF de VS Code. (4) Verificar que el PDF final: tiene portada con nombre del proyecto y equipo, tabla CA-Test legible, los 4 screenshots de JaCoCo visibles con porcentajes, tabla de cobertura consolidada, y sección de patrones de diseño. El nombre del archivo debe ser exactamente informe-pruebas-unitarias.pdf (checklist EP3). Este documento es el artefacto central del Indicador 4 (encargo grupal) y el material de apoyo para el Indicador 8 (defensa oral) del EP3.
