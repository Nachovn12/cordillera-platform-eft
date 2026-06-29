# HU-CORD-132 — Crear repositorios GitHub y generar repositorios.txt

## Historia de Usuario

Como equipo de Grupo Cordillera

necesitamos crear los repositorios GitHub individuales de cada componente y actualizar el archivo repositorios.txt

para cumplir con el checklist de entrega del EP3 (ítem "repositorios.txt con URL y descripción del propósito de cada uno") y verificar que todos los repositorios estén públicamente accesibles.

## Contexto Técnico e Integración

El proyecto existe como monorepo en cordillera-platform-parcial-2 con la siguiente estructura de carpetas:
- frontend/ → React 19 + Vite
- bff-gateway/ → Spring Boot 4, puerto 8081
- data-service/ → Spring Boot 4, puerto 8083, MySQL data_db
- kpi-service/ → Spring Boot 4, puerto 8084, MySQL kpi_db
- report-service/ → Spring Boot 4, puerto 8085, MySQL report_db

El archivo repositorios.txt ya existe en la raíz pero necesita las URLs de los repos individuales.

Requisito del Indicador 2 EP3: "Los dos microservicios deben usar lenguajes o stacks distintos entre sí". En este proyecto todos los microservicios son Java/Spring Boot. Para cumplir esta rubrica, el frontend en React (JavaScript) es el segundo stack distinto al backend Java. El BFF es Java, los microservicios son Java, y el frontend es JavaScript/React — esto cubre la diferencia de stack exigida.

## Relación con Patrones y Arquitectura (Foco Defensa Oral 70%)

- Indicador 2 (10%): 2 stacks distintos → Java (bff-gateway + microservicios) + JavaScript/React (frontend).
- Checklist EP3: todos los repositorios deben estar actualizados y accesibles públicamente. El repositorios.txt debe tener URLs + descripción de cada uno.
- Indicador 6 (20%): durante la defensa, cada integrante debe conocer las instrucciones de instalación de su componente (README.md de cada repo).

## Criterios de Aceptación (Gherkin)

AC1: Repositorios públicos en GitHub

Dado que el evaluador recibe el repositorios.txt
Cuando accede a cada URL listada
Entonces cada repositorio carga correctamente en GitHub como público
Y cada repo tiene un README.md con instrucciones de instalación y ejecución

AC2: repositorios.txt completo

Dado el archivo repositorios.txt en la raíz del monorepo
Cuando el evaluador lo abre
Entonces contiene exactamente las URLs de los repos con descripción breve de cada uno
Y el monorepo principal también está listado

## Archivos a Crear o Modificar

| Archivo | Acción | Descripción |
|---|---|---|
| repositorios.txt | Actualizar | URLs definitivas + descripciones de los 6 repos |
| frontend/README.md | Crear/actualizar | Instrucciones: npm install, npm run dev, npm run build, rutas |
| bff-gateway/README.md | Verificar | Ya existe — revisar que tiene instrucciones mvn + docker |
| data-service/README.md | Verificar | Ya existe — revisar instrucciones |
| kpi-service/README.md | Verificar | Ya existe — revisar instrucciones |
| report-service/README.md | Verificar | Ya existe — revisar instrucciones |

## Definición de Hecho (DoD)

- [ ] 5 repos individuales (o el monorepo compartido) públicamente accesibles en GitHub
- [ ] repositorios.txt actualizado con URLs reales y descripciones
- [ ] Cada repo tiene README.md con instrucciones de instalación y ejecución
- [ ] frontend/README.md cubre: npm install, npm run dev, npm run build, variables de entorno (VITE_API_URL)
- [ ] docker-compose.yml del monorepo funciona con docker-compose up --build

## Épico

Vinculada al Épico CORD-112 — EP3 — Pruebas Unitarias e Integración. Sprint 3.

## SUB-TASKS

### Sub-task 1 [CORD-206]: Crear repos en GitHub y preparar README.md de cada componente

Descripcion: Crear en la cuenta de GitHub del equipo los repositorios necesarios para la entrega. Estrategia recomendada: un único monorepo público cordillera-platform que contiene todas las carpetas (frontend, bff-gateway, data-service, kpi-service, report-service) — esto es más simple que 5 repos separados y cumple igualmente el checklist. Alternativamente, crear repos individuales. Para cada componente, verificar y completar el README.md con las secciones obligatorias del checklist EP3: (1) frontend/README.md: sección "Instalación" (npm install), sección "Desarrollo" (npm run dev → http://localhost:3000), sección "Build producción" (npm run build → genera dist/), sección "Variables de entorno" (VITE_API_URL=http://localhost:8081), sección "Rutas disponibles" (lista las 8 rutas: /, /kpis, /reports, /datos, /alerts, /services, /settings, /users); (2) Para cada microservicio Java: confirmar que tiene instrucciones de mvn clean test, mvn spring-boot:run y referencia al docker-compose.yml. Verificar que todos los repos están en modo público en GitHub (Settings → Visibility → Public). Cumple checklist EP3 y apoya Indicador 6 EP3 (conocimiento de lenguajes y tecnologías durante la defensa).

### Sub-task 2 [CORD-207]: Push del código actualizado a GitHub

Descripcion: Subir el código actualizado del Sprint 3 (con los tests, las correcciones y los documentos) a los repositorios GitHub. (1) Desde el monorepo local, ejecutar: git add ., git commit -m "EP3: tests unitarios JaCoCo >=60%, estados UI, documentacion", git push origin main. (2) Si se crearon repos individuales, hacer push de cada carpeta al repo correspondiente: opción A (git subtree): git subtree push --prefix=frontend https://github.com/equipo/cordillera-frontend main; opción B (más simple): copiar la carpeta al directorio del repo individual, hacer commit y push. (3) Verificar que en GitHub se ve el código actualizado con las fechas de commit del Sprint 3. (4) Confirmar que los commits incluyen los archivos de tests (src/test/java/...) y los documentos (docs/). El checklist EP3 exige "repositorios actualizados y accesibles públicamente" — esto es lo que verifica el evaluador al acceder a las URLs del repositorios.txt (Indicadores 2 y 6 EP3).

### Sub-task 3 [CORD-208]: Actualizar repositorios.txt con URLs definitivas y descripciones

Descripcion: Actualizar el archivo repositorios.txt (ya existente en la raíz del monorepo) con el formato requerido por el checklist EP3 ("URL + descripción del propósito de cada uno"). Contenido final del archivo:

REPOSITORIOS DEL PROYECTO — GRUPO CORDILLERA EP3
DSY1106 — Desarrollo Fullstack III

1. Repositorio Principal (Monorepo)
   URL: https://github.com/[equipo]/cordillera-platform-parcial-2
   Descripción: Monorepo con todos los componentes del sistema (BFF Gateway,
   data-service, kpi-service, report-service, frontend React). Incluye
   docker-compose.yml, documentación y diagramas.

2. Frontend (React 19 + Vite)
   URL: https://github.com/[equipo]/cordillera-frontend
   Descripción: SPA con React 19 que consume el BFF Gateway. Implementa
   DashboardContext (patrón Observer), 8 pantallas funcionales y estados
   de UI (loading, error, degradado).

3. BFF Gateway (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-bff-gateway
   Descripción: Backend For Frontend — único punto de entrada al sistema.
   Agrega datos de los 3 microservicios, gestiona autenticación en memoria
   y sirve el frontend React como recurso estático.

4. Data Service (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-data-service
   Descripción: Microservicio de datos operacionales. Gestiona ventas,
   inventario y datos de sucursales en MySQL (data_db).
   Patrón: Repository (Spring Data JPA).

5. KPI Service (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-kpi-service
   Descripción: Microservicio de indicadores KPI. Calcula valores con
   KpiFactory (patrón Factory Method). Se comunica con data-service
   mediante Circuit Breaker (Resilience4j).

6. Report Service (Spring Boot 4 - Java 21)
   URL: https://github.com/[equipo]/cordillera-report-service
   Descripción: Microservicio de reportes ejecutivos. Exporta en PDF,
   Excel y JSON usando ExportadorFactory (Factory Method + Strategy).
   Garantiza idempotencia en generación de reportes.

Reemplazar [equipo] con el usuario real de GitHub del equipo. Verificar que cada URL abre correctamente en el browser. Guardar el archivo con encoding UTF-8. Incluir repositorios.txt en el ZIP de entrega EP3 en la raíz (exactamente como lo exige el checklist). Esto asegura el cumplimiento del checklist item "archivo repositorios.txt o PDF con URLs de todos los repositorios GitHub" (Indicadores 2 y 6 EP3).
