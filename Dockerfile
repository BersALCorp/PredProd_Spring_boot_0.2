FROM maven:3.8.4-openjdk-17-slim AS build
COPY . /app
WORKDIR /app
RUN mvn -e -DskipTests=true clean package

FROM openjdk:17-jdk

COPY --from=build /app/target/spring-boot_security-demo-0.0.1-SNAPSHOT.jar /app.jar

ENV DB_BASE_URL=jdbc:postgresql
ENV DB_HOST=postgres
ENV DB_PORT=5432
ENV DB_USERNAME=bers
ENV DB_PASSWORD=123456we
ENV DB_SCHEMA=public
ENV SERVER_PORT=8082

EXPOSE $SERVER_PORT

CMD ["java", "-jar", "/app.jar"]
