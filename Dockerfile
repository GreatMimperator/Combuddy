FROM openjdk:17-oracle
MAINTAINER combuddy.ru
WORKDIR /app
COPY build/libs/*.jar app.jar
# datasource not set
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]