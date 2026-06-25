# Etapa 1: Compilación
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/app
WORKDIR /home/app
# Compila el proyecto saltándose los tests para ahorrar tiempo
RUN gradle build -x test --no-daemon

# Etapa 2: Ejecución (Usando el estándar actual Eclipse Temurin)
FROM eclipse-temurin:17-jre-alpine
EXPOSE 8089
COPY --from=build /home/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]