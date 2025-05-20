FROM openjdk:17

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} social-service.jar

ENTRYPOINT ["java", "-jar", "social-service.jar"]

EXPOSE 8090