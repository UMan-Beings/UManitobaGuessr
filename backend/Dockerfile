# Use an official Gradle image to build the application
FROM amazoncorretto:21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew bootJar

# Use an official OpenJDK image to run the application
FROM amazoncorretto:21

# Set the working directory inside the container
WORKDIR /app

# Set default Spring profile to prod
ENV SPRING_PROFILES_ACTIVE=prod

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]