FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src
RUN chmod +x mvnw && ./mvnw clean package spring-boot:repackage -Dmaven.test.skip=true

FROM eclipse-temurin:25-jre
WORKDIR /app
RUN useradd --system --create-home --shell /usr/sbin/nologin appuser
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
USER appuser
ENTRYPOINT ["java", "-jar", "app.jar"]