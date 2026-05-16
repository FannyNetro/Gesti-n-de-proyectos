# Stage 1: Build
FROM gradle:8.5-jdk17 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Usamos --no-daemon y limitamos la memoria de Gradle
RUN ./gradlew :buildFatJar --no-daemon -Dorg.gradle.jvmargs="-Xmx384m"

# Stage 2: Run
FROM openjdk:17-slim
EXPOSE 8080
COPY --from=build /home/gradle/src/build/libs/*.jar /app/vgtech-backend.jar
ENTRYPOINT ["java", "-Xmx384m", "-jar", "/app/vgtech-backend.jar"]
