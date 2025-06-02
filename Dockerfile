# ----------- Build stage -----------
FROM maven:3.9-amazoncorretto-11-alpine as build

WORKDIR /app

# Copy only pom.xml files first to take advantage of Docker layer caching
COPY pom.xml .
COPY common/pom.xml common/
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY maria-output-adapter/pom.xml maria-output-adapter/
COPY mongo-output-adapter/pom.xml mongo-output-adapter/
COPY rest-input-adapter/pom.xml rest-input-adapter/
COPY cli-input-adapter/pom.xml cli-input-adapter/

# Download dependencies (will be cached unless pom files change)
RUN mvn dependency:go-offline -B

# Copy all source code
COPY common/src common/src
COPY domain/src domain/src
COPY application/src application/src
COPY maria-output-adapter/src maria-output-adapter/src
COPY mongo-output-adapter/src mongo-output-adapter/src
COPY rest-input-adapter/src rest-input-adapter/src
COPY cli-input-adapter/src cli-input-adapter/src

# Build the entire project (you can also use -pl rest-input-adapter to limit to that module)
RUN mvn clean install -DskipTests

# ----------- Runtime stage -----------
FROM amazoncorretto:17-alpine

WORKDIR /app

# Copy only the compiled JAR from the build stage (rest-input-adapter)
COPY --from=build /app/rest-input-adapter/target/*.jar app.jar
COPY docker-application.properties application.properties

# Expose port 3000 for the REST API
EXPOSE 3000

# Start the Spring Boot application with the provided properties file
ENTRYPOINT ["java", "-jar", "app.jar", "-Dspring.config.location=application.properties"]
