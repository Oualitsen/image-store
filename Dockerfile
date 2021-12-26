FROM amazoncorretto:17.0.1

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar", "--spring.data.mongodb.host=host.docker.internal" ]

