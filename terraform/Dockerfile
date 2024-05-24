# Using base image from OpenJDK
FROM openjdk:17-jdk

# Adding JAR from application to the container
COPY ./Ranky.jar /app/Ranky.jar

# Run application on container init
ENTRYPOINT ["java", "-Dspring.profiles.active=secret", "-jar", "/app/Ranky.jar"]