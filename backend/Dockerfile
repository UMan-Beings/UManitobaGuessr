# ------------------
# Build Stage
# ------------------

# Use Amazon Corretto to build the Spring Boot backend
FROM amazoncorretto:21 AS build

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files first to cache dependency downloads
COPY gradlew gradlew.bat build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Make Gradle wrapper executable
RUN chmod +x ./gradlew

# Download dependencies (Gradle will store them in the volume)
RUN ./gradlew dependencies --no-daemon

# Copy backend source code
COPY src /app/src

# Build the Spring Boot backend JAR
RUN ./gradlew bootJar --no-daemon

# ------------------
# Runtime Stage
# ------------------

# Use Amazon Corretto as runtime for the Spring Boot backend
FROM amazoncorretto:21

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Runtime defaults so published image can run without external env files.
ENV SPRING_PROFILES_ACTIVE=prod \
	SPRING_DATASOURCE_URL=jdbc:postgresql://db_umg:5432/users_database \
	SPRING_DATASOURCE_USERNAME=appuser \
	SPRING_DATASOURCE_PASSWORD=apppass \
	DB_SSL_MODE=disable \
	SPRING_SECURITY_USER_NAME=user \
	SPRING_SECURITY_USER_PASSWORD=password \
	JWT_SECRET=user_secret_key_do_not_use_in_production

# Expose backend port
EXPOSE 8081

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]