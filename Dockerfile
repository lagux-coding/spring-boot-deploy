FROM openjdk:21

ARG FILE_JAR=target/web4-0.0.1-SNAPSHOT.jar

ADD ${FILE_JAR} api-service2.jar

ENTRYPOINT ["java", "-jar", "api-service2.jar"]

EXPOSE 8080