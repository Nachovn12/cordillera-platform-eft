# Evaluación Parcial N°3 — Desarrollo Fullstack III (DSY1106)
**Tema:** Integración de arquitectura de microservicios  
**Ponderación:** 30% de la nota final del curso  
**Tiempo:** 8 horas | **Modalidad:** Equipos (Encargo grupal + Defensa individual)

---

## Contexto del proyecto

Continuación del caso trabajado en las Parciales 1 y 2. El equipo debe entregar una solución completa de arquitectura de microservicios que integre:

- Un **BFF** (Backend for Frontend)
- **Dos microservicios** independientes con distintos lenguajes/tecnologías
- Un **frontend** desarrollado con framework moderno
- Comunicación vía **API REST**
- **Persistencia de datos** mediante JPA y/o Stored Procedures
- **Pruebas unitarias** con cobertura mínima del 60%

---

## Distribución de la nota

| Situación evaluativa | % de la Parcial |
|----------------------|-----------------|
| Encargo grupal       | 30%             |
| Defensa oral         | 70%             |
| **Total**            | **100%**        |

---

## Entregable requerido

Archivo comprimido (ZIP o RAR) subido a Blackboard + enlaces a repositorios GitHub.

### Estructura de carpetas esperada

```
entrega-ep3/
├── documentacion/
│   ├── diagrama-arquitectura.png (o .jpg / .pdf)
│   │     Debe mostrar: frontend ↔ BFF ↔ microservicio1 ↔ microservicio2 ↔ BD
│   ├── descripcion-persistencia.pdf
│   │     Explica cómo se implementa JPA y/o Stored Procedures
│   └── informe-pruebas-unitarias.pdf
│         Cobertura (con gráficos), métricas de herramientas de testing, ejemplos
│
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json          ← scripts de ejecución
│   └── README.md             ← instrucciones de instalación, ejecución y pruebas
│
├── backend/
│   ├── bff/
│   │   ├── src/
│   │   ├── application.properties (o equivalente)
│   │   ├── pom.xml (o equivalente)
│   │   └── README.md
│   ├── microservicio-1/
│   │   ├── src/
│   │   ├── [archivos de configuración]
│   │   └── README.md
│   └── microservicio-2/
│       ├── src/
│       ├── [archivos de configuración]
│       └── README.md
│
├── api-rest/
│   └── coleccion-postman.json (o swagger.yaml)
│         Debe incluir ejemplos de request/response por endpoint
│
├── pruebas-unitarias/
│   ├── [código de pruebas por componente]
│   ├── [archivos de configuración para ejecutar pruebas]
│   └── reportes-cobertura/   ← HTML o PDF generados por la herramienta
│
└── repositorios.txt (o .pdf)
      - Repositorio principal (documentación general)
      - Repositorio frontend
      - Repositorio microservicio 1
      - Repositorio microservicio 2
      Incluir breve descripción del propósito de cada uno
```

---

## Rúbrica de Evaluación

### Niveles de desempeño

| Nivel                  | % logro | Descripción |
|------------------------|---------|-------------|
| Muy buen desempeño     | 100%    | Logro destacado de todos los aspectos del indicador |
| Buen desempeño         | 80%     | Alto desempeño con pequeñas omisiones o errores |
| Desempeño aceptable    | 60%     | Elementos básicos logrados, con omisiones o errores |
| Desempeño incipiente   | 30%     | Importantes omisiones que impiden evidenciar competencia |
| Desempeño no logrado   | 0%      | Ausencia o desempeño incorrecto |

---

### DIMENSIÓN 1: Encargo Grupal (30% de la Parcial)

---

#### Indicador 1 — Arquitectura de microservicios (5%)

**¿Qué se evalúa?**  
Propuesta estructurada que divide correctamente las funcionalidades del sistema en un BFF y dos microservicios, respondiendo a la problemática planteada.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Propuesta creativa y bien estructurada. División correcta de funcionalidades entre BFF y dos microservicios. Responde claramente a la problemática. Implementación sólida de la arquitectura. |
| **80% — Buen desempeño** | Propuesta bien estructurada y correcta, pero con pequeñas omisiones en la justificación o en la división de funcionalidades. |
| **60% — Desempeño aceptable** | Cumple elementos básicos, pero con errores o división incompleta de funcionalidades. Respuesta limitada a la problemática. |
| **30% — Desempeño incipiente** | Importantes omisiones o errores en la división de funcionalidades o en la aplicación de la arquitectura. |
| **0% — No logrado** | No presenta propuesta adecuada o coherente con la arquitectura de microservicios. |

> ✅ **Para lograr el 100%:** El diagrama de arquitectura debe mostrar claramente el BFF, los dos microservicios diferenciados, la API REST y la persistencia. La justificación de por qué cada funcionalidad está donde está debe ser explícita.

---

#### Indicador 2 — Desarrollo frontend y backend (10%)

**¿Qué se evalúa?**  
Diseño y desarrollo de componentes frontend con framework moderno + componentes backend con distintos lenguajes y tecnologías, cumpliendo requerimientos del cliente.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Frontend con framework moderno y backend con distintos lenguajes y tecnologías. Cumple perfectamente todos los requerimientos del cliente. |
| **80% — Buen desempeño** | Desarrollo adecuado de ambas capas, cumpliendo requerimientos, aunque con pequeñas omisiones en alguna tecnología. |
| **60% — Desempeño aceptable** | Elementos básicos presentes, pero implementación limitada o con errores que afectan algunos requerimientos. |
| **30% — Desempeño incipiente** | Importantes omisiones o errores que dificultan el cumplimiento de los requerimientos. |
| **0% — No logrado** | No cumple con los requerimientos ni desarrolla adecuadamente los componentes. |

> ✅ **Para lograr el 100%:** Los dos microservicios deben usar lenguajes o stacks diferentes entre sí (ej: uno en Java/Spring, otro en Node.js). El frontend debe ser un framework real (React, Angular, Vue, etc.), no HTML plano.

---

#### Indicador 3 — Integración API REST y persistencia (5%)

**¿Qué se evalúa?**  
Integración correcta entre frontend y backend mediante API REST, con comunicación efectiva y persistencia de datos mediante JPA o Stored Procedures.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Integración impecable. Comunicación fluida entre servicios. Persistencia eficiente mediante JPA o SPs. |
| **80% — Buen desempeño** | Integración adecuada con pequeñas omisiones o dificultades en comunicación o persistencia. |
| **60% — Desempeño aceptable** | Integración funcional básica, pero con errores o limitaciones que afectan el rendimiento. |
| **30% — Desempeño incipiente** | Importantes problemas en la integración que impiden funcionamiento efectivo. |
| **0% — No logrado** | No logra integrar los componentes ni asegurar persistencia adecuada. |

> ✅ **Para lograr el 100%:** La colección Postman/Swagger debe demostrar que cada endpoint funciona. El PDF de persistencia debe explicar las entidades JPA o el código de los SPs con ejemplos reales.

---

#### Indicador 4 — Pruebas unitarias (10%)

**¿Qué se evalúa?**  
Implementación de pruebas unitarias con cobertura mínima del 60% en todos los componentes, aplicando patrones de diseño adecuados.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Cobertura superior al 60%. Aplicación adecuada de patrones de diseño. Calidad asegurada en todos los componentes. |
| **80% — Buen desempeño** | Cobertura cercana o exactamente en 60%. Buenos patrones de diseño con pequeñas omisiones o errores en algunas pruebas. |
| **60% — Desempeño aceptable** | Cobertura mínima alcanzada, pero con errores o limitaciones en la aplicación de patrones de diseño. |
| **30% — Desempeño incipiente** | Cobertura inferior al 60% o aplicación deficiente de patrones de diseño. |
| **0% — No logrado** | Pruebas no implementadas adecuadamente o cobertura muy por debajo del 60%. |

> ✅ **Para lograr el 100%:** El informe debe incluir screenshots o exportación del reporte de cobertura (ej: JaCoCo para Java, Jest --coverage para JS). Los patrones de diseño aplicados deben ser identificables y justificados (ej: Repository, Factory, Strategy, etc.).

---

### DIMENSIÓN 2: Defensa Oral — Calificación Individual (70% de la Parcial)

La presentación es en equipo (15 minutos), pero la nota es **individual** según las respuestas de cada estudiante.

---

#### Indicador 5 — Técnicas de ideación y justificación de microservicios (15%)

**¿Qué se evalúa?**  
Explicar con claridad y lógica las técnicas de ideación usadas para desarrollar la solución, justificando por qué se aplicaron microservicios específicos.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Explicación clara, estructurada y lógica de las técnicas de ideación. Justifica con precisión por qué se aplicaron esos microservicios y cómo resuelven la problemática. |
| **80% — Buen desempeño** | Explica adecuadamente las técnicas y justifica el uso de microservicios, con pequeñas omisiones o errores menores. |
| **60% — Desempeño aceptable** | Explicación básica con justificación limitada sobre los microservicios específicos. Algunas omisiones. |
| **30% — Desempeño incipiente** | Explicaciones vagas o incompletas. Justificación confusa o insuficiente. |
| **0% — No logrado** | No explica coherentemente las técnicas ni justifica el uso de microservicios. |

> ✅ **Para preparar el 100%:** Cada integrante debe poder explicar qué técnica de ideación usó el equipo (Design Thinking, brainstorming, mapas mentales, etc.) y por qué la arquitectura propuesta (BFF + 2 microservicios) es la respuesta adecuada a la necesidad del cliente.

---

#### Indicador 6 — Conocimiento de lenguajes y tecnologías (20%)

**¿Qué se evalúa?**  
Demostrar dominio de los lenguajes y tecnologías utilizados, describiendo cómo se integran para responder a los requerimientos del cliente.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Dominio excelente. Describe con claridad cómo cada tecnología se integra para cumplir con los requerimientos. |
| **80% — Buen desempeño** | Buen conocimiento con pequeñas omisiones o errores menores en la descripción de la integración. |
| **60% — Desempeño aceptable** | Conocimiento adecuado pero con explicaciones limitadas o errores en la descripción de la integración. |
| **30% — Desempeño incipiente** | Conocimiento limitado o confuso, con importantes omisiones en la integración. |
| **0% — No logrado** | No demuestra conocimiento claro ni explica la integración tecnológica. |

> ✅ **Para preparar el 100%:** Cada integrante debe dominar el stack completo: framework frontend (por qué ese y no otro), lenguaje del microservicio que desarrolló, qué hace la API REST, cómo funciona JPA o los SPs en el proyecto.

---

#### Indicador 7 — Presentación de la integración y escalabilidad (15%)

**¿Qué se evalúa?**  
Presentar la integración frontend-backend demostrando funcionalidad y escalabilidad de la solución mediante la arquitectura de microservicios.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Presenta de manera clara y efectiva la integración, con ejemplos que demuestran funcionalidad y escalabilidad. |
| **80% — Buen desempeño** | Integración bien presentada con pequeñas omisiones en la demostración de funcionalidad o escalabilidad. |
| **60% — Desempeño aceptable** | Integración funcional pero presentación básica; dificultades para demostrar escalabilidad. |
| **30% — Desempeño incipiente** | Importantes errores en la integración que afectan la funcionalidad y la demostración de escalabilidad. |
| **0% — No logrado** | No presenta coherentemente la integración ni demuestra funcionalidad o escalabilidad. |

> ✅ **Para preparar el 100%:** Mostrar el sistema funcionando en vivo o con evidencia (video/screenshots). Argumentar cómo la separación en microservicios permite escalar solo la parte que lo necesita (ej: si el microservicio de pagos recibe más carga, se puede replicar sin tocar el de usuarios).

---

#### Indicador 8 — Presentación de pruebas unitarias y patrones de diseño (20%)

**¿Qué se evalúa?**  
Presentar las pruebas unitarias implementadas, explicar cómo se asegura la cobertura y cómo los patrones de diseño mejoran la calidad y mantenibilidad.

| Nivel | Descripción |
|-------|-------------|
| **100% — Muy buen desempeño** | Presentación detallada de las pruebas. Explica claramente cómo se asegura la cobertura. Demuestra cómo los patrones de diseño mejoran calidad y mantenibilidad. |
| **80% — Buen desempeño** | Explica adecuadamente las pruebas y cobertura con ejemplos claros, pero con pequeñas omisiones en la relación entre patrones y mantenibilidad. |
| **60% — Desempeño aceptable** | Explicación básica de pruebas. Cobertura mínima asegurada, pero con limitaciones para justificar cómo los patrones mejoran la calidad. |
| **30% — Desempeño incipiente** | Implementación limitada. Cobertura insuficiente. Explicación confusa sobre el impacto de los patrones de diseño. |
| **0% — No logrado** | No presenta las pruebas adecuadamente, no asegura cobertura mínima, ni justifica el aporte de los patrones de diseño. |

> ✅ **Para preparar el 100%:** Mostrar el reporte de cobertura durante la defensa. Explicar qué patrón de diseño se usó (ej: Repository desacopla la capa de datos → las pruebas no dependen de la BD real → se puede usar mock). Conectar patrón → ventaja en pruebas → ventaja en mantenibilidad futura.

---

## Resumen de ponderaciones

| # | Indicador | Dimensión | Ponderación |
|---|-----------|-----------|-------------|
| 1 | Arquitectura de microservicios (BFF + 2 microservicios) | Encargo grupal | 5% |
| 2 | Desarrollo frontend y backend | Encargo grupal | 10% |
| 3 | Integración API REST y persistencia | Encargo grupal | 5% |
| 4 | Pruebas unitarias (≥60% cobertura) | Encargo grupal | 10% |
| 5 | Técnicas de ideación y justificación de microservicios | Defensa oral | 15% |
| 6 | Conocimiento de lenguajes y tecnologías | Defensa oral | 20% |
| 7 | Presentación de integración y escalabilidad | Defensa oral | 15% |
| 8 | Presentación de pruebas unitarias y patrones de diseño | Defensa oral | 20% |
| | **Total** | | **100%** |

---

## Checklist de entrega

Antes de comprimir y enviar, verificar:

- [ ] Diagrama de arquitectura incluye: Frontend → BFF → Microservicio 1 → Microservicio 2 → BD
- [ ] PDF de persistencia explica JPA (entidades, repositorios) y/o Stored Procedures con código real
- [ ] Informe de pruebas unitarias incluye gráfico o reporte de cobertura ≥ 60%
- [ ] Frontend tiene `package.json` con scripts y `README.md` con instrucciones
- [ ] Cada microservicio tiene `README.md` con instrucciones para instalar, ejecutar y probar
- [ ] Colección Postman o Swagger documenta todos los endpoints con ejemplos de request/response
- [ ] Archivo `repositorios.txt` o PDF con URLs de todos los repositorios GitHub
- [ ] Todos los repositorios están actualizados y accesibles públicamente
- [ ] Los dos microservicios usan lenguajes o stacks **distintos**
- [ ] Los patrones de diseño aplicados son identificables en el código y en la presentación

---

*DSY1106 — Desarrollo Fullstack III | Parcial 3 — 30% ponderación final*
