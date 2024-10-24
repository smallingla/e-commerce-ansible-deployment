# Use an official Maven image to build the application
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and download the dependencies (this helps with caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application.
COPY src ./src
RUN mvn clean package -DskipTests

# Set JDK image for running the application
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on, in this case 8080
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
