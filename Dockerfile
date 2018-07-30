FROM maven:3.5.0-jdk-8-alpine
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN gradle build


FROM java:8
COPY --from=0 /usr/src/app/build/libs/rest-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]