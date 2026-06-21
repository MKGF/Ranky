# Using base image from OpenJDK
FROM eclipse-temurin:24-jdk

# Creating /app /classes directory
RUN mkdir -p /app/classes

# Adding JAR from application to the container
COPY ./Ranky.jar /app/Ranky.jar

# Adding contents of the config directory into /app/classes/config
COPY ./config /app/classes/config

# Run application on container init
ENTRYPOINT ["java", "-jar", "/app/Ranky.jar", "--spring.profiles.active=secret"]