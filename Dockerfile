# Etapa 1: Compilación
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /home/app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew build -x test --no-daemon

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jre-alpine
EXPOSE 8089
COPY --from=build /home/app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]