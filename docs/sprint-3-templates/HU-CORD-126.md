# HU-CORD-126 — Configurar timeouts y manejo de errores en RestTemplate BFF

## Historia de Usuario

Como Arquitecto del equipo Grupo Cordillera
quiero configurar timeouts en el RestTemplate del BFF Gateway
para que los hilos del servidor no queden bloqueados indefinidamente si un microservicio no responde, implementando el patrón Fail-Fast

## Contexto Técnico e Integración

El RestTemplateConfig.java actual del bff-gateway solo retorna new RestTemplate() sin ningún timeout configurado — esto es lo que hay que corregir.

Estado actual del archivo real:

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate(); // SIN timeout — hay que corregir
}
```

Estado objetivo tras CORD-183:

```java
@Bean
public RestTemplate restTemplate() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(2000);  // 2 segundos
    factory.setReadTimeout(5000);     // 5 segundos
    return new RestTemplate(factory);
}
```

IMPORTANTE: SimpleClientHttpRequestFactory está incluida en spring-web — NO requiere agregar dependencias al pom.xml. El bean debe mantener el nombre restTemplate para no romper la inyección existente en DashboardService.

Para testear: instanciar directamente new RestTemplateConfig().restTemplate() y verificar los valores del factory con assertEquals — sin MockWebServer ni WireMock (no están en el pom.xml).

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- **Fail-Fast**: cortar la conexión rápido (2s connect, 5s read) y retornar status="Degradado" es mejor que dejar al usuario esperando 30s sin respuesta.
- **BFF como punto de control**: al centralizar el RestTemplate en un único Bean @Configuration, todos los clientes HTTP del BFF heredan los timeouts automáticamente.
- **Testabilidad directa**: SimpleClientHttpRequestFactory permite verificar los valores sin servidor mock — patrón de test simple del profesor.

## Criterios de Aceptación (Gherkin)

**AC1: Timeouts configurados correctamente**

```
Dado que RestTemplateConfig.java usa SimpleClientHttpRequestFactory
Cuando se instancia el Bean restTemplate
Entonces factory.getConnectTimeout() retorna 2000
Y factory.getReadTimeout() retorna 5000
```

**AC2: El bean compila sin dependencias adicionales**

```
Dado que el pom.xml del bff-gateway solo tiene spring-boot-starter-web
Cuando ejecuto mvn -pl bff-gateway compile
Entonces BUILD SUCCESS sin agregar ninguna dependencia nueva
```

**AC3: DashboardService sigue funcionando**

```
Dado que DashboardService inyecta RestTemplate por @Bean
Cuando el BFF arranca con docker-compose up
Entonces GET /api/dashboard/stats retorna 200 normalmente
```

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| `RestTemplateConfig.java` | Modificar | Agregar SimpleClientHttpRequestFactory con connectTimeout=2000 y readTimeout=5000 |
| `RestTemplateConfigTest.java` | Crear | 3 tests verificando valores del factory directamente, sin MockWebServer |
| `DashboardServiceTest.java` | Modificar | Agregar test de ResourceAccessException por timeout → status="Degradado" |

## Estrategia de Testing (Cobertura > 60%)

Test directo sobre el Bean: instanciar new RestTemplateConfig().restTemplate(), castear el factory a SimpleClientHttpRequestFactory y verificar los valores con assertEquals. Sin MockWebServer, sin WireMock, sin dependencias extras. Exactamente el patrón de tests simple del profesor.

## Definición de Hecho (DoD)

- [ ] mvn -pl bff-gateway compile BUILD SUCCESS tras la modificación
- [ ] RestTemplateConfigTest verde: assertEquals(2000, factory.getConnectTimeout()) y assertEquals(5000, factory.getReadTimeout())
- [ ] GET /api/dashboard/stats sigue respondiendo normalmente tras docker-compose up
- [ ] PR revisado por otro integrante

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-183]: Configurar connectTimeout y readTimeout en RestTemplateConfig

Modificar RestTemplateConfig.java (clase @Configuration en el bff-gateway). Estado actual: el @Bean restTemplate() retorna new RestTemplate() sin factory. Cambiar a: SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory(); factory.setConnectTimeout(2000); factory.setReadTimeout(5000); return new RestTemplate(factory). IMPORTANTE: (1) SimpleClientHttpRequestFactory está en spring-web — NO agregar dependencias al pom.xml; (2) el método @Bean debe seguir llamándose restTemplate() para que DashboardService lo reciba correctamente; (3) ejecutar mvn -pl bff-gateway compile para confirmar que compila; (4) levantar docker-compose y verificar que GET /api/dashboard/stats sigue respondiendo. Implementa el patrón Fail-Fast: si data-service no responde en 2s, el BFF corta y retorna status="Degradado" (Indicadores 2 y 7 EP3).

### Sub-task 2 [CORD-184]: Crear RestTemplateConfigTest verificando valores del factory

Crear RestTemplateConfigTest.java en src/test/java/.../config/. Este test NO necesita MockWebServer ni WireMock (no están en el pom.xml). Implementar 3 tests: (1) restTemplate_tieneConnectTimeoutConfigurado: instanciar RestTemplate rt = new RestTemplateConfig().restTemplate(), castear rt.getRequestFactory() a SimpleClientHttpRequestFactory factory, verificar assertEquals(2000, factory.getConnectTimeout()); (2) restTemplate_tieneReadTimeoutConfigurado: mismo setup, verificar assertEquals(5000, factory.getReadTimeout()); (3) restTemplate_factoryEsSimpleClientHttpRequestFactory: verificar assertInstanceOf(SimpleClientHttpRequestFactory.class, rt.getRequestFactory()). Estos 3 tests cubren el 100% de RestTemplateConfig.java y contribuyen al quality gate JaCoCo del bff-gateway (Indicador 4 EP3).

### Sub-task 3 [CORD-185]: Verificar manejo de ResourceAccessException por timeout en DashboardService

Con el timeout configurado en CORD-183, cuando un microservicio no responde en 5s, RestTemplate lanza ResourceAccessException. Verificar que DashboardService ya lo maneja: en DashboardService.fetchList(), el restTemplate.exchange() está dentro de un try/catch (Exception e) que retorna FetchResult.failure() — esto ya captura ResourceAccessException y el dashboard retorna status="Degradado" automáticamente. En DashboardServiceTest.java, agregar test getDashboard_conTimeoutEnKpiService_retornaStatusDegradado: mockear restTemplate.exchange() para lanzar new ResourceAccessException("Connection timed out"), verificar que getDashboard() retorna DashboardResponse con status="Degradado" y lista de alertas con severidad="Critica". Este test confirma que el Fail-Fast del timeout activa el mecanismo de resiliencia del BFF (Indicadores 4 y 7 EP3).
