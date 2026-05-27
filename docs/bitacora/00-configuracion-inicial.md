# Bitácora 00 - Configuración inicial Cordillera Platform

## Datos generales

**Proyecto:** Cordillera Platform - Parcial 2  
**Asignatura:** Desarrollo Full Stack III  
**Sección:** 001D  
**Fecha:** 09-05-2026  
**Equipo:** Ignacio Valeria, Benjamín Palma y Benjamín Flores  

---

## Objetivo de esta bitácora

Registrar la configuración inicial del proyecto antes de comenzar el desarrollo del Sprint 1.

Esta bitácora se deja como evidencia de que el equipo preparó la estructura base del proyecto, el arquetipo Maven, los microservicios backend, el frontend y las carpetas de documentación solicitadas para el Parcial 2.

---

## 1. Entorno de desarrollo utilizado

Se validó el entorno local de trabajo en Windows 11.

| Herramienta | Versión utilizada |
|---|---|
| Java | 25.0.3 LTS |
| Maven | 3.9.15 |
| Node.js / NPM | Instalado y funcionando |
| Git | Instalado y funcionando |
| Sistema Operativo | Windows 11 |

Aunque el entorno local utiliza Java 25, los proyectos Maven generados mantienen configuración de compilación compatible con Java 21.

---

## 2. Arquetipo Maven utilizado

Para cumplir con el requerimiento de arquetipos Maven, se utilizó el proyecto base `arquetipo-maven` entregado por el docente.

A partir de ese proyecto se generó un arquetipo local con Maven, usando los siguientes comandos:

- `mvn clean install`
- `mvn archetype:create-from-project`
- `cd target\generated-sources\archetype`
- `mvn clean install`

El arquetipo quedó instalado correctamente en el repositorio local de Maven.

Este arquetipo se usó como base para generar los componentes backend del proyecto.

---

## 3. Componentes backend generados

Desde el arquetipo Maven local se generaron los siguientes componentes backend:

| Componente | Tipo | Responsabilidad inicial |
|---|---|---|
| `bff-gateway` | Backend For Frontend | Punto de entrada entre frontend y microservicios |
| `data-service` | Microservicio | Gestión de datos organizacionales |
| `kpi-service` | Microservicio | Gestión y cálculo de KPIs |
| `report-service` | Microservicio | Generación de reportes ejecutivos |

Cada componente fue creado con estructura Maven, archivo `pom.xml`, Maven Wrapper, `application.properties` y separación inicial por carpetas.

---

## 4. Estructura base por microservicio

Cada microservicio quedó organizado con una estructura común por capas:

```txt
src/main/java/cl/duoc/cordillera/<servicio>/
├── config/
├── controller/
├── dto/
├── exception/
├── model/
├── repository/
├── service/
└── <Servicio>Application.java
```

También se dejó una clase de prueba base por servicio:

```txt
src/test/java/cl/duoc/cordillera/<servicio>/
└── <Servicio>ApplicationTests.java
```

Esta estructura permite mantener ordenado el código y facilita la implementación posterior de patrones como Repository Pattern, Factory Method y Circuit Breaker.

---

## 5. Puertos definidos inicialmente

Se configuraron los puertos base de los servicios según la planificación del proyecto:

| Servicio | Puerto |
|---|---:|
| `bff-gateway` | 8081 |
| `data-service` | 8083 |
| `kpi-service` | 8084 |
| `report-service` | 8085 |

> Nota de integracion Docker: la planificacion inicial consideraba `8080`, pero la configuracion final del proyecto usa `bff-gateway` en el puerto `8081` para evitar conflictos con servicios ya ocupados en equipos del laboratorio.

---

## 6. Validación inicial de backend

Se ejecutó una prueba inicial de compilación en los cuatro componentes backend con:

- `mvn clean test`

Resultado obtenido:

| Servicio | Resultado |
|---|---|
| `bff-gateway` | BUILD SUCCESS |
| `data-service` | BUILD SUCCESS |
| `kpi-service` | BUILD SUCCESS |
| `report-service` | BUILD SUCCESS |

Cada servicio ejecutó correctamente su prueba base `contextLoads()`.

---

## 7. Frontend creado

Se creó el componente frontend utilizando React con Vite.

Comando utilizado:

- `npm create vite@latest frontend -- --template react`

Luego se validó el build productivo con:

- `npm run build`

Resultado:

- Build ejecutado correctamente.
- Proyecto frontend listo para comenzar la implementación del dashboard ejecutivo.

---

## 8. Estructura inicial del proyecto

La estructura general del proyecto quedó organizada como monorepo:

```txt
cordillera-platform-parcial-2/
├── frontend/
├── bff-gateway/
├── data-service/
├── kpi-service/
├── report-service/
├── docs/
│   ├── arquetipo-maven/
│   ├── bitacora/
│   ├── evidencias/
│   ├── pdf/
│   └── repositorios.txt
├── .gitignore
└── README.md
```

---

## 9. Limpieza antes del primer commit

Antes de subir el proyecto a GitHub, se eliminaron carpetas generadas localmente para evitar archivos innecesarios en el repositorio.

Carpetas eliminadas:

- `frontend/node_modules`
- `frontend/dist`
- `bff-gateway/target`
- `data-service/target`
- `kpi-service/target`
- `report-service/target`

También se creó un archivo `.gitignore` para excluir automáticamente carpetas generadas, dependencias locales, archivos temporales y salidas de compilación.

---

## 10. Relación con la rúbrica

Esta configuración inicial aporta evidencia para los siguientes puntos del Parcial 2:

| Requerimiento de rúbrica | Evidencia preparada |
|---|---|
| Componentes frontend | Carpeta `frontend/` creada con React + Vite |
| Componentes backend | BFF y microservicios generados con Maven |
| Arquetipos Maven | Arquetipo generado desde proyecto base docente |
| Código organizado | Estructura por capas en cada servicio |
| Pruebas iniciales | `mvn clean test` ejecutado correctamente |
| Documentación | Carpetas `docs/`, `pdf/`, `bitacora/` y `repositorios.txt` |
| Versionamiento | Proyecto preparado para GitHub y Git Flow |

---

## 11. Pendientes después del primer commit

Quedan pendientes para el desarrollo del Sprint 1 y Sprint 2:

- Subir el repositorio a GitHub.
- Crear rama `develop`.
- Crear ramas `feature/frontend`, `feature/bff-gateway`, `feature/data-service`, `feature/kpi-service` y `feature/report-service`.
- Iniciar Sprint 1 en Jira.
- Implementar endpoints principales.
- Implementar patrones de diseño.
- Completar README por componente.
- Documentar el plan de branching.
- Agregar evidencias de Pull Requests, merges y conflicto resuelto.
- Implementar pruebas unitarias obligatorias en `report-service`.
- Validar cobertura mínima con JaCoCo.

---

## Estado final

La configuración inicial del proyecto queda completada.

El proyecto está listo para realizar el primer commit en `main`, crear el repositorio en GitHub y comenzar el flujo de trabajo con Git Flow.
