# 00 — Índice Maestro Sprint 3 · Grupo Cordillera EP3

**Sprint:** S3 — EP3 Integración + Testing
**Período:** 16 jun – 21 jun (20 actividades)
**Fuente de verdad Jira:** exportación XML del 24 Jun 2026 (post-limpieza de duplicados)
**Total Stories:** 20 (CORD-113 a CORD-132)
**Total Sub-tasks:** 60 (CORD-134 a CORD-208, tras eliminar 15 duplicados)

---

## Objetivo del Sprint

Cerrar EP3: integración end-to-end Frontend → BFF → Microservicios, suite de pruebas unitarias con cobertura JaCoCo ≥ 60% en los 4 servicios Spring Boot, frontend mejorado con estados visuales (loading/error/degradado) y documentación completa para la entrega del parcial.

---

## Alineación con Rúbrica EP3

| Indicador EP3 | % | CORD relacionadas |
|---|---|---|
| Ind. 1 — Arquitectura BFF + 2 microservicios | 5% | CORD-131 (diagrama), CORD-124 (BFF aislamiento), CORD-129 (ER) |
| Ind. 2 — Frontend + Backend con distintos stacks | 10% | CORD-127, CORD-128, CORD-113 a CORD-122 |
| Ind. 3 — Integración API REST + persistencia | 5% | CORD-129 (PDF persistencia), CORD-132 (repos), CORD-113/114/120/122 |
| Ind. 4 — Pruebas unitarias ≥ 60% | 10% | CORD-116, CORD-119, CORD-123, CORD-125, CORD-130 (informe) |
| Ind. 5 — Técnicas de ideación + justificación MS | 15% | Defensa oral — basarse en todos los archivos HU |
| Ind. 6 — Conocimiento lenguajes y tecnologías | 20% | Defensa oral — dominar el stack real del proyecto |
| Ind. 7 — Integración + escalabilidad | 15% | CORD-124, CORD-126, CORD-127, CORD-128 |
| Ind. 8 — Pruebas + patrones de diseño | 20% | CORD-117, CORD-118, CORD-120, CORD-121, CORD-130 |

---

## DATA-SERVICE (CORD-113 a CORD-117) — 5 Stories · 9 Sub-tasks

| Story | Título | Sub-tasks |
|---|---|---|
| CORD-113 | Registrar dato operacional desde sistema externo (POS/ERP/CRM) | CORD-134, CORD-135, CORD-136 |
| CORD-114 | Listar datos operacionales con filtros (sistema/sucursal) | CORD-137, CORD-138, CORD-139 |
| CORD-115 | Actualizar y eliminar dato operacional (PUT/DELETE) | CORD-150, CORD-141, CORD-142 |
| CORD-116 | Configurar quality gate JaCoCo 60% en data-service | CORD-154, CORD-155, CORD-143 |
| CORD-117 | Consultar KPIs filtrados por categoría (Factory Method) | CORD-156, CORD-157, CORD-158 |

**Patrones cubiertos:** Repository Pattern · Bean Validation · GlobalExceptionHandler · JaCoCo Quality Gate

---

## KPI-SERVICE (CORD-118 a CORD-121) — 4 Stories · 9 Sub-tasks

| Story | Título | Sub-tasks |
|---|---|---|
| CORD-118 | CRUD completo de KPIs con recálculo vía KpiFactory | CORD-159, CORD-160, CORD-161 |
| CORD-119 | Configurar quality gate JaCoCo 60% en kpi-service | CORD-162, CORD-163, CORD-164 |
| CORD-120 | Generar reporte ejecutivo consolidado (POST /api/reportes/generar) | CORD-165, CORD-166, CORD-167 |
| CORD-121 | Exportar reporte PDF/Excel/JSON con ExportadorFactory | CORD-168, CORD-169, CORD-170 |

> ⚠️ Nota: CORD-120 y CORD-121 corresponden al **report-service** (no al kpi-service), están agrupadas bajo KPI en el sprint pero las sub-tasks son de report-service.

**Patrones cubiertos:** Factory Method (KpiFactory) · Strategy (calculadoras de KPI) · JaCoCo Quality Gate

---

## REPORT-SERVICE (CORD-122 a CORD-125) — 4 Stories · 9 Sub-tasks

| Story | Título | Sub-tasks |
|---|---|---|
| CORD-122 | CRUD reportes y filtrado por area | CORD-171, CORD-172, CORD-173 |
| CORD-123 | Configurar quality gate JaCoCo 60% en report-service | CORD-174, CORD-175, CORD-176 |
| CORD-124 | Dashboard consolidado en BFF con Circuit Breaker | CORD-177, CORD-178, CORD-179 |
| CORD-125 | Configurar quality gate JaCoCo 60% en bff-gateway | CORD-180, CORD-181, CORD-182 |

**Patrones cubiertos:** Factory Method (ExportadorFactory) · Strategy (PdfExportador/ExcelExportador/JsonExportador) · Repository Pattern · Circuit Breaker · JaCoCo Quality Gate

---

## BFF-GATEWAY (CORD-126 a CORD-129) — 4 Stories · 8 Sub-tasks

| Story | Título | Sub-tasks |
|---|---|---|
| CORD-126 | Configurar timeouts y manejo de errores en RestTemplate BFF | CORD-183, CORD-184, CORD-185 |
| CORD-127 | Migrar navegación a React Router con rutas declarativas | CORD-186, CORD-187, CORD-188 |
| CORD-128 | Estados loading/empty/error/degradado en 6 screens | CORD-194, CORD-195, CORD-196 |
| CORD-129 | Documento docs/persistencia.pdf con JPA y diagrama ER | CORD-197, CORD-198, CORD-199 |

**Patrones cubiertos:** BFF/API Gateway · Fail-Fast (timeouts) · SPA (React Router) · Observer (DashboardContext)

---

## FRONTEND + DOCS (CORD-130 a CORD-132) — 3 Stories · 9 Sub-tasks

| Story | Título | Sub-tasks |
|---|---|---|
| CORD-130 | Documento docs/informe-pruebas-unitarias.pdf con cobertura y CA-Test | CORD-200, CORD-201, CORD-202 |
| CORD-131 | Diagrama de arquitectura actualizado (FE+BE+REST+persistencia) | CORD-203, CORD-204, CORD-205 |
| CORD-132 | Migrar a submódulos de Git y generar repositorios.txt | CORD-206, CORD-207, CORD-208 |

**Artefactos entregables:** diagrama-arquitectura.png/pdf · descripcion-persistencia.pdf · informe-pruebas-unitarias.pdf · repositorios.txt

---

## Mapa Completo de Sub-tasks (ORDER BY key ASC)

```
CORD-134 CORD-135 CORD-136 CORD-137 CORD-138 CORD-139
CORD-141 CORD-142 CORD-143
CORD-150
CORD-154 CORD-155 CORD-156 CORD-157 CORD-158 CORD-159
CORD-160 CORD-161 CORD-162 CORD-163 CORD-164 CORD-165
CORD-166 CORD-167 CORD-168 CORD-169 CORD-170 CORD-171
CORD-172 CORD-173 CORD-174 CORD-175 CORD-176 CORD-177
CORD-178 CORD-179 CORD-180 CORD-181 CORD-182 CORD-183
CORD-184 CORD-185 CORD-186 CORD-187 CORD-188
CORD-194 CORD-195 CORD-196 CORD-197 CORD-198 CORD-199
CORD-200 CORD-201 CORD-202 CORD-203 CORD-204 CORD-205
CORD-206 CORD-207 CORD-208
```

**Total: 60 Sub-tasks activas** (tras eliminar 15 duplicados: CORD-144 a 153, CORD-189 a 193)

---

## Checklist de Entrega EP3

- [ ] diagrama-arquitectura.png/pdf → CORD-203, CORD-204
- [ ] descripcion-persistencia.pdf → CORD-197, CORD-198, CORD-199
- [ ] informe-pruebas-unitarias.pdf con gráficos y ≥ 60% → CORD-200, CORD-201, CORD-202
- [ ] coleccion-postman.json o swagger.yaml → generado en CORD-113 a CORD-122
- [ ] repositorios.txt con URLs → CORD-208
- [ ] frontend/README.md con instrucciones → CORD-131/132
- [ ] cada microservicio tiene README.md → CORD-131/132
- [ ] Los 2 microservicios principales (data-service, kpi-service) están separados como repos → CORD-206/207
- [ ] JaCoCo ≥ 60% en los 4 servicios Java → CORD-116, CORD-119, CORD-123, CORD-125
