# ─── Stage 1: Build del frontend React ───────────────────────────────────────
# El build context es la raíz del proyecto (ver docker-compose.yml),
# por eso las rutas COPY apuntan a frontend/ y bff-gateway/.
FROM node:24-alpine AS frontend-builder

WORKDIR /frontend

COPY frontend/package*.json ./

RUN npm install

COPY frontend/ .

# Sin VITE_API_BASE_URL: las URLs son relativas (/api/...) y se resuelven
# contra el mismo origen (BFF en :8081), eliminando cualquier problema de CORS.
RUN npm run build

# ─── Stage 2: Build del JAR de Spring Boot ────────────────────────────────────
FROM maven:3.9.9-eclipse-temurin-21 AS backend-builder

WORKDIR /app

COPY bff-gateway/pom.xml .
COPY bff-gateway/src ./src

# Copia el dist del frontend al directorio static del BFF antes de compilar,
# para que quede empaquetado dentro del JAR como recurso estático de Spring Boot.
COPY --from=frontend-builder /frontend/dist ./src/main/resources/static

RUN mvn -DskipTests clean package

# ─── Stage 3: Imagen final ────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=backend-builder /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
