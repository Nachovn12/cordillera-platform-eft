# HU-CORD-116 — Configurar quality gate JaCoCo 60% en data-service

## Historia de Usuario

Como desarrollador backend del equipo Grupo Cordillera
quiero configurar el plugin JaCoCo en el data-service con una compuerta de calidad mínima del 60%
para verificar objetivamente la cobertura requerida por la rúbrica EP3 y asegurar que los tests del data-service son válidos antes del despliegue

## Contexto Técnico e Integración

El data-service gestiona los datos operacionales de las sucursales (ventas, stock, pedidos) en MySQL data_db. El jacoco-maven-plugin 0.8.13 ya está declarado en el pom.xml del data-service con ejecuciones prepare-agent y report. Falta agregar la ejecución check con la regla mínima del 60%.

La BD de pruebas es H2 en memoria — los tests @DataJpaTest NO requieren MySQL levantado.

Stack: Spring Boot 4.0.6 · Java 21 · jacoco-maven-plugin 0.8.13 · H2 en scope test.

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Quality Gate como CI**: JaCoCo actúa como compuerta automática que falla el build si la cobertura cae bajo el 60% — evidencia de Integración Continua (CASO sección 3).
- **H2 in-memory**: los tests corren sobre BD aislada, sin tocar data_db de producción — el patrón Repository desacopla los tests del entorno real.
- **Indicador 4 EP3**: la cobertura mínima del 60% es requisito explícito de la rúbrica.

## Criterios de Aceptación (Gherkin)

**AC1: Reporte HTML generado exitosamente**

```
Dado el data-service con tests básicos (DatoRepositoryTest, DatoServiceTest, DatoControllerTest)
Cuando ejecuto mvn clean verify -pl data-service
Entonces el plugin genera target/site/jacoco/index.html con cobertura >= 60%
Y el build termina con BUILD SUCCESS
```

**AC2: Quality gate rechaza cobertura insuficiente**

```
Dado que se comentan los tests del data-service
Cuando ejecuto mvn verify -pl data-service
Entonces el build termina con BUILD FAILURE
Y el mensaje indica "Coverage check failed for project: data-service"
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `data-service/pom.xml` | Modificar | Agregar ejecución check con mínimo 0.60 en jacoco-maven-plugin |
| `src/test/resources/application-test.properties` | Crear | H2 en memoria: spring.datasource.url=jdbc:h2:mem:testdb |

## Estrategia de Testing (Cobertura > 60%)

- Los tests de CORD-113, 114 y 115 (DatoRepositoryTest, DatoServiceTest, DatoControllerTest) cubren las clases principales del data-service.
- Asegurar que application-test.properties existe en src/test/resources/ con la configuración H2.
- Ejecutar mvn clean verify -pl data-service para validar el quality gate.

## Definición de Hecho (DoD)

- [ ] mvn clean verify -pl data-service → BUILD SUCCESS con JaCoCo >= 60%
- [ ] target/site/jacoco/index.html generado y abre en navegador
- [ ] H2 en application-test.properties confirmado (tests NO requieren MySQL levantado)
- [ ] Screenshot del reporte JaCoCo guardado en docs/jacoco-data-service.png para el informe PDF
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-143]: Agregar bloque check-coverage al pom.xml del data-service

Verificar que el plugin jacoco-maven-plugin esté correctamente configurado en data-service/pom.xml con: (1) ejecución prepare-agent en fase initialize para instrumentar el código; (2) ejecución report en fase verify para generar HTML en target/site/jacoco/; (3) ejecución check con regla: <rule><element>BUNDLE</element><limits><limit><counter>INSTRUCTION</counter><value>COVEREDRATIO</value><minimum>0.60</minimum></limit></limits></rule>; (4) dependencia H2 con scope=test. Confirmar con mvn clean verify -pl data-service que se genera target/site/jacoco/index.html. Alineado a Indicador 4 EP3 (pruebas con cobertura >=60%).

### Sub-task 2 [CORD-154]: Agregar maven-antrun-plugin para copiar reporte a docs/

En pom.xml de data-service, agregar el plugin maven-antrun-plugin dentro de <build><plugins>. Configurar una ejecución atada a la fase verify: <copy file="${project.build.directory}/site/jacoco/index.html" tofile="${project.basedir}/../docs/jacoco-data-service.html"/>. Esto asegura que el reporte de cobertura quede en la carpeta docs/ del repositorio principal, cumpliendo el requisito del informe-pruebas-unitarias.pdf (Indicador 4 EP3 y checklist de entrega).

### Sub-task 3 [CORD-155]: Validar que mvn verify falla con cobertura menor al 60%

Prueba de la compuerta: comentar temporalmente los tests de DatoServiceTest y DatoControllerTest, ejecutar mvn verify -pl data-service y confirmar que el build FALLA con "Coverage check failed". Restaurar los tests completos y ejecutar nuevamente para confirmar BUILD SUCCESS. Capturar screenshot del reporte JaCoCo mostrando porcentaje >= 60% en cl.duoc.cordillera.dataservice. Guardar en docs/jacoco-data-service.png para el informe PDF (Indicador 4 EP3: screenshots de cobertura).
