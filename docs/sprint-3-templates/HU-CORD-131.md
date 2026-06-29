# HU-CORD-131 — Diagrama de arquitectura actualizado (FE+BE+REST+persistencia)

## Historia de Usuario

Como equipo de Grupo Cordillera

necesitamos crear el diagrama de arquitectura final del sistema que muestre Frontend, BFF, los 3 microservicios, API REST y bases de datos

para cumplir con el checklist de entrega del EP3 (ítem "diagrama-arquitectura.png mostrando frontend ↔ BFF ↔ microservicio1 ↔ microservicio2 ↔ BD") y el Indicador 1 de la rúbrica.

## Contexto Técnico e Integración

El diagrama debe reflejar exactamente la arquitectura implementada:

[Browser / React 19 + Vite]     → puerto 3000 (Docker + Nginx)
         ↓ HTTP/REST (JSON)
[BFF Gateway / Spring Boot 4]   → puerto 8081 (único expuesto al host)
    ↓              ↓              ↓
[data-service]  [kpi-service]  [report-service]
Spring Boot 4   Spring Boot 4   Spring Boot 4
puerto 8083     puerto 8084     puerto 8085
(interno Docker) (interno Docker) (interno Docker)
    ↓                ↓               ↓
[data_db]       [kpi_db]        [report_db]
MySQL/XAMPP     MySQL/XAMPP     MySQL/XAMPP
host:3306       host:3306       host:3306

Patrones a identificar visualmente en el diagrama:
- BFF (único punto de entrada): anotado en el bff-gateway
- Database per Service: 3 BDs separadas con colores distintos
- Circuit Breaker: flechas kpi→data y report→kpi con icono de CB
- API REST: todas las flechas etiquetadas con "HTTP/REST"
- JPA/JDBC: flechas de cada MS a su BD etiquetadas con "JDBC/JPA"

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- Indicador 1 (5%): el diagrama es el artefacto principal que demuestra la arquitectura BFF + 2 microservicios independientes. Debe mostrar la división correcta de responsabilidades.
- Indicador 5 (15%): durante la defensa oral, cada integrante debe poder explicar el diagrama señalando dónde está el BFF, por qué están separados los microservicios y qué patrón resuelve cada problema del caso Grupo Cordillera.
- Checklist EP3: requiere exactamente diagrama-arquitectura.png (o .jpg / .pdf).

## Criterios de Aceptación (Gherkin)

AC1: Diagrama correcto y completo

Dado el diagrama de arquitectura exportado
Cuando el evaluador lo revisa
Entonces se ven claramente: Frontend, BFF Gateway, los 3 microservicios diferenciados,
las 3 bases de datos separadas, las flechas de comunicación con protocolo,
y los puertos de cada componente

AC2: Archivos en docs/ con nombres exactos

Dado que el equipo exportó el diagrama
Cuando se arma el ZIP de entrega EP3
Entonces existen: docs/diagrama-arquitectura.png Y docs/diagrama-arquitectura.pdf
Y el README.md del monorepo muestra el diagrama renderizado

## Archivos a Crear o Modificar

| Archivo | Formato | Uso |
|---|---|---|
| docs/diagrama-arquitectura.drawio | draw.io fuente | Editable para futuras versiones |
| docs/diagrama-arquitectura.png | PNG 300 DPI | Para presentaciones y ZIP de entrega |
| docs/diagrama-arquitectura.pdf | PDF vectorial | Para el ZIP de entrega EP3 |

## Definición de Hecho (DoD)

- [ ] docs/diagrama-arquitectura.png existe con todos los componentes visibles
- [ ] docs/diagrama-arquitectura.pdf existe como alternativa vectorial
- [ ] El diagrama muestra Frontend + BFF + 3 microservicios + 3 BDs + Docker network
- [ ] Los patrones BFF, Database per Service y Circuit Breaker están anotados/identificables
- [ ] README.md principal del monorepo muestra el diagrama con ![Arquitectura](docs/diagrama-arquitectura.png)

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-203]: Crear el diagrama en draw.io o Excalidraw

Descripcion: Crear el diagrama de arquitectura completo del sistema usando draw.io (app.diagrams.net, gratuito, sin instalación) o Excalidraw. El diagrama DEBE mostrar (requerimiento explícito de la rúbrica EP3 Indicador 1): (1) Frontend React (rectángulo azul, puerto 3000, "React 19 + Vite + Nginx"); (2) BFF Gateway (rectángulo verde, puerto 8081, "Spring Boot 4 · Java 21", etiqueta "BFF / API Gateway"); (3) data-service (rectángulo naranja, puerto 8083, "Spring Boot 4 · MySQL data_db"); (4) kpi-service (rectángulo naranja, puerto 8084, "Spring Boot 4 · MySQL kpi_db", ícono de Circuit Breaker); (5) report-service (rectángulo naranja, puerto 8085, "Spring Boot 4 · MySQL report_db", ícono de Circuit Breaker); (6) MySQL/XAMPP host indicando que las 3 BDs corren en el host Windows (no en Docker). Agregar: flechas con etiquetas "HTTP/REST" entre componentes, "JDBC/JPA" entre servicios y BDs, un rectángulo punteado "Docker Network cordillera-network" rodeando los 4 servicios (BFF + 3 MS). Incluir leyenda de patrones en el margen: BFF, Database per Service, Circuit Breaker, Factory Method. Guardar como docs/diagrama-arquitectura.drawio (Indicador 1 EP3: arquitectura de microservicios).

### Sub-task 2 [CORD-204]: Exportar a PNG y PDF e incluir en docs/

Descripcion: Desde draw.io, exportar el diagrama en 2 formatos: (1) docs/diagrama-arquitectura.png: File → Export As → PNG, activar "Scale" 2x o 3x para alta resolución (mínimo 1200px de ancho), fondo blanco. Verificar que al abrir la imagen se leen claramente los nombres de todos los componentes y los puertos. (2) docs/diagrama-arquitectura.pdf: File → Export As → PDF, escala "fit page". Verificar que el PDF es vectorial (hacer zoom en el PDF y confirmar que las letras no se pixelan). El checklist EP3 requiere específicamente "diagrama-arquitectura.png (o .jpg / .pdf)" mostrando el flujo "frontend ↔ BFF ↔ microservicio1 ↔ microservicio2 ↔ BD" — los 3 microservicios cuentan como "microservicio1 ↔ microservicio2" (la rúbrica dice mínimo 2, tenemos 3 que es mejor). Ambos archivos van en el directorio docs/ del monorepo (Indicadores 1 y 3 EP3).

### Sub-task 3 [CORD-205]: Actualizar README.md principal con sección Arquitectura

Descripcion: En el README.md del directorio raíz del monorepo (cordillera-platform-parcial-2/README.md), agregar una sección completa de Arquitectura: (1) Título ## Arquitectura del Sistema; (2) Imagen incrustada: ![Diagrama de Arquitectura Cordillera](docs/diagrama-arquitectura.png); (3) Párrafo de descripción del flujo: "El Frontend React se comunica exclusivamente con el BFF Gateway (puerto 8081), que actúa como único punto de entrada. El BFF agrega datos de los 3 microservicios internos (data-service, kpi-service, report-service), cada uno con su propia base de datos MySQL (patrón Database per Service)"; (4) Tabla de puertos: BFF 8081 (expuesto), data-service 8083, kpi-service 8084, report-service 8085 (todos internos Docker); (5) Link al fuente editable: [Editar diagrama en draw.io](docs/diagrama-arquitectura.drawio); (6) Sección "Patrones implementados" listando: BFF, Factory Method, Strategy, Circuit Breaker, Repository, Observer. Verificar en GitHub que el README renderiza la imagen correctamente (la imagen debe estar en la ruta relativa correcta docs/diagrama-arquitectura.png). Cumple checklist EP3 "repositorios actualizados" e Indicadores 1 y 5 EP3 (arquitectura y justificación ante el evaluador).
