FROM openjdk:11-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/nasa-rover-image-downloader-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar
COPY src/main/resources/dates.txt /app/src/main/resources/dates.txt

# Create directory for images
RUN mkdir -p /app/nasa_images

# Set the entry point
ENTRYPOINT ["java", "-jar", "app.jar"]