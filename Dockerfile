FROM maven:3.9-amazoncorretto-25 AS build

WORKDIR /app

COPY pom.xml .
COPY qoq-test-app-application/pom.xml qoq-test-app-application/
COPY qoq-test-app-common-library/pom.xml qoq-test-app-common-library/

RUN mvn dependency:go-offline

COPY qoq-test-app-application/src qoq-test-app-application/src
COPY qoq-test-app-common-library/src qoq-test-app-common-library/src

RUN mvn clean package -DskipTests

FROM amazoncorretto:25-alpine

WORKDIR /app

COPY --from=build /app/qoq-test-app-application/target/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]