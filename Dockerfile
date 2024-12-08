# Use the official OpenJDK 17 image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the application's JAR file into the container
COPY build/libs/mini-social-network-0.0.1-SNAPSHOT.jar app.jar

# Expose the application's default port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]