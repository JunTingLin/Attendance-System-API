# Stage 1: Maven build
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.15.0/opentelemetry-javaagent.jar .

ENTRYPOINT ["sh", "-c", "\
  if [ -n \"$OTEL_EXPORTER_OTLP_ENDPOINT\" ]; then \
    java \
      -javaagent:opentelemetry-javaagent.jar \
      -Dotel.resource.attributes=$OTEL_RESOURCE_ATTRIBUTES \
      -Dotel.exporter.otlp.endpoint=$OTEL_EXPORTER_OTLP_ENDPOINT \
      -Dotel.exporter.otlp.protocol=$OTEL_EXPORTER_OTLP_PROTOCOL \
      -Dotel.exporter.otlp.headers=\"$OTEL_EXPORTER_OTLP_HEADERS\" \
      -jar app.jar; \
  else \
    java -jar app.jar; \
  fi"]