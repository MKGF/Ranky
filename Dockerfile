FROM openjdk:14-jdk-alpine
VOLUME /tmp
COPY target/Ranky-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]