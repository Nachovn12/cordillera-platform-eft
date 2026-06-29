# HU-CORD-119 — Configurar quality gate JaCoCo 60% en kpi-service

## Historia de Usuario

Como desarrollador backend del equipo Grupo Cordillera
quiero configurar el plugin JaCoCo en el kpi-service con una compuerta de calidad mínima del 60%
para garantizar que el código de KpiFactory, KpiService y KpiController tiene cobertura verificada antes del despliegue

## Contexto Técnico e Integración

El kpi-service concentra la lógica más pesada del sistema: KpiFactory (patrón Factory Method con 4 calculadoras) y KpiService (recálculo automático de valores). El jacoco-maven-plugin 0.8.13 ya está declarado en pom.xml. Falta agregar la ejecución check con regla mínima 60%.

La BD de pruebas es H2 en memoria — los tests NO requieren kpi_db MySQL levantado.

Stack: Spring Boot 4.0.6 · Java 21 · jacoco-maven-plugin 0.8.13 · H2 en scope test.

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Quality Gate como CI**: JaCoCo falla el build si la cobertura cae bajo el 60% — pipeline de Integración Continua (CASO sección 3).
- **H2 in-memory**: los tests de KpiRepository corren sobre BD aislada, no toca kpi_db de producción.
- **KpiFactory + Strategy**: los tests de KpiFactoryTest cubren 100% de la factory en pocos tests — alta densidad de cobertura por clase.

## Criterios de Aceptación (Gherkin)

**AC1: Reporte JaCoCo HTML generado con >= 60%**

```
Dado la configuración correcta con KpiFactoryTest, KpiServiceTest y KpiControllerTest
Cuando ejecuto mvn clean verify -pl kpi-service
Entonces se genera target/site/jacoco/index.html con >= 60% de cobertura
Y BUILD SUCCESS
```

**AC2: Quality gate rechaza cobertura insuficiente**

```
Dado que se comentan KpiFactoryTest y KpiServiceTest
Cuando ejecuto mvn verify -pl kpi-service
Entonces BUILD FAILURE con "Coverage check failed for project: kpi-service"
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `kpi-service/pom.xml` | Modificar | Agregar ejecución check con mínimo 0.60 en jacoco-maven-plugin |
| `src/test/resources/application-test.properties` | Crear | H2 en memoria: spring.datasource.url=jdbc:h2:mem:testdb |

## Estrategia de Testing (Cobertura > 60%)

- KpiFactoryTest (CORD-156) y KpiServiceTest (CORD-158) concentran la mayor densidad de líneas del kpi-service.
- Asegurar que application-test.properties usa H2 para que los tests @DataJpaTest no requieran MySQL.

## Definición de Hecho (DoD)

- [ ] mvn clean verify -pl kpi-service → BUILD SUCCESS con JaCoCo >= 60%
- [ ] target/site/jacoco/index.html generado
- [ ] H2 en application-test.properties confirmado
- [ ] Screenshot del reporte JaCoCo guardado en docs/jacoco-kpi-service.png
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-162]: Agregar bloque check-coverage al pom.xml de kpi-service

En kpi-service/pom.xml, dentro del plugin jacoco-maven-plugin, agregar ejecución con id="check" en fase "verify": configurar <rule><element>BUNDLE</element><limits><limit><counter>INSTRUCTION</counter><value>COVEREDRATIO</value><minimum>0.60</minimum></limit></limits></rule>. También asegurar que la dependencia H2 (com.h2database:h2) esté en scope test. Ejecutar mvn clean verify -pl kpi-service para confirmar que el quality gate funciona. Alineado con Indicador 4 EP3 (calidad asegurada en todos los componentes).

### Sub-task 2 [CORD-163]: Configurar post-verify para copiar reporte a docs/

En kpi-service/pom.xml, agregar el plugin maven-antrun-plugin con ejecución en fase "verify" que copie el reporte JaCoCo generado: de target/site/jacoco/index.html hacia ${project.basedir}/../docs/jacoco-kpi-service.html. Confirmar que tras mvn verify el archivo docs/jacoco-kpi-service.html existe y contiene el reporte de cobertura real del kpi-service. Este archivo será incluido en el informe PDF de pruebas (checklist EP3, Indicador 4).

### Sub-task 3 [CORD-164]: Validar mvn verify con cobertura menor al 60%

Crear un escenario de validación: comentar temporalmente los tests de KpiFactoryTest y KpiServiceTest, ejecutar mvn verify -pl kpi-service y verificar que el build FALLA con "Coverage check failed". Restaurar los tests completos y ejecutar nuevamente para confirmar BUILD SUCCESS con cobertura real >60%. Capturar screenshot del reporte JaCoCo mostrando el porcentaje final. Guardar en docs/jacoco-kpi-service.png para el informe de pruebas (Indicador 4 EP3: screenshots de cobertura).
