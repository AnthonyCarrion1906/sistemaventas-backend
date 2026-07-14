# 1. Definimos la imagen base con Java 21
FROM eclipse-temurin:21-jdk-jammy AS build

# 2. Instalamos Maven
RUN apt-get update && apt-get install -y maven

# 3. Establecemos el directorio de trabajo
WORKDIR /app

# 4. Copiamos el archivo pom.xml y descargamos dependencias (caché eficiente)
COPY pom.xml .
RUN mvn dependency:go-offline

# 5. Copiamos todo el código fuente
COPY src ./src

# 6. Compilamos el proyecto sin ejecutar los tests
RUN mvn clean package -DskipTests

# 7. Creamos la imagen final más ligera (solo con Java instalado)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 8. Copiamos solo el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# 9. Exponemos el puerto 8080 (donde escucha Spring Boot)
EXPOSE 10000

# 10. Comando para arrancar la app
ENTRYPOINT ["java", "-jar", "app.jar"]