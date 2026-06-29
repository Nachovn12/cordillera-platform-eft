# HU-CORD-129 — Documento docs/persistencia.pdf con JPA y diagrama ER

## Historia de Usuario

Como equipo de Grupo Cordillera

necesitamos crear el documento docs/descripcion-persistencia.pdf que valide el uso de JPA, entidades y repositorios del sistema

para cumplir con el checklist de entrega del EP3 (ítem "descripcion-persistencia.pdf explica JPA con entidades, repositorios y ejemplos reales del proyecto").

## Contexto Técnico e Integración

El sistema implementa el patrón Database per Service: 3 bases de datos MySQL independientes (data_db, kpi_db, report_db), cada una gestionada por su respectivo microservicio mediante Spring Data JPA.

Entidades reales del proyecto:
- Dato.java (data-service → data_db.datos): campos id, sistemaOrigen, tipoDato, valor, fechaRegistro (@PrePersist), sucursalId
- Kpi.java (kpi-service → kpi_db.kpis): campos id, nombre, valor (BigDecimal), unidad, categoria, estado
- Reporte.java (report-service → report_db.reportes): campos id, titulo, tipo, area, valor, fechaGeneracion, anio, mes + constraint UNIQUE (area, tipo, anio, mes) para idempotencia

Repositorios reales del proyecto:
- DatoRepository extends JpaRepository<Dato, Long>: query methods findBySistemaOrigen(String) y findBySucursalId(Long)
- KpiRepository extends JpaRepository<Kpi, Long>: query method findByCategoria(String)
- ReporteRepository extends JpaRepository<Reporte, Long>: query methods findByArea(String) y existsByAreaAndTipoAndAnioAndMes(String, String, Integer, Integer)

Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- Repository Pattern: Spring Data JPA implementa automáticamente los repositorios a partir de interfaces, siguiendo el patrón Repository que abstrae la capa de persistencia del dominio (mencionado explícitamente en el CASO sección 1).
- Database per Service: cada microservicio tiene su propia base de datos — no hay foreign keys cross-service, evitando common coupling.
- @PrePersist en Dato.java: la fecha de registro se auto-asigna al momento de persistir, siguiendo principios DDD de lógica de dominio en la entidad.
- Constraint UNIQUE en Reporte: garantiza idempotencia en la generación de reportes (no se puede generar el mismo reporte dos veces para el mismo período).

## Criterios de Aceptación (Gherkin)

AC1: Documento PDF entregable

Dado que el equipo completa las 3 sub-tasks de esta historia
Cuando el ZIP de entrega EP3 se arma
Entonces existe docs/descripcion-persistencia.pdf
Y el PDF contiene al menos: definición de JPA, las 3 entidades con sus anotaciones reales,
los 3 repositorios con sus query methods reales, el diagrama ER de las 3 BDs,
y 1 ejemplo de código real (entidad + repositorio)

AC2: Diagrama ER preciso

Dado el diagrama ER creado en CORD-198
Cuando el evaluador lo revisa
Entonces se ven las 3 tablas (datos, kpis, reportes) con sus columnas y tipos
Y queda claro que las 3 BDs son independientes (no hay FK cross-service)

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| docs/persistencia.md | Crear | Contenido fuente en Markdown |
| docs/diagrama-er.png | Crear | Diagrama ER exportado de dbdiagram.io o draw.io |
| docs/diagrama-er.pdf | Crear | Mismo diagrama en PDF |
| docs/descripcion-persistencia.pdf | Crear | PDF final para entregar (generado desde el .md) |

## Definición de Hecho (DoD)

- [ ] docs/descripcion-persistencia.pdf existe con contenido real del proyecto
- [ ] El PDF incluye código real de las entidades (anotaciones JPA reales del proyecto)
- [ ] El PDF incluye el diagrama ER con las 3 tablas
- [ ] El nombre del archivo es exactamente descripcion-persistencia.pdf (como pide el checklist EP3)
- [ ] El PDF tiene mínimo 4 páginas con contenido real (no páginas en blanco)

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-197]: Redactar docs/persistencia.md con secciones de entidades y repositorios

Descripcion: Crear docs/persistencia.md con las siguientes secciones documentando la implementación real del proyecto: (1) Introducción (2 párrafos): explicar que la persistencia se implementa con JPA/Spring Data JPA con el patrón Database per Service — 3 schemas MySQL independientes sin FK cross-service; (2) Entidades JPA (1 sección por entidad): documentar Dato.java, Kpi.java y Reporte.java con sus campos reales y anotaciones (@Entity, @Table, @Id, @GeneratedValue(strategy=IDENTITY), @Column, @PrePersist en Dato, @Table(uniqueConstraints=...) en Reporte); incluir el snippet real del constructor de la entidad y su anotación @UniqueConstraint(columnNames={"area","tipo","anio","mes"}) en Reporte; (3) Repositorios Spring Data JPA: documentar las 3 interfaces con sus query methods reales (findBySistemaOrigen, findBySucursalId, findByCategoria, findByArea, existsByAreaAndTipoAndAnioAndMes) — explicar que Spring genera la implementación SQL automáticamente en base al nombre del método; (4) Código de ejemplo: pegar el código real de Dato.java completo (campos + @PrePersist) y DatoRepository.java completo como ejemplo integrado. Cubre el Indicador 3 EP3 y el checklist item "descripcion-persistencia.pdf explica JPA (entidades, repositorios) y/o SPs con código real".

### Sub-task 2 [CORD-198]: Crear diagrama ER de las 3 BDs (dbdiagram.io o draw.io)

Descripcion: Crear el diagrama entidad-relación de las 3 bases de datos del proyecto. Usar dbdiagram.io (herramienta online gratuita, no requiere instalación) o draw.io. En dbdiagram.io escribir el DBML: Table datos { id int [pk, increment]; sistemaOrigen varchar(50); tipoDato varchar(50); valor varchar(255); fechaRegistro datetime; sucursalId bigint } y equivalentes para kpis y reportes (incluir el UNIQUE en reportes: indexes { (area, tipo, anio, mes) [unique] }). En el diagrama indicar claramente con una nota o leyenda que las 3 tablas son de bases de datos separadas (data_db, kpi_db, report_db) y NO tienen foreign keys entre ellas — esto es el patrón Database per Service. Exportar como docs/diagrama-er.png (resolución alta) y docs/diagrama-er.pdf. El diagrama ER se incrusta en el descripcion-persistencia.pdf final (Indicadores 1 y 3 EP3: arquitectura + persistencia).

### Sub-task 3 [CORD-199]: Generar docs/descripcion-persistencia.pdf con Pandoc o equivalente

Descripcion: Convertir docs/persistencia.md a PDF e incrustar el diagrama ER. Opción A (recomendada si Pandoc está instalado): pandoc docs/persistencia.md docs/diagrama-er.png -o docs/descripcion-persistencia.pdf --pdf-engine=wkhtmltopdf --metadata title="Grupo Cordillera - Persistencia JPA". Opción B (sin herramientas extra): abrir docs/persistencia.md en VS Code, usar la extensión "Markdown PDF" para exportar a PDF, e insertar el diagrama ER manualmente en el documento. Opción C (online): usar markdowntopdf.com. El PDF final debe contener: portada con nombre del proyecto y integrantes del equipo, sección de introducción, las 3 entidades con su código real, los 3 repositorios con sus query methods, el diagrama ER de las 3 BDs, y el ejemplo de código. Verificar que el nombre del archivo sea exactamente docs/descripcion-persistencia.pdf (el checklist EP3 exige ese nombre exacto). Comprobar que el PDF se abre correctamente y tiene al menos 4 páginas con contenido real (Indicador 3 EP3: integración API REST y persistencia).
