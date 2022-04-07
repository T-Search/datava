### Build Stage ###
FROM maven:3-openjdk-17-slim AS build
WORKDIR /opt/datava/
ADD . .
RUN cp -f src/main/resources/application.properties.sample src/main/resources/application.properties
RUN mvn package

### Run Stage ###
FROM openjdk:17-slim
WORKDIR /opt/datava/
COPY --from=build /opt/datava/target/Datava.jar .

EXPOSE 8080
ENTRYPOINT ["java","-jar","Datava.jar"]