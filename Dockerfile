# Etapa 1: Compilación
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/app
WORKDIR /home/app
# Compila el proyecto saltándose los tests para ahorrar tiempo de procesamiento en el plan free
RUN gradle build -x test --no-daemon

# Etapa 2: Ejecución
FROM openjdk:17-jdk-slim
# Render mapeará automáticamente el puerto que expongamos aquí
EXPOSE 8089
COPY --from=build /home/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]