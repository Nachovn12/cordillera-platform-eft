# HU-CORD-123 — Configurar quality gate JaCoCo 60% en report-service

## Historia de Usuario

Como desarrollador backend del equipo Grupo Cordillera

quiero configurar el plugin JaCoCo en el report-service con una compuerta de calidad (quality gate) mínima del 60%

para garantizar que el código que maneja la generación de reportes ejecutivos (PDF/Excel/JSON) y la integración con kpi-service tenga cobertura verificada objetivamente antes del despliegue.

## Contexto Técnico e Integración

El report-service es el microservicio más complejo del sistema: integra el patrón Factory Method (ExportadorFactory → PdfExportador / ExcelExportador / JsonExportador), el patrón Circuit Breaker (Resilience4j hacia kpi-service) y la restricción de idempotencia en la generación de reportes (constraint UNIQUE en area + tipo + anio + mes en la tabla reportes de report_db).

El quality gate JaCoCo asegura que toda esa lógica crítica esté cubierta. La BD de pruebas es H2 en memoria (no toca report_db de producción en MySQL/XAMPP).

Stack: Spring Boot 4.0.6 · Java 21 · resilience4j-spring-boot4 2.4.0 · jacoco-maven-plugin 0.8.13 (ya declarado en pom.xml) · H2 en scope test.

Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- Factory Method (ExportadorFactory): cada exportador (PDF/Excel/JSON) es una estrategia concreta → aislable en test unitario sin Spring.
- Circuit Breaker (Resilience4j): el fallback de KpiClienteService retorna lista vacía → testeable con mock sin levantar kpi-service real.
- Repository Pattern (ReporteRepository): constraint UNIQUE (area, tipo, anio, mes) → verificable con @DataJpaTest y H2.
- Quality Gate como CI: JaCoCo actúa como compuerta automática que fallaría el build antes de mergear a main si la cobertura cae.

## Criterios de Aceptación (Gherkin)

AC1: Reporte HTML generado con cobertura ≥ 60%

Dado que el report-service tiene tests para ReporteService, ExportadorFactory y ReporteRepository
Cuando ejecuto mvn clean verify -pl report-service
Entonces el build termina con BUILD SUCCESS
Y se genera target/site/jacoco/index.html con cobertura de líneas ≥ 60% en cl.duoc.cordillera.reportservice

AC2: Quality gate rechaza cobertura insuficiente

Dado que se comentan los tests de ExportadorFactoryTest y ReporteServiceTest
Cuando ejecuto mvn verify -pl report-service
Entonces el build termina con BUILD FAILURE
Y el mensaje indica "Coverage check failed for project: report-service"

AC3: BD de pruebas usa H2 (no toca report_db MySQL)

Dado que application-test.properties tiene spring.datasource.url=jdbc:h2:mem:testdb
Cuando los tests @DataJpaTest se ejecutan
Entonces las operaciones de base de datos ocurren en H2 en memoria
Y report_db en MySQL/XAMPP no es afectada

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| report-service/pom.xml | Verificar/modificar | Confirmar jacoco-maven-plugin con ejecuciones prepare-agent, report y check |
| src/test/resources/application-test.properties | Crear | H2 en memoria, spring.jpa.hibernate.ddl-auto=create-drop |
| src/test/java/.../ReporteRepositoryTest.java | Crear | @DataJpaTest sobre Reporte entity y constraint UNIQUE |
| src/test/java/.../ExportadorFactoryTest.java | Crear | Test de las 3 implementaciones + caso inválido + null |
| src/test/java/.../ReporteServiceTest.java | Crear | Mockito sobre ReporteRepository + KpiClienteService |

## Estrategia de Testing (Cobertura > 60%)

Los tres archivos de test más prioritarios para superar el 60%:
1. ExportadorFactoryTest — cubre ExportadorFactory + 3 clases exportadoras (alta densidad de líneas)
2. ReporteServiceTest — cubre la lógica de negocio con circuit breaker (fallback) y constraint de idempotencia
3. ReporteRepositoryTest — cubre el acceso a datos con H2

## Definición de Hecho (DoD)

- [ ] mvn clean verify -pl report-service → BUILD SUCCESS con JaCoCo ≥ 60%
- [ ] target/site/jacoco/index.html generado y abre correctamente en navegador
- [ ] H2 en application-test.properties confirmado (los tests NO requieren MySQL levantado)
- [ ] Screenshot del reporte JaCoCo guardado en docs/jacoco-report-service.png para el informe PDF
- [ ] PR revisado por otro integrante del equipo

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-174]: Agregar bloque check-coverage al pom.xml de report-service

Descripcion: En report-service/pom.xml, dentro del plugin jacoco-maven-plugin (ya presente), agregar una tercera ejecución con <id>check</id> en la fase verify: configurar <rule><element>BUNDLE</element><limits><limit><counter>LINE</counter><value>COVEREDRATIO</value><minimum>0.60</minimum></limit></limits></rule>. También verificar que la dependencia com.h2database:h2 esté en scope test. Crear src/test/resources/application-test.properties con: spring.datasource.url=jdbc:h2:mem:testdb, spring.datasource.driver-class-name=org.h2.Driver, spring.jpa.hibernate.ddl-auto=create-drop. Ejecutar mvn clean verify -pl report-service para confirmar que el quality gate actúa como compuerta. Alineado con Indicador 4 EP3 (calidad asegurada en todos los componentes, JaCoCo mencionado en CASO sección 3) y la arquitectura de microservicios aislados.

### Sub-task 2 [CORD-175]: Configurar post-verify para copiar reporte a docs/

Descripcion: En report-service/pom.xml, agregar el plugin maven-antrun-plugin con ejecución en fase verify que copie el reporte JaCoCo hacia la carpeta docs/ del proyecto raíz: <copy file="${project.build.directory}/site/jacoco/index.html" tofile="${project.basedir}/../docs/jacoco-report-service.html"/>. También copiar el directorio completo de recursos CSS/JS de JaCoCo para que el HTML sea navegable. Tras ejecutar mvn verify, confirmar que docs/jacoco-report-service.html existe y muestra las métricas reales del report-service (ExportadorFactory, ReporteService, ReporteController, KpiClienteService). Este archivo es el 3er reporte de los 4 necesarios (junto con data-service, kpi-service, bff-gateway) para el informe-pruebas-unitarias.pdf (Indicador 4 EP3, checklist de entrega).

### Sub-task 3 [CORD-176]: Validar mvn verify con cobertura menor al 60%

Descripcion: Prueba de la compuerta de calidad del report-service: (1) Comentar temporalmente los tests de ExportadorFactoryTest.java y ReporteServiceTest.java (los que cubren la mayor parte del código), ejecutar mvn verify -pl report-service y verificar que el BUILD FALLA con el mensaje "Coverage check failed for project: report-service: instructions covered ratio is X, but expected minimum is 0.60". (2) Restaurar todos los tests, ejecutar nuevamente y confirmar BUILD SUCCESS. (3) Capturar screenshot del reporte JaCoCo en target/site/jacoco/index.html mostrando el porcentaje de cobertura ≥ 60% en el paquete cl.duoc.cordillera.reportservice. (4) Guardar screenshot en docs/jacoco-report-service.png. Este ciclo de validación demuestra la Integración Continua mencionada en el CASO sección 3 (pipeline CI que valida cobertura automáticamente en cada actualización del código) — Indicador 4 EP3.
