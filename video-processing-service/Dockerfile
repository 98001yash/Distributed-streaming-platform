# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
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

RUN mvn clean package -pl video-processing-service -am -DskipTests

# ---------- RUNTIME STAGE ----------
FROM jrottenberg/ffmpeg:6.0-ubuntu2204

WORKDIR /app

COPY --from=build /app/video-processing-service/target/*.jar app.jar

EXPOSE 8094

ENTRYPOINT ["java", "-jar", "app.jar"]