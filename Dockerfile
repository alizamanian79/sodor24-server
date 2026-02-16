FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/server-0.0.1-SNAPSHOT.jar sodor24-server.jar

EXPOSE 8181

CMD ["java","-jar","sodor24-server.jar"]
