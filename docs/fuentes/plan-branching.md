# Plan de Branching - Cordillera Platform

Documento tecnico correspondiente al Parcial 2 de **Desarrollo Full Stack III (DSY1106)**.

## 1. Objetivo del documento

Este documento describe la estrategia de branching utilizada en el repositorio **Cordillera Platform**, correspondiente al caso Grupo Cordillera.

El objetivo es evidenciar como el equipo organizo el trabajo colaborativo usando Git y GitHub, manteniendo control de versiones, separacion de responsabilidades, Pull Requests, merges y resolucion de conflictos.

## 2. Repositorio del proyecto

Repositorio principal:

```text
https://github.com/Nachovn12/cordillera-platform-parcial-2
```

El proyecto se trabaja como monorepo, incluyendo:

- Frontend React 19 + Vite + Nginx.
- BFF Gateway.
- Data Service.
- KPI Service.
- Report Service.
- Docker Compose.
- Documentacion y evidencias.

## 3. Estrategia utilizada

La estrategia utilizada se basa en Git Flow simplificado, adaptado al contexto academico del Parcial 2.

Ramas principales:

| Rama | Proposito |
|---|---|
| main | Rama estable del proyecto. Representa la version lista para entrega o produccion. |
| develop | Rama de integracion. Recibe cambios desde las ramas feature mediante Pull Request. |
| feature/* | Ramas de desarrollo por componente o funcionalidad. |
| hotfix/* | Rama reservada para correcciones urgentes sobre main. No fue necesario usarla en esta etapa. |

## 4. Ramas utilizadas

Evidencia obtenida con:

```powershell
git branch -a
```

Resultado principal:

```text
develop
feature/bff-gateway
feature/data-service
feature/docker-compose-integration
feature/kpi-service
feature/report-service
main
remotes/origin/develop
remotes/origin/feature/bff-gateway
remotes/origin/feature/data-service
remotes/origin/feature/kpi-service
remotes/origin/feature/report-service
remotes/origin/main
```

## 5. Rama de integracion final Docker

Para la integracion final se creo la rama:

```text
feature/docker-compose-integration
```

Motivo:

- `develop` ya contenia cambios acumulados.
- Se necesitaba integrar Docker Compose sin ensuciar directamente `develop`.
- Se debian agregar Dockerfiles por servicio.
- Se debia validar frontend con Nginx.
- Se debia probar la arquitectura completa con MySQL Docker.
- Se debia preparar evidencia para presentacion.

## 6. Flujo de trabajo aplicado

El flujo utilizado por el equipo fue:

```text
main
  ^
develop
  ^
feature/nombre-componente
```

Proceso:

- Cada integrante trabajo en una rama feature.
- Los cambios se validaron localmente.
- Se creo Pull Request hacia `develop`.
- Se revisaron los cambios.
- Se hizo merge hacia `develop`.
- La integracion final Docker se trabajo en `feature/docker-compose-integration`.
- Al finalizar, esta rama se integrara mediante Pull Request hacia `develop`.

## 7. Distribucion por integrante

| Integrante | Responsabilidad | Ramas/componentes |
|---|---|---|
| Ignacio Valeria | Frontend, Report Service, documentacion, integracion Docker | feature/frontend, feature/report-service, feature/docker-compose-integration |
| Benjamin Palma | BFF Gateway, KPI Service | feature/bff-gateway, feature/kpi-service |
| Benjamin Flores | Data Service | feature/data-service |

## 8. Evidencia de merges

Evidencia obtenida con:

```powershell
git log --oneline --graph --decorate --all -n 30
```

Merges identificados:

```text
34a3a5e Merge pull request #12 from Nachovn12/feature/frontend
f236574 Merge pull request #8 from Nachovn12/feature/bff-respuestas-degradadas
1875379 Merge pull request #11 from Nachovn12/feature/report-service-tests
```

Estos merges evidencian integracion mediante Pull Request hacia `develop`.

## 9. Pull Requests relevantes

| PR | Rama origen | Proposito |
|---|---|---|
| PR #8 | feature/bff-respuestas-degradadas | Mejoras en BFF Gateway y respuestas degradadas. |
| PR #11 | feature/report-service-tests | Pruebas unitarias obligatorias de Report Service. |
| PR #12 | feature/frontend | Integracion del frontend ejecutivo. |

## 10. Gestion de conflictos

Durante la integracion se detectaron situaciones de conflicto potencial o inconsistencia, especialmente por cambios acumulados en `develop` y ajustes simultaneos en frontend, BFF, documentacion y Docker.

Ejemplo documentado:

```text
Rama de trabajo: feature/docker-compose-integration
Rama base: develop
Situacion: develop ya tenia cambios previos en frontend, BFF, properties y README.
Decision: no trabajar directamente sobre develop para evitar ensuciar la rama de integracion.
Solucion: crear feature/docker-compose-integration y realizar ahi la integracion Docker.
```

Tambien se resolvieron inconsistencias de configuracion:

- Puerto BFF original documentado como `8080`.
- Puerto real del proyecto definido como `8081`.
- Puertos `8080` y `8082` detectados como potencialmente ocupados en equipos del laboratorio.
- Se mantuvo BFF en `8081`.
- Se actualizaron README, Docker Compose y documentacion para evitar contradicciones.

## 11. Conflicto documentado de configuracion

| Elemento | Problema | Resolucion |
|---|---|---|
| Puerto BFF | Existian referencias historicas a 8080, pero el proyecto real usa 8081. | Se actualizo documentacion y configuracion activa a 8081. |
| MySQL local/XAMPP | XAMPP podia ocupar 3306. | Se configuro MySQL Docker en host 3307 y contenedor 3306. |
| container_name en Docker Compose | Podia generar conflictos en otros computadores. | Se eliminaron los `container_name` para que Compose genere nombres automaticos. |
| React 18 vs React 19 | Jira mencionaba React 18, pero el proyecto real usa React 19. | Se documento React 19 + Vite de forma coherente con `package.json`. |

## 12. Beneficios de la estrategia

La estrategia de branching permitio:

- Separar responsabilidades por componente.
- Evitar cambios directos en `main`.
- Mantener `develop` como rama de integracion.
- Revisar cambios mediante Pull Request.
- Documentar merges relevantes.
- Integrar Docker sin romper el avance previo.
- Facilitar colaboracion entre los tres integrantes.
- Mantener trazabilidad para la defensa oral.

## 13. Validacion final de ramas

Comando usado:

```powershell
git branch --show-current
```

Resultado:

```text
feature/docker-compose-integration
```

Esto confirma que la integracion final se realizo en una rama feature, siguiendo el flujo definido.

## 14. Validacion del remoto

Comando usado:

```powershell
git remote -v
```

Resultado:

```text
origin  https://github.com/Nachovn12/cordillera-platform-parcial-2.git (fetch)
origin  https://github.com/Nachovn12/cordillera-platform-parcial-2.git (push)
```

## 15. Relacion con la rubrica

Este plan responde al indicador de branching del encargo grupal:

- Estrategia clara y organizada.
- Uso de ramas `main`, `develop` y `feature/*`.
- Evidencia de merges mediante Pull Requests.
- Resolucion de conflictos documentada.
- Gestion colaborativa del equipo.

Tambien apoya la defensa oral, donde se debe explicar como la estructura de ramas favorecio la colaboracion y el control de versiones.

## 16. Conclusion

El equipo aplico una estrategia de branching basada en Git Flow simplificado, manteniendo separacion entre ramas estables, integracion y desarrollo por funcionalidad.

La rama `feature/docker-compose-integration` permitio integrar Docker Compose, Nginx, MySQL Docker, BFF Gateway y microservicios sin comprometer directamente `develop`.

La evidencia de ramas, merges y decisiones de resolucion demuestra control de versiones y colaboracion organizada para el Parcial 2.
