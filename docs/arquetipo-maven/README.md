# Arquetipo Maven Base - Cordillera Platform

Documento tecnico correspondiente al Parcial 3 de **Desarrollo Full Stack III (DSY1106)**.

## Objetivo

Este documento describe el arquetipo Maven utilizado como base para los servicios backend de **Cordillera Platform**.

El arquetipo se construyo siguiendo el procedimiento entregado por el profesor, basado en la creacion de un proyecto Spring Boot desde Initializr, su validacion con Maven y posterior generacion mediante:

```powershell
mvn archetype:create-from-project
```

La finalidad fue contar con una estructura repetible para crear microservicios Java con Maven, manteniendo coherencia tecnica entre los componentes del proyecto.

## Contexto del arquetipo entregado por el profesor

El procedimiento base consistia en:

- Crear un proyecto Spring Boot normal desde Initializr.
- Agregar las dependencias necesarias.
- Configurar Java 25 y Maven en el entorno local.
- Validar que el proyecto compile correctamente.
- Ejecutar el proyecto.
- Generar un arquetipo Maven desde el proyecto base.
- Instalar el arquetipo en el repositorio local de Maven.
- Usar el arquetipo para crear nuevos microservicios.

## Configuracion de entorno Java y Maven

El profesor indico que Java Extension Pack podia generar problemas con Maven desde terminal, por lo que era necesario asegurar las variables de entorno.

Comandos usados en PowerShell:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="C:\Program Files\Java\jdk-21\bin;$env:Path"
echo $env:JAVA_HOME
```

Luego se validaba el proyecto con:

```powershell
mvn clean install
```

Y se ejecutaba con:

```powershell
mvn spring-boot:run
```

## Configuracion del perfil Maven

Para evitar problemas con Maven, se configuro el archivo `settings.xml` del usuario.

Comandos:

```powershell
mkdir $env:USERPROFILE\.m2 -Force
notepad $env:USERPROFILE\.m2\settings.xml
```

Contenido base:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">

</settings>
```

## Generacion del arquetipo desde el proyecto base

Desde la raiz del proyecto base se ejecuto:

```powershell
mvn archetype:create-from-project
```

Cuando el proceso finaliza correctamente, Maven genera la siguiente carpeta:

```text
target/generated-sources/archetype
```

Luego se ingresa a esa carpeta:

```powershell
cd target/generated-sources/archetype
```

Y se instala el arquetipo en el repositorio local:

```powershell
mvn clean install
```

## Creacion de un nuevo microservicio desde el arquetipo

Una vez instalado el arquetipo, se puede crear un nuevo microservicio usando:

```powershell
mvn --% archetype:generate -DarchetypeCatalog=local -DarchetypeGroupId=cl -DarchetypeArtifactId=duoc-archetype -DarchetypeVersion=0.0.1-SNAPSHOT -DgroupId=cl.duoc -DartifactId=ms-clientes -Dversion=1.0.0 -Dpackage=cl.duoc.clientes -DinteractiveMode=false
```

Para revisar arquetipos instalados:

```powershell
notepad $env:USERPROFILE\.m2\repository\archetype-catalog.xml
```

## Aplicacion en Cordillera Platform

En Cordillera Platform se aplico este enfoque para mantener una estructura homogenea entre los servicios backend.

Servicios del proyecto:

| Servicio | Rol | Puerto | Base de datos |
|---|---|---:|---|
| bff-gateway | Backend For Frontend | 8081 | No aplica |
| data-service | Gestion de datos operacionales | 8083 | data_db |
| kpi-service | Gestion y calculo de KPIs | 8084 | kpi_db |
| report-service | Gestion y exportacion de reportes | 8085 | report_db |

## Estructura base aplicada

Cada servicio backend mantiene una estructura Maven independiente:

```text
nombre-servicio/
|-- pom.xml
|-- Dockerfile
|-- .dockerignore
`-- src/
    |-- main/
    |   |-- java/
    |   |   `-- cl/duoc/cordillera/{servicio}/
    |   |       |-- controller/
    |   |       |-- service/
    |   |       |-- repository/
    |   |       |-- model/
    |   |       |-- dto/
    |   |       `-- config/
    |   `-- resources/
    |       `-- application.properties
    `-- test/
        `-- java/
```

## Separacion por capas

La estructura aplicada sigue una arquitectura por capas:

```text
Controller -> Service -> Repository -> Model
```

### Controller

Expone endpoints REST y recibe solicitudes HTTP.

Ejemplos del proyecto:

- `DatoController`
- `KpiController`
- `ReporteController`
- `DashboardController`

### Service

Contiene la logica de negocio del servicio.

Ejemplos:

- `DatoService`
- `KpiService`
- `ReporteService`
- `DashboardService`

### Repository

Gestiona persistencia usando Spring Data JPA.

Ejemplos:

- `DatoRepository`
- `KpiRepository`
- `ReporteRepository`

Ejemplo:

```java
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByArea(String area);
}
```

### Model

Define entidades JPA persistidas en MySQL.

Ejemplos:

- `Dato`
- `Kpi`
- `Reporte`

### DTO

Permite transportar informacion entre capas y servicios.

Ejemplos:

- `DashboardResponse`
- `KpiResumenDto`

### Config

Agrupa configuracion tecnica del servicio.

Ejemplos:

- Configuracion CORS.
- Configuracion de `RestTemplate`.
- Configuracion de carga inicial de datos.
- Configuracion de integracion entre microservicios.

## pom.xml independiente por servicio

Cada microservicio tiene su propio `pom.xml`.

Esto permite:

- Compilar servicios por separado.
- Gestionar dependencias por componente.
- Mantener independencia tecnica.
- Escalar servicios sin afectar todo el sistema.
- Cumplir el enfoque de microservicios.

Comandos de ejemplo:

```powershell
cd data-service
mvn clean package
```

```powershell
cd kpi-service
mvn clean test
```

```powershell
cd report-service
mvn clean verify
```

## Dependencias comunes

Dependencias utilizadas en los servicios backend:

```text
spring-boot-starter-web
spring-boot-starter-data-jpa
mysql-connector-j
spring-boot-starter-test
mockito
jacoco-maven-plugin
```

Dependencia usada cuando un servicio consume otro servicio interno:

```text
resilience4j-spring-boot3
```

## Database-per-service

Cordillera Platform aplica el patron database-per-service.

Cada microservicio mantiene su propia base de datos:

| Servicio | Base de datos |
|---|---|
| data-service | data_db |
| kpi-service | kpi_db |
| report-service | report_db |

Ventajas:

- Reduce acoplamiento.
- Facilita mantenimiento.
- Mejora escalabilidad.
- Permite independencia entre servicios.
- Evita que un servicio dependa directamente de las tablas internas de otro.

## Configuracion con application.properties

Cada servicio usa `application.properties` con variables de entorno para funcionar tanto en local como en Docker.

Ejemplo:

```properties
spring.datasource.url=${REPORT_DB_URL:jdbc:mysql://localhost:3306/report_db?createDatabaseIfNotExist=true}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:root}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
server.port=8085
```

En Docker, estas variables se inyectan desde `docker-compose.yml`.

## Dockerfile por microservicio

Cada servicio backend tiene su propio `Dockerfile` multi-stage.

Ejemplo base:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Ventajas:

- Compila el servicio dentro de Docker.
- No depende de Maven instalado en el PC de presentacion.
- Genera una imagen final mas limpia.
- Facilita levantar toda la arquitectura con Docker Compose.

## Docker Compose

La arquitectura completa se levanta desde la raiz con:

```powershell
docker compose up -d --build
```

Servicios definidos:

```text
mysql
data-service
kpi-service
report-service
bff-gateway
frontend
```

Puertos finales del proyecto:

| Componente | Puerto |
|---|---:|
| Frontend React 19 + Nginx | 3000 |
| BFF Gateway | 8081 |
| Data Service | 8083 |
| KPI Service | 8084 |
| Report Service | 8085 |
| MySQL host | 3307 |
| MySQL contenedor | 3306 |

## Patrones asociados al arquetipo

### Repository Pattern

Aplicado en:

- `DatoRepository`
- `KpiRepository`
- `ReporteRepository`

Permite separar persistencia y logica de negocio.

### Factory Method

Aplicado en `kpi-service`:

- `KpiFactory`
- `KpiCalculator`
- `VentasCalculator`
- `InventarioCalculator`
- `LogisticaCalculator`
- `RentabilidadCalculator`

Aplicado en `report-service`:

- `ExportadorFactory`
- `Exportador`
- `PdfExportador`
- `ExcelExportador`
- `JsonExportador`

Permite crear objetos especializados sin acoplar la logica principal a implementaciones concretas.

### API Gateway / BFF

Aplicado en:

- `bff-gateway`
- `DashboardController`
- `DashboardService`

Permite que el frontend consuma un unico punto de entrada.

### Circuit Breaker

Aplicado en integraciones internas:

```text
kpi-service -> data-service
report-service -> kpi-service
```

Permite tolerancia a fallos y evita caidas en cascada.

## Guia breve para crear un nuevo microservicio

1. Crear proyecto Spring Boot desde Initializr con dependencias necesarias.
2. Validar Java 25 y Maven.
3. Ejecutar `mvn clean install`.
4. Ejecutar `mvn spring-boot:run`.
5. Generar arquetipo con `mvn archetype:create-from-project`.
6. Instalar el arquetipo con `mvn clean install`.
7. Crear nuevo microservicio con `mvn archetype:generate`.
8. Ajustar paquetes `controller`, `service`, `repository`, `model`, `dto` y `config`.
9. Configurar `application.properties`.
10. Crear `Dockerfile`.
11. Registrar el nuevo servicio en `docker-compose.yml`.
12. Asignar base de datos propia si corresponde.

## Validacion actual

La arquitectura fue validada mediante:

```powershell
docker compose up -d --build
docker compose ps
```

Servicios operativos:

```text
frontend       0.0.0.0:3000->3000/tcp
bff-gateway    0.0.0.0:8081->8081/tcp
data-service   0.0.0.0:8083->8083/tcp
kpi-service    0.0.0.0:8084->8084/tcp
report-service 0.0.0.0:8085->8085/tcp
mysql          0.0.0.0:3307->3306/tcp
```

Tambien se valido el BFF Gateway:

```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/dashboard/stats" -Method Get | ConvertTo-Json -Depth 10
```

Resultado:

```text
BFF Gateway operativo, servicios 4/4 operativos, KPIs visibles, ventas totales y reportes recientes disponibles.
```

## Conclusion

El arquetipo Maven base entregado por el profesor permitio definir una estructura repetible para los microservicios de Cordillera Platform.

Sobre esa base, el equipo implemento servicios independientes con Java 25, Spring Boot, Maven, JPA, MySQL, Docker, pruebas unitarias y patrones de diseno.

Esto mejora:

- Mantenibilidad.
- Escalabilidad.
- Orden del codigo.
- Separacion de responsabilidades.
- Reutilizacion de estructura.
- Facilidad para crear nuevos microservicios.
- Cumplimiento de la rubrica del Parcial 3.
