# Stage 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project file
# Copying pom.xml first helps leverage Docker layer caching for dependencies
COPY pom.xml .

# Download project dependencies (optional, but can speed up subsequent builds)
# RUN mvn dependency:go-offline

# Copy the entire project source code
COPY src ./src

# Copy other necessary root files (like SQLite DB if used directly)
# Adjust this if identifier.sqlite is needed at runtime and not generated
COPY identifier.sqlite .
COPY InstructionOnSwaggerUi.md .
COPY InstructionOnSwaggerUi.pdf .
COPY Readme.md .
COPY .gitignore .


# Package the application, skipping tests
# This will create the JAR file in the /app/target directory
RUN mvn package -DskipTests

# Stage 2: Create the final runtime image
# Use a slim JRE image for a smaller footprint
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
# Assumes your final JAR is named something like backend-*.jar in the target folder.
# Adjust the pattern if your artifactId/version results in a different name.
# We copy it to app.jar for a consistent name.
COPY --from=build /app/target/*.jar app.jar

# Copy the SQLite database if it's needed by the running application
COPY --from=build /app/identifier.sqlite .

# Expose the port the application runs on
# Check your application.properties or application.yml for the actual port (default is often 8080)
EXPOSE 9091

# Command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add arguments like Spring profiles here
# CMD ["--spring.profiles.active=prod"]
