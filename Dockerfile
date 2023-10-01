FROM maven:3.8.4-openjdk-17-slim AS build
COPY . /app
WORKDIR /app
RUN mvn -e -DskipTests=true clean package

FROM openjdk:17-jdk
COPY --from=build /app/target/spring-boot_security-demo-0.0.1-SNAPSHOT.jar /app.jar

CMD ["java", "-jar", "/app.jar"]