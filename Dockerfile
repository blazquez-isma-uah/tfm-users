# -------- Build stage --------
# Usa una imagen de Maven con JDK para compilar la aplicación
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# Copia el archivo pom.xml y descarga las dependencias
# Esto permite aprovechar la caché de Docker si no cambian las dependencias
# y evita recompilar todo si solo cambian los archivos fuente
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

# Copia el código y compila
COPY src ./src
# Construye el Jar sin ejecutar las pruebas
RUN mvn -B -DskipTests package

# -------- Runtime stage --------
# Usa una imagen de JRE para ejecutar la aplicación (mas ligera)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Crea un usuario no root
# Esto es una buena práctica de seguridad para evitar correr la app como root
# y minimizar riesgos en caso de vulnerabilidades
RUN useradd -r -s /bin/false appuser
USER appuser

# Copia el JAR desde la etapa de build
COPY --from=build /app/target/*.jar /app/app.jar

# Puerto de la app
EXPOSE 8080

# Variables típicas (puedes sobreescribirlas en compose/Actions)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
