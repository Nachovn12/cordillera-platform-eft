# Caso Semestral — DSY1106 Desarrollo Fullstack III

## "Grupo Cordillera – Plataforma de monitoreo inteligente para el desempeño organizacional"

---

## Introducción

En el sector del retail y la comercialización de productos para el hogar y tecnología, las empresas deben gestionar grandes volúmenes de información provenientes de las áreas del negocio, por lo que la capacidad de analizar los datos de manera oportuna es fundamental para la toma de decisiones por la alta gerencia.

Grupo Cordillera es una empresa dedicada en el retail y comercialización con presencia en distintas ciudades con múltiples sucursales a lo largo del país.

En los últimos años, la organización ha experimentado un crecimiento de sus operaciones, ampliando la gama de productos y expandiendo su presencia a nuevos mercados. Sin embargo, este crecimiento ha generado desafíos importantes para la gestión y análisis de la información, en donde la organización ha subsanado utilizando diferentes softwares y herramientas digitales, entre ellos:

- Sistemas de punto de venta
- Plataformas de comercio electrónico
- Sistemas de gestión de inventarios
- Herramientas de gestión financiera
- Sistema de atención al cliente

Dada la amplia gama de software utilizado, la información se encuentra dispersa en múltiples plataformas, lo que dificulta la visión y el desempeño de la organización. Por lo anterior, los ejecutivos enfrentan dificultades para acceder a información confiable y consolidada, lo que complejiza la capacidad de tomar decisiones o arriesga el entregar información errónea.

Para resolver estos problemas, Grupo Cordillera quiere desarrollar una solución basada en microservicios, con una capa frontend moderna y flexible, y un backend escalable y seguro, permitiendo consolidar la información y proporcionar herramientas de monitoreo en tiempo real del desempeño organizacional.

El desarrollo de este sistema se llevará a cabo en **tres etapas**, alineadas con las evaluaciones parciales del curso. Finalmente, en el **Examen Final Transversal**, los/as estudiantes consolidarán su solución integrando todos los módulos desarrollados en un sistema funcional.

---

## Sección 1: Diseño de Arquitectura y Patrones de Microservicios (Parcial 1)

### Contexto

Grupo Cordillera gestiona diariamente grandes volúmenes de datos provenientes de diversas áreas del negocio. Debido a la existencia de múltiples sistemas independientes, el acceso a la información se vuelve un proceso complejo.

Actualmente para generar reportes, los equipos deben realizar procesos manuales que incluyen:

- Extracción de datos desde diferentes sistemas
- Consolidación de información en hojas de cálculo
- Formatear información y transcribir de un sistema a otro
- Validación manual de indicadores
- Preparación de reportes para reuniones ejecutivas

El flujo anterior genera diversas dificultades:

- Retrasos en la generación de reportes
- Posibilidad de errores en la consolidación de información
- Falta de acceso en tiempo real a indicadores clave del negocio
- Dependencia de procesos manuales para elaborar reportes

La alta dirección requiere poder visualizar información de forma clara y rápida, considerando indicadores de ventas, inventarios, logística y rentabilidad.

Para mejorar la gestión, Grupo Cordillera ha decidido desarrollar una plataforma tecnológica que permita monitorear indicadores en tiempo real, consolidando datos provenientes de distintos sistemas internos.

La solución debe contemplar tres módulos principales:

- **Gestión de datos organizacionales:** Permite consolidar información asegurando que los datos estén disponibles de manera centralizada para el análisis, reportería u otra forma de representación.
- **Gestión de indicadores (KPI):** Permite definir, analizar indicadores clave con los objetivos estratégicos de la organización.
- **Visualización de reportes:** Proporcionar un panel de control que permita visualizar indicadores estratégicos y generar reportes para la alta dirección.

### Requerimientos Técnicos

Los/as estudiantes deberán diseñar una **arquitectura de microservicios escalable**, aplicando **patrones de diseño y arquetipos arquitectónicos** que permitan la modularización del sistema. Para ello, deberán:

- **Definir los microservicios clave**, asegurando separación de responsabilidades y escalabilidad.
- Diseñar una **API Gateway** que gestione la comunicación entre microservicios y el frontend.
- Implementar patrones como **Repository Pattern** para la persistencia de datos, **Factory Method** para la creación de instancias y **Circuit Breaker** para manejar fallos en la comunicación entre servicios.
- Asegurar que los servicios sean **escalables y desacoplados**, permitiendo futuras mejoras sin afectar el funcionamiento del sistema.
- Documentar las decisiones arquitectónicas y justificar la selección de patrones.

Al finalizar esta etapa, los equipos deberán presentar un informe con la propuesta de arquitectura, un diagrama detallado de los microservicios y una justificación de los patrones seleccionados.

---

## Sección 2: Desarrollo de Componentes Frontend y Backend (Parcial 2)

### Contexto

Después de definir la arquitectura del sistema, Grupo Cordillera busca desarrollar una primera versión funcional que permita validar la propuesta tecnológica. La empresa requiere un sistema que cuente con una **interfaz de usuario intuitiva y responsiva**. El backend debe ser capaz de procesar volúmenes de datos sin comprometer el rendimiento.

### Requerimientos Técnicos

Los/as estudiantes deberán desarrollar la solución con los siguientes elementos clave:

- **Frontend:** Implementar una interfaz construida con un framework moderno (React, Angular o Vue.js), asegurando que la comunicación con el backend se realice vía API REST. Los componentes frontend deberán ser empaquetados como **módulos NPM** reutilizables.
- **Backend:** Construir al menos tres componentes backend utilizando **arquetipos Maven personalizados**:
  - Un **Backend For Frontend (BFF)** para gestionar la interacción entre el frontend y los microservicios.
  - Dos **microservicios independientes**, conectados a bases de datos mediante JPA.
- **Conexión con Bases de Datos:** Utilizar **JPA y entidades**, asegurando la persistencia de datos. También se podrán utilizar procedimientos almacenados (SPs) para optimizar operaciones.
- **Versionamiento del Código:** Todos los componentes deberán ser versionados en **Git**, utilizando estrategias de branching como Git Flow o GitHub Flow para facilitar el trabajo colaborativo.
- **Implementación de Patrones de Diseño:** Aplicar patrones adecuados para mejorar la organización y mantenibilidad del código, asegurando que los servicios sean modulares y fácilmente extensibles.

Como resultado de esta etapa, los/as estudiantes deberán entregar la primera versión funcional del sistema, acompañada de una presentación donde expliquen las decisiones técnicas, la implementación de los componentes y la integración entre frontend y backend.

---

## Sección 3: Integración, Pruebas Unitarias y Presentación Final (Parcial 3)

### Contexto

Con el sistema en funcionamiento, Grupo Cordillera quiere asegurar que la solución sea robusta y confiable antes de su implementación definitiva. Para ello, se requiere que el código desarrollado cumpla con **estándares de calidad**, aplicando **pruebas unitarias** y validando la **cobertura del código** antes del lanzamiento.

En esta última fase, la empresa evaluará cómo se integran los diferentes módulos del sistema, garantizando que la plataforma sea escalable y esté lista para su despliegue en producción.

### Requerimientos Técnicos

En esta etapa, los/as estudiantes deberán:

- **Realizar Pruebas Unitarias:** Implementar pruebas para cada uno de los componentes del sistema, asegurando una cobertura mínima del **60% del código**. La validación de cobertura se realizará utilizando herramientas como **SonarQube**.
- **Refactorización y Mejora del Código:** Revisar el código desarrollado, identificar oportunidades de optimización y aplicar mejoras siguiendo buenas prácticas de desarrollo.
- **Despliegue y Ejecución de Pruebas:** Integrar las pruebas unitarias en un pipeline de **Integración Continua**, asegurando que se ejecuten automáticamente en cada actualización del código.
- **Documentación Final:** Completar la documentación del sistema, incluyendo diagramas actualizados, descripción de la arquitectura final y detalles sobre la implementación de pruebas.

### Presentación Final

Cada equipo presentará su solución ante el/la docente, abordando los siguientes puntos clave:

- Explicación de la arquitectura del sistema y decisiones técnicas.
- Demostración del funcionamiento del software, mostrando la integración entre frontend y backend.
- Estrategia de pruebas implementada y validación de cobertura.
- Reflexión sobre los desafíos enfrentados y aprendizajes adquiridos.

Esta presentación servirá como cierre del curso, permitiendo evaluar de manera integral la capacidad de los/as estudiantes para diseñar, desarrollar, integrar y validar una solución de software basada en **microservicios, patrones de diseño y pruebas automatizadas**.
