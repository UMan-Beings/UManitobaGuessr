# -------------------------
# Step 1: Build the JAR
# -------------------------
FROM amazoncorretto:21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Build the application
RUN ./gradlew bootJar

# -------------------------
# Step 1: Runtime
# -------------------------
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# start springboot when a container is run from this image
ENTRYPOINT ["java", "-jar", "app.jar"]