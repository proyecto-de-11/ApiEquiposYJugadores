# Primera fase: Construcción
# Usa una imagen de Maven que incluye OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establece el directorio de trabajo
WORKDIR /app

# Copia los archivos de Maven (pom.xml) y el código fuente (src)
COPY pom.xml .
COPY src ./src

# Compila el proyecto. Esto genera el JAR en /app/target/
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# --- Segunda fase: Ejecución ---
# Usa una imagen ligera de OpenJDK 21 para la ejecución
FROM openjdk:21-jdk-slim

# Establece el directorio de trabajo
WORKDIR /app

# Línea CORREGIDA: Usa COPY --from=build para transferir el JAR
# El asterisco (*) encuentra el JAR con la versión (ej: ApiEquiposYJugadores-0.0.1-SNAPSHOT.jar)
# y lo copia y renombra a app.jar en el directorio actual.
COPY --from=build /app/target/*.jar /app/app.jar

# Expone el puerto de la aplicación
EXPOSE 8082

# Comando para ejecutar la aplicación
# NOTA: Usamos el puerto 8082 que configuraste.
CMD ["java", "-jar", "/app/app.jar"]