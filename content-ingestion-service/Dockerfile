# ==============================
# 🏗️ BUILD STAGE
# ==============================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy root pom
COPY pom.xml .

# Copy all modules
COPY common-lib ./common-lib
COPY discovery-server ./discovery-server
COPY api-gateway ./api-gateway
COPY auth-service ./auth-service
COPY user-service ./user-service
COPY content-service ./content-service
COPY content-ingestion-service ./content-ingestion-service
COPY video-processing-service ./video-processing-service
COPY streaming-service ./streaming-service
COPY analytic-service ./analytic-service


RUN mvn clean package -pl content-ingestion-service -am -DskipTests



FROM eclipse-temurin:21-jre

WORKDIR /app


COPY --from=build /app/content-ingestion-service/target/*.jar app.jar


EXPOSE 8093

# Run
ENTRYPOINT ["java", "-jar", "app.jar"]