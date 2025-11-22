
FROM maven:3.9.2-eclipse-temurin-17 AS build

WORKDIR /app

RUN rm -rf /app/target

COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests


FROM eclipse-temurin:17-jdk

WORKDIR /app


COPY --from=build /app/target/*.jar app.jar


EXPOSE 8181


ENTRYPOINT ["java","-jar","app.jar"]
