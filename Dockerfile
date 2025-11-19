# Primera fase: Construcción
# Usa una imagen de Maven que incluye OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de Maven (pom.xml) y el código fuente (src)
COPY pom.xml .
COPY src ./src

# Compila el proyecto, saltando las pruebas
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# --- Segunda fase: Ejecución ---
# Usa una imagen ligera de OpenJDK 21 para la ejecución
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR generado y lo renombra a "app.jar" para simplificar
RUN --from=build sh -c "ls /app/target/*.jar | xargs -n1 cp {} /app/app.jar"

# Expone el puerto de la aplicación
EXPOSE 8082

# Comando para ejecutar la aplicación
# Si tienes varias configuraciones (e.g., application-prod.properties),
# usarías -Dspring.profiles.active=prod aquí.
CMD ["java", "-jar", "/app/app.jar"]