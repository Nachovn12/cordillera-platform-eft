# HU-CORD-125 — Configurar quality gate JaCoCo 60% en bff-gateway

## Historia de Usuario

Como desarrollador backend del equipo Grupo Cordillera

quiero configurar JaCoCo en el bff-gateway con una compuerta de calidad mínima del 60%

para garantizar que el código del único punto de entrada al sistema (DashboardService, AuthService, ConfiguracionController y los controllers proxy) tenga cobertura verificada antes del despliegue.

## Contexto Técnico e Integración

El bff-gateway es el servicio más crítico del sistema: todo el tráfico del frontend pasa por él. Concentra lógica de:
- Agregación de datos en DashboardService (llama a data-service + kpi-service + report-service con RestTemplate)
- Autenticación en memoria en AuthService (ConcurrentHashMap con usuarios, sin Spring Security)
- Proxy transparente en DatosProxyController, KpisProxyController, ReportesProxyController
- Configuración en ConfiguracionController

No tiene JPA propio (no persiste en BD), por lo que la cobertura se logra con tests de los Services y Controllers.

Stack: Spring Boot 4.0.6 · Java 21 · RestTemplate con SimpleClientHttpRequestFactory · jacoco-maven-plugin 0.8.13 (ya en pom.xml).

Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- BFF / API Gateway: el bff-gateway es el patrón más importante del sistema — actúa como fachada que oculta la topología interna al frontend. Sus tests validan que el contrato expuesto al frontend es correcto.
- Circuit Breaker: el DashboardService maneja fallos parciales — si kpi-service cae, retorna status="Degradado". Testeable con mocks sin levantar microservicios.
- AuthService (in-memory store): los tests de AuthService validan el flujo login, creación y eliminación de usuarios sin BD real.
- Quality Gate: garantiza que el BFF, al ser crítico, tiene la máxima verificación antes de cada despliegue.

## Criterios de Aceptación (Gherkin)

AC1: Reporte JaCoCo generado con cobertura ≥ 60%

Dado que el bff-gateway tiene tests para DashboardService, AuthService y al menos un Controller
Cuando ejecuto mvn clean verify -pl bff-gateway
Entonces BUILD SUCCESS
Y se genera target/site/jacoco/index.html con cobertura ≥ 60% en cl.duoc.cordillera.bffgateway

AC2: Quality gate rechaza cobertura insuficiente

Dado que se eliminan o comentan los tests del DashboardService y AuthService
Cuando ejecuto mvn verify -pl bff-gateway
Entonces BUILD FAILURE con "Coverage check failed for project: bff-gateway"

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| bff-gateway/pom.xml | Modificar | Agregar ejecución check con mínimo 0.60 en jacoco-maven-plugin |
| src/test/java/.../DashboardServiceTest.java | Crear | Test con RestTemplate mockeado — estado Operativo y Degradado |
| src/test/java/.../AuthServiceTest.java | Crear | Test de login, creación y eliminación de usuarios |
| src/test/java/.../DashboardControllerTest.java | Crear | @WebMvcTest para /api/dashboard/stats y /api/dashboard/sucursal/{id} |

## Estrategia de Testing (Cobertura > 60%)

Prioridad de tests para alcanzar el 60%:
1. DashboardServiceTest — cubre la lógica de aggregation más densa del BFF
2. AuthServiceTest — cubre AuthService completo (login exitoso, credenciales inválidas, crear/eliminar usuario)
3. DashboardControllerTest — cubre los endpoints más usados por el frontend

## Definición de Hecho (DoD)

- [ ] mvn clean verify -pl bff-gateway → BUILD SUCCESS con JaCoCo ≥ 60%
- [ ] target/site/jacoco/index.html generado
- [ ] Screenshot del reporte guardado en docs/jacoco-bff-gateway.png
- [ ] PR revisado por otro integrante del equipo

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-180]: Agregar bloque check-coverage al pom.xml de bff-gateway

Descripcion: En bff-gateway/pom.xml, dentro del plugin jacoco-maven-plugin (ya presente), agregar una ejecución con <id>check</id> en fase verify: configurar <rule><element>BUNDLE</element><limits><limit><counter>LINE</counter><value>COVEREDRATIO</value><minimum>0.60</minimum></limit></limits></rule>. El bff-gateway no tiene repositorios JPA propios, por lo que no necesita H2 — la cobertura se logrará con tests de Services (mockeando RestTemplate) y Controllers (@WebMvcTest). Ejecutar mvn clean verify -pl bff-gateway para confirmar que el quality gate funciona y que los tests existentes (o los nuevos del sprint) superan el 60%. Alineado con Indicador 4 EP3 (calidad asegurada en TODOS los componentes, incluido el BFF).

### Sub-task 2 [CORD-181]: Configurar post-verify para copiar reporte a docs/

Descripcion: En bff-gateway/pom.xml, agregar el plugin maven-antrun-plugin con ejecución en fase verify para copiar target/site/jacoco/index.html hacia ${project.basedir}/../docs/jacoco-bff-gateway.html. Tras ejecutar mvn verify del bff-gateway, verificar que docs/jacoco-bff-gateway.html existe y muestra métricas reales del BFF: cobertura de DashboardService, AuthService, ConfiguracionController, etc. Este es el 4to y último reporte de cobertura necesario (junto con jacoco-data-service.html, jacoco-kpi-service.html y jacoco-report-service.html) para conformar la sección de reportes del informe-pruebas-unitarias.pdf que exige el checklist EP3 (Indicador 4: "Informe de pruebas con gráficos o reporte de cobertura ≥ 60%").

### Sub-task 3 [CORD-182]: Validar mvn verify con cobertura menor al 60%

Descripcion: Prueba de la compuerta del BFF: (1) Reducir temporalmente los tests del bff-gateway (comentar DashboardServiceTest y AuthServiceTest), ejecutar mvn verify -pl bff-gateway y verificar que el BUILD FALLA con "Coverage check failed for project: bff-gateway". (2) Restaurar todos los tests (DashboardServiceTest, AuthServiceTest, DashboardControllerTest), ejecutar nuevamente y confirmar BUILD SUCCESS con cobertura real ≥ 60% en el paquete cl.duoc.cordillera.bffgateway. (3) Capturar screenshot del reporte JaCoCo mostrando el porcentaje final por clase (DashboardService, AuthService, etc.). Guardar en docs/jacoco-bff-gateway.png. Este ciclo demuestra la Integración Continua del CASO sección 3 aplicada al componente más crítico de la arquitectura (el BFF, único punto de entrada) — Indicador 4 EP3.
